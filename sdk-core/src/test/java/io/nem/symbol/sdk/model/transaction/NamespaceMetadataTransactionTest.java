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

import io.nem.symbol.core.utils.StringEncoder;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

/** Tests for the {@link NamespaceMetadataTransaction} and the factory. */
public class NamespaceMetadataTransactionTest extends AbstractTransactionTester {

  private static Account account =
      new Account(
          "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728", NetworkType.TEST_NET);

  @Test
  void shouldBuild() {
    NamespaceId namespaceId = NamespaceId.createFromId(BigInteger.valueOf(1000));
    NamespaceMetadataTransaction transaction =
        NamespaceMetadataTransactionFactory.create(
                NetworkType.TEST_NET,
                new Deadline(BigInteger.ONE),
                account.getPublicAccount().getAddress(),
                namespaceId,
                BigInteger.TEN,
                StringEncoder.getBytes("ABC123"))
            .valueSizeDelta(10)
            .build();
    assertEquals("ABC123", StringEncoder.getString(transaction.getValue()));
    assertEquals(namespaceId, transaction.getTargetNamespaceId());
    assertEquals(NetworkType.TEST_NET, transaction.getNetworkType());
    assertEquals(10, transaction.getValueSizeDelta());
    assertEquals(BigInteger.TEN, transaction.getScopedMetadataKey());

    assertEquals(account.getAddress(), transaction.getTargetAddress());
  }

  @Test
  void shouldGenerateBytes() {
    NamespaceId namespaceId = NamespaceId.createFromId(BigInteger.valueOf(1000));
    NamespaceMetadataTransaction transaction =
        NamespaceMetadataTransactionFactory.create(
                NetworkType.TEST_NET,
                new Deadline(BigInteger.ONE),
                account.getAddress(),
                namespaceId,
                BigInteger.TEN,
                StringEncoder.getBytes("ABC123"))
            .valueSizeDelta(10)
            .signer(account.getPublicAccount())
            .build();

    String expectedHash =
        "B20000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E60000000001984443000000000000000001000000000000009883025FF3A8AB5AD104631FB370F290004952CD1FB931E00A00000000000000E8030000000000000A000600414243313233";
    assertSerialization(expectedHash, transaction);

    String expectedEmbeddedHash =
        "6200000000000000F6503F78FBF99544B906872DDB392F4BE707180D285E7919DBACEF2E9573B1E600000000019844439883025FF3A8AB5AD104631FB370F290004952CD1FB931E00A00000000000000E8030000000000000A000600414243313233";
    assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
  }
}
