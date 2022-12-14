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
import io.nem.symbol.sdk.api.MetadataSearchCriteria;
import io.nem.symbol.sdk.api.MetadataTransactionService;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.metadata.MetadataType;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.NamespaceMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/** Integration tests around account metadata. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NamespaceMetadataIntegrationTest extends BaseIntegrationTest {

  private Account testAccount;

  @BeforeEach
  void setup() {
    testAccount = config().getDefaultAccount();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void addMetadataToNamespace(RepositoryType type) {
    String namespaceName =
        "namespace-for-metadata-integration-test-"
            + Double.valueOf(Math.floor(Math.random() * 10000)).intValue();

    NamespaceId targetNamespaceId = createRootNamespace(type, testAccount, namespaceName);

    System.out.println("Setting metadata " + targetNamespaceId.getIdAsHex());

    String message = "This is the message in the Namespace!";
    BigInteger key = BigInteger.TEN;
    Address targetAddress = testAccount.getAddress();

    MetadataTransactionService metadataTransactionService =
        new MetadataTransactionServiceImpl(getRepositoryFactory(type));

    Observable<NamespaceMetadataTransactionFactory> namespaceMetadataTransactionFactory =
        metadataTransactionService.createNamespaceMetadataTransactionFactory(
            targetAddress, key, message, targetAddress, targetNamespaceId);

    NamespaceMetadataTransaction transaction = get(namespaceMetadataTransactionFactory).build();
    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.createComplete(
                getNetworkType(),
                getDeadline(),
                Collections.singletonList(transaction.toAggregate(testAccount.getPublicAccount())))
            .maxFee(maxFee)
            .build();

    AggregateTransaction announceCorrectly =
        announceAndValidate(type, testAccount, aggregateTransaction);

    Assertions.assertEquals(testAccount.getPublicAccount(), announceCorrectly.getSigner().get());
    Assertions.assertEquals(1, announceCorrectly.getInnerTransactions().size());
    Assertions.assertEquals(
        transaction.getType(), announceCorrectly.getInnerTransactions().get(0).getType());
    NamespaceMetadataTransaction processedTransaction =
        (NamespaceMetadataTransaction) announceCorrectly.getInnerTransactions().get(0);

    // TODO problem comparing namespaces, sometime they are negative big integers
    Assertions.assertEquals(
        transaction.getTargetNamespaceId().getIdAsHex(),
        processedTransaction.getTargetNamespaceId().getIdAsHex());
    Assertions.assertEquals(
        transaction.getValueSizeDelta(), processedTransaction.getValueSizeDelta());

    Assertions.assertEquals(
        transaction.getScopedMetadataKey(), processedTransaction.getScopedMetadataKey());

    System.out.println("Metadata '" + message + "' stored!");

    sleep(3000);
    List<Metadata> metadata =
        get(getRepositoryFactory(type)
                .createMetadataRepository()
                .search(
                    new MetadataSearchCriteria()
                        .targetId(targetNamespaceId)
                        .metadataType(MetadataType.NAMESPACE)))
            .getData();

    assertMetadata(transaction, metadata);

    assertMetadata(
        transaction,
        get(getRepositoryFactory(type)
                .createMetadataRepository()
                .search(
                    new MetadataSearchCriteria()
                        .targetId(targetNamespaceId)
                        .metadataType(MetadataType.NAMESPACE)
                        .scopedMetadataKey(key)))
            .getData());

    assertMetadata(
        transaction,
        get(getRepositoryFactory(type)
                .createMetadataRepository()
                .search(
                    new MetadataSearchCriteria()
                        .targetId(targetNamespaceId)
                        .metadataType(MetadataType.NAMESPACE)
                        .targetAddress(targetAddress)
                        .scopedMetadataKey(key)))
            .getData());

    Assertions.assertArrayEquals(StringEncoder.getBytes(message), processedTransaction.getValue());
  }

  private String assertMetadata(NamespaceMetadataTransaction transaction, List<Metadata> metadata) {

    Optional<Metadata> endpointMetadata =
        metadata.stream()
            .filter(
                m ->
                    m.getScopedMetadataKey().equals(transaction.getScopedMetadataKey())
                        && m.getMetadataType().equals(MetadataType.NAMESPACE)
                        && m.getTargetAddress().equals(testAccount.getAddress()))
            .findFirst();

    Assertions.assertTrue(endpointMetadata.isPresent());

    Assertions.assertEquals(
        transaction.getTargetNamespaceId(), endpointMetadata.get().getTargetId().get());
    Assertions.assertNotNull(endpointMetadata.get().getCompositeHash());
    Assertions.assertArrayEquals(transaction.getValue(), endpointMetadata.get().getValue());
    return endpointMetadata.get().getCompositeHash();
  }
}
