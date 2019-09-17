/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.sdk.model.transaction;

import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.mosaic.MosaicProperties;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link MosaicDefinitionTransaction}
 */
public class MosaicDefinitionTransactionFactory extends
    TransactionFactory<MosaicDefinitionTransaction> {

    private final MosaicNonce mosaicNonce;
    private final MosaicId mosaicId;
    private final MosaicProperties mosaicProperties;


    public MosaicDefinitionTransactionFactory(NetworkType networkType, MosaicNonce mosaicNonce,
        MosaicId mosaicId, MosaicProperties mosaicProperties) {
        super(TransactionType.MOSAIC_DEFINITION, networkType);
        Validate.notNull(mosaicNonce, "MosaicNonce must not be null");
        Validate.notNull(mosaicId, "MosaicId must not be null");
        Validate.notNull(mosaicProperties, "MosaicProperties must not be null");
        this.mosaicNonce = mosaicNonce;
        this.mosaicId = mosaicId;
        this.mosaicProperties = mosaicProperties;
    }

    /**
     * Returns mosaic id generated from namespace name and mosaic name.
     *
     * @return MosaicId
     */
    public MosaicId getMosaicId() {
        return mosaicId;
    }

    /**
     * Returns mosaic mosaicNonce.
     *
     * @return String
     */
    public MosaicNonce getMosaicNonce() {
        return mosaicNonce;
    }

    /**
     * Returns mosaic properties defining mosaic.
     *
     * @return {@link MosaicProperties}
     */
    public MosaicProperties getMosaicProperties() {
        return mosaicProperties;
    }

    @Override
    public MosaicDefinitionTransaction build() {
        return new MosaicDefinitionTransaction(this);
    }
}
