/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.vertx;

import io.nem.sdk.api.MetadataRepository;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.metadata.Metadata;
import io.nem.sdk.model.metadata.MetadataEntry;
import io.nem.sdk.model.metadata.MetadataType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.openapi.vertx.api.MetadataRoutesApi;
import io.nem.sdk.openapi.vertx.api.MetadataRoutesApiImpl;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.sdk.openapi.vertx.model.MetadataDTO;
import io.nem.sdk.openapi.vertx.model.MetadataEntriesDTO;
import io.nem.sdk.openapi.vertx.model.MetadataEntryDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * Implementation of {@link MetadataRepository}
 */
public class MetadataRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    MetadataRepository {

    private final MetadataRoutesApi client;

    public MetadataRepositoryVertxImpl(ApiClient apiClient,
        Supplier<NetworkType> networkType) {
        super(apiClient, networkType);
        client = new MetadataRoutesApiImpl(apiClient);
    }

    @Override
    public Observable<List<Metadata>> getAccountMetadata(Address address,
        Optional<QueryParams> queryParams) {
        Consumer<Handler<AsyncResult<MetadataEntriesDTO>>> callback = handler -> getClient()
            .getAccountMetadata(address.encoded(), getPageSize(queryParams), getId(queryParams),
                getOrder(queryParams),
                handler);
        return handleList(callback);
    }


    @Override
    public Observable<List<Metadata>> getAccountMetadataByKey(Address address, String key) {
        Consumer<Handler<AsyncResult<MetadataEntriesDTO>>> callback = handler -> getClient()
            .getAccountMetadataByKey(address.encoded(), key, handler);
        return handleList(callback);
    }


    @Override
    public Observable<List<Metadata>> getMosaicMetadata(MosaicId mosaicId,
        Optional<QueryParams> queryParams) {
        Consumer<Handler<AsyncResult<MetadataEntriesDTO>>> callback = handler -> getClient()
            .getMosaicMetadata(mosaicId.getIdAsHex(), getPageSize(queryParams), getId(queryParams),
                getOrder(queryParams),
                handler);
        return handleList(callback);
    }

    @Override
    public Observable<List<Metadata>> getMosaicMetadataByKey(MosaicId mosaicId, String key) {
        Consumer<Handler<AsyncResult<MetadataEntriesDTO>>> callback = handler -> getClient()
            .getMosaicMetadataByKey(mosaicId.getIdAsHex(), key, handler);
        return handleList(callback);
    }

    @Override
    public Observable<Metadata> getAccountMetadataByKeyAndSender(Address address, String key,
        String publicKey) {
        Consumer<Handler<AsyncResult<MetadataDTO>>> callback = handler -> getClient()
            .getAccountMetadataByKeyAndSender(address.encoded(), key, publicKey,
                handler);
        return handleOne(callback);
    }


    @Override
    public Observable<Metadata> getMosaicMetadataByKeyAndSender(MosaicId mosaicId, String key,
        String publicKey) {
        Consumer<Handler<AsyncResult<MetadataDTO>>> callback = handler -> getClient()
            .getMosaicMetadataByKeyAndSender(mosaicId.getIdAsHex(), key, publicKey,
                handler);
        return handleOne(callback);
    }

    @Override
    public Observable<List<Metadata>> getNamespaceMetadata(NamespaceId namespaceId,
        Optional<QueryParams> queryParams) {
        Consumer<Handler<AsyncResult<MetadataEntriesDTO>>> callback = handler -> getClient()
            .getNamespaceMetadata(namespaceId.getIdAsHex(), getPageSize(queryParams),
                getId(queryParams),
                getOrder(queryParams),
                handler);
        return handleList(callback);
    }

    @Override
    public Observable<List<Metadata>> getNamespaceMetadataByKey(NamespaceId namespaceId,
        String key) {
        Consumer<Handler<AsyncResult<MetadataEntriesDTO>>> callback = handler -> getClient()
            .getNamespaceMetadataByKey(namespaceId.getIdAsHex(), key, handler);
        return handleList(callback);
    }

    @Override
    public Observable<Metadata> getNamespaceMetadataByKeyAndSender(NamespaceId namespaceId,
        String key, String publicKey) {
        Consumer<Handler<AsyncResult<MetadataDTO>>> callback = handler -> getClient()
            .getNamespaceMetadataByKeyAndSender(namespaceId.getIdAsHex(), key, publicKey,
                handler);
        return handleOne(callback);
    }


    public MetadataRoutesApi getClient() {
        return client;
    }

    private Observable<List<Metadata>> handleList(
        Consumer<Handler<AsyncResult<MetadataEntriesDTO>>> callback) {
        return exceptionHandling(
            call(callback).map(MetadataEntriesDTO::getMetadataEntries).flatMapIterable(item -> item)
                .map(this::toMetadata).toList()
                .toObservable());
    }

    private Observable<Metadata> handleOne(
        Consumer<Handler<AsyncResult<MetadataDTO>>> callback) {
        return exceptionHandling(call(callback)
            .map(this::toMetadata));
    }

    private Metadata toMetadata(MetadataDTO dto) {

        MetadataEntryDTO entryDto = dto.getMetadataEntry();
        MetadataEntry metadataEntry = new MetadataEntry(entryDto.getCompositeHash(),
            entryDto.getSenderPublicKey(), entryDto.getTargetPublicKey(),
            new BigInteger(entryDto.getScopedMetadataKey()),
            MetadataType.rawValueOf(entryDto.getMetadataType().getValue()), entryDto.getValueSize(),
            entryDto.getValue(),
            Optional.ofNullable(Objects.toString(entryDto.getTargetId(), null)));
        return new Metadata(dto.getId(), metadataEntry);
    }
}
