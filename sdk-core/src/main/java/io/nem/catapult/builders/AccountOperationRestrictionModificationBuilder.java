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

/** Account operation restriction modification. */
public final class AccountOperationRestrictionModificationBuilder extends AccountRestrictionModificationBuilder {
    /** Transaction type restriction value. */
    private final EntityTypeDto value;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected AccountOperationRestrictionModificationBuilder(final DataInput stream) {
        super(stream);
        this.value = EntityTypeDto.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param modificationAction Modification action.
     * @param value Transaction type restriction value.
     */
    protected AccountOperationRestrictionModificationBuilder(final AccountRestrictionModificationActionDto modificationAction, final EntityTypeDto value) {
        super(modificationAction);
        GeneratorUtils.notNull(value, "value is null");
        this.value = value;
    }

    /**
     * Creates an instance of AccountOperationRestrictionModificationBuilder.
     *
     * @param modificationAction Modification action.
     * @param value Transaction type restriction value.
     * @return Instance of AccountOperationRestrictionModificationBuilder.
     */
    public static AccountOperationRestrictionModificationBuilder create(final AccountRestrictionModificationActionDto modificationAction, final EntityTypeDto value) {
        return new AccountOperationRestrictionModificationBuilder(modificationAction, value);
    }

    /**
     * Gets transaction type restriction value.
     *
     * @return Transaction type restriction value.
     */
    public EntityTypeDto getValue() {
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
     * Creates an instance of AccountOperationRestrictionModificationBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of AccountOperationRestrictionModificationBuilder.
     */
    public static AccountOperationRestrictionModificationBuilder loadFromBinary(final DataInput stream) {
        return new AccountOperationRestrictionModificationBuilder(stream);
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
