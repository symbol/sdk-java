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

import io.nem.sdk.model.blockchain.BlockchainStorageInfo;
import io.nem.sdk.model.blockchain.ServerInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DiagnosticHttpTest extends BaseTest {
    private DiagnosticHttp diagnosticHttp;

    @BeforeAll
    void setup() throws IOException {
        diagnosticHttp = new DiagnosticHttp(this.getApiUrl());
    }

    @Test
    void getBlockchainStorage() throws ExecutionException, InterruptedException {
        BlockchainStorageInfo blockchainStorageInfo = diagnosticHttp
                .getBlockchainStorage()
                .toFuture()
                .get();

        assertTrue(blockchainStorageInfo.getNumAccounts() > 0);
        assertTrue(blockchainStorageInfo.getNumTransactions() > 0);
        assertTrue(blockchainStorageInfo.getNumBlocks() > 0);
    }

    @Test
    void getServerInfo() throws ExecutionException, InterruptedException {
        ServerInfo serverInfo = diagnosticHttp
                .getServerInfo()
                .toFuture()
                .get();

        assertTrue(serverInfo.getRestVersion() != "");
        assertTrue(serverInfo.getSdkVersion() != "");
    }
}
