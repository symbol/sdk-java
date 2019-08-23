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

/**
 * Account property modification type
 */
public enum PropertyModificationType {
    Add(0x00),
    Remove(0x01);

    private Integer value;

    PropertyModificationType(int value) {
        this.value = value;
    }

    public static PropertyModificationType rawValueOf(String value) {
        switch (value) {
            case "0x00":
                return PropertyModificationType.Add;
            case "0x01":
                return PropertyModificationType.Remove;
            default:
                throw new IllegalArgumentException(value + " is not a valid value");
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
