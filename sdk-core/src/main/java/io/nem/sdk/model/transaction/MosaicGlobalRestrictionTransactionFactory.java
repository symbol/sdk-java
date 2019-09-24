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
import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

public class MosaicGlobalRestrictionTransactionFactory
    extends TransactionFactory<MosaicGlobalRestrictionTransaction> {

    private final MosaicId mosaicId;
    private final MosaicId referenceMosaicId;
    private final BigInteger restrictionKey;
    private final BigInteger previousRestrictionValue;
    private final MosaicRestrictionType previousRestrictionType;
    private final BigInteger newRestrictionValue;
    private final MosaicRestrictionType newRestrictionType;

    public MosaicGlobalRestrictionTransactionFactory(
        NetworkType networkType,
        MosaicId mosaicId,
        MosaicId referenceMosaicId,
        BigInteger restrictionKey,
        BigInteger previousRestrictionValue,
        MosaicRestrictionType previousRestrictionType,
        BigInteger newRestrictionValue,
        MosaicRestrictionType newRestrictionType) {
        super(TransactionType.MOSAIC_GLOBAL_RESTRICTION, networkType);
        Validate.notNull(mosaicId, "RestrictedMosaicId must not be null");
        Validate.notNull(referenceMosaicId, "ReferenceMosaicId must not be null");
        Validate.notNull(restrictionKey, "RestrictionKey must not be null");
        Validate.notNull(previousRestrictionValue, "PreviousRestrictionValue must not be null");
        Validate.notNull(previousRestrictionType, "PreviousRestrictionType must not be null");
        Validate.notNull(newRestrictionValue, "NewRestrictionValue must not be null");
        Validate.notNull(newRestrictionType, "NewRestrictionType must not be null");
        this.mosaicId = mosaicId;
        this.referenceMosaicId = referenceMosaicId;
        this.restrictionKey = restrictionKey;
        this.previousRestrictionValue = previousRestrictionValue;
        this.previousRestrictionType = previousRestrictionType;
        this.newRestrictionValue = newRestrictionValue;
        this.newRestrictionType = newRestrictionType;
    }

    @Override
    public MosaicGlobalRestrictionTransaction build() {
        return new MosaicGlobalRestrictionTransaction(this);
    }

    public MosaicId getMosaicId() {
        return mosaicId;
    }

    public MosaicId getReferenceMosaicId() {
        return referenceMosaicId;
    }

    public BigInteger getRestrictionKey() {
        return restrictionKey;
    }

    public BigInteger getPreviousRestrictionValue() {
        return previousRestrictionValue;
    }

    public MosaicRestrictionType getPreviousRestrictionType() { return  previousRestrictionType; }

    public BigInteger getNewRestrictionValue() {
        return newRestrictionValue;
    }

    public MosaicRestrictionType getNewRestrictionType() { return  newRestrictionType; }
}
