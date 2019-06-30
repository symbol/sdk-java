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

package io.nem.sdk.model.transaction;

/**
 * Enum containing account restriction modification type constants.
 */
public enum AccountRestrictionModificationType {
    /**
     * Add account restriction value.
     */
    ADD((byte)0),

    /**
     * Remove account restriction value
     */
    REMOVE((byte)1);

    private byte value;

    AccountRestrictionModificationType(final byte value) {
        this.value = value;
    }

    /**
     * Gets enum value.
     *
     * @param value Raw value of the enum.
     * @return Enum value.
     */
    public static AccountRestrictionModificationType rawValueOf(final byte value) {
        for (AccountRestrictionModificationType current : AccountRestrictionModificationType.values()) {
            if (value == current.value) {
                return current;
            }
        }
        throw new IllegalArgumentException(value + " was not a backing value for AccountRestrictionModificationType.");
    }

    /**
     * Returns enum value.
     *
     * @return byte
     */
    public byte getValue() {
        return value;
    }

}

