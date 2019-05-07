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

package io.nem.sdk.model.namespace;

/**
 * The alias action. Supported actions are:
 * 0: Link an alias.
 * 1: Unlink an alias.
 */
public enum AliasAction {

    Link(0),
    Unlink(1);

    private Integer value;

    public static AliasAction rawValueOf(int value) {
        switch (value) {
            case 0:
                return AliasAction.Link;
            case 1:
                return AliasAction.Unlink;
            default:
                throw new IllegalArgumentException(value + " is not a valid value");
        }
    }

    AliasAction(int value) {
        this.value = value;
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
