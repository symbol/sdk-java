package io.nem.sdk.model.transaction;

import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.BlockDurationDto;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.MosaicAddressRestrictionTransactionBuilder;
import io.nem.catapult.builders.MosaicDefinitionTransactionBuilder;
import io.nem.catapult.builders.MosaicIdDto;
import io.nem.catapult.builders.MosaicNonceDto;
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
