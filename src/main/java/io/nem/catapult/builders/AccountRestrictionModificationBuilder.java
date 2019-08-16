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
 * Account restriction basic modification.
 */
public class AccountRestrictionModificationBuilder {

    /**
     * Modification action.
     */
    private final AccountRestrictionModificationActionDto modificationAction;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected AccountRestrictionModificationBuilder(final DataInput stream) {
        this.modificationAction = AccountRestrictionModificationActionDto.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param modificationAction Modification action.
     */
    protected AccountRestrictionModificationBuilder(
        final AccountRestrictionModificationActionDto modificationAction) {
        GeneratorUtils.notNull(modificationAction, "modificationAction is null");
        this.modificationAction = modificationAction;
    }

    /**
     * Creates an instance of AccountRestrictionModificationBuilder.
     *
     * @param modificationAction Modification action.
     * @return Instance of AccountRestrictionModificationBuilder.
     */
    public static AccountRestrictionModificationBuilder create(
        final AccountRestrictionModificationActionDto modificationAction) {
        return new AccountRestrictionModificationBuilder(modificationAction);
    }

    /**
     * Creates an instance of AccountRestrictionModificationBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of AccountRestrictionModificationBuilder.
     */
    public static AccountRestrictionModificationBuilder loadFromBinary(final DataInput stream) {
        return new AccountRestrictionModificationBuilder(stream);
    }

    /**
     * Gets modification action.
     *
     * @return Modification action.
     */
    public AccountRestrictionModificationActionDto getModificationAction() {
        return this.modificationAction;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.modificationAction.getSize();
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
        });
    }
}
