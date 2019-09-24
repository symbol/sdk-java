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

import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.EmbeddedMosaicGlobalRestrictionTransactionBuilder;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.MosaicGlobalRestrictionTransactionBuilder;
import io.nem.catapult.builders.MosaicRestrictionTypeDto;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class MosaicGlobalRestrictionTransaction extends Transaction {

    private final MosaicId mosaicId;
    private final MosaicId referenceMosaicId;
    private final BigInteger restrictionKey;
    private final BigInteger previousRestrictionValue;
    private final MosaicRestrictionType previousRestrictionType;
    private final BigInteger newRestrictionValue;
    private final MosaicRestrictionType newRestrictionType;

    MosaicGlobalRestrictionTransaction(MosaicGlobalRestrictionTransactionFactory factory) {
        super(factory);
        mosaicId = factory.getMosaicId();
        referenceMosaicId = factory.getReferenceMosaicId();
        restrictionKey = factory.getRestrictionKey();
        previousRestrictionValue = factory.getPreviousRestrictionValue();
        previousRestrictionType = factory.getPreviousRestrictionType();
        newRestrictionValue = factory.getNewRestrictionValue();
        newRestrictionType = factory.getNewRestrictionType();
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

    @Override
    byte[] generateBytes() {
        // Add place holders to the signer and signature until actually signed
        final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
        final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

        MosaicGlobalRestrictionTransactionBuilder txBuilder =
            MosaicGlobalRestrictionTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                getEntityTypeDto(),
                new AmountDto(getMaxFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                new UnresolvedMosaicIdDto(getMosaicId().getIdAsLong()),
                new UnresolvedMosaicIdDto(getReferenceMosaicId().getIdAsLong()),
                getRestrictionKey().longValue(),
                getPreviousRestrictionValue().longValue(),
                MosaicRestrictionTypeDto.rawValueOf(getPreviousRestrictionType().getValue()),
                getNewRestrictionValue().longValue(),
                MosaicRestrictionTypeDto.rawValueOf(getNewRestrictionType().getValue())
            );
        return txBuilder.serialize();
    }

    @Override
    byte[] generateEmbeddedBytes() {

        EmbeddedMosaicGlobalRestrictionTransactionBuilder txBuilder =
            EmbeddedMosaicGlobalRestrictionTransactionBuilder.create(
                new KeyDto(getRequiredSignerBytes()),
                getNetworkVersion(),
                getEntityTypeDto(),
                new UnresolvedMosaicIdDto(getMosaicId().getIdAsLong()),
                new UnresolvedMosaicIdDto(getReferenceMosaicId().getIdAsLong()),
                getRestrictionKey().longValue(),
                getPreviousRestrictionValue().longValue(),
                MosaicRestrictionTypeDto.rawValueOf(getPreviousRestrictionType().getValue()),
                getNewRestrictionValue().longValue(),
                MosaicRestrictionTypeDto.rawValueOf(getNewRestrictionType().getValue())
            );
        return txBuilder.serialize();
    }
}
