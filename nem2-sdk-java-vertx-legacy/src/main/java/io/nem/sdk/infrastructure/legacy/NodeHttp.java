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

import io.nem.sdk.api.NodeRepository;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.node.NodeInfo;
import io.nem.sdk.model.node.NodeTime;
import io.nem.sdk.model.node.RoleType;
import io.nem.sdk.model.transaction.UInt64;
import io.reactivex.Observable;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import java.math.BigInteger;

/**
 * Node http repository.
 */
public class NodeHttp extends Http implements NodeRepository {

    /**
     * Constructor
     */
    public NodeHttp(String host) {
        this(host, new NetworkHttp(host));
    }

    /**
     * Constructor
     */
    public NodeHttp(String host, NetworkHttp networkHttp) {
        super(host, networkHttp);
    }

    /**
     * Get node info
     *
     * @return Observable<NodeInfo>
     */
    public Observable<NodeInfo> getNodeInfo() {
        return this.client
            .getAbs(this.url + "/node/info")
            .as(BodyCodec.jsonObject())
            .rxSend()
            .toObservable()
            .map(Http::mapJsonObjectOrError)
            .map(
                nodeInfoDTO ->
                    new NodeInfo(
                        nodeInfoDTO.getString("publicKey"),
                        nodeInfoDTO.getInteger("port").intValue(),
                        NetworkType.rawValueOf(nodeInfoDTO.getInteger("networkIdentifier")),
                        nodeInfoDTO.getInteger("version").intValue(),
                        RoleType.rawValueOf(nodeInfoDTO.getInteger("roles").intValue()),
                        nodeInfoDTO.getString("host"),
                        nodeInfoDTO.getString("friendlyName")));
    }

    /**
     * Get node time
     *
     * @return Observable<NodeTime>
     */
    public Observable<NodeTime> getNodeTime() {
        return this.client
            .getAbs(this.url + "/node/time")
            .as(BodyCodec.jsonObject())
            .rxSend()
            .toObservable()
            .map(Http::mapJsonObjectOrError)
            .map(
                nodeTimeDTO ->
                    new NodeTime(
                        extractBigInteger(
                            nodeTimeDTO
                                .getJsonObject("communicationTimestamps")
                                .getJsonArray("sendTimestamp")),
                        extractBigInteger(
                            nodeTimeDTO
                                .getJsonObject("communicationTimestamps")
                                .getJsonArray("receiveTimestamp"))));
    }

    public static BigInteger extractBigInteger(JsonArray input) {
        return UInt64.fromLongArray(
            input.stream().map(Object::toString).map(Long::parseLong).mapToLong(Long::longValue)
                .toArray());
    }
}
