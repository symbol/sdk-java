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

import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicInfo;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.transaction.UInt64Id;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MosaicHttpTest extends BaseTest {
    private MosaicHttp mosaicHttp;
    private MosaicId mosaicId, mosaicId2;
    List<UInt64Id> mosaicIds = new ArrayList<>();

    @BeforeAll
    void setup() throws IOException {
        mosaicHttp = new MosaicHttp(this.getApiUrl());
        mosaicId = new MosaicId("27d29cc897bbe161");  // currency mosaic id
        mosaicId2 = new MosaicId("7db6ea8a3f189370"); // harvesting mosaic id
        mosaicIds.add(mosaicId);
        mosaicIds.add(mosaicId2);
    }

    @Test
    void getMosaicViaMosaicId() throws ExecutionException, InterruptedException {
        MosaicInfo mosaicInfo = mosaicHttp
                .getMosaic(mosaicId)
                .toFuture()
                .get();

        assertEquals(new BigInteger("1"), mosaicInfo.getHeight());
        assertEquals(mosaicId, mosaicInfo.getMosaicId());
    }

    @Test
    void getMosaicsViaMosaicId() throws ExecutionException, InterruptedException {
        List<MosaicInfo> mosaicsInfo = mosaicHttp
                .getMosaics(mosaicIds)
                .toFuture()
                .get();

        assertEquals(mosaicIds.size(), mosaicsInfo.size());
        assertEquals(mosaicIds.get(0).getIdAsHex(), mosaicsInfo.get(0).getMosaicId().getIdAsHex());
    }

/*    @Test
    void getMosaicViaNamespaceId() throws ExecutionException, InterruptedException {
        MosaicInfo mosaicInfo = mosaicHttp
                .getMosaic(NetworkCurrencyMosaic.NAMESPACEID)
                .toFuture()
                .get();

        assertEquals(new BigInteger("1"), mosaicInfo.getHeight());
        assertEquals(NetworkCurrencyMosaic.NAMESPACEID, mosaicInfo.getMosaicId());
    }

    @Test
    void getMosaicsViaNamespaceId() throws ExecutionException, InterruptedException {
        List<MosaicInfo> mosaicsInfo = mosaicHttp
                .getMosaics(Collections.singletonList(NetworkCurrencyMosaic.NAMESPACEID))
                .toFuture()
                .get();

        assertEquals(NetworkCurrencyMosaic.NAMESPACEID, mosaicsInfo.get(0).getMosaicId());
    }

    @Test
    void getMosaicsFromNamespace() throws ExecutionException, InterruptedException {
        List<MosaicInfo> mosaicsInfo = mosaicHttp
                .getMosaicsFromNamespace(NetworkCurrencyMosaic.NAMESPACEID)
                .toFuture()
                .get();

        assertEquals(NetworkCurrencyMosaic.NAMESPACEID, mosaicsInfo.get(0).getMosaicId());
    }

    @Test
    void getMosaicNames() throws ExecutionException, InterruptedException {
        List<MosaicName> mosaicNames = mosaicHttp
                .getMosaicNames(Collections.singletonList(NetworkCurrencyMosaic.NAMESPACEID))
                .toFuture()
                .get();

        assertEquals("xem", mosaicNames.get(0).getName());
        assertEquals(NetworkCurrencyMosaic.NAMESPACEID, mosaicNames.get(0).getMosaicId());
    }*/

    @Test
    void throwExceptionWhenMosaicDoesNotExists() {
        //TestObserver<MosaicInfo> testObserver = new TestObserver<>();
        mosaicHttp
                .getMosaic(new MosaicId("1E46EE18BE375DA2"))
                .subscribeOn(Schedulers.single())
                .test()
                .awaitDone(2, TimeUnit.SECONDS)
                .assertFailure(RuntimeException.class);
    }

}
