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

import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.namespace.AliasAction;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

public class AddressAliasTransactionTest extends AbstractTransactionTester {

  @Test
  void shouldSerialize() {

    NetworkType networkType = NetworkType.TEST_NET;
    BigInteger fee = BigInteger.ONE;
    NamespaceId namespaceId = NamespaceId.createFromId(new BigInteger("-8884663987180930485"));
    PublicAccount signature =
        PublicAccount.createFromPublicKey(
            "68b3fbb18729c1fde225c57f8ce080fa828f0067e451a3fd81fa628842b0b763",
            NetworkType.TEST_NET);
    TransactionInfo transactionInfo =
        TransactionInfo.createAggregate(
            new BigInteger("121855"),
            1,
            "5A3D23889CD1E800015929A9",
            "3D28C804EDD07D5A728E5C5FFEC01AB07AFA5766AE6997B38526D36015A4D006",
            "5A0069D83F17CF0001777E55");

    String signatureHex = createRandomSignature();

    AddressAliasTransaction transaction =
        AddressAliasTransactionFactory.create(
                networkType,
                new Deadline(BigInteger.ONE),
                AliasAction.LINK,
                namespaceId,
                signature.getAddress())
            .signer(signature)
            .transactionInfo(transactionInfo)
            .signature(signatureHex)
            .maxFee(fee)
            .build();

    String expectedHash =
        "A100000000000000"
            + signatureHex
            + "68B3FBB18729C1FDE225C57F8CE080FA828F0067E451A3FD81FA628842B0B7630000000001984E42010000000000000001000000000000004BFA5F372D55B3849849E14BEBCA93758EB36805BAE760A57239976F00726BA601";
    assertSerialization(expectedHash, transaction);

    String expectedEmbeddedHash =
        "510000000000000068B3FBB18729C1FDE225C57F8CE080FA828F0067E451A3FD81FA628842B0B7630000000001984E424BFA5F372D55B3849849E14BEBCA93758EB36805BAE760A57239976F00726BA601";
    assertEmbeddedSerialization(expectedEmbeddedHash, transaction);
  }
}
