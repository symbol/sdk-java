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

import io.nem.core.crypto.Hashes;
import io.nem.core.utils.ArrayUtils;
import io.nem.core.utils.ByteUtils;
import io.nem.sdk.infrastructure.SerializationUtils;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.UnresolvedAddress;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.UnresolvedMosaicId;
import java.math.BigInteger;
import java.util.List;
import org.bouncycastle.util.encoders.Hex;

public class ResolutionStatement<T> {

    private final ResolutionType resolutionType;
    private final BigInteger height;
    private final T unresolved;
    private final List<ResolutionEntry> resolutionEntries;

    /**
     * Constructor
     *
     * @param height Height
     * @param unresolved An unresolved address or unresolved mosaicId (UnresolvedAddress | UnresolvedMosaic).
     * @param resolutionEntries Array of resolution entries.
     */
    public ResolutionStatement(
        ResolutionType resolutionType, BigInteger height, T unresolved, List<ResolutionEntry> resolutionEntries) {
        this.height = height;
        this.unresolved = unresolved;
        this.resolutionEntries = resolutionEntries;
        this.resolutionType = resolutionType;
        this.validateType();
    }

    /**
     * Returns An unresolved address or unresolved mosaicId (UnresolvedAddress | UnresolvedMosaic).
     *
     * @return An unresolved address or unresolved mosaicId (UnresolvedAddress | UnresolvedMosaic).
     */
    public T getUnresolved() {
        return this.unresolved;
    }

    /**
     * Returns block height
     *
     * @return block height
     */
    public BigInteger getHeight() {
        return this.height;
    }

    /**
     * Returns Array of resolution entries.
     *
     * @return Array of resolution entries.
     */
    public List<ResolutionEntry> getResolutionEntries() {
        return this.resolutionEntries;
    }

    /**
     * Returns resolution type
     *
     * @return resolution type
     */
    public ResolutionType getResolutionType() {
        return this.resolutionType;
    }

    /**
     * Validate unresolved type against resolutionEntry
     *
     * @return void
     */
    private void validateType() {
        Class unresolvedClass = this.unresolved.getClass();
        if ((!UnresolvedAddress.class.isAssignableFrom(unresolvedClass) && resolutionType == ResolutionType.ADDRESS)
            || (!UnresolvedMosaicId.class.isAssignableFrom(unresolvedClass) && resolutionType == ResolutionType.MOSAIC)) {
            throw new IllegalArgumentException(
                "Unresolved type: ["
                    + unresolvedClass.getName()
                    + "] is not valid for this ResolutionStatement");
        }
        this.resolutionEntries.forEach(
            entry -> {
                if ((Address.class.isAssignableFrom(entry.getResolved().getClass())
                    && entry.getType() != ReceiptType.ADDRESS_ALIAS_RESOLUTION)
                    || (MosaicId.class.isAssignableFrom(entry.getResolved().getClass())
                    && entry.getType() != ReceiptType.MOSAIC_ALIAS_RESOLUTION)) {
                    throw new IllegalArgumentException(
                        "Unresolved type: ["
                            + entry.getResolved().getClass().getName()
                            + "] does not match ResolutionEntry's type: ["
                            + entry.getType().name()
                            + "]for this ResolutionStatement");
                }
            });
    }

    /**
     * Serialize resolution statement and generate hash
     *
     * @return resolution statement hash
     */
    public String generateHash() {

        final byte[] versionByte = ByteUtils.shortToBytes(Short.reverseBytes((short)ReceiptVersion.RESOLUTION_STATEMENT.getValue()));
        final byte[] typeByte = getResolutionType() == ResolutionType.ADDRESS ?
            ByteUtils.shortToBytes(Short.reverseBytes((short)ReceiptType.ADDRESS_ALIAS_RESOLUTION.getValue())) :
            ByteUtils.shortToBytes(Short.reverseBytes((short)ReceiptType.MOSAIC_ALIAS_RESOLUTION.getValue()));
        final byte[] unresolvedBytes = serializeUnresolved();

        byte[] results =  ArrayUtils.concat(versionByte, typeByte, unresolvedBytes);

        for (final ResolutionEntry entry : resolutionEntries) {
            results = ArrayUtils.concat(results, entry.serialize());
        }

        byte[] hash = Hashes.sha3_256(results);
        return Hex.toHexString(hash).toUpperCase();
    }

    /**
     * Generate unresolved bytes
     *
     * @return unresolved bytes
     */
    private byte[] serializeUnresolved() {
        if (getResolutionType() == ResolutionType.ADDRESS) {
            return SerializationUtils.fromUnresolvedAddressToByteBuffer((UnresolvedAddress)getUnresolved()).array();
        }
        return ByteUtils.reverseCopy(ByteUtils.bigIntToBytes(((UnresolvedMosaicId)getUnresolved()).getId()));
    }

}
