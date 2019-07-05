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

package io.nem.sdk.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceInfo;
import io.nem.sdk.model.namespace.NamespaceName;
import io.nem.sdk.model.transaction.UInt64;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NamespaceHttpTest extends BaseTest {

    private PublicAccount publicAccount;
    private NamespaceId namespaceId;
    private NamespaceHttp namespaceHttp;

    @BeforeAll
    void setup() throws IOException {
        // String publicKey = "B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF";
        String publicKey = "F227B3268481DF7F9825CFB7C2051F441A9BC0C65FA0AA2CF3A438C4B3177B81";
        publicAccount = PublicAccount.createFromPublicKey(publicKey, NetworkType.MIJIN_TEST);
        namespaceId = NetworkCurrencyMosaic.NAMESPACEID;
        namespaceHttp = new NamespaceHttp(this.getApiUrl());
    }

    @Test
    void getNamespace() throws ExecutionException, InterruptedException {
        NamespaceInfo namespaceInfo = namespaceHttp.getNamespace(namespaceId).toFuture().get();

        //  9636553580561478212 85BBEA6CC462B244
        // -8810190493148073404 85BBEA6CC462B244
        assertEquals(new BigInteger("1"), namespaceInfo.getStartHeight());
        assertEquals(new BigInteger("-1"), namespaceInfo.getEndHeight());
        String namespaceIdHex =
            UInt64.bigIntegerToHex(UInt64.fromLowerAndHigher(3294802500L, 2243684972L));
        assertEquals(namespaceIdHex, namespaceId.getIdAsHex());
        assertEquals(namespaceId.getIdAsLong(), namespaceInfo.getLevels().get(1).getIdAsLong());
    }

    @Test
    void getNamespacesFromAccount() throws ExecutionException, InterruptedException {
        List<NamespaceInfo> namespacesInfo =
            namespaceHttp.getNamespacesFromAccount(publicAccount.getAddress()).toFuture().get();

        assertEquals(1, namespacesInfo.size());
        assertEquals(new BigInteger("1"), namespacesInfo.get(0).getStartHeight());
        assertEquals(new BigInteger("-1"), namespacesInfo.get(0).getEndHeight());
        assertEquals(namespaceId.getIdAsLong(),
            namespacesInfo.get(0).getLevels().get(0).getIdAsLong());
    }

    @Test
    void getNamespacesFromAccounts() throws ExecutionException, InterruptedException {
        List<NamespaceInfo> namespacesInfo =
            namespaceHttp
                .getNamespacesFromAccounts(
                    Collections.singletonList(
                        Address.createFromRawAddress("SARNASAS2BIAB6LMFA3FPMGBPGIJGK6IJETM3ZSP")))
                .toFuture()
                .get();

        assertEquals(1, namespacesInfo.size());
        assertEquals(new BigInteger("1"), namespacesInfo.get(0).getStartHeight());
        assertEquals(new BigInteger("-1"), namespacesInfo.get(0).getEndHeight());
        assertEquals(namespaceId, namespacesInfo.get(0).getLevels().get(0).getIdAsLong());
    }

    @Test
    void getNamespaceNames() throws ExecutionException, InterruptedException {
        List<NamespaceName> namespaceNames =
            namespaceHttp.getNamespaceNames(Collections.singletonList(namespaceId)).toFuture()
                .get();

        assertEquals(1, namespaceNames.size());
        assertEquals("nem", namespaceNames.get(0).getName());
        assertEquals(namespaceId, namespaceNames.get(0).getNamespaceId());
    }

    @Test
    void throwExceptionWhenNamespaceDoesNotExists() {
        // TestObserver<NamespaceInfo> testObserver = new TestObserver<>();
        namespaceHttp
            .getNamespace(new NamespaceId("nonregisterednamespace"))
            .subscribeOn(Schedulers.single())
            .test()
            .awaitDone(2, TimeUnit.SECONDS)
            .assertFailure(RuntimeException.class);
    }
}
