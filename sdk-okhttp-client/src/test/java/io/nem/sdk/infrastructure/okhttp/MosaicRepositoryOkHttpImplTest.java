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

import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicInfo;
import io.nem.sdk.model.mosaic.MosaicNames;
import io.nem.sdk.model.transaction.UInt64;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicDefinitionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicMetaDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicNamesDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicPropertyDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicPropertyIdEnum;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicsNamesDTO;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link MosaicRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class MosaicRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

    private MosaicRepositoryOkHttpImpl repository;


    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new MosaicRepositoryOkHttpImpl(apiClientMock);
    }


    @Test
    public void shouldGetMosaicsNamesFromPublicKeys() throws Exception {

        MosaicId mosaicId = new MosaicId(UInt64.fromLowerAndHigher(481110499, 231112638));

        MosaicNamesDTO dto = new MosaicNamesDTO();
        dto.setMosaicId(Arrays.asList(481110499L, 231112638L));
        dto.setNames(Collections.singletonList("accountalias"));

        MosaicsNamesDTO accountsNamesDTO = new MosaicsNamesDTO();
        accountsNamesDTO.setAccountNames(Collections.singletonList(dto));

        mockRemoteCall(accountsNamesDTO);

        List<MosaicNames> resolvedList = repository
            .getMosaicsNames(Collections.singletonList(mosaicId))
            .toFuture().get();

        Assertions.assertEquals(1, resolvedList.size());

        MosaicNames accountNames = resolvedList.get(0);

        Assertions.assertEquals(mosaicId, accountNames.getMosaicId());
        Assertions.assertEquals("accountalias", accountNames.getNames().get(0).getName());
    }

    @Test
    public void shouldGetMosaics() throws Exception {

        resolveNetworkType();
        MosaicId mosaicId = new MosaicId(UInt64.fromLowerAndHigher(481110499, 231112638));

        MosaicMetaDTO mosiacMetaDto = new MosaicMetaDTO();
        MosaicDefinitionDTO mosaicDto = new MosaicDefinitionDTO();
        MosaicInfoDTO mosaicInfoDto = new MosaicInfoDTO();

        mosaicDto.setOwner("B630EFDDFADCC4A2077AB8F1EC846B08FEE2D2972EACF95BBAC6BFAC3D31834C");
        mosaicDto.setMosaicId(Arrays.asList(481110499L, 231112638L));
        mosaicDto.setRevision(123);

        MosaicPropertyDTO mosaicPropertyDTO1 = new MosaicPropertyDTO();
        mosaicPropertyDTO1.setId(MosaicPropertyIdEnum.NUMBER_0);
        mosaicPropertyDTO1.setValue(Arrays.asList(5L, 0L));

        MosaicPropertyDTO mosaicPropertyDTO2 = new MosaicPropertyDTO();
        mosaicPropertyDTO2.setId(MosaicPropertyIdEnum.NUMBER_1);
        mosaicPropertyDTO2.setValue(Arrays.asList(6L, 0L));

        MosaicPropertyDTO mosaicPropertyDTO3 = new MosaicPropertyDTO();
        mosaicPropertyDTO3.setId(MosaicPropertyIdEnum.NUMBER_2);
        mosaicPropertyDTO3.setValue(Arrays.asList(7L, 0L));

        mosaicDto.setProperties(
            Arrays.asList(mosaicPropertyDTO1, mosaicPropertyDTO2, mosaicPropertyDTO3));

        mosaicInfoDto.setMeta(mosiacMetaDto);

        mosaicInfoDto.setMosaic(mosaicDto);
        mockRemoteCall(Collections.singletonList(mosaicInfoDto));

        List<MosaicInfo> resolvedList = repository
            .getMosaics(Collections.singletonList(mosaicId))
            .toFuture().get();

        Assertions.assertEquals(1, resolvedList.size());

        MosaicInfo mosaicInfo = resolvedList.get(0);
        Assertions.assertEquals(mosaicId, mosaicInfo.getMosaicId());
        Assertions.assertEquals(mosaicDto.getRevision(), mosaicInfo.getRevision());
        Assertions
            .assertEquals(mosaicDto.getOwner(), mosaicInfo.getOwner().getPublicKey().toString());

        Assertions.assertFalse(mosaicInfo.isTransferable());
        Assertions.assertEquals(6, mosaicInfo.getDivisibility());
        Assertions.assertEquals(BigInteger.valueOf(7), mosaicInfo.getDuration());
    }

    @Test
    public void shouldGetMosaic() throws Exception {

        resolveNetworkType();
        MosaicId mosaicId = new MosaicId(UInt64.fromLowerAndHigher(481110499, 231112638));

        MosaicMetaDTO mosiacMetaDto = new MosaicMetaDTO();
        MosaicDefinitionDTO mosaicDto = new MosaicDefinitionDTO();
        MosaicInfoDTO mosaicInfoDto = new MosaicInfoDTO();

        mosaicDto.setOwner("B630EFDDFADCC4A2077AB8F1EC846B08FEE2D2972EACF95BBAC6BFAC3D31834C");
        mosaicDto.setMosaicId(Arrays.asList(481110499L, 231112638L));
        mosaicDto.setRevision(123);

        MosaicPropertyDTO mosaicPropertyDTO1 = new MosaicPropertyDTO();
        mosaicPropertyDTO1.setId(MosaicPropertyIdEnum.NUMBER_0);
        mosaicPropertyDTO1.setValue(Arrays.asList(5L, 0L));

        MosaicPropertyDTO mosaicPropertyDTO2 = new MosaicPropertyDTO();
        mosaicPropertyDTO2.setId(MosaicPropertyIdEnum.NUMBER_1);
        mosaicPropertyDTO2.setValue(Arrays.asList(6L, 0L));

        MosaicPropertyDTO mosaicPropertyDTO3 = new MosaicPropertyDTO();
        mosaicPropertyDTO3.setId(MosaicPropertyIdEnum.NUMBER_2);
        mosaicPropertyDTO3.setValue(Arrays.asList(7L, 0L));

        mosaicDto.setProperties(
            Arrays.asList(mosaicPropertyDTO1, mosaicPropertyDTO2, mosaicPropertyDTO3));

        mosaicInfoDto.setMeta(mosiacMetaDto);

        mosaicInfoDto.setMosaic(mosaicDto);
        mockRemoteCall(mosaicInfoDto);

        MosaicInfo mosaicInfo = repository
            .getMosaic(mosaicId)
            .toFuture().get();

        Assertions.assertEquals(mosaicId, mosaicInfo.getMosaicId());
        Assertions.assertEquals(mosaicDto.getRevision(), mosaicInfo.getRevision());
        Assertions
            .assertEquals(mosaicDto.getOwner(), mosaicInfo.getOwner().getPublicKey().toString());

        Assertions.assertFalse(mosaicInfo.isTransferable());
        Assertions.assertEquals(6, mosaicInfo.getDivisibility());
        Assertions.assertEquals(BigInteger.valueOf(7), mosaicInfo.getDuration());
    }


    @Override
    public MosaicRepositoryOkHttpImpl getRepository() {
        return repository;
    }
}
