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

package io.nem.sdk.model.account;

import java.util.Arrays;

/**
 * Account property modification type
 */
public enum PropertyModificationType {
    ADD(0x01),
    REMOVE(0x00);

    private Integer value;

    PropertyModificationType(int value) {
        this.value = value;
    }

    public static PropertyModificationType rawValueOf(String stringValue) {
        try {
            int value = Integer.decode(stringValue);
            return Arrays.stream(values()).filter(e -> e.value == value).findFirst()
                .orElseThrow(
                    () -> new IllegalArgumentException(stringValue + " is not a valid value"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(stringValue + " is not a valid value");
        }
    }

    /**
     * Returns enum value.
     *
     * @return enum value
     */
    public Integer getValue() {
        return this.value;
    }
}
