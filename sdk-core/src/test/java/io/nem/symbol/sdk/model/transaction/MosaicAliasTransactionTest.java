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

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.AliasAction;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

/** Tests for the {@link MosaicAliasTransaction} and the factory. */
public class MosaicAliasTransactionTest extends AbstractTransactionTester {

  private static Account account =
      new Account(
          "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728", NetworkType.TEST_NET);

  @Test
  void shouldBuild() {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    NamespaceId namespaceId = NamespaceId.createFromName("anamespaced");
    MosaicAliasTransaction transaction =
        MosaicAliasTransactionFactory.create(
                NetworkType.TEST_NET,
                new Deadline(BigInteger.ONE),
                AliasAction.LINK,
                namespaceId,
                mosaicId)
            .build();

    assertEquals(NetworkType.TEST_NET, transaction.getNetworkType());
    assertEquals(AliasAction.LINK, transaction.getAliasAction());
    assertEquals(mosaicId, transaction.getMosaicId());
    assertEquals(namespaceId, transaction.getNamespaceId());
  }

  @Test
  void serialize() {
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);
    NamespaceId namespaceId = NamespaceId.createFromName("anamespaced");
    MosaicAliasTransaction transaction =
        MosaicAliasTransactionFactory.create(
                NetworkType.TEST_NET,
                new Deadline(BigInteger.ONE),
                AliasAction.LINK,
                namespaceId,
                mosaicId)
            .signer(account.getPublicAccount())
            .build();

    String expectedHash =
        "910000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E60000000001984E4300000000000000000100000000000000A487791451FDF1B60A0000000000000001";
    assertSerialization(expectedHash, transaction);

    String expectedEmbeddedHash =
        "4100000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E60000000001984E43A487791451FDF1B60A0000000000000001";
    assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
  }
}
