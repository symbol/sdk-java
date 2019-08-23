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

import io.nem.sdk.api.DiagnosticRepository;
import io.nem.sdk.openapi.vertx.model.ServerInfoDTO;
import io.nem.sdk.openapi.vertx.model.StorageInfoDTO;
import io.nem.sdk.model.blockchain.BlockchainStorageInfo;
import io.nem.sdk.model.blockchain.ServerInfo;
import io.reactivex.Observable;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

/**
 * Diagnostic http repository.
 */
public class DiagnosticHttp extends Http implements DiagnosticRepository {

    /**
     * Constructor
     */
    public DiagnosticHttp(String host) {
        this(host, new NetworkHttp(host));
    }

    /**
     * Constructor
     */
    public DiagnosticHttp(String host, NetworkHttp networkHttp) {
        super(host, networkHttp);
    }

    /**
     * Get storage info
     *
     * @return Observable<BlockchainStorageInfo>
     */
    public Observable<BlockchainStorageInfo> getBlockchainStorage() {
        return this.client
            .getAbs(this.url + "/diagnostic/storage")
            .as(BodyCodec.jsonObject())
            .rxSend()
            .toObservable()
            .map(Http::mapJsonObjectOrError)
            .map(json -> objectMapper.readValue(json.toString(), StorageInfoDTO.class))
            .map(
                blockchainStorageInfoDTO ->
                    new BlockchainStorageInfo(
                        blockchainStorageInfoDTO.getNumAccounts(),
                        blockchainStorageInfoDTO.getNumBlocks(),
                        blockchainStorageInfoDTO.getNumBlocks()));
    }

    /**
     * Get server info
     *
     * @return Observable<ServerInfo>
     */
    public Observable<ServerInfo> getServerInfo() {
        return this.client
            .getAbs(this.url + "/diagnostic/server")
            .as(BodyCodec.jsonObject())
            .rxSend()
            .toObservable()
            .map(Http::mapJsonObjectOrError)
            .map(json -> objectMapper.readValue(json.toString(), ServerInfoDTO.class))
            .map(
                serverInfoDTO ->
                    new ServerInfo(serverInfoDTO.getRestVersion(), serverInfoDTO.getRestVersion()));
    }
}
