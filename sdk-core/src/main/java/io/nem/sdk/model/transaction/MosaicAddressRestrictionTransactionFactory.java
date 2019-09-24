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

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

public class MosaicAddressRestrictionTransactionFactory
    extends TransactionFactory<MosaicAddressRestrictionTransaction> {

    private final MosaicId unresolvedMosaicId;
    private final BigInteger restrictionKey;
    private final Address targetAddress;
    private final BigInteger previousRestrictionValue;
    private final BigInteger newRestrictionValue;

    public MosaicAddressRestrictionTransactionFactory(
        NetworkType networkType,
        MosaicId unresolvedMosaicId,
        BigInteger restrictionKey,
        Address targetAddress,
        BigInteger previousRestrictionValue,
        BigInteger newRestrictionValue) {
        super(TransactionType.MOSAIC_ADDRESS_RESTRICTION, networkType);
        Validate.notNull(unresolvedMosaicId, "UnresolvedMosaicId must not be null");
        Validate.notNull(restrictionKey, "RestrictionKey must not be null");
        Validate.notNull(targetAddress, "TargetAddress must not be null");
        Validate.notNull(previousRestrictionValue, "PreviousRestrictionValue must not be null");
        Validate.notNull(newRestrictionValue, "NewRestrictionValue must not be null");
        this.unresolvedMosaicId = unresolvedMosaicId;
        this.restrictionKey = restrictionKey;
        this.targetAddress = targetAddress;
        this.previousRestrictionValue = previousRestrictionValue;
        this.newRestrictionValue = newRestrictionValue;
    }

    @Override
    public MosaicAddressRestrictionTransaction build() {
        return new MosaicAddressRestrictionTransaction(this);
    }

    public MosaicId getUnresolvedMosaicId() {
        return unresolvedMosaicId;
    }

    public BigInteger getRestrictionKey() {
        return restrictionKey;
    }

    public Address getTargetAddress() {
        return targetAddress;
    }

    public BigInteger getPreviousRestrictionValue() {
        return previousRestrictionValue;
    }

    public BigInteger getNewRestrictionValue() {
        return newRestrictionValue;
    }
}
