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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nem.sdk.infrastructure.Listener;
import io.nem.sdk.infrastructure.ListenerBase;
import io.nem.sdk.infrastructure.ListenerChannel;
import io.nem.sdk.infrastructure.ListenerSubscribeMessage;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.TransactionStatusError;
import io.nem.sdk.model.transaction.UInt64;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Legacy implementation of the {@link Listener}.
 *
 * @since 1.0
 */
public class ListenerLegacy extends ListenerBase implements Listener {

    private final URL url;

    private WebSocket webSocket;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String UID;

    /**
     * @param url nis host
     */
    public ListenerLegacy(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Parameter '" + url +
                "' is not a valid URL. " + ExceptionUtils.getMessage(e));
        }
    }

    /**
     * @return a {@link CompletableFuture} that resolves when the websocket connection is opened
     */
    @Override
    public CompletableFuture<Void> open() {

        CompletableFuture<Void> future = new CompletableFuture<>();
        if (this.webSocket != null) {
            return CompletableFuture.completedFuture(null);
        }
        HttpClient httpClient = Vertx.vertx().createHttpClient();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.setHost(this.url.getHost());
        requestOptions.setPort(this.url.getPort());
        requestOptions.setURI("/ws");
        httpClient.websocket(
            requestOptions,
            webSocket -> {
                this.webSocket = webSocket;
                webSocket.handler(
                    handler -> {
                        JsonObject message = handler.toJsonObject();
                        if (message.containsKey("uid")) {
                            this.UID = message.getString("uid");
                            future.complete(null);
                        } else if (message.containsKey("transaction")) {
                            onNext(ListenerChannel
                                    .rawValueOf(message.getJsonObject("meta").getString("channelName")),
                                new TransactionMappingLegacy().apply(message));
                        } else if (message.containsKey("block")) {
                            final JsonObject meta = message.getJsonObject("meta");
                            final JsonObject block = message.getJsonObject("block");
                            onNext(ListenerChannel.BLOCK,
                                BlockInfo.create(
                                    meta.getString("hash"),
                                    meta.getString("generationHash"),
                                    extractBigInteger(block.getJsonArray("totalFee")),
                                    block.getInteger("numTransactions"),
                                    block.getJsonArray("subCacheMerkleRoots").stream()
                                        .map(o -> o.toString())
                                        .collect(Collectors.toList()),
                                    block.getString("signature"),
                                    block.getString("signer"),
                                    block.getInteger("version"),
                                    block.getInteger("type"),
                                    extractBigInteger(block.getJsonArray("height")),
                                    extractBigInteger(block.getJsonArray("timestamp")),
                                    extractBigInteger(block.getJsonArray("difficulty")),
                                    block.getInteger("feeMultiplier"),
                                    block.getString("previousBlockHash"),
                                    block.getString("blockTransactionsHash"),
                                    block.getString("blockReceiptsHash"),
                                    block.getString("stateHash"),
                                    block.getString("beneficiaryPublicKey")));
                        } else if (message.containsKey("status")) {
                            onNext(
                                ListenerChannel.STATUS,
                                new TransactionStatusError(
                                    message.getString("hash"),
                                    message.getString("status"),
                                    new Deadline(
                                        extractBigInteger(message.getJsonArray("deadline")))));
                        } else if (message.containsKey("meta")) {
                            onNext(
                                ListenerChannel.rawValueOf(
                                    message.getJsonObject("meta").getString("channelName")),
                                message.getJsonObject("meta").getString("hash"));
                        } else if (message.containsKey("parentHash")) {
                            onNext(
                                ListenerChannel.COSIGNATURE,
                                new CosignatureSignedTransaction(
                                    message.getString("parenthash"),
                                    message.getString("signature"),
                                    message.getString("signer")));
                        }
                    });
            });
        return future;
    }

    protected void subscribeTo(String channel) {
        final ListenerSubscribeMessage subscribeMessage =
            new ListenerSubscribeMessage(this.UID, channel);
        String json;
        try {
            json = objectMapper.writeValueAsString(subscribeMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getCause());
        }
        this.webSocket.writeTextMessage(json);
    }

    /**
     * Close webSocket connection
     */
    @Override
    public void close() {
        if (this.webSocket != null) {
            this.webSocket.close();
            this.webSocket = null;
        }
    }

    /**
     * // TODO: should we remove it?
     *
     * @return the UID connected to
     */
    @Override
    public String getUID() {
        return UID;
    }

    public static BigInteger extractBigInteger(JsonArray input) {
        return UInt64.fromLongArray(
            input.stream().map(Object::toString).map(Long::parseLong).mapToLong(Long::longValue)
                .toArray());
    }
}
