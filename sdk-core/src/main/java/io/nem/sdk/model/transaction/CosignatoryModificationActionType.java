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

import java.util.Arrays;

/**
 * Enum containing multisig cosignatory modification type constants.
 *
 * @since 1.0
 */
public enum CosignatoryModificationActionType {
    /**
     * Add cosignatory.
     */
    ADD(1),

    /**
     * Remove cosignatory
     */
    REMOVE(0);

    private final int value;

    CosignatoryModificationActionType(int value) {
        this.value = value;
    }

    /**
     * Static constructor converting multsig cosignatory modification raw value to enum instance.
     *
     * @param value Multisig cosignatory modification type raw value
     * @return {@link CosignatoryModificationActionType}
     */
    public static CosignatoryModificationActionType rawValueOf(int value) {
        return Arrays.stream(values()).filter(e -> e.value == value).findFirst()
            .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
    }

    /**
     * Returns enum value.
     *
     * @return int
     */
    public int getValue() {
        return value;
    }
}
