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
import java.util.ArrayList;
import java.util.EnumSet;

/** Binary layout for a mosaic definition transaction. */
final class MosaicDefinitionTransactionBodyBuilder {
    /** Mosaic nonce. */
    private final MosaicNonceDto mosaicNonce;
    /** Id of the mosaic. */
    private final MosaicIdDto mosaicId;
    /** Mosaic flags. */
    private final EnumSet<MosaicFlagsDto> flags;
    /** Mosaic divisibility. */
    private final byte divisibility;
    /** Optional properties. */
    private final ArrayList<MosaicPropertyBuilder> properties;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected MosaicDefinitionTransactionBodyBuilder(final DataInput stream) {
        try {
            this.mosaicNonce = MosaicNonceDto.loadFromBinary(stream);
            this.mosaicId = MosaicIdDto.loadFromBinary(stream);
            final byte propertiesCount = stream.readByte();
            this.flags = GeneratorUtils.toSet(MosaicFlagsDto.class, stream.readByte());
            this.divisibility = stream.readByte();
            this.properties = new java.util.ArrayList<>(propertiesCount);
            for (int i = 0; i < propertiesCount; i++) {
                properties.add(MosaicPropertyBuilder.loadFromBinary(stream));
            }
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Constructor.
     *
     * @param mosaicNonce Mosaic nonce.
     * @param mosaicId Id of the mosaic.
     * @param flags Mosaic flags.
     * @param divisibility Mosaic divisibility.
     * @param properties Optional properties.
     */
    protected MosaicDefinitionTransactionBodyBuilder(final MosaicNonceDto mosaicNonce, final MosaicIdDto mosaicId, final EnumSet<MosaicFlagsDto> flags, final byte divisibility, final ArrayList<MosaicPropertyBuilder> properties) {
        GeneratorUtils.notNull(mosaicNonce, "mosaicNonce is null");
        GeneratorUtils.notNull(mosaicId, "mosaicId is null");
        GeneratorUtils.notNull(flags, "flags is null");
        GeneratorUtils.notNull(properties, "properties is null");
        this.mosaicNonce = mosaicNonce;
        this.mosaicId = mosaicId;
        this.flags = flags;
        this.divisibility = divisibility;
        this.properties = properties;
    }

    /**
     * Creates an instance of MosaicDefinitionTransactionBodyBuilder.
     *
     * @param mosaicNonce Mosaic nonce.
     * @param mosaicId Id of the mosaic.
     * @param flags Mosaic flags.
     * @param divisibility Mosaic divisibility.
     * @param properties Optional properties.
     * @return Instance of MosaicDefinitionTransactionBodyBuilder.
     */
    public static MosaicDefinitionTransactionBodyBuilder create(final MosaicNonceDto mosaicNonce, final MosaicIdDto mosaicId, final EnumSet<MosaicFlagsDto> flags, final byte divisibility, final ArrayList<MosaicPropertyBuilder> properties) {
        return new MosaicDefinitionTransactionBodyBuilder(mosaicNonce, mosaicId, flags, divisibility, properties);
    }

    /**
     * Gets mosaic nonce.
     *
     * @return Mosaic nonce.
     */
    public MosaicNonceDto getMosaicNonce() {
        return this.mosaicNonce;
    }

    /**
     * Gets id of the mosaic.
     *
     * @return Id of the mosaic.
     */
    public MosaicIdDto getMosaicId() {
        return this.mosaicId;
    }

    /**
     * Gets mosaic flags.
     *
     * @return Mosaic flags.
     */
    public EnumSet<MosaicFlagsDto> getFlags() {
        return this.flags;
    }

    /**
     * Gets mosaic divisibility.
     *
     * @return Mosaic divisibility.
     */
    public byte getDivisibility() {
        return this.divisibility;
    }

    /**
     * Gets optional properties.
     *
     * @return Optional properties.
     */
    public ArrayList<MosaicPropertyBuilder> getProperties() {
        return this.properties;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.mosaicNonce.getSize();
        size += this.mosaicId.getSize();
        size += 1; // propertiesCount
        size += MosaicFlagsDto.values()[0].getSize(); // flags
        size += 1; // divisibility
        size += this.properties.stream().mapToInt(o -> o.getSize()).sum();
        return size;
    }

    /**
     * Creates an instance of MosaicDefinitionTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MosaicDefinitionTransactionBodyBuilder.
     */
    public static MosaicDefinitionTransactionBodyBuilder loadFromBinary(final DataInput stream) {
        return new MosaicDefinitionTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] mosaicNonceBytes = this.mosaicNonce.serialize();
            dataOutputStream.write(mosaicNonceBytes, 0, mosaicNonceBytes.length);
            final byte[] mosaicIdBytes = this.mosaicId.serialize();
            dataOutputStream.write(mosaicIdBytes, 0, mosaicIdBytes.length);
            dataOutputStream.writeByte((byte) this.properties.size());
            final byte bitMask = (byte) GeneratorUtils.toLong(MosaicFlagsDto.class, this.flags);
            dataOutputStream.writeByte(bitMask);
            dataOutputStream.writeByte(this.getDivisibility());
            for (int i = 0; i < this.properties.size(); i++) {
                final byte[] propertiesBytes = this.properties.get(i).serialize();
                dataOutputStream.write(propertiesBytes, 0, propertiesBytes.length);
            }
        });
    }
}
