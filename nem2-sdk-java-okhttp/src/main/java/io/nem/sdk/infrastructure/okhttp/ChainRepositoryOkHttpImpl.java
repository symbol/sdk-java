/*
 *  Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.okhttp;

import io.nem.sdk.api.ChainRepository;
import io.nem.sdk.model.blockchain.BlockchainScore;
import io.nem.sdk.model.transaction.UInt64;
import io.nem.sdk.openapi.okhttp_gson.api.ChainRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiCallback;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.ChainScoreDTO;
import io.nem.sdk.openapi.okhttp_gson.model.HeightInfoDTO;
import io.reactivex.Observable;
import java.math.BigInteger;

/**
 * Chain http repository.
 */
public class ChainRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    ChainRepository {

    private final ChainRoutesApi client;

    public ChainRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        client = new ChainRoutesApi(apiClient);
    }

    public ChainRoutesApi getClient() {
        return client;
    }

    /**
     * Get Block chain height
     *
     * @return Observable<BigInteger>
     */
    public Observable<BigInteger> getBlockchainHeight() {

        ApiCall<ApiCallback<HeightInfoDTO>> callback = client::getBlockchainHeightAsync;
        return exceptionHandling(
            call(callback).map(blockchainHeight -> extractIntArray(blockchainHeight.getHeight())));

    }

    /**
     * Get Block chain score
     *
     * @return Observable<BigInteger>
     */
    public Observable<BlockchainScore> getBlockchainScore() {
        ApiCall<ApiCallback<ChainScoreDTO>> callback = client::getChainScoreAsync;
        return exceptionHandling(call(callback).map(
            blockchainScoreDTO ->
                new BlockchainScore(
                    UInt64.extractBigInteger(blockchainScoreDTO.getScoreLow()),
                    UInt64.extractBigInteger(blockchainScoreDTO.getScoreHigh()))));
    }
}
