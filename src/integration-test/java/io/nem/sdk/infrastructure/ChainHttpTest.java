/*
 * Copyright 2019 NEM
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

import io.nem.sdk.model.blockchain.BlockchainScore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChainHttpTest extends BaseTest {
    private ChainHttp chainHttp;

    @BeforeAll
    void setup() throws IOException {
        chainHttp = new ChainHttp(this.getApiUrl());
    }

    @Test
    void getBlockchainHeight() throws ExecutionException, InterruptedException {
        BigInteger blockchainHeight = chainHttp
                .getBlockchainHeight()
                .toFuture()
                .get();

        assertTrue(blockchainHeight.intValue() > 0);
    }

    @Test
    void getBlockchainScore() throws ExecutionException, InterruptedException {
        BlockchainScore blockchainScore = chainHttp
                .getBlockchainScore()
                .toFuture()
                .get();

        assertTrue(blockchainScore.getScoreLow().longValue() >= 0);
        assertTrue(blockchainScore.getScoreHigh().longValue() >= 0);
    }
}
