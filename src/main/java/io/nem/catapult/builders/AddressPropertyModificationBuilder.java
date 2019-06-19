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

/** Account properties address modification. */
public final class AddressPropertyModificationBuilder extends PropertyModificationBuilder {
    /** Value. */
    private final AddressDto value;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected AddressPropertyModificationBuilder(final DataInput stream) {
        super(stream);
        this.value = AddressDto.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param modificationType Modification type.
     * @param value Value.
     */
    protected AddressPropertyModificationBuilder(final PropertyModificationTypeDto modificationType, final AddressDto value) {
        super(modificationType);
        GeneratorUtils.notNull(value, "value is null");
        this.value = value;
    }

    /**
     * Creates an instance of AddressPropertyModificationBuilder.
     *
     * @param modificationType Modification type.
     * @param value Value.
     * @return Instance of AddressPropertyModificationBuilder.
     */
    public static AddressPropertyModificationBuilder create(final PropertyModificationTypeDto modificationType, final AddressDto value) {
        return new AddressPropertyModificationBuilder(modificationType, value);
    }

    /**
     * Gets Value.
     *
     * @return Value.
     */
    public AddressDto getValue() {
        return this.value;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.value.getSize();
        return size;
    }

    /**
     * Creates an instance of AddressPropertyModificationBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of AddressPropertyModificationBuilder.
     */
    public static AddressPropertyModificationBuilder loadFromBinary(final DataInput stream) {
        return new AddressPropertyModificationBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] superBytes = super.serialize();
            dataOutputStream.write(superBytes, 0, superBytes.length);
            final byte[] valueBytes = this.value.serialize();
            dataOutputStream.write(valueBytes, 0, valueBytes.length);
        });
    }
}
