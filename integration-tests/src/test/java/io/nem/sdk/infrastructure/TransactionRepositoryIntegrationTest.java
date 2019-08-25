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

import io.nem.sdk.api.TransactionRepository;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionStatus;
import io.nem.sdk.model.transaction.TransactionType;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionRepositoryIntegrationTest extends BaseIntegrationTest {

    private final String transactionHash =
        "EE5B39DBDA00BA39D06B9E67AE5B43162366C862D9B8F656F7E7068D327377BE";


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void getTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException {
        Transaction transaction = getTransactionRepository(type).getTransaction(transactionHash)
            .toFuture()
            .get();

        assertEquals(TransactionType.TRANSFER.getValue(), transaction.getType().getValue());
        assertEquals(transactionHash, transaction.getTransactionInfo().get().getHash().get());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void getTransactions(RepositoryType type)
        throws ExecutionException, InterruptedException {
        List<Transaction> transaction =
            getTransactionRepository(type)
                .getTransactions(Collections.singletonList(transactionHash))
                .toFuture()
                .get();

        assertEquals(TransactionType.TRANSFER.getValue(), transaction.get(0).getType().getValue());
        assertEquals(transactionHash,
            transaction.get(0).getTransactionInfo().get().getHash().get());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void getTransactionStatus(RepositoryType type)
        throws ExecutionException, InterruptedException {
        TransactionStatus transactionStatus =
            getTransactionRepository(type).getTransactionStatus(transactionHash).toFuture().get();

        assertEquals(transactionHash, transactionStatus.getHash());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void getTransactionsStatuses(RepositoryType type)
        throws ExecutionException, InterruptedException {
        List<TransactionStatus> transactionStatuses =
            getTransactionRepository(type)
                .getTransactionStatuses(Collections.singletonList(transactionHash))
                .toFuture()
                .get();

        assertEquals(transactionHash, transactionStatuses.get(0).getHash());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void throwExceptionWhenTransactionStatusOfATransactionDoesNotExists(
        RepositoryType type) {
        TestObserver<TransactionStatus> testObserver = new TestObserver<>();
        getTransactionRepository(type)
            .getTransactionStatus(transactionHash)
            .subscribeOn(Schedulers.single())
            .test()
            .awaitDone(2, TimeUnit.SECONDS)
            .assertFailure(RuntimeException.class);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void throwExceptionWhenTransactionDoesNotExists(
        RepositoryType type) {
        TestObserver<Transaction> testObserver = new TestObserver<>();
        getTransactionRepository(type)
            .getTransaction(transactionHash)
            .subscribeOn(Schedulers.single())
            .test()
            .awaitDone(2, TimeUnit.SECONDS)
            .assertFailure(RuntimeException.class);
    }

    private TransactionRepository getTransactionRepository(
        RepositoryType type) {
        return getRepositoryFactory(type).createTransactionRepository();
    }
}
