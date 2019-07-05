package io.nem.sdk.infrastructure;

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.node.NodeInfo;
import io.nem.sdk.model.node.NodeTime;
import io.nem.sdk.model.node.RoleType;
import io.nem.sdk.model.transaction.UInt64;
import io.reactivex.Observable;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Node http repository.
 */
public class NodeHttp extends Http implements NodeRepository {

    /**
     * Constructor
     */
    public NodeHttp(String host) throws MalformedURLException {
        this(host, new NetworkHttp(host));
    }

    /**
     * Constructor
     */
    public NodeHttp(String host, NetworkHttp networkHttp) throws MalformedURLException {
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

    private BigInteger extractBigInteger(JsonArray input) {
        List<Long> array = new ArrayList();
        input.stream().forEach(item -> array.add(new Long(item.toString())));
        return UInt64.fromIntArray(array.stream().mapToInt(Long::intValue).toArray());
    }
}
