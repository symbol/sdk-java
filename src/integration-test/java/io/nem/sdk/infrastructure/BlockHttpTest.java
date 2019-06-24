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

import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.blockchain.BlockchainStorageInfo;
import io.nem.sdk.model.receipt.Statement;
import io.nem.sdk.model.transaction.Transaction;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlockHttpTest extends BaseTest {
    private BlockHttp blockHttp;

    @BeforeAll
    void setup() throws IOException {
        blockHttp = new BlockHttp(this.getApiUrl());
    }

    @Test
    void getBlockByHeight() throws ExecutionException, InterruptedException {
        BlockInfo blockInfo = blockHttp
                .getBlockByHeight(BigInteger.valueOf(1))
                .toFuture()
                .get();

        assertEquals(1, blockInfo.getHeight().intValue());
        assertEquals(0, blockInfo.getTimestamp().intValue());

    }

    // TODO to fix after catbuffer integration
    @Test
    void getBlockTransactions() throws ExecutionException, InterruptedException {
        List<Transaction> transactions = blockHttp
                .getBlockTransactions(BigInteger.valueOf(1))
                .toFuture()
                .get();

        assertEquals(10, transactions.size());

        List<Transaction> nextTransactions = blockHttp
                .getBlockTransactions(BigInteger.valueOf(1), new QueryParams(10, transactions.get(0).getTransactionInfo().get().getId().get()))
                .toFuture()
                .get();

        assertEquals(10, nextTransactions.size());
        assertEquals(transactions.get(1).getTransactionInfo().get().getHash(), nextTransactions.get(0).getTransactionInfo().get().getHash());
    }

    @Test
    void getBlockReceipts() throws ExecutionException, InterruptedException {
        Statement statement = blockHttp
                .getBlockReceipts(BigInteger.valueOf(6262))
                .toFuture()
                .get();

        assertEquals(statement.getTransactionStatements().isEmpty(), false);
    }
    @Test
    void throwExceptionWhenBlockDoesNotExists() {
        TestObserver<BlockInfo> testObserver = new TestObserver<>();
        blockHttp
                .getBlockByHeight(BigInteger.valueOf(1000000000))
                .subscribeOn(Schedulers.single())
                .test()
                .awaitDone(2, TimeUnit.SECONDS)
                .assertFailure(RuntimeException.class);
    }
}
