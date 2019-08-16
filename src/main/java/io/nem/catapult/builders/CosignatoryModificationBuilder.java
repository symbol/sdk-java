/**
 * ** Copyright (c) 2016-present, ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights
 * reserved. ** ** This file is part of Catapult. ** ** Catapult is free software: you can
 * redistribute it and/or modify ** it under the terms of the GNU Lesser General Public License as
 * published by ** the Free Software Foundation, either version 3 of the License, or ** (at your
 * option) any later version. ** ** Catapult is distributed in the hope that it will be useful, **
 * but WITHOUT ANY WARRANTY; without even the implied warranty of ** MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the ** GNU Lesser General Public License for more details. ** ** You
 * should have received a copy of the GNU Lesser General Public License ** along with Catapult. If
 * not, see <http://www.gnu.org/licenses/>.
 **/

package io.nem.catapult.builders;

import java.io.DataInput;

/**
 * Binary layout for a cosignatory modification.
 */
public class CosignatoryModificationBuilder {

    /**
     * Modification action.
     */
    private final CosignatoryModificationActionDto modificationAction;
    /**
     * Cosignatory account public key.
     */
    private final KeyDto cosignatoryPublicKey;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected CosignatoryModificationBuilder(final DataInput stream) {
        this.modificationAction = CosignatoryModificationActionDto.loadFromBinary(stream);
        this.cosignatoryPublicKey = KeyDto.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param modificationAction Modification action.
     * @param cosignatoryPublicKey Cosignatory account public key.
     */
    protected CosignatoryModificationBuilder(
        final CosignatoryModificationActionDto modificationAction,
        final KeyDto cosignatoryPublicKey) {
        GeneratorUtils.notNull(modificationAction, "modificationAction is null");
        GeneratorUtils.notNull(cosignatoryPublicKey, "cosignatoryPublicKey is null");
        this.modificationAction = modificationAction;
        this.cosignatoryPublicKey = cosignatoryPublicKey;
    }

    /**
     * Creates an instance of CosignatoryModificationBuilder.
     *
     * @param modificationAction Modification action.
     * @param cosignatoryPublicKey Cosignatory account public key.
     * @return Instance of CosignatoryModificationBuilder.
     */
    public static CosignatoryModificationBuilder create(
        final CosignatoryModificationActionDto modificationAction,
        final KeyDto cosignatoryPublicKey) {
        return new CosignatoryModificationBuilder(modificationAction, cosignatoryPublicKey);
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
     * Gets modification action.
     *
     * @return Modification action.
     */
    public CosignatoryModificationActionDto getModificationAction() {
        return this.modificationAction;
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
        size += this.modificationAction.getSize();
        size += this.cosignatoryPublicKey.getSize();
        return size;
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] modificationActionBytes = this.modificationAction.serialize();
            dataOutputStream.write(modificationActionBytes, 0, modificationActionBytes.length);
            final byte[] cosignatoryPublicKeyBytes = this.cosignatoryPublicKey.serialize();
            dataOutputStream.write(cosignatoryPublicKeyBytes, 0, cosignatoryPublicKeyBytes.length);
        });
    }
}
