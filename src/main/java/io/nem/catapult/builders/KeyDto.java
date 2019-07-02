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

import io.nem.core.utils.HexEncoder;
import io.nem.core.utils.StringEncoder;

import java.io.DataInput;
import java.nio.ByteBuffer;

/** Key. */
public final class KeyDto {
    /** Key. */
    public final static int SIZE = 32;
    private final ByteBuffer key;

    /**
     * Constructor.
     *
     * @param key Key.
     */
    public KeyDto(final ByteBuffer key) {
        GeneratorUtils.notNull(key, "key is null");
        GeneratorUtils.isTrue(key.array().length == SIZE, "key should be " + SIZE + " bytes");
        this.key = key;
    }

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize.
     */
    public KeyDto(final DataInput stream) {
        try {
            this.key = ByteBuffer.allocate(SIZE);
            stream.readFully(this.key.array());
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Creates a KeyDto from a key string.
     *
     * @param key String.
     */
    public static KeyDto create(final String key) {
        return new KeyDto(StringEncoder.getByteBuffer(key, SIZE));
    }

    /**
     * Gets Key.
     *
     * @return Key.
     */
    public ByteBuffer getKey() {
        return this.key;
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
     * Creates an instance of KeyDto from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of KeyDto.
     */
    public static KeyDto loadFromBinary(final DataInput stream) {
        return new KeyDto(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.write(this.key.array(), 0, this.key.array().length);
        });
    }

    /**
     * Returns the object data as a string value
     *
     * @return String
     */
    public String asString() {
        return HexEncoder.getString(this.getKey().array());
    }
}
