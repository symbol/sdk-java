/*
 * Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.model.transaction;

/** Account restriction types. */
public enum AccountRestrictionType {
    /** Account restriction type is an address. */
    ADDRESS((byte) 1),
    /** Account restriction type is a mosaic id. */
    MOSAIC_ID((byte) 2),
    /** Account restriction type is a transaction type. */
    TRANSACTION_TYPE((byte) 4),
    /** Account restriction type sentinel. */
    SENTINEL((byte) 5),
    /** Account restriction is interpreted as blocking address operation. */
    BLOCK_ADDRESS((byte) 129),
    /** Account restriction is interpreted as blocking mosaicId operation. */
    BLOCK_MOSAIC_ID((byte) 130),
    /** Account restriction is interpreted as blocking transaction type operation. */
    BLOCK_TRANSACTION_TYPE((byte) 132);

    /** Enum value. */
    private final byte value;

    /**
     * Constructor.
     *
     * @param value Enum value.
     */
    AccountRestrictionType(final byte value) {
        this.value = value;
    }

    /**
     * Gets enum value.
     *
     * @param value Raw value of the enum.
     * @return Enum value.
     */
    public static AccountRestrictionType rawValueOf(final byte value) {
        for (AccountRestrictionType current : AccountRestrictionType.values()) {
            if (value == current.value) {
                return current;
            }
        }
        throw new IllegalArgumentException(value + " was not a backing value for AccountRestrictionType.");
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
