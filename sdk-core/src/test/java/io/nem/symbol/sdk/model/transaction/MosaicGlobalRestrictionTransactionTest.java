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
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class MosaicGlobalRestrictionTransactionTest extends AbstractTransactionTester {

  static Account account =
      new Account("26b64cb10f005e5988a36744ca19e20d835ccc7c105aaa5f3b212da593180930", networkType);
  static String generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

  static Duration epochAdjustment = Duration.ofSeconds(1573430400);

  @Test
  void createAMosaicGlobalRestrictionTransactionViaStaticConstructor() {
    MosaicGlobalRestrictionTransaction transaction =
        MosaicGlobalRestrictionTransactionFactory.create(
                networkType,
                Deadline.create(epochAdjustment),
                new MosaicId(new BigInteger("1")), // restrictedMosaicId
                BigInteger.valueOf(1), // restrictionKey
                BigInteger.valueOf(8), // newRestrictionValue
                MosaicRestrictionType.GE // newRestrictionType
                )
            .referenceMosaicId(new MosaicId(new BigInteger("2")))
            .previousRestrictionValue(BigInteger.valueOf(9))
            .previousRestrictionType(MosaicRestrictionType.EQ)
            .build();

    assertEquals(networkType, transaction.getNetworkType());
    assertTrue(1 == transaction.getVersion());
    assertTrue(
        LocalDateTime.now().isBefore(transaction.getDeadline().getLocalDateTime(epochAdjustment)));
    assertEquals(BigInteger.valueOf(0), transaction.getMaxFee());
    assertEquals(new BigInteger("1"), transaction.getMosaicId().getId());
    assertEquals(new BigInteger("2"), transaction.getReferenceMosaicId().getId());
    assertEquals(BigInteger.valueOf(1), transaction.getRestrictionKey());
    assertEquals(BigInteger.valueOf(9), transaction.getPreviousRestrictionValue());
    assertEquals(MosaicRestrictionType.EQ, transaction.getPreviousRestrictionType());
    assertEquals(BigInteger.valueOf(8), transaction.getNewRestrictionValue());
    assertEquals(MosaicRestrictionType.GE, transaction.getNewRestrictionType());
  }

  @Test
  void serializeAndSignTransaction() {
    MosaicGlobalRestrictionTransaction transaction =
        MosaicGlobalRestrictionTransactionFactory.create(
                networkType,
                new Deadline(BigInteger.ONE),
                new MosaicId(new BigInteger("1")), // restricted
                // MosaicId
                BigInteger.valueOf(1), // restrictionKey
                BigInteger.valueOf(8), // newRestrictionValue
                MosaicRestrictionType.GE // newRestrictionType
                )
            .referenceMosaicId(new MosaicId(new BigInteger("2")))
            .previousRestrictionValue(BigInteger.valueOf(9))
            .previousRestrictionType(MosaicRestrictionType.EQ)
            .build();

    SignedTransaction signedTransaction = transaction.signWith(account, generationHash);

    assertEquals(
        "00000000010000000000000002000000000000000100000000000000090000000000000008000000000000000106",
        signedTransaction.getPayload().substring(248));
  }

  @Test
  void serialize() {
    MosaicGlobalRestrictionTransaction transaction =
        MosaicGlobalRestrictionTransactionFactory.create(
                networkType,
                new Deadline(BigInteger.ONE),
                new MosaicId(new BigInteger("3456")), // restricted
                // MosaicId
                BigInteger.valueOf(1), // restrictionKey
                BigInteger.valueOf(8), // newRestrictionValue
                MosaicRestrictionType.GE // newRestrictionType
                )
            .referenceMosaicId(new MosaicId(new BigInteger("2")))
            .previousRestrictionValue(BigInteger.valueOf(9))
            .previousRestrictionType(MosaicRestrictionType.EQ)
            .signer(account.getPublicAccount())
            .build();

    String expected =
        "AA00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009801508C58666C746F471538E43002B85B1CD542F9874B2861183919BA8787B6000000000198514100000000000000000100000000000000800D00000000000002000000000000000100000000000000090000000000000008000000000000000106";
    assertSerialization(expected, transaction);

    String expectedEmbeddedHash =
        "5A000000000000009801508C58666C746F471538E43002B85B1CD542F9874B2861183919BA8787B60000000001985141800D00000000000002000000000000000100000000000000090000000000000008000000000000000106";
    assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
  }
}
