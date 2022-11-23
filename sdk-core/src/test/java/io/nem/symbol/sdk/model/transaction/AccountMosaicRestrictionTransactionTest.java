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

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountMosaicRestrictionTransactionTest extends AbstractTransactionTester {

  static Account account =
      new Account(
          "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728", NetworkType.TEST_NET);

  static UnresolvedMosaicId mosaicId = new MosaicId(BigInteger.valueOf(1000));

  static UnresolvedMosaicId mosaicId2 = new MosaicId(BigInteger.valueOf(2000));

  @Test
  void create() {
    List<UnresolvedMosaicId> additions = Collections.singletonList(mosaicId);
    List<UnresolvedMosaicId> deletions = Collections.singletonList(mosaicId2);

    AccountMosaicRestrictionTransaction transaction =
        AccountMosaicRestrictionTransactionFactory.create(
                NetworkType.TEST_NET,
                new Deadline(BigInteger.ONE),
                AccountMosaicRestrictionFlags.ALLOW_INCOMING_MOSAIC,
                additions,
                deletions)
            .build();
    Assertions.assertEquals(
        AccountMosaicRestrictionFlags.ALLOW_INCOMING_MOSAIC, transaction.getRestrictionFlags());
    Assertions.assertEquals(additions, transaction.getRestrictionAdditions());
    Assertions.assertEquals(deletions, transaction.getRestrictionDeletions());
  }

  @Test
  void shouldGenerateBytes() {

    List<UnresolvedMosaicId> additions = Collections.singletonList(mosaicId);
    List<UnresolvedMosaicId> deletions = Collections.singletonList(mosaicId2);
    AccountMosaicRestrictionTransaction transaction =
        AccountMosaicRestrictionTransactionFactory.create(
                NetworkType.TEST_NET,
                new Deadline(BigInteger.ONE),
                AccountMosaicRestrictionFlags.ALLOW_INCOMING_MOSAIC,
                additions,
                deletions)
            .signer(account.getPublicAccount())
            .build();

    String expected =
        "980000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E60000000001985042000000000000000001000000000000000200010100000000E803000000000000D007000000000000";
    assertSerialization(expected, transaction);

    String expectedEmbeddedHash =
        "4800000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E600000000019850420200010100000000E803000000000000D007000000000000";
    assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
  }
}
