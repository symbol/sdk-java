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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.api.MosaicRepository;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicInfo;
import io.nem.sdk.model.transaction.UInt64Id;
import io.reactivex.schedulers.Schedulers;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MosaicRepositoryIntegrationTest extends BaseIntegrationTest {

    List<UInt64Id> mosaicIds = new ArrayList<>();
    private MosaicId mosaicId, mosaicId2;

    @BeforeAll
    void setup() {
        mosaicId = new MosaicId("27d29cc897bbe161"); // currency mosaic id
        mosaicId2 = new MosaicId("7db6ea8a3f189370"); // harvesting mosaic id
        mosaicIds.add(mosaicId);
        mosaicIds.add(mosaicId2);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMosaicViaMosaicId(RepositoryType type) throws ExecutionException, InterruptedException {
        MosaicInfo mosaicInfo = getMosaicRepository(type).getMosaic(mosaicId).toFuture().get();

        assertEquals(new BigInteger("1"), mosaicInfo.getHeight());
        assertEquals(mosaicId, mosaicInfo.getMosaicId());
    }

    private MosaicRepository getMosaicRepository(
        RepositoryType type) {
        return getRepositoryFactory(type).createMosaicRepository();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void getMosaicsViaMosaicId(RepositoryType type)
        throws ExecutionException, InterruptedException {
        List<MosaicInfo> mosaicsInfo = getMosaicRepository(type).getMosaics(mosaicIds).toFuture()
            .get();

        assertEquals(mosaicIds.size(), mosaicsInfo.size());
        assertEquals(mosaicIds.get(0).getIdAsHex(), mosaicsInfo.get(0).getMosaicId().getIdAsHex());
    }

  /*    @ParameterizedTest
    @EnumSource(RepositoryType.class)
  void getMosaicViaNamespaceId() throws ExecutionException, InterruptedException {
      MosaicInfo mosaicInfo = mosaicRepository
              .getMosaic(NetworkCurrencyMosaic.NAMESPACEID)
              .toFuture()
              .get();

      assertEquals(new BigInteger("1"), mosaicInfo.getHeight());
      assertEquals(NetworkCurrencyMosaic.NAMESPACEID, mosaicInfo.getMosaicId());
  }

  @ParameterizedTest
    @EnumSource(RepositoryType.class)
  void getMosaicsViaNamespaceId() throws ExecutionException, InterruptedException {
      List<MosaicInfo> mosaicsInfo = mosaicRepository
              .getMosaics(Collections.singletonList(NetworkCurrencyMosaic.NAMESPACEID))
              .toFuture()
              .get();

      assertEquals(NetworkCurrencyMosaic.NAMESPACEID, mosaicsInfo.get(0).getMosaicId());
  }

  @ParameterizedTest
    @EnumSource(RepositoryType.class)
  void getMosaicsFromNamespace() throws ExecutionException, InterruptedException {
      List<MosaicInfo> mosaicsInfo = mosaicRepository
              .getMosaicsFromNamespace(NetworkCurrencyMosaic.NAMESPACEID)
              .toFuture()
              .get();

      assertEquals(NetworkCurrencyMosaic.NAMESPACEID, mosaicsInfo.get(0).getMosaicId());
  }

  @ParameterizedTest
    @EnumSource(RepositoryType.class)
  void getMosaicNames() throws ExecutionException, InterruptedException {
      List<MosaicName> mosaicNames = mosaicRepository
              .getMosaicNames(Collections.singletonList(NetworkCurrencyMosaic.NAMESPACEID))
              .toFuture()
              .get();

      assertEquals("xem", mosaicNames.get(0).getName());
      assertEquals(NetworkCurrencyMosaic.NAMESPACEID, mosaicNames.get(0).getMosaicId());
  }*/

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void throwExceptionWhenMosaicDoesNotExists(RepositoryType type) {
        // TestObserver<MosaicInfo> testObserver = new TestObserver<>();
        getMosaicRepository(type)
            .getMosaic(new MosaicId("1E46EE18BE375DA2"))
            .subscribeOn(Schedulers.single())
            .test()
            .awaitDone(2, TimeUnit.SECONDS)
            .assertFailure(RuntimeException.class);
    }
}
