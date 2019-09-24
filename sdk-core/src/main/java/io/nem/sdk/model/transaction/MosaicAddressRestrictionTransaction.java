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
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.MosaicAddressRestrictionTransactionBuilder;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.catapult.builders.UnresolvedAddressDto;
import io.nem.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class MosaicAddressRestrictionTransaction extends Transaction {

    private final MosaicId unresolvedMosaicId;
    private final BigInteger restrictionKey;
    private final Address targetAddress;
    private final BigInteger previousRestrictionValue;
    private final BigInteger newRestrictionValue;

    MosaicAddressRestrictionTransaction(MosaicAddressRestrictionTransactionFactory factory) {
        super(factory);
        unresolvedMosaicId = factory.getUnresolvedMosaicId();
        restrictionKey = factory.getRestrictionKey();
        targetAddress = factory.getTargetAddress();
        previousRestrictionValue = factory.getPreviousRestrictionValue();
        newRestrictionValue = factory.getNewRestrictionValue();
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

    @Override
    byte[] generateBytes() {
        // Add place holders to the signer and signature until actually signed
        final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
        final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

        MosaicAddressRestrictionTransactionBuilder txBuilder =
            MosaicAddressRestrictionTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                getEntityTypeDto(),
                new AmountDto(getMaxFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                new UnresolvedMosaicIdDto(getUnresolvedMosaicId().getIdAsLong()),
                getRestrictionKey().longValue(),
                new UnresolvedAddressDto(getTargetAddress().getByteBuffer()),
                getPreviousRestrictionValue().longValue(),
                getNewRestrictionValue().longValue()
            );
        return txBuilder.serialize();
    }

    @Override
    byte[] generateEmbeddedBytes() {
        return new byte[0];
    }
}
