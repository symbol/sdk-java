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

import io.nem.sdk.infrastructure.model.*;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.blockchain.MerkelPathItem;
import io.nem.sdk.model.blockchain.MerkelProofInfo;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.receipt.Statement;
import io.nem.sdk.model.transaction.Transaction;
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
 * Blockchain http repository.
 *
 * @since 1.0
 */
public class BlockHttp extends Http implements BlockRepository {

    public BlockHttp(String host) throws MalformedURLException {
        this(host, new NetworkHttp(host));
    }

    public BlockHttp(String host, NetworkHttp networkHttp) throws MalformedURLException {
        super(host, networkHttp);
    }

    public Observable<BlockInfo> getBlockByHeight(BigInteger height) {
        Observable<NetworkType> networkTypeResolve = getNetworkTypeObservable();
        return networkTypeResolve
                .flatMap(networkType -> this.client
                        .getAbs(this.url + "/block/" + height.toString())
                        .as(BodyCodec.jsonObject())
                        .rxSend()
                        .toObservable()
                        .map(Http::mapJsonObjectOrError)
                        .map(json -> objectMapper.readValue(json.toString(), BlockInfoDTO.class))
                        .map(blockInfoDTO -> this.extractBlockInfo(blockInfoDTO)));
    }

    public Observable<List<Transaction>> getBlockTransactions(BigInteger height, QueryParams queryParams) {
        return this.getBlockTransactions(height, Optional.of(queryParams));
    }

    public Observable<List<Transaction>> getBlockTransactions(BigInteger height) {
        return this.getBlockTransactions(height, Optional.empty());
    }

    public Observable<List<BlockInfo>> getBlocksByHeightWithLimit(BigInteger height, int limit, Optional<QueryParams> queryParams) {
        return this.client
                .getAbs(this.url + "/block/" + height + "/limit" + limit + (queryParams.isPresent() ? queryParams.get().toUrl() : ""))
                .as(BodyCodec.jsonArray())
                .rxSend()
                .toObservable()
                .map(Http::mapJsonArrayOrError)
                .map(json -> new JsonArray(json.toString()).stream().map(s -> (JsonObject) s).collect(Collectors.toList()))
                .flatMapIterable(item -> item)
                .map(json -> objectMapper.readValue(json.toString(), BlockInfoDTO.class))
                .map(blockInfoDTO -> this.extractBlockInfo(blockInfoDTO))
                .toList()
                .toObservable();
    }

    public Observable<MerkelProofInfo> getMerkleReceipts(BigInteger height, String hash){
        return this.client
                .getAbs(this.url + "/block/" + height + "/receipt/" + hash + "/merkle")
                .as(BodyCodec.jsonArray())
                .rxSend()
                .toObservable()
                .map(Http::mapJsonArrayOrError)
                .map(json -> objectMapper.readValue(json.toString(), MerkleProofInfoDTO.class))
                .map(MerkelProofInfoDto -> {
                    List<MerkelPathItem> pathItems = MerkelProofInfoDto.getPayload().getMerklePath().stream()
                            .map(pathItem -> new MerkelPathItem(pathItem.getPosition(), pathItem.getHash())).collect(Collectors.toList());
                    return new MerkelProofInfo(pathItems, MerkelProofInfoDto.getType());
                });
    }

    public Observable<MerkelProofInfo> getMerkleTransaction(BigInteger height, String hash){
        return this.client
                .getAbs(this.url + "/block/" + height + "/transaction/" + hash + "/merkle")
                .as(BodyCodec.jsonArray())
                .rxSend()
                .toObservable()
                .map(Http::mapJsonArrayOrError)
                .map(json -> objectMapper.readValue(json.toString(), MerkleProofInfoDTO.class))
                .map(MerkelProofInfoDto -> {
                    List<MerkelPathItem> pathItems = MerkelProofInfoDto.getPayload().getMerklePath().stream()
                            .map(pathItem -> new MerkelPathItem(pathItem.getPosition(), pathItem.getHash())).collect(Collectors.toList());
                    return new MerkelProofInfo(pathItems, MerkelProofInfoDto.getType());
                });
    }

    public Observable<Statement> getBlockReceipts(BigInteger height){
        Observable<NetworkType> networkTypeResolve = getNetworkTypeObservable();
        return networkTypeResolve
                .flatMap(networkType -> this.client
                .getAbs(this.url + "/block/" + height + "/receipts")
                .as(BodyCodec.jsonObject())
                .rxSend()
                .toObservable()
                .map(Http::mapJsonObjectOrError)
                .map(statementsDTO -> ReceiptMapping.CreateStatementFromDto(statementsDTO, networkType)));
    }

    private Observable<List<Transaction>> getBlockTransactions(BigInteger height, Optional<QueryParams> queryParams) {
        return this.client
                .getAbs(this.url + "/block/" + height + "/transactions" + (queryParams.isPresent() ? queryParams.get().toUrl() : ""))
                .as(BodyCodec.jsonArray())
                .rxSend()
                .toObservable()
                .map(Http::mapJsonArrayOrError)
                .map(json -> new JsonArray(json.toString()).stream().map(s -> (JsonObject) s).collect(Collectors.toList()))
                .flatMapIterable(item -> item)
                .map(new TransactionMapping())
                .toList()
                .toObservable();
    }


    private BlockInfo extractBlockInfo(BlockInfoDTO blockInfoDTO) {
        return BlockInfo.create(blockInfoDTO.getMeta().getHash(),
                blockInfoDTO.getMeta().getGenerationHash(),
                Optional.of(extractIntArray(blockInfoDTO.getMeta().getTotalFee())),
                Optional.of(blockInfoDTO.getMeta().getNumTransactions().intValue()),
                blockInfoDTO.getBlock().getSignature(),
                blockInfoDTO.getBlock().getSigner(),
                blockInfoDTO.getBlock().getVersion().intValue(),
                blockInfoDTO.getBlock().getType().intValue(),
                extractIntArray(blockInfoDTO.getBlock().getHeight()),
                extractIntArray(blockInfoDTO.getBlock().getTimestamp()),
                extractIntArray(blockInfoDTO.getBlock().getDifficulty()),
                blockInfoDTO.getBlock().getFeeMultiplier(),
                blockInfoDTO.getBlock().getPreviousBlockHash(),
                blockInfoDTO.getBlock().getBlockTransactionsHash(),
                blockInfoDTO.getBlock().getBlockReceiptsHash(),
                blockInfoDTO.getBlock().getStateHash(),
                Optional.ofNullable(blockInfoDTO.getBlock().getBeneficiary()));
    }
}
