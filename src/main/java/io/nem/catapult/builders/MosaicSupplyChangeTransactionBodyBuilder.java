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

/** Binary layout for a mosaic supply change transaction. */
final class MosaicSupplyChangeTransactionBodyBuilder {
    /** Id of the affected mosaic. */
    private final UnresolvedMosaicIdDto mosaicId;
    /** Supply change direction. */
    private final MosaicSupplyChangeDirectionDto direction;
    /** Amount of the change. */
    private final AmountDto delta;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected MosaicSupplyChangeTransactionBodyBuilder(final DataInput stream) {
        this.mosaicId = UnresolvedMosaicIdDto.loadFromBinary(stream);
        this.direction = MosaicSupplyChangeDirectionDto.loadFromBinary(stream);
        this.delta = AmountDto.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param mosaicId Id of the affected mosaic.
     * @param direction Supply change direction.
     * @param delta Amount of the change.
     */
    protected MosaicSupplyChangeTransactionBodyBuilder(final UnresolvedMosaicIdDto mosaicId, final MosaicSupplyChangeDirectionDto direction, final AmountDto delta) {
        GeneratorUtils.notNull(mosaicId, "mosaicId is null");
        GeneratorUtils.notNull(direction, "direction is null");
        GeneratorUtils.notNull(delta, "delta is null");
        this.mosaicId = mosaicId;
        this.direction = direction;
        this.delta = delta;
    }

    /**
     * Creates an instance of MosaicSupplyChangeTransactionBodyBuilder.
     *
     * @param mosaicId Id of the affected mosaic.
     * @param direction Supply change direction.
     * @param delta Amount of the change.
     * @return Instance of MosaicSupplyChangeTransactionBodyBuilder.
     */
    public static MosaicSupplyChangeTransactionBodyBuilder create(final UnresolvedMosaicIdDto mosaicId, final MosaicSupplyChangeDirectionDto direction, final AmountDto delta) {
        return new MosaicSupplyChangeTransactionBodyBuilder(mosaicId, direction, delta);
    }

    /**
     * Gets id of the affected mosaic.
     *
     * @return Id of the affected mosaic.
     */
    public UnresolvedMosaicIdDto getMosaicId() {
        return this.mosaicId;
    }

    /**
     * Gets supply change direction.
     *
     * @return Supply change direction.
     */
    public MosaicSupplyChangeDirectionDto getDirection() {
        return this.direction;
    }

    /**
     * Gets amount of the change.
     *
     * @return Amount of the change.
     */
    public AmountDto getDelta() {
        return this.delta;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.mosaicId.getSize();
        size += this.direction.getSize();
        size += this.delta.getSize();
        return size;
    }

    /**
     * Creates an instance of MosaicSupplyChangeTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MosaicSupplyChangeTransactionBodyBuilder.
     */
    public static MosaicSupplyChangeTransactionBodyBuilder loadFromBinary(final DataInput stream) {
        return new MosaicSupplyChangeTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] mosaicIdBytes = this.mosaicId.serialize();
            dataOutputStream.write(mosaicIdBytes, 0, mosaicIdBytes.length);
            final byte[] directionBytes = this.direction.serialize();
            dataOutputStream.write(directionBytes, 0, directionBytes.length);
            final byte[] deltaBytes = this.delta.serialize();
            dataOutputStream.write(deltaBytes, 0, deltaBytes.length);
        });
    }
}
