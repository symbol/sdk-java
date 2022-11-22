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
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MultisigAccountModificationTransactionTest extends AbstractTransactionTester {

  private static final Account account =
      new Account(
          "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728", NetworkType.TEST_NET);

  @Test
  void createAMultisigModificationTransactionViaConstructor() {
    Duration epochAdjustment = Duration.ofSeconds(100);
    List<UnresolvedAddress> additions =
        Collections.singletonList(
            PublicAccount.createFromPublicKey(
                    "68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b111",
                    NetworkType.TEST_NET)
                .getAddress());
    List<UnresolvedAddress> deletions =
        Collections.singletonList(
            PublicAccount.createFromPublicKey(
                    "68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b222",
                    NetworkType.TEST_NET)
                .getAddress());
    MultisigAccountModificationTransaction multisigAccountModificationTransaction =
        MultisigAccountModificationTransactionFactory.create(
                NetworkType.TEST_NET,
                Deadline.create(epochAdjustment),
                (byte) 2,
                (byte) 1,
                additions,
                deletions)
            .build();

    assertEquals(NetworkType.TEST_NET, multisigAccountModificationTransaction.getNetworkType());
    assertTrue(1 == multisigAccountModificationTransaction.getVersion());
    assertTrue(
        LocalDateTime.now()
            .isBefore(
                multisigAccountModificationTransaction
                    .getDeadline()
                    .getLocalDateTime(epochAdjustment)));
    assertEquals(BigInteger.valueOf(0), multisigAccountModificationTransaction.getMaxFee());
    assertEquals(2, multisigAccountModificationTransaction.getMinApprovalDelta());
    assertEquals(1, multisigAccountModificationTransaction.getMinRemovalDelta());
    assertEquals(additions, multisigAccountModificationTransaction.getAddressAdditions());
    assertEquals(deletions, multisigAccountModificationTransaction.getAddressDeletions());
  }

  @Test
  @DisplayName("Serialization")
  void serialization() {
    // Generated at
    // symbol-library-js/test/transactions/ModifyMultisigAccountTransaction.spec.js
    List<UnresolvedAddress> additions =
        Collections.singletonList(
            PublicAccount.createFromPublicKey(
                    "68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b111",
                    NetworkType.TEST_NET)
                .getAddress());
    List<UnresolvedAddress> deletions =
        Collections.singletonList(
            PublicAccount.createFromPublicKey(
                    "68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b222",
                    NetworkType.TEST_NET)
                .getAddress());
    MultisigAccountModificationTransaction transaction =
        MultisigAccountModificationTransactionFactory.create(
                NetworkType.TEST_NET,
                new Deadline(BigInteger.ONE),
                (byte) 2,
                (byte) 1,
                additions,
                deletions)
            .signer(account.getPublicAccount())
            .build();

    String expected =
        "B80000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E60000000001985541000000000000000001000000000000000102010100000000985ED2343582DFB4D14DC837BF18E3C9BE5271FF9B34A1D0988760369DC78761E7EBCC6CFAEA44EE946ED0637B81D44F";

    assertSerialization(expected, transaction);

    String expectedEmbedded =
        "6800000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E600000000019855410102010100000000985ED2343582DFB4D14DC837BF18E3C9BE5271FF9B34A1D0988760369DC78761E7EBCC6CFAEA44EE946ED0637B81D44F";

    assertEmbeddedSerialization(expectedEmbedded, transaction);
  }
}
