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

import io.nem.sdk.model.account.*;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountHttpTest extends BaseTest {
    private AccountHttp accountHttp;

    @BeforeAll
    void setup() { /* initializations done in BaseTest class */ }
    public AccountHttp getAccountHttp() throws IOException {
        if (this.accountHttp == null)
            this.accountHttp = new AccountHttp(this.getApiUrl());
        return this.accountHttp;
    }

    @Test
    void getAccountInfo() throws ExecutionException, InterruptedException, IOException {
        AccountInfo accountInfo = this.getAccountHttp()
                .getAccountInfo(this.getTestAccountAddress())
                .toFuture()
                .get();

        String publicKey = this.config().getTestAccountPublicKey();
        assertEquals(this.config().getTestAccountPublicKey(), accountInfo.getPublicKey());
    }

    @Test
    void getAccountsInfo() throws ExecutionException, InterruptedException, IOException {
        List<AccountInfo> accountInfos = this.getAccountHttp()
                .getAccountsInfo(Collections.singletonList(this.getTestAccountAddress()))
                .toFuture()
                .get();

        assertEquals(1, accountInfos.size());
        assertEquals(this.config().getTestAccountPublicKey(), accountInfos.get(0).getPublicKey());
    }

    @Test
    void getMultipleAccountsInfo() throws ExecutionException, InterruptedException, IOException {
        List<AccountInfo> accountInfos = this.getAccountHttp()
                .getAccountsInfo(Collections.singletonList(this.getTestAccountAddress()))
                .toFuture()
                .get();

        assertEquals(1, accountInfos.size());
        assertEquals(this.config().getTestAccountPublicKey(), accountInfos.get(0).getPublicKey());
    }

    @Test
    void getMultisigAccountInfo() throws ExecutionException, InterruptedException, IOException {
        MultisigAccountInfo multisigAccountInfo = this.getAccountHttp()
                .getMultisigAccountInfo(Address.createFromRawAddress("SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX"))
                .toFuture()
                .get();

        assertEquals("B694186EE4AB0558CA4AFCFDD43B42114AE71094F5A1FC4A913FE9971CACD21D", multisigAccountInfo.getAccount().getPublicKey());
    }

    @Test
    void getMultisigAccountGraphInfo() throws ExecutionException, InterruptedException, IOException {
        MultisigAccountGraphInfo multisigAccountGraphInfos = this.getAccountHttp()
                .getMultisigAccountGraphInfo(Address.createFromRawAddress("SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX"))
                .toFuture()
                .get();

        assertEquals(new HashSet<>(Arrays.asList(-2, -1, 0, 1)), multisigAccountGraphInfos.getLevelsNumber());
    }

    @Test
    void transactions() throws ExecutionException, InterruptedException, IOException {
        List<Transaction> transactions = this.getAccountHttp()
                .transactions(this.getTestPublicAccount())
                .toFuture()
                .get();

        assertEquals(10, transactions.size());
    }

    @Test
    void transactionsWithPagination() throws ExecutionException, InterruptedException, IOException {
        List<Transaction> transactions = this.getAccountHttp()
                .transactions(this.getTestPublicAccount())
                .toFuture()
                .get();

        assertEquals(10, transactions.size());

        List<Transaction> nextTransactions = this.getAccountHttp()
                .transactions(this.getTestPublicAccount(), new QueryParams(11, transactions.get(0).getTransactionInfo().get().getId().get()))
                .toFuture()
                .get();

        assertEquals(11, nextTransactions.size());
        assertEquals(transactions.get(1).getTransactionInfo().get().getHash(), nextTransactions.get(0).getTransactionInfo().get().getHash());
    }

    @Test
    void incomingTransactions() throws ExecutionException, InterruptedException, IOException {
        List<Transaction> transactions = this.getAccountHttp()
                .incomingTransactions(this.getTestPublicAccount())
                .toFuture()
                .get();

        // TODO generate incoming transactions in order to test non-zero incoming transactions size
        assertEquals(0, transactions.size());
    }

    @Test
    void outgoingTransactions() throws ExecutionException, InterruptedException, IOException {
        List<Transaction> transactions = this.getAccountHttp()
                .outgoingTransactions(this.getTestPublicAccount())
                .toFuture()
                .get();

        assertEquals(10, transactions.size());
    }

    @Test
    void aggregateBondedTransactions() throws ExecutionException, InterruptedException, IOException {
        List<AggregateTransaction> transactions = this.getAccountHttp()
                .aggregateBondedTransactions(this.getTestPublicAccount())
                .toFuture()
                .get();

        assertEquals(0, transactions.size());
    }

    @Test
    void unconfirmedTransactions() throws ExecutionException, InterruptedException, IOException {
        List<Transaction> transactions = this.getAccountHttp()
                .unconfirmedTransactions(this.getTestPublicAccount())
                .toFuture()
                .get();

        assertEquals(0, transactions.size());
    }

    @Test
    void throwExceptionWhenBlockDoesNotExists() throws IOException {
        TestObserver<AccountInfo> testObserver = new TestObserver<>();
        this.getAccountHttp()
                .getAccountInfo(Address.createFromRawAddress("SARDGFTDLLCB67D4HPGIMIHPNSRYRJRT7DOBGWZY"))
                .subscribeOn(Schedulers.single())
                .test()
                .awaitDone(2, TimeUnit.SECONDS)
                .assertFailure(RuntimeException.class);
    }
}