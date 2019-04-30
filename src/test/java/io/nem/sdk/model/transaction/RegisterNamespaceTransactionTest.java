/*
 * Copyright 2018 NEM
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

package io.nem.sdk.model.transaction;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RegisterNamespaceTransactionTest {

    String publicKey = "b4f12e7c9f6946091e2cb8b6d3a12b50d17ccbbf646386ea27ce2946a7423dcf";
    Account testAccount = Account.createFromPrivateKey(publicKey, NetworkType.MIJIN_TEST);

    @Test
    void createANamespaceCreationRootNamespaceTransactionViaStaticConstructor() {
        RegisterNamespaceTransaction registerNamespaceTransaction = RegisterNamespaceTransaction.createRootNamespace(
                new Deadline(2, ChronoUnit.HOURS),
                "root-test-namespace",
                BigInteger.valueOf(1000),
                NetworkType.MIJIN_TEST
        );

        SignedTransaction signedTransaction = registerNamespaceTransaction.signWith(testAccount);

        assertEquals(signedTransaction.getPayload().substring(240), "00E803000000000000CFCBE72D994BE69B13726F6F742D746573742D6E616D657370616365");
        assertEquals(NetworkType.MIJIN_TEST, registerNamespaceTransaction.getNetworkType());
        assertTrue(2 == registerNamespaceTransaction.getVersion());
        assertTrue(LocalDateTime.now().isBefore(registerNamespaceTransaction.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), registerNamespaceTransaction.getFee());
        assertEquals("root-test-namespace", registerNamespaceTransaction.getNamespaceName());
        assertEquals(NamespaceType.RootNamespace, registerNamespaceTransaction.getNamespaceType());
        assertEquals(BigInteger.valueOf(1000), registerNamespaceTransaction.getDuration().get());
        assertEquals(new BigInteger("11233749441794526159"), registerNamespaceTransaction.getNamespaceId().getId());
    }

    @Test
    void createANamespaceCreationSubNamespaceTransactionViaStaticConstructor() {
        RegisterNamespaceTransaction registerNamespaceTransaction = RegisterNamespaceTransaction.createSubNamespace(
                new Deadline(2, ChronoUnit.HOURS),
                "root-test-namespace",
                "parent-test-namespace",
                NetworkType.MIJIN_TEST
        );

        SignedTransaction signedTransaction = registerNamespaceTransaction.signWith(testAccount);

        assertEquals(signedTransaction.getPayload().substring(240), "014DF55E7F6D8FB7FF924207DF2CA1BBF313726F6F742D746573742D6E616D657370616365");
        assertEquals(NetworkType.MIJIN_TEST, registerNamespaceTransaction.getNetworkType());
        assertTrue(2 == registerNamespaceTransaction.getVersion());
        assertTrue(LocalDateTime.now().isBefore(registerNamespaceTransaction.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), registerNamespaceTransaction.getFee());
        assertEquals("root-test-namespace", registerNamespaceTransaction.getNamespaceName());
        assertEquals(NamespaceType.SubNamespace, registerNamespaceTransaction.getNamespaceType());
        assertEquals(Optional.empty(), registerNamespaceTransaction.getDuration());
        assertEquals(new BigInteger("-883935687755742574"), registerNamespaceTransaction.getNamespaceId().getId());
    }

    @Test
    void createANamespaceCreationSubNamespaceWithParentIdTransactionViaStaticConstructor() {
        RegisterNamespaceTransaction registerNamespaceTransaction = RegisterNamespaceTransaction.createSubNamespace(
                new Deadline(2, ChronoUnit.HOURS),
                "root-test-namespace",
                new NamespaceId(new BigInteger("18426354100860810573")),
                NetworkType.MIJIN_TEST
        );

        SignedTransaction signedTransaction = registerNamespaceTransaction.signWith(testAccount);

        assertEquals(signedTransaction.getPayload().substring(240), "014DF55E7F6D8FB7FF924207DF2CA1BBF313726F6F742D746573742D6E616D657370616365");
        assertEquals(NetworkType.MIJIN_TEST, registerNamespaceTransaction.getNetworkType());
        assertTrue(2 == registerNamespaceTransaction.getVersion());
        assertTrue(LocalDateTime.now().isBefore(registerNamespaceTransaction.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), registerNamespaceTransaction.getFee());
        assertEquals("root-test-namespace", registerNamespaceTransaction.getNamespaceName());
        assertEquals(NamespaceType.SubNamespace, registerNamespaceTransaction.getNamespaceType());
        assertEquals(Optional.empty(), registerNamespaceTransaction.getDuration());
        assertEquals(new BigInteger("-883935687755742574"), registerNamespaceTransaction.getNamespaceId().getId());
        assertEquals(new BigInteger("18426354100860810573"), registerNamespaceTransaction.getParentId().get().getId());
    }

    @Test
    @DisplayName("Serialization root namespace")
    void serializationRootNamespace() {
        // Generated at nem2-library-js/test/transactions/RegisterNamespaceTransaction.spec.js
        byte[] expected = new byte[]{(byte)150,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                2, (byte)144, 78, 65, 0, 0, 0, 0, 0, 0, 0, 0,1,0,0,0,0,0,0,0,0,16,39,0,0,0,0,0,0,126,(byte)233,(byte)179,(byte)184,(byte)175,(byte)223,83,-64,12,110,101,119,110,97,109,101,115,112,97,99,101};

        RegisterNamespaceTransaction registerNamespaceTransaction = RegisterNamespaceTransaction.createRootNamespace(
                new FakeDeadline(),
                "newnamespace",
                BigInteger.valueOf(10000),
                NetworkType.MIJIN_TEST
        );

        byte[] actual = registerNamespaceTransaction.generateBytes();
        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("Serialization sub namespace")
    void serializationSubNamespace() {
        // Generated at nem2-library-js/test/transactions/RegisterNamespaceTransaction.spec.js
        byte[] expected = new byte[]{(byte)150,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                2,(byte)144,78,65,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,1,126,(byte)233,(byte)179,(byte)184,(byte)175,(byte)223,83,64,3,18,(byte)152,27,120,121,(byte)163,-15,12,115,117,98,110,97,109,101,115,112,97,99,101};

        RegisterNamespaceTransaction registerNamespaceTransaction = RegisterNamespaceTransaction.createSubNamespace(
                new FakeDeadline(),
                "subnamespace",
                new NamespaceId(new BigInteger("4635294387305441662")),
                NetworkType.MIJIN_TEST
        );

        byte[] actual = registerNamespaceTransaction.generateBytes();
        assertArrayEquals(expected, actual);
    }
}
