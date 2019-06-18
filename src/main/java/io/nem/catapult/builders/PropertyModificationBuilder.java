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

/** Account properties basic modification. */
public class PropertyModificationBuilder {
    /** Modification type. */
    private final PropertyModificationTypeDto modificationType;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected PropertyModificationBuilder(final DataInput stream) {
        this.modificationType = PropertyModificationTypeDto.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param modificationType Modification type.
     */
    protected PropertyModificationBuilder(final PropertyModificationTypeDto modificationType) {
        GeneratorUtils.notNull(modificationType, "modificationType is null");
        this.modificationType = modificationType;
    }

    /**
     * Creates an instance of PropertyModificationBuilder.
     *
     * @param modificationType Modification type.
     * @return Instance of PropertyModificationBuilder.
     */
    public static PropertyModificationBuilder create(final PropertyModificationTypeDto modificationType) {
        return new PropertyModificationBuilder(modificationType);
    }

    /**
     * Gets Modification type.
     *
     * @return Modification type.
     */
    public PropertyModificationTypeDto getModificationType() {
        return this.modificationType;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.modificationType.getSize();
        return size;
    }

    /**
     * Creates an instance of PropertyModificationBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of PropertyModificationBuilder.
     */
    public static PropertyModificationBuilder loadFromBinary(final DataInput stream) {
        return new PropertyModificationBuilder(stream);
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
        });
    }
}
