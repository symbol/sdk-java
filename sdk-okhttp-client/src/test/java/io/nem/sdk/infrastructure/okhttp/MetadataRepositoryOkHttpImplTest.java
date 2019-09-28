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

import io.nem.core.utils.MapperUtils;
import io.nem.sdk.api.MetadataRepository;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.metadata.Metadata;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.openapi.okhttp_gson.model.MetadataDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MetadataEntriesDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MetadataEntryDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MetadataTypeEnum;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link MetadataRepositoryOkHttpImpl}
 *
 * @author Fernando Boucquez
 */
public class MetadataRepositoryOkHttpImplTest extends AbstractOkHttpRespositoryTest {

    private MetadataRepositoryOkHttpImpl repository;

    @BeforeEach
    public void setUp() {
        super.setUp();
        repository = new MetadataRepositoryOkHttpImpl(apiClientMock);
    }

    @Test
    public void shouldGetAccountMetadata() throws Exception {
        Address address = MapperUtils.toAddress("SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");
        MetadataEntriesDTO dto = getMetadataEntriesDTO();
        mockRemoteCall(dto);
        List<Metadata> resultList = repository.getAccountMetadata(address, Optional.empty())
            .toFuture().get();
        assertMetadataList(dto, resultList);
    }

    @Test
    public void shouldGetAccountMetadataByKey() throws Exception {
        Address address = MapperUtils.toAddress("SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");
        MetadataEntriesDTO dto = getMetadataEntriesDTO();
        mockRemoteCall(dto);
        List<Metadata> resultList = repository.getAccountMetadataByKey(address, "someKey")
            .toFuture().get();
        assertMetadataList(dto, resultList);
    }

    @Test
    public void shouldGetAccountMetadataByKeyAndSender() throws Exception {
        Address address = MapperUtils.toAddress("SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");
        MetadataDTO expected = createMetadataDto("MosaicMeta", MetadataTypeEnum.NUMBER_1, "11111");
        mockRemoteCall(expected);
        Metadata result = repository
            .getAccountMetadataByKeyAndSender(address, "MosaicMeta", "someSender")
            .toFuture().get();
        assertMetadata(expected, result);
    }

    @Test
    public void shouldGetMosaicMetadata() throws Exception {
        MosaicId mosaicId = new MosaicId(BigInteger.valueOf(1234));
        MetadataEntriesDTO dto = getMetadataEntriesDTO();
        mockRemoteCall(dto);
        List<Metadata> resultList = repository.getMosaicMetadata(mosaicId, Optional.empty())
            .toFuture().get();
        assertMetadataList(dto, resultList);
    }

    @Test
    public void shouldGetMosaicMetadataByKey() throws Exception {
        MosaicId mosaicId = new MosaicId(BigInteger.valueOf(1234));
        MetadataEntriesDTO dto = getMetadataEntriesDTO();
        mockRemoteCall(dto);
        List<Metadata> resultList = repository.getMosaicMetadataByKey(mosaicId, "SomeKey")
            .toFuture().get();
        assertMetadataList(dto, resultList);
    }

    @Test
    public void shouldGetMosaicMetadataByKeyAndSender() throws Exception {
        MosaicId mosaicId = new MosaicId(BigInteger.valueOf(1234));
        MetadataDTO expected = createMetadataDto("MosaicMeta", MetadataTypeEnum.NUMBER_1, "11111");
        mockRemoteCall(expected);
        Metadata result = repository
            .getMosaicMetadataByKeyAndSender(mosaicId, "MosaicMeta", "someSender")
            .toFuture().get();
        assertMetadata(expected, result);
    }


    @Test
    public void shouldGetNamespaceMetadata() throws Exception {
        NamespaceId namespaceId = NamespaceId.createFromName("mynamespace");
        MetadataEntriesDTO dto = getMetadataEntriesDTO();
        mockRemoteCall(dto);
        List<Metadata> resultList = repository.getNamespaceMetadata(namespaceId, Optional.empty())
            .toFuture().get();
        assertMetadataList(dto, resultList);
    }

    @Test
    public void shouldGetNamespaceMetadataByKey() throws Exception {
        NamespaceId namespaceId = NamespaceId.createFromName("mynamespace");
        MetadataEntriesDTO dto = getMetadataEntriesDTO();
        mockRemoteCall(dto);
        List<Metadata> resultList = repository.getNamespaceMetadataByKey(namespaceId, "SomeKey")
            .toFuture().get();
        assertMetadataList(dto, resultList);
    }

