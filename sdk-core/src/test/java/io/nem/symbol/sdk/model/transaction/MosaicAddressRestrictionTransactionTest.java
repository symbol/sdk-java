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
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class MosaicAddressRestrictionTransactionTest extends AbstractTransactionTester {

  static Account account =
      new Account(
          "26b64cb10f005e5988a36744ca19e20d835ccc7c105aaa5f3b212da593180930", NetworkType.TEST_NET);
  static String generationHash = "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

  @Test
  void createAMosaicAddressRestrictionTransactionViaStaticConstructor() {

    Duration epochAdjustment = Duration.ofSeconds(100);
    MosaicAddressRestrictionTransaction transaction =
        MosaicAddressRestrictionTransactionFactory.create(
                NetworkType.TEST_NET,
                Deadline.create(epochAdjustment),
                new MosaicId(new BigInteger("0")), // restricted
                // MosaicId
                BigInteger.valueOf(1), // restrictionKey
                account.getAddress(), // targetAddress
                // previousRestrictionValue
                BigInteger.valueOf(8) // newRestrictionValue
                )
            .previousRestrictionValue(BigInteger.valueOf(9))
            .build();

    assertEquals(NetworkType.TEST_NET, transaction.getNetworkType());
    assertEquals(1, (int) transaction.getVersion());
    assertTrue(
        LocalDateTime.now().isBefore(transaction.getDeadline().getLocalDateTime(epochAdjustment)));
    assertEquals(BigInteger.valueOf(0), transaction.getMaxFee());
    assertEquals(new BigInteger("0"), transaction.getMosaicId().getId());
    assertEquals(BigInteger.valueOf(1), transaction.getRestrictionKey());
    assertEquals(account.getAddress(), transaction.getTargetAddress());
    assertEquals(BigInteger.valueOf(9), transaction.getPreviousRestrictionValue());
    assertEquals(BigInteger.valueOf(8), transaction.getNewRestrictionValue());
  }

  @Test
  void serializeAndSignTransaction() {
    MosaicAddressRestrictionTransaction transaction =
        MosaicAddressRestrictionTransactionFactory.create(
                NetworkType.TEST_NET,
                new Deadline(BigInteger.ONE),
                new MosaicId(new BigInteger("1")), // restricted
                // MosaicId
                BigInteger.valueOf(1), // restrictionKey
                account.getAddress(), // targetAddress
                BigInteger.valueOf(8) // newRestrictionValue
                )
            .previousRestrictionValue(BigInteger.valueOf(9))
            .build();

    SignedTransaction signedTransaction = transaction.signWith(account, generationHash);

    assertEquals(
        "B80000000000000058465D666400CFADA970F9303D3CEEAC6307691D73BECF3C1851864E595F29957969A435DE77E7F45C2AC4601646E7D14E12F19A04E70BC387179C2BD8CAC3049801508C58666C746F471538E43002B85B1CD542F9874B2861183919BA8787B6000000000198514200000000000000000100000000000000010000000000000001000000000000000900000000000000080000000000000098D66C33420E5411995BACFCA2B28CF1C9F5DD7AB1914687",
        signedTransaction.getPayload());
  }

  @Test
  void serialize() {
    MosaicAddressRestrictionTransaction transaction =
        MosaicAddressRestrictionTransactionFactory.create(
                NetworkType.TEST_NET,
                new Deadline(BigInteger.ONE),
                new MosaicId(new BigInteger("1")), // restricted
                // MosaicId
                BigInteger.valueOf(1), // restrictionKey
                account.getAddress(), // targetAddress
                BigInteger.valueOf(8) // newRestrictionValue
                )
            .previousRestrictionValue(BigInteger.valueOf(9))
            .signer(account.getPublicAccount())
            .build();

    String expected =
        "B800000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000009801508C58666C746F471538E43002B85B1CD542F9874B2861183919BA8787B6000000000198514200000000000000000100000000000000010000000000000001000000000000000900000000000000080000000000000098D66C33420E5411995BACFCA2B28CF1C9F5DD7AB1914687";
    assertSerialization(expected, transaction);

    String expectedEmbeddedHash =
        "68000000000000009801508C58666C746F471538E43002B85B1CD542F9874B2861183919BA8787B60000000001985142010000000000000001000000000000000900000000000000080000000000000098D66C33420E5411995BACFCA2B28CF1C9F5DD7AB1914687";
    assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
  }
}
