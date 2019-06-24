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

import io.nem.sdk.infrastructure.model.BlockInfoDTO;
import io.nem.sdk.infrastructure.model.BlockchainScoreDTO;
import io.nem.sdk.infrastructure.model.HeightInfoDTO;
import io.nem.sdk.infrastructure.model.StorageInfoDTO;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.blockchain.BlockchainScore;
import io.nem.sdk.model.blockchain.BlockchainStorageInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.UInt64;
import io.reactivex.Observable;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Chain http repository.
 *
 */
public class ChainHttp extends Http implements ChainRepository {

    /**
     * Constructor
     * @param host
     * @throws MalformedURLException
     */
    public ChainHttp(String host) throws MalformedURLException {
        this(host, new NetworkHttp(host));
    }

    /**
     * Constructor
     * @param host
     * @param networkHttp
     * @throws MalformedURLException
     */
    public ChainHttp(String host, NetworkHttp networkHttp) throws MalformedURLException {
        super(host, networkHttp);
    }

    /**
     * Get Block chain height
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
                .map(blockchainHeight -> extractIntArray(blockchainHeight.getHeight()));
    }

    /**
     * Get Block chain score
     * @return Observable<BigInteger>
     */
    public Observable<BlockchainScore> getBlockchainScore() {
        return this.client
                .getAbs(this.url + "/chain/score")
                .as(BodyCodec.jsonObject())
                .rxSend()
                .toObservable()
                .map(Http::mapJsonObjectOrError)
                .map(json -> objectMapper.readValue(json.toString(), BlockchainScoreDTO.class))
                .map(blockchainScoreDTO -> new BlockchainScore(UInt64.fromIntArray(blockchainScoreDTO.getScoreLow().stream().mapToInt(i->i).toArray()),
                            UInt64.fromIntArray(blockchainScoreDTO.getScoreHigh().stream().mapToInt(i->i).toArray())));
    }
}
