/**
*** Copyright (c) 2016-present,
*** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
***
*** This file is part of Catapult.
***
*** Catapult is free software: you can redistribute it and/or modify
*** it under the terms of the GNU Lesser General Public License as published by
*** the Free Software Foundation, either version 3 of the License, or
*** (at your option) any later version.
***
*** Catapult is distributed in the hope that it will be useful,
*** but WITHOUT ANY WARRANTY; without even the implied warranty of
*** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
*** GNU Lesser General Public License for more details.
***
*** You should have received a copy of the GNU Lesser General Public License
*** along with Catapult. If not, see <http://www.gnu.org/licenses/>.
**/

package io.nem.catapult.builders;

import java.io.DataInput;

/** Binary layout for a cosignatory modification. */
public class CosignatoryModificationBuilder {
    /** Modification type. */
    private final CosignatoryModificationTypeDto modificationType;
    /** Cosignatory account public key. */
    private final KeyDto cosignatoryPublicKey;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected CosignatoryModificationBuilder(final DataInput stream) {
        this.modificationType = CosignatoryModificationTypeDto.loadFromBinary(stream);
        this.cosignatoryPublicKey = KeyDto.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param modificationType Modification type.
     * @param cosignatoryPublicKey Cosignatory account public key.
     */
    protected CosignatoryModificationBuilder(final CosignatoryModificationTypeDto modificationType, final KeyDto cosignatoryPublicKey) {
        GeneratorUtils.notNull(modificationType, "modificationType is null");
        GeneratorUtils.notNull(cosignatoryPublicKey, "cosignatoryPublicKey is null");
        this.modificationType = modificationType;
        this.cosignatoryPublicKey = cosignatoryPublicKey;
    }

    /**
     * Creates an instance of CosignatoryModificationBuilder.
     *
     * @param modificationType Modification type.
     * @param cosignatoryPublicKey Cosignatory account public key.
     * @return Instance of CosignatoryModificationBuilder.
     */
    public static CosignatoryModificationBuilder create(final CosignatoryModificationTypeDto modificationType, final KeyDto cosignatoryPublicKey) {
        return new CosignatoryModificationBuilder(modificationType, cosignatoryPublicKey);
    }

    /**
     * Gets modification type.
     *
     * @return Modification type.
     */
    public CosignatoryModificationTypeDto getModificationType() {
        return this.modificationType;
    }

    /**
     * Gets cosignatory account public key.
     *
     * @return Cosignatory account public key.
     */
    public KeyDto getCosignatoryPublicKey() {
        return this.cosignatoryPublicKey;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.modificationType.getSize();
        size += this.cosignatoryPublicKey.getSize();
        return size;
    }

    /**
     * Creates an instance of CosignatoryModificationBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of CosignatoryModificationBuilder.
     */
    public static CosignatoryModificationBuilder loadFromBinary(final DataInput stream) {
        return new CosignatoryModificationBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] modificationTypeBytes = this.modificationType.serialize();
            dataOutputStream.write(modificationTypeBytes, 0, modificationTypeBytes.length);
            final byte[] cosignatoryPublicKeyBytes = this.cosignatoryPublicKey.serialize();
            dataOutputStream.write(cosignatoryPublicKeyBytes, 0, cosignatoryPublicKeyBytes.length);
        });
    }
}
