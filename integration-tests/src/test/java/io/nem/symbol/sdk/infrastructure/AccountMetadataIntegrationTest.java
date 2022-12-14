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
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.metadata.MetadataType;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransactionFactory;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/** Integration tests around account metadata. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountMetadataIntegrationTest extends BaseIntegrationTest {

  private Account testAccount;

  @BeforeEach
  void setup() {
    testAccount = config().getDefaultAccount();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  public void addMetadataToAccount(RepositoryType type) {
    BigInteger key =
        SerializationUtils.toUnsignedBigInteger(
            RandomUtils.generateRandomInt(100000) + Long.MAX_VALUE);
    Assertions.assertTrue(key.compareTo(BigInteger.ZERO) > 0);
    System.out.println("Key: " + key);

    String message = "This is the message for this account! 汉字" + key;
    System.out.println(
        "Storing message '"
            + message
            + "' in account metadata "
            + testAccount.getAddress().plain());

    AccountMetadataTransaction transaction =
        AccountMetadataTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                testAccount.getAddress(),
                key,
                StringEncoder.getBytes(message))
            .maxFee(maxFee)
            .build();

    AccountMetadataTransaction processedTransaction =
        announceAggregateAndValidate(type, transaction, testAccount).getLeft();

    Assertions.assertEquals(
        transaction.getValueSizeDelta(), processedTransaction.getValueSizeDelta());

    Assertions.assertEquals(
        transaction.getScopedMetadataKey(), processedTransaction.getScopedMetadataKey());

    sleep(1000);

    Metadata metadata =
        assertMetadata(
            transaction,
            get(getRepositoryFactory(type)
                    .createMetadataRepository()
                    .search(
                        new MetadataSearchCriteria()
                            .metadataType(MetadataType.ACCOUNT)
                            .sourceAddress(testAccount.getAddress())
                            .scopedMetadataKey(transaction.getScopedMetadataKey())))
                .getData());

    assertMetadata(
        transaction,
        get(getRepositoryFactory(type)
                .createMetadataRepository()
                .search(
                    new MetadataSearchCriteria()
                        .metadataType(MetadataType.ACCOUNT)
                        .sourceAddress(testAccount.getAddress())
                        .scopedMetadataKey(metadata.getScopedMetadataKey())))
            .getData());

    assertMetadata(
        transaction,
        get(getRepositoryFactory(type)
                .createMetadataRepository()
                .search(
                    new MetadataSearchCriteria()
                        .metadataType(MetadataType.ACCOUNT)
                        .sourceAddress(testAccount.getAddress())
                        .targetAddress(testAccount.getAddress())
                        .scopedMetadataKey(metadata.getScopedMetadataKey())))
            .getData());

    Assertions.assertArrayEquals(StringEncoder.getBytes(message), processedTransaction.getValue());
  }

  private Metadata assertMetadata(AccountMetadataTransaction transaction, List<Metadata> metadata) {

    Optional<Metadata> endpointMetadata =
        metadata.stream()
            .filter(
                m ->
                    m.getScopedMetadataKey().equals(transaction.getScopedMetadataKey())
                        && m.getMetadataType().equals(MetadataType.ACCOUNT)
                        && m.getTargetAddress().equals(testAccount.getAddress()))
            .findFirst();

    Assertions.assertTrue(endpointMetadata.isPresent());
    Assertions.assertArrayEquals(transaction.getValue(), endpointMetadata.get().getValue());
    return endpointMetadata.get();
  }
}
