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

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;

public class MosaicAlias implements Alias {

    private final MosaicId mosaicId;

    @Override
    public MosaicId getMosaicId() {
        return this.mosaicId;
    }

    @Override
    public Address getAddress() {
        return null;
    }

    /**
     * Create MosaicAlias from mosaicId
     *
     * @param mosaicId
     */
    public MosaicAlias(MosaicId mosaicId) {
        this.mosaicId = mosaicId;
    }

    public AliasType getType(){
        return AliasType.Mosaic;
    }

    @Override
    public boolean equals(Alias alias) {
        return alias instanceof MosaicAlias || alias.getType() == AliasType.Mosaic;
    }

    @Override
    public boolean isEmpty() { return false; }
}
