/*
 * Copyright 2019 NEM
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

package io.nem.sdk.model.receipt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class ArtifactExpiryReceiptTest {

    @Test
    void shouldCreateMosaicExpiryReceipt() {
        MosaicId mosaicId = new MosaicId("85BBEA6CC462B244");
        ArtifactExpiryReceipt<MosaicId> mosaicExpiryReceipt =
            new ArtifactExpiryReceipt(
                mosaicId, ReceiptType.MOSAIC_EXPIRED, ReceiptVersion.ARTIFACT_EXPIRY);
        assertEquals(mosaicExpiryReceipt.getType(), ReceiptType.MOSAIC_EXPIRED);
        assertEquals(mosaicExpiryReceipt.getSize(), null);
        assertEquals(mosaicExpiryReceipt.getVersion(), ReceiptVersion.ARTIFACT_EXPIRY);
        assertEquals(
            mosaicExpiryReceipt.getArtifactId().getIdAsHex().toUpperCase(), "85BBEA6CC462B244");
    }

    @Test
    void shouldCreateNamespaceExpiryReceipt() {
        NamespaceId namespaceId = new NamespaceId(new BigInteger("-8884663987180930485"));
        ArtifactExpiryReceipt<NamespaceId> namespaceExpiryReceipt =
            new ArtifactExpiryReceipt(
                namespaceId, ReceiptType.NAMESPACE_EXPIRED, ReceiptVersion.ARTIFACT_EXPIRY);
        assertEquals(namespaceExpiryReceipt.getType(), ReceiptType.NAMESPACE_EXPIRED);
        assertEquals(namespaceExpiryReceipt.getSize(), null);
        assertEquals(namespaceExpiryReceipt.getVersion(), ReceiptVersion.ARTIFACT_EXPIRY);
        assertEquals(
            namespaceExpiryReceipt.getArtifactId().getId(), new BigInteger("-8884663987180930485"));
    }

    @Test
    void shouldCreateMosaicExpiryReceiptWithSize() {
        MosaicId mosaicId = new MosaicId("85BBEA6CC462B244");
        ArtifactExpiryReceipt<MosaicId> mosaicExpiryReceipt =
            new ArtifactExpiryReceipt(
                mosaicId, ReceiptType.MOSAIC_EXPIRED, ReceiptVersion.ARTIFACT_EXPIRY,
                Optional.of(100));
        assertEquals(mosaicExpiryReceipt.getType(), ReceiptType.MOSAIC_EXPIRED);
        assertEquals(mosaicExpiryReceipt.getVersion(), ReceiptVersion.ARTIFACT_EXPIRY);
        assertEquals(
            mosaicExpiryReceipt.getArtifactId().getIdAsHex().toUpperCase(), "85BBEA6CC462B244");
        assertEquals(mosaicExpiryReceipt.getSize().get().intValue(), 100);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithWrongType() {
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                new ArtifactExpiryReceipt(
                    "", ReceiptType.MOSAIC_EXPIRED, ReceiptVersion.ARTIFACT_EXPIRY, null);
            });
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithWrongReceiptType() {
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                MosaicId mosaicId = new MosaicId("85BBEA6CC462B244");
                new ArtifactExpiryReceipt(
                    mosaicId, ReceiptType.LOCK_HASH_COMPLETED, ReceiptVersion.ARTIFACT_EXPIRY, null);
            });
    }
}
