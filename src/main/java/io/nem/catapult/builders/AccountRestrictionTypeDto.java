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

/** Account restriction types. */
public enum AccountRestrictionTypeDto {
    /** Account restriction type is an address. */
    ADDRESS((byte) 1),
    /** Account restriction type is a mosaic id. */
    MOSAIC_ID((byte) 2),
    /** Account restriction type is a transaction type. */
    TRANSACTION_TYPE((byte) 4),
    /** Account restriction type sentinel. */
    SENTINEL((byte) 5),
    /** Account restriction is interpreted as blocking operation. */
    BLOCK((byte) 128);

    /** Enum value. */
    private final byte value;

    /**
     * Constructor.
     *
     * @param value Enum value.
     */
     AccountRestrictionTypeDto(final byte value) {
        this.value = value;
    }

    /**
     * Gets enum value.
     *
     * @param value Raw value of the enum.
     * @return Enum value.
     */
    public static AccountRestrictionTypeDto rawValueOf(final byte value) {
        for (AccountRestrictionTypeDto current : AccountRestrictionTypeDto.values()) {
            if (value == current.value) {
                return current;
            }
        }
        throw new IllegalArgumentException(value + " was not a backing value for AccountRestrictionTypeDto.");
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        return 1;
    }

    /**
     * Creates an instance of AccountRestrictionTypeDto from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of AccountRestrictionTypeDto.
     */
    public static AccountRestrictionTypeDto loadFromBinary(final DataInput stream) {
        try {
            final byte streamValue = stream.readByte();
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
            dataOutputStream.writeByte(this.value);
        });
    }
}
