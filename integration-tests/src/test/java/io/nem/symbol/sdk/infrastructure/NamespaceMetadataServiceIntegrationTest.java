/*
 * Copyright 2020 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.core.utils.StringEncoder;
import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.api.MetadataSearchCriteria;
import io.nem.symbol.sdk.api.MetadataTransactionService;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.transaction.NamespaceMetadataTransaction;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/** Integration tests around namespace metadata service. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NamespaceMetadataServiceIntegrationTest extends BaseIntegrationTest {

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void setAndUpdateNamespaceMetadata(RepositoryType type) {

    // TODO try target != signer
    Account signerAccount = config().getDefaultAccount();
    Account targetAccount = config().getDefaultAccount();
    String namespaceName =
        "namespace-id-metadata-service-integration-test-"
            + Double.valueOf(Math.floor(Math.random() * 10000)).intValue();

    NamespaceId targetNamespaceId = super.createRootNamespace(type, signerAccount, namespaceName);

    BigInteger key = BigInteger.valueOf(RandomUtils.generateRandomInt(100000));

    String originalMessage = "The original message";
    String newMessage = "The new Message";

    RepositoryFactory repositoryFactory = getRepositoryFactory(type);
    MetadataRepository metadataRepository = repositoryFactory.createMetadataRepository();

    MetadataTransactionService service = new MetadataTransactionServiceImpl(repositoryFactory);

    NamespaceMetadataTransaction originalTransaction =
        get(service.createNamespaceMetadataTransactionFactory(
                targetAccount.getAddress(),
                key,
                originalMessage,
                signerAccount.getAddress(),
                targetNamespaceId))
            .maxFee(maxFee)
            .build();

    announceAggregateAndValidate(type, originalTransaction, signerAccount);

    waitForIndexing();

    assertMetadata(targetNamespaceId, key, originalMessage, metadataRepository, signerAccount);

    NamespaceMetadataTransaction updateTransaction =
        get(service.createNamespaceMetadataTransactionFactory(
                targetAccount.getAddress(),
                key,
                newMessage,
                signerAccount.getAddress(),
                targetNamespaceId))
            .maxFee(maxFee)
            .build();

    announceAggregateAndValidate(type, updateTransaction, signerAccount);

    waitForIndexing();

    assertMetadata(targetNamespaceId, key, newMessage, metadataRepository, signerAccount);
  }

  private void assertMetadata(
      NamespaceId targetNamespaceId,
      BigInteger key,
      String value,
      MetadataRepository metadataRepository,
      Account signerAccount) {
    MetadataSearchCriteria criteria =
        new MetadataSearchCriteria()
            .targetId(targetNamespaceId)
            .scopedMetadataKey(key)
            .sourceAddress(signerAccount.getAddress());
    Metadata originalMetadata = get(metadataRepository.search(criteria)).getData().get(0);
    Assertions.assertArrayEquals(StringEncoder.getBytes(value), originalMetadata.getValue());
  }
}
