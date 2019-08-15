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

import io.nem.sdk.api.MosaicRepository;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicInfo;
import io.nem.sdk.model.mosaic.MosaicProperties;
import io.nem.sdk.model.transaction.UInt64;
import io.nem.sdk.model.transaction.UInt64Id;
import io.nem.sdk.openapi.okhttp_gson.api.MosaicRoutesApi;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiCallback;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicIds;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicPropertyDTO;
import io.reactivex.Observable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mosaic http repository.
 *
 * @since 1.0
 */
public class MosaicRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements
    MosaicRepository {

    private final MosaicRoutesApi client;

    public MosaicRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        client = new MosaicRoutesApi(apiClient);
    }

    public MosaicRoutesApi getClient() {
        return client;
    }


    @Override
    public Observable<MosaicInfo> getMosaic(UInt64Id mosaicId) {

        ApiCall<ApiCallback<MosaicInfoDTO>> callback = handler -> getClient()
            .getMosaicAsync(UInt64.bigIntegerToHex(mosaicId.getId()), handler);
        return exceptionHandling(call(callback).map(this::createMosaicInfo));
    }

    @Override
    public Observable<List<MosaicInfo>> getMosaics(List<UInt64Id> ids) {

        MosaicIds mosaicIds = new MosaicIds();
        mosaicIds.mosaicIds(ids.stream()
            .map(id -> UInt64.bigIntegerToHex(id.getId()))
            .collect(Collectors.toList()));
        ApiCall<ApiCallback<List<MosaicInfoDTO>>> callback = handler -> getClient()
            .getMosaicsAsync(mosaicIds, handler);
        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::createMosaicInfo).toList()
                .toObservable());
    }

    private MosaicInfo createMosaicInfo(MosaicInfoDTO mosaicInfoDTO) {
        NetworkType networkType = getNetworkTypeBlocking();
        return MosaicInfo.create(
            mosaicInfoDTO.getMeta().getId(),
            new MosaicId(extractIntArray(mosaicInfoDTO.getMosaic().getMosaicId())),
            extractIntArray(mosaicInfoDTO.getMosaic().getSupply()),
            extractIntArray(mosaicInfoDTO.getMosaic().getHeight()),
            new PublicAccount(mosaicInfoDTO.getMosaic().getOwner(), networkType),
            mosaicInfoDTO.getMosaic().getRevision(),
            extractMosaicProperties(mosaicInfoDTO.getMosaic().getProperties()));
    }

    private MosaicProperties extractMosaicProperties(List<MosaicPropertyDTO> mosaicPropertiesDTO) {
        String flags =
            "00" + Integer.toBinaryString(
                extractIntArray(mosaicPropertiesDTO.get(0).getValue()).intValue());
        String bitMapFlags = flags.substring(flags.length() - 2);
        return MosaicProperties.create(
            bitMapFlags.charAt(1) == '1',
            bitMapFlags.charAt(0) == '1',
            extractIntArray(mosaicPropertiesDTO.get(1).getValue()).intValue(),
            extractIntArray(mosaicPropertiesDTO.get(2).getValue()));
    }
}