    @Test
    public void shouldGetNamespaceMetadataByKeyAndSender() throws Exception {
        NamespaceId namespaceId = NamespaceId.createFromName("mynamespace");
        MetadataDTO expected = createMetadataDto("NamespaceMeta", MetadataTypeEnum.NUMBER_1,
            "11111");
        mockRemoteCall(expected);
        Metadata result = repository
            .getNamespaceMetadataByKeyAndSender(namespaceId, "NamespaceMeta", "someSender")
            .toFuture().get();
        assertMetadata(expected, result);
    }


    private void assertMetadataList(MetadataEntriesDTO expected, List<Metadata> resultList) {
        int index = 0;
        Assertions.assertEquals(expected.getMetadataEntries().size(), resultList.size());

        Assertions.assertEquals(3, resultList.size());

        for (Metadata metadata : resultList) {
            MetadataDTO metadataDTO = expected.getMetadataEntries().get(index++);
            assertMetadata(metadataDTO, metadata);
        }
    }

    private void assertMetadata(MetadataDTO expected, Metadata result) {
        Assertions.assertEquals(expected.getId(), result.getId());
        Assertions.assertEquals(expected.getMetadataEntry().getCompositeHash(),
            result.getMetadataEntry().getCompositeHash());
        Assertions.assertEquals(expected.getMetadataEntry().getSenderPublicKey(),
            result.getMetadataEntry().getSenderPublicKey());
        Assertions.assertEquals(expected.getMetadataEntry().getTargetPublicKey(),
            result.getMetadataEntry().getTargetPublicKey());
        Assertions.assertEquals(expected.getMetadataEntry().getMetadataType(),
            MetadataTypeEnum
                .fromValue(result.getMetadataEntry().getMetadataType().getValue()));

        Assertions.assertEquals(expected.getMetadataEntry().getValue(),
            result.getMetadataEntry().getValue());

        if (expected.getMetadataEntry().getTargetId() != null) {
            Assertions
                .assertTrue(result.getMetadataEntry().getTargetId().isPresent());
            Assertions
                .assertEquals(
                    new BigInteger(expected.getMetadataEntry().getTargetId().toString()),
                    result.getMetadataEntry().getTargetId().get().getId());
        } else {
            Assertions
                .assertFalse(result.getMetadataEntry().getTargetId().isPresent());
        }

        Assertions.assertEquals(expected.getMetadataEntry().getValueSize(),
            result.getMetadataEntry().getValueSize());
    }

    private MetadataEntriesDTO getMetadataEntriesDTO() {
        MetadataEntriesDTO dto = new MetadataEntriesDTO();
        List<MetadataDTO> medataEntryDtos = new ArrayList<>();
        medataEntryDtos.add(createMetadataDto("AddressMeta", MetadataTypeEnum.NUMBER_0, null));
        medataEntryDtos.add(createMetadataDto("MosaicMeta", MetadataTypeEnum.NUMBER_1, "11111"));
        medataEntryDtos.add(createMetadataDto("NamespaceMeta", MetadataTypeEnum.NUMBER_2, "22222"));
        dto.setMetadataEntries(medataEntryDtos);
        return dto;
    }

    private MetadataDTO createMetadataDto(String name,
        MetadataTypeEnum type, String targetId) {
        MetadataDTO dto = new MetadataDTO();
        dto.setId(name);
        MetadataEntryDTO metadataEntry = new MetadataEntryDTO();
        metadataEntry.setCompositeHash("ompositeHash " + name);
        metadataEntry.setMetadataType(type);
        metadataEntry.setScopedMetadataKey("10");
        metadataEntry.setSenderPublicKey("senderPublicKey " + name);
        metadataEntry.setTargetId(targetId);
        metadataEntry.setTargetPublicKey("targetPublicKey " + name);
        metadataEntry.setValue(name + " message");
        metadataEntry.setValueSize(10);
        dto.setMetadataEntry(metadataEntry);
        return dto;
    }

    @Override
    protected MetadataRepositoryOkHttpImpl getRepository() {
        return repository;
    }

}
