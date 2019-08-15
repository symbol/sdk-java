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

import io.nem.sdk.api.BlockRepository;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.blockchain.MerkelPathItem;
import io.nem.sdk.model.blockchain.MerkelProofInfo;
import io.nem.sdk.model.receipt.Statement;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.openapi.okhttp_gson.api.BlockRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiCallback;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.BlockInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MerkleProofInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.StatementsDTO;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Blockchain http repository.
 *
 * @since 1.0
 */
public class BlockRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    BlockRepository {

    private final BlockRoutesApi client;

    public BlockRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        client = new BlockRoutesApi(apiClient);
    }

    public Observable<BlockInfo> getBlockByHeight(BigInteger height) {
        ApiCall<ApiCallback<BlockInfoDTO>> callback = handler -> getClient()
            .getBlockByHeightAsync(height.longValue(), handler);
        return exceptionHandling(call(callback).map(this::toBlockInfo));
    }

    public Observable<List<Transaction>> getBlockTransactions(
        BigInteger height, QueryParams queryParams) {
        return this.getBlockTransactions(height, Optional.of(queryParams));
    }

    public Observable<List<Transaction>> getBlockTransactions(BigInteger height) {
        return this.getBlockTransactions(height, Optional.empty());
    }

    public Observable<List<BlockInfo>> getBlocksByHeightWithLimit(
        BigInteger height, int limit, Optional<QueryParams> queryParams) {
        //TODO queryParams not defined in the descriptor nor generated.
        ApiCall<ApiCallback<List<BlockInfoDTO>>> callback = (handler) ->
            client.getBlocksByHeightWithLimitAsync(height.longValue(), limit, handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toBlockInfo).toList()
                .toObservable());
    }

    public Observable<MerkelProofInfo> getMerkleReceipts(BigInteger height, String hash) {

        ApiCall<ApiCallback<MerkleProofInfoDTO>> callback = (handler) ->
            client.getMerkleReceiptsAsync(height.longValue(), hash, handler);
        return exceptionHandling(call(callback).map(this::toMerkelProofInfo));


    }

    private MerkelProofInfo toMerkelProofInfo(MerkleProofInfoDTO dto) {
        List<MerkelPathItem> pathItems =
            dto.getMerklePath().stream()
                .map(
                    pathItem ->
                        new MerkelPathItem(pathItem.getPosition(), pathItem.getHash()))
                .collect(Collectors.toList());
        return new MerkelProofInfo(pathItems, "TODO MerkelProofInfoDto.getType()");
    }

    public Observable<MerkelProofInfo> getMerkleTransaction(BigInteger height, String hash) {
        ApiCall<ApiCallback<MerkleProofInfoDTO>> callback = (handler) ->
            client.getMerkleTransactionAsync(height.longValue(), hash, handler);
        return exceptionHandling(call(callback).map(this::toMerkelProofInfo));

    }

    public Observable<Statement> getBlockReceipts(BigInteger height) {
        ApiCall<ApiCallback<StatementsDTO>> callback = (handler) ->
            client.getBlockReceiptsAsync(height.longValue(), handler);
        return exceptionHandling(call(callback).map(statementsDTO ->
            new ReceiptMappingOkHttp(getJsonHelper())
                .createStatementFromDto(statementsDTO, getNetworkTypeBlocking())));
    }

    private Observable<List<Transaction>> getBlockTransactions(
        BigInteger height, Optional<QueryParams> queryParams) {
        ApiCall<ApiCallback<List<TransactionInfoDTO>>> callback = (handler) ->
            client.getBlockTransactionsAsync(height.longValue(),
                getPageSize(queryParams),
                getId(queryParams),
                null,
                handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction).toList()
                .toObservable());
    }

    private Transaction toTransaction(TransactionInfoDTO input) {
        return new TransactionMappingOkHttp(getJsonHelper()).apply(input);
    }

    private BlockInfo toBlockInfo(BlockInfoDTO blockInfoDTO) {
        return BlockInfo.create(
            blockInfoDTO.getMeta().getHash(),
            blockInfoDTO.getMeta().getGenerationHash(),
            extractBigInteger(blockInfoDTO.getMeta().getTotalFee()),
            blockInfoDTO.getMeta().getNumTransactions(),
            blockInfoDTO.getMeta().getSubCacheMerkleRoots(),
            blockInfoDTO.getBlock().getSignature(),
            blockInfoDTO.getBlock().getSigner(),
            blockInfoDTO.getBlock().getVersion(),
            blockInfoDTO.getBlock().getType().getValue(),
            extractBigInteger(blockInfoDTO.getBlock().getHeight()),
            extractBigInteger(blockInfoDTO.getBlock().getTimestamp()),
            extractBigInteger(blockInfoDTO.getBlock().getDifficulty()),
            blockInfoDTO.getBlock().getFeeMultiplier(),
            blockInfoDTO.getBlock().getPreviousBlockHash(),
            blockInfoDTO.getBlock().getBlockTransactionsHash(),
            blockInfoDTO.getBlock().getBlockReceiptsHash(),
            blockInfoDTO.getBlock().getStateHash(),
            blockInfoDTO.getBlock().getBeneficiary());
    }

    public BlockRoutesApi getClient() {
        return client;
    }
}
