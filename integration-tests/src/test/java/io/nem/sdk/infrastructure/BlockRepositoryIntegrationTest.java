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
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.nem.sdk.api.BlockRepository;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.receipt.Statement;
import io.nem.sdk.model.transaction.Transaction;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlockRepositoryIntegrationTest extends BaseIntegrationTest {


    private BlockRepository getBlockRepository(RepositoryType type) {
        return getRepositoryFactory(type).createBlockRepository();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getBlockByHeight(RepositoryType type) throws ExecutionException, InterruptedException {
        BlockInfo blockInfo = getBlockRepository(type).getBlockByHeight(BigInteger.valueOf(1))
            .toFuture()
            .get();

        assertEquals(1, blockInfo.getHeight().intValue());
        assertEquals(0, blockInfo.getTimestamp().intValue());
    }

    // TODO to fix after catbuffer integration
    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getBlockTransactions(RepositoryType type) throws ExecutionException, InterruptedException {
        List<Transaction> transactions =
            getBlockRepository(type).getBlockTransactions(BigInteger.valueOf(1)).toFuture().get();

        assertEquals(10, transactions.size());

        List<Transaction> nextTransactions =
            getBlockRepository(type)
                .getBlockTransactions(
                    BigInteger.valueOf(1),
                    new QueryParams(10,
                        transactions.get(0).getTransactionInfo().get().getId().get()))
                .toFuture()
                .get();

        assertEquals(10, nextTransactions.size());
        assertEquals(
            transactions.get(1).getTransactionInfo().get().getHash(),
            nextTransactions.get(0).getTransactionInfo().get().getHash());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getBlockReceipts(RepositoryType type) throws ExecutionException, InterruptedException {
        Statement statement = getBlockRepository(type).getBlockReceipts(BigInteger.valueOf(6262))
            .toFuture()
            .get();

        assertFalse(statement.getTransactionStatements().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void throwExceptionWhenBlockDoesNotExists(RepositoryType type) {
        TestObserver<BlockInfo> testObserver = new TestObserver<>();
        getBlockRepository(type)
            .getBlockByHeight(BigInteger.valueOf(0))
            .subscribeOn(Schedulers.single())
            .test()
            .awaitDone(2, TimeUnit.SECONDS)
            .assertFailureAndMessage(RepositoryCallException.class,
                "Not Found");
    }
}
