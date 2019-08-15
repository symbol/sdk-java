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

import io.nem.sdk.api.DiagnosticRepository;
import io.nem.sdk.model.blockchain.BlockchainStorageInfo;
import io.nem.sdk.model.blockchain.ServerInfo;
import io.nem.sdk.openapi.okhttp_gson.api.DiagnosticRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiCallback;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.ServerDTO;
import io.nem.sdk.openapi.okhttp_gson.model.ServerInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.StorageInfoDTO;
import io.reactivex.Observable;

/**
 * Diagnostic http repository.
 */
public class DiagnosticRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    DiagnosticRepository {

    private final DiagnosticRoutesApi client;

    public DiagnosticRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        client = new DiagnosticRoutesApi(apiClient);
    }

    public DiagnosticRoutesApi getClient() {
        return client;
    }

    /**
     * Get storage info
     *
     * @return Observable<BlockchainStorageInfo>
     */
    public Observable<BlockchainStorageInfo> getBlockchainStorage() {
        ApiCall<ApiCallback<StorageInfoDTO>> callback = client::getDiagnosticStorageAsync;
        return exceptionHandling(call(callback).map(this::toBlockchainStorageInfo));
    }

    private BlockchainStorageInfo toBlockchainStorageInfo(StorageInfoDTO blockchainStorageInfoDTO) {
        return new BlockchainStorageInfo(
            blockchainStorageInfoDTO.getNumAccounts(),
            blockchainStorageInfoDTO.getNumBlocks(),
            blockchainStorageInfoDTO.getNumBlocks());
    }

    /**
     * Get server info
     *
     * @return Observable<ServerInfo>
     */
    public Observable<ServerInfo> getServerInfo() {
        ApiCall<ApiCallback<ServerDTO>> callback = client::getServerInfoAsync;
        return exceptionHandling(call(callback).map(ServerDTO::getServerInfo).map(this::toServerInfo));
    }

    private ServerInfo toServerInfo(ServerInfoDTO serverInfoDTO) {
        return new ServerInfo(serverInfoDTO.getRestVersion(), serverInfoDTO.getRestVersion());
    }
}
