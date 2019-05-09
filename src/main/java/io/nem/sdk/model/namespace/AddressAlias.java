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

/**
 * The AddressAlias structure describes address aliases
 *
 * @since
 */

package io.nem.sdk.model.namespace;

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;

public class AddressAlias implements Alias {

    private final Address address;

    @Override
    public Address getAddress() {
        return this.address;
    }

    @Override
    public MosaicId getMosaicId() {
        return null;
    }

    /**
     * Create AddressAlias from address
     *
     * @param address
     */
    public AddressAlias(Address address) {
        this.address = address;
    }

    public AliasType getType(){
        return AliasType.Address;
    }

    @Override
    public boolean equals(Alias alias) {
        return alias instanceof AddressAlias || alias.getType() == AliasType.Address;
    }

    @Override
    public boolean isEmpty() { return false; }
}
