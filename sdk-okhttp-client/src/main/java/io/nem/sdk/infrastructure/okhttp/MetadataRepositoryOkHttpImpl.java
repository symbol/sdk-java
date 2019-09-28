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

package io.nem.sdk.infrastructure.okhttp;

import io.nem.sdk.api.MetadataRepository;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.metadata.Metadata;
import io.nem.sdk.model.metadata.MetadataEntry;
import io.nem.sdk.model.metadata.MetadataType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.openapi.okhttp_gson.api.MetadataRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.MetadataDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MetadataEntriesDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MetadataEntryDTO;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;


/**
 * Implementation of {@link MetadataRepository}
 */
public class MetadataRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    MetadataRepository {

    private final MetadataRoutesApi client;

    public MetadataRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        client = new MetadataRoutesApi(apiClient);
    }

    @Override
    public Observable<List<Metadata>> getAccountMetadata(Address address,
        Optional<QueryParams> queryParams) {
        Callable<MetadataEntriesDTO> callback = () -> getClient()
            .getAccountMetadata(address.encoded(), getPageSize(queryParams), getId(queryParams),
                getOrder(queryParams)
            );
        return handleList(callback);
    }


    @Override
    public Observable<List<Metadata>> getAccountMetadataByKey(Address address, String key) {
        Callable<MetadataEntriesDTO> callback = () -> getClient()
            .getAccountMetadataByKey(address.encoded(), key);
        return handleList(callback);
    }


    @Override
    public Observable<List<Metadata>> getMosaicMetadata(MosaicId mosaicId,
        Optional<QueryParams> queryParams) {
        Callable<MetadataEntriesDTO> callback = () -> getClient()
            .getMosaicMetadata(mosaicId.getIdAsHex(), getPageSize(queryParams), getId(queryParams),
                getOrder(queryParams));
        return handleList(callback);
    }

    @Override
    public Observable<List<Metadata>> getMosaicMetadataByKey(MosaicId mosaicId, String key) {
        Callable<MetadataEntriesDTO> callback = () -> getClient()
            .getMosaicMetadataByKey(mosaicId.getIdAsHex(), key);
        return handleList(callback);
    }

    @Override
    public Observable<Metadata> getAccountMetadataByKeyAndSender(Address address, String key,
        String publicKey) {
        Callable<MetadataDTO> callback = () -> getClient()
            .getAccountMetadataByKeyAndSender(address.encoded(), key, publicKey);
        return handleOne(callback);
    }


    @Override
    public Observable<Metadata> getMosaicMetadataByKeyAndSender(MosaicId mosaicId, String key,
        String publicKey) {
        Callable<MetadataDTO> callback = () -> getClient()
            .getMosaicMetadataByKeyAndSender(mosaicId.getIdAsHex(), key, publicKey);
        return handleOne(callback);
    }

    @Override
    public Observable<List<Metadata>> getNamespaceMetadata(NamespaceId namespaceId,
        Optional<QueryParams> queryParams) {
        Callable<MetadataEntriesDTO> callback = () -> getClient()
            .getNamespaceMetadata(namespaceId.getIdAsHex(), getPageSize(queryParams),
                getId(queryParams),
                getOrder(queryParams));
        return handleList(callback);
    }

    @Override
    public Observable<List<Metadata>> getNamespaceMetadataByKey(NamespaceId namespaceId,
        String key) {
        Callable<MetadataEntriesDTO> callback = () -> getClient()
            .getNamespaceMetadataByKey(namespaceId.getIdAsHex(), key);
        return handleList(callback);
    }

    @Override
    public Observable<Metadata> getNamespaceMetadataByKeyAndSender(NamespaceId namespaceId,
        String key, String publicKey) {
        Callable<MetadataDTO> callback = () -> getClient()
            .getNamespaceMetadataByKeyAndSender(namespaceId.getIdAsHex(), key, publicKey);
        return handleOne(callback);
    }


    public MetadataRoutesApi getClient() {
        return client;
    }

    private Observable<List<Metadata>> handleList(
        Callable<MetadataEntriesDTO> callback) {
        return exceptionHandling(
            call(callback).map(MetadataEntriesDTO::getMetadataEntries).flatMapIterable(item -> item)
                .map(this::toMetadata).toList()
                .toObservable());
    }

    private Observable<Metadata> handleOne(
        Callable<MetadataDTO> callback) {
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
