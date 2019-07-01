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

