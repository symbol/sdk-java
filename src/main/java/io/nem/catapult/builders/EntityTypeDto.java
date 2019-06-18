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

/** Enumeration of entity types. */
public enum EntityTypeDto {
    /** Reserved entity type. */
    RESERVED((short) 0),
    /** Transfer transaction builder. */
    TRANSFER_TRANSACTION_BUILDER((short) 16724);

    /** Enum value. */
    private final short value;

    /**
     * Constructor.
     *
     * @param value Enum value.
     */
     EntityTypeDto(final short value) {
        this.value = value;
    }

    /**
     * Gets enum value.
     *
     * @param value Raw value of the enum.
     * @return Enum value.
     */
    public static EntityTypeDto rawValueOf(final short value) {
        for (EntityTypeDto current : EntityTypeDto.values()) {
            if (value == current.value) {
                return current;
            }
        }
        throw new IllegalArgumentException(value + " was not a backing value for EntityTypeDto.");
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        return 2;
    }

    /**
     * Creates an instance of EntityTypeDto from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EntityTypeDto.
     */
    public static EntityTypeDto loadFromBinary(final DataInput stream) {
        try {
            final short streamValue = Short.reverseBytes(stream.readShort());
            return rawValueOf(streamValue);
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.writeShort(Short.reverseBytes(this.value));
        });
    }

    /**
     * Returns the object data as a string value
     *
     * @return String
     */
    public String asString() {
        return Short.toString(this.value);
    }
}
