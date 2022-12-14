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

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.core.utils.StringEncoder;
import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.api.MetadataSearchCriteria;
import io.nem.symbol.sdk.api.MetadataTransactionService;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransaction;
import java.math.BigInteger;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/** Integration tests around mosaic metadata service. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MosaicMetadataServiceIntegrationTest extends BaseIntegrationTest {

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void setAndUpdateMosaicMetadata(RepositoryType type) {
    // TODO FIX THIS ONE when target != signerAccount
    Account signerAccount = config().getDefaultAccount();
    Account targetAccount = config().getDefaultAccount();

    Assertions.assertFalse(helper().isMultisig(type, signerAccount));
    Assertions.assertFalse(helper().isMultisig(type, targetAccount));

    MosaicId targetMosaicId = super.createMosaic(signerAccount, type, BigInteger.ZERO, null);

    BigInteger key = BigInteger.valueOf(RandomUtils.generateRandomInt(100000));

    String originalMessage = "The original message";
    String newMessage = "The new Message";

    RepositoryFactory repositoryFactory = getRepositoryFactory(type);
    MetadataRepository metadataRepository = repositoryFactory.createMetadataRepository();

    MetadataTransactionService service = new MetadataTransactionServiceImpl(repositoryFactory);

    MosaicMetadataTransaction originalTransaction =
        get(service.createMosaicMetadataTransactionFactory(
                targetAccount.getAddress(),
                key,
                originalMessage,
                signerAccount.getAddress(),
                targetMosaicId))
            .maxFee(maxFee)
            .build();

    Assertions.assertEquals(targetAccount.getAddress(), originalTransaction.getTargetAddress());
    Assertions.assertEquals(targetMosaicId, originalTransaction.getTargetMosaicId());
    Assertions.assertEquals(key, originalTransaction.getScopedMetadataKey());
    Assertions.assertArrayEquals(
        StringEncoder.getBytes(originalMessage), originalTransaction.getValue());

    helper().announceAggregateAndValidate(type, originalTransaction, signerAccount);

    waitForIndexing();

    assertMetadata(
        targetMosaicId, key, originalMessage, metadataRepository, signerAccount, targetAccount);

    MosaicMetadataTransaction updateTransaction =
        get(service.createMosaicMetadataTransactionFactory(
                targetAccount.getAddress(),
                key,
                newMessage,
                signerAccount.getAddress(),
                targetMosaicId))
            .maxFee(maxFee)
            .build();

    Assertions.assertEquals(targetAccount.getAddress(), updateTransaction.getTargetAddress());
    Assertions.assertEquals(targetMosaicId, updateTransaction.getTargetMosaicId());
    Assertions.assertEquals(key, updateTransaction.getScopedMetadataKey());

    Pair<String, Integer> xorAndDelta = ConvertUtils.xorValues(originalMessage, newMessage);
    Assertions.assertArrayEquals(
        StringEncoder.getBytes(xorAndDelta.getLeft()), updateTransaction.getValue());
    Assertions.assertEquals(xorAndDelta.getRight(), updateTransaction.getValueSizeDelta());

    helper().announceAggregateAndValidate(type, updateTransaction, signerAccount);

    waitForIndexing();

    assertMetadata(
        targetMosaicId, key, newMessage, metadataRepository, signerAccount, targetAccount);
  }

  private void assertMetadata(
      MosaicId targetMosaicId,
      BigInteger key,
      String value,
      MetadataRepository metadataRepository,
      Account signerAccount,
      Account targetAccount) {
    MetadataSearchCriteria criteria =
        new MetadataSearchCriteria()
            .targetId(targetMosaicId)
            .targetAddress(targetAccount.getAddress())
            .scopedMetadataKey(key)
            .sourceAddress(signerAccount.getAddress());
    Metadata originalMetadata = get(metadataRepository.search(criteria)).getData().get(0);
    Assertions.assertArrayEquals(StringEncoder.getBytes(value), originalMetadata.getValue());
  }
}
