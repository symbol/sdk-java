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
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.mosaic.Currency;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HashLockTransactionTest extends AbstractTransactionTester {

  private static NetworkType networkType = NetworkType.TEST_NET;

  static Account account =
      new Account("787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d", networkType);
  static String generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

  @Test
  @DisplayName("Serialization")
  void serialization() {
    String expected =
        "B800000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002134E47AEE6F2392A5B3D1238CD7714EABEB739361B7CCF24BAE127F10DF17F200000000019848410000000000000000010000000000000044B262C46CEABB85809698000000000064000000000000008498B38D89C1DC8A448EA5824938FF828926CD9F7747B1844B59B4B6807E878B";
    HashLockTransaction transaction =
        HashLockTransactionFactory.create(
                networkType,
                new Deadline(BigInteger.ONE),
                Currency.CAT_CURRENCY.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                "8498B38D89C1DC8A448EA5824938FF828926CD9F7747B1844B59B4B6807E878B")
            .signer(account.getPublicAccount())
            .build();

    assertSerialization(expected, transaction);
  }

  @Test
  @DisplayName("To aggregate")
  void toAggregate() {
    String expected =
        "68000000000000009A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24000000000198484144B262C46CEABB85809698000000000064000000000000008498B38D89C1DC8A448EA5824938FF828926CD9F7747B1844B59B4B6807E878B";

    HashLockTransaction transaction =
        HashLockTransactionFactory.create(
                networkType,
                new Deadline(BigInteger.ONE),
                Currency.CAT_CURRENCY.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                "8498B38D89C1DC8A448EA5824938FF828926CD9F7747B1844B59B4B6807E878B")
            .build();

    transaction.toAggregate(
        new PublicAccount(
            "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24", networkType));

    assertEmbeddedSerialization(expected, transaction);
  }

  @Test
  void serializeAndSignTransaction() {
    SignedTransaction signedTransaction =
        new SignedTransaction(
            Account.generateNewAccount(networkType).getPublicAccount(),
            "payload",
            "8498B38D89C1DC8A448EA5824938FF828926CD9F7747B1844B59B4B6807E878B",
            TransactionType.AGGREGATE_BONDED);
    HashLockTransaction transaction =
        HashLockTransactionFactory.create(
                networkType,
                new Deadline(BigInteger.ONE),
                Currency.CAT_CURRENCY.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                signedTransaction)
            .build();
    SignedTransaction lockFundsTransactionSigned = transaction.signWith(account, generationHash);

    String payload = lockFundsTransactionSigned.getPayload();
    assertEquals(
        "010000000000000044B262C46CEABB85809698000000000064000000000000008498B38D89C1DC8A448EA5824938FF828926CD9F7747B1844B59B4B6807E878B",
        payload.substring(240));
    assertEquals(
        "5E87D03744C7F89512B7F7CA5386E4E0A16E5A46A8AA58EE1408368880A488DB",
        lockFundsTransactionSigned.getHash());
  }

  @Test
  void shouldThrowExceptionWhenSignedTransactionIsNotTypeAggregateBonded() {

    SignedTransaction signedTransaction =
        new SignedTransaction(
            Account.generateNewAccount(networkType).getPublicAccount(),
            "payload",
            "8498B38D89C1DC8A448EA5824938FF828926CD9F7747B1844B59B4B6807E878B",
            TransactionType.TRANSFER);
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          HashLockTransactionFactory.create(
                  networkType,
                  new Deadline(BigInteger.ONE),
                  Currency.CAT_CURRENCY.createRelative(BigInteger.valueOf(10)),
                  BigInteger.valueOf(100),
                  signedTransaction)
              .build();
        },
        "Signed transaction must be Aggregate Bonded Transaction");
  }
}
