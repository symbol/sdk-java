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
package io.nem.symbol.sdk.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.BinarySerializationImpl;
import io.nem.symbol.sdk.infrastructure.RandomUtils;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.Collections;

/** Super class of all transaction unit tests. */
abstract class AbstractTransactionTester {

  public static NetworkType networkType = NetworkType.TEST_NET;

  private BinarySerializationImpl binarySerialization = new BinarySerializationImpl();

  /**
   * This method asserts that the given transaction has a serialization of the expected string.
   *
   * <p>It also asserts that the transaction can be serialized and deserialized back to the expected
   * payload testing that the transaction can be reproduced.
   *
   * @param expected the expected serialized payload
   * @param transaction the transaction under test
   * @param <T> the type of the transaction.
   * @return the cloned transaction
   */
  protected <T extends Transaction> T assertSerialization(String expected, T transaction) {
    byte[] actual = transaction.serialize();

    assertEquals(expected.toUpperCase(), ConvertUtils.toHex(actual));
    assertEquals(ConvertUtils.fromHexToBytes(expected).length, transaction.getSize());
    T deserialized = (T) binarySerialization.deserialize(actual);

    assertEquals(
        expected.toUpperCase(), ConvertUtils.toHex(binarySerialization.serialize(deserialized)));
    if (!AggregateTransaction.class.isInstance(transaction)) {
      assertAggregate(transaction);
    }
    return deserialized;
  }

  /**
   * This method asserts that the given transaction, if put into a aggregate transaction, can be
   * reproduced.
   *
   * <p>It also asserts that the transaction can be deserialized back to the same aggregate
   * transaction.
   *
   * @param transaction the transaction under test
   * @param <T> the type of the transaction.
   * @return the cloned transaction
   */
  protected <T extends Transaction> T assertAggregate(T transaction) {
    byte[] actual = transaction.serialize();
    // Clone
    T deserialized = (T) binarySerialization.deserialize(actual);
    String generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";
    Account account = Account.generateNewAccount(deserialized.getNetworkType());

    AggregateTransaction aggregateTransaction =
        AggregateTransactionFactory.create(
                TransactionType.AGGREGATE_BONDED,
                deserialized.getNetworkType(),
                deserialized.getDeadline(),
                Collections.singletonList(deserialized.toAggregate(account.getPublicAccount())),
                Collections.emptyList())
            .maxFee(BigInteger.TEN)
            .build();
    account.sign(aggregateTransaction, generationHash);

    byte[] serializedAggregate = aggregateTransaction.serialize();

    assertEquals(serializedAggregate.length, aggregateTransaction.getSize());

    assertEquals(
        ConvertUtils.toHex(serializedAggregate),
        ConvertUtils.toHex(binarySerialization.deserialize(serializedAggregate).serialize()));
    return deserialized;
  }

  /**
   * This method asserts that the given transaction has a embedded serialization of the expected
   * string.
   *
   * <p>It also asserts that the transaction can be serialized and deserialized back to the expected
   * embedded payload testing that the transaction can be reproduced if embedded.
   *
   * @param expected the expected serialized payload
   * @param transaction the transaction under test
   * @param <T> the type of the transaction.
   * @return the cloned transaction
   */
  protected <T extends Transaction> T assertEmbeddedSerialization(String expected, T transaction) {
    byte[] actual = binarySerialization.serializeEmbedded(transaction);
    assertEquals(expected.toUpperCase(), ConvertUtils.toHex(actual));
    T deserialized =
        (T) binarySerialization.deserializeEmbedded(SerializationUtils.toDataInput(actual));
    assertEquals(
        expected.toUpperCase(),
        ConvertUtils.toHex(binarySerialization.serializeEmbedded(deserialized)));
    return deserialized;
  }

  protected String createRandomSignature() {
    return ConvertUtils.toHex(RandomUtils.generateRandomBytes(64));
  }
}
