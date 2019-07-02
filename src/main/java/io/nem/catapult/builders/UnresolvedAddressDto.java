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

import io.nem.core.utils.Base32Encoder;
import io.nem.core.utils.ByteUtils;
import io.nem.core.utils.StringEncoder;
import org.apache.commons.codec.binary.Base32;

import java.io.DataInput;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/** Unresolved address. */
public final class UnresolvedAddressDto {
    /** Unresolved address. */
    public final static int SIZE = 25;
    private final ByteBuffer unresolvedAddress;

    /**
     * Constructor.
     *
     * @param unresolvedAddress Unresolved address.
     */
    public UnresolvedAddressDto(final ByteBuffer unresolvedAddress) {
        GeneratorUtils.notNull(unresolvedAddress, "unresolvedAddress is null");
        GeneratorUtils.isTrue(unresolvedAddress.array().length == SIZE, "unresolvedAddress should be " + SIZE + " bytes but is " + unresolvedAddress.array().length);
        this.unresolvedAddress = unresolvedAddress;
    }

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize.
     */
    public UnresolvedAddressDto(final DataInput stream) {
        try {
            this.unresolvedAddress = ByteBuffer.allocate(SIZE);
            stream.readFully(this.unresolvedAddress.array());
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Create a UnresolvedAddressDto from a unresolvedAddress string.
     *
     * @param plainAddress Unresolved address.
     */
    public static UnresolvedAddressDto create(final String plainAddress) {
        byte[] address = new Base32().decode(plainAddress.getBytes(StandardCharsets.UTF_8));
        return new UnresolvedAddressDto(ByteBuffer.wrap(address));
    }

    /**
     * Gets Unresolved address.
     *
     * @return Unresolved address.
     */
    public ByteBuffer getUnresolvedAddress() {
        return this.unresolvedAddress;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        return SIZE;
    }

    /**
     * Creates an instance of UnresolvedAddressDto from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of UnresolvedAddressDto.
     */
    public static UnresolvedAddressDto loadFromBinary(final DataInput stream) {
        return new UnresolvedAddressDto(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.write(this.unresolvedAddress.array(), 0, this.unresolvedAddress.array().length);
        });
    }

    /**
     * Returns the object data as a string value
     *
     * @return String
     */
    public String asString() {
        return Base32Encoder.getString(this.getUnresolvedAddress().array());
    }
}
