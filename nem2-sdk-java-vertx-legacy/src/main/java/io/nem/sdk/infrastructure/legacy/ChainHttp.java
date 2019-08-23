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

package io.nem.sdk.infrastructure.legacy;

import io.nem.sdk.api.ChainRepository;
import io.nem.sdk.model.blockchain.BlockchainScore;
import io.nem.sdk.model.transaction.UInt64;
import io.nem.sdk.openapi.vertx.model.ChainScoreDTO;
import io.nem.sdk.openapi.vertx.model.HeightInfoDTO;
import io.reactivex.Observable;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import java.math.BigInteger;

/**
 * Chain http repository.
 */
public class ChainHttp extends Http implements ChainRepository {

    /**
     * Constructor
     */
    public ChainHttp(String host) {
        this(host, new NetworkHttp(host));
    }

    /**
     * Constructor
     */
    public ChainHttp(String host, NetworkHttp networkHttp) {
        super(host, networkHttp);
    }

    /**
     * Get Block chain height
     *
     * @return Observable<BigInteger>
     */
    public Observable<BigInteger> getBlockchainHeight() {
        return this.client
            .getAbs(this.url + "/chain/height")
            .as(BodyCodec.jsonObject())
            .rxSend()
            .toObservable()
            .map(Http::mapJsonObjectOrError)
            .map(json -> objectMapper.readValue(json.toString(), HeightInfoDTO.class))
            .map(blockchainHeight -> extractBigInteger(blockchainHeight.getHeight()));
    }

    /**
     * Get Block chain score
     *
     * @return Observable<BigInteger>
     */
    public Observable<BlockchainScore> getBlockchainScore() {
        return this.client
            .getAbs(this.url + "/chain/score")
            .as(BodyCodec.jsonObject())
            .rxSend()
            .toObservable()
            .map(Http::mapJsonObjectOrError)
            .map(json -> objectMapper.readValue(json.toString(), ChainScoreDTO.class))
            .map(
                blockchainScoreDTO ->
                    new BlockchainScore(
                        UInt64.extractBigInteger(
                            blockchainScoreDTO.getScoreLow()),
                        UInt64.extractBigInteger(
                            blockchainScoreDTO.getScoreHigh())));
    }
}
