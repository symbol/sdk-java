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
package io.nem.sdk.model.node;

public enum RoleType {
    PeerNode(1),
    ApiNode(2);

    private int value;

    RoleType(int value) {
        this.value = value;
    }

    /**
     * Returns enum value.
     *
     * @return enum value
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Static constructor converting role type raw value to enum instance.
     *
     * @return {@link RoleType}
     */
    public static RoleType rawValueOf(int value) {
        switch (value) {
            case 1:
                return RoleType.PeerNode;
            case 2:
                return RoleType.ApiNode;
            default:
                throw new IllegalArgumentException(value + " is not a valid role type");
        }
    }
}
