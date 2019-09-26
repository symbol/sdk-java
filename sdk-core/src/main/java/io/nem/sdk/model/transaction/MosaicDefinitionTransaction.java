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

package io.nem.sdk.model.transaction;

import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.BlockDurationDto;
import io.nem.catapult.builders.EmbeddedMosaicDefinitionTransactionBuilder;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.MosaicDefinitionTransactionBuilder;
import io.nem.catapult.builders.MosaicFlagsDto;
import io.nem.catapult.builders.MosaicIdDto;
import io.nem.catapult.builders.MosaicNonceDto;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.sdk.model.blockchain.BlockDuration;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.mosaic.MosaicFlags;
import java.nio.ByteBuffer;
import java.util.EnumSet;

/**
 * Mosaic definition transaction.
 *
 * Before a mosaic can be created or transferred, a corresponding definition of the mosaic has to be
 * created and published to the network. This is done via a mosaic definition transaction.
 *
 * @since 1.0
 */
public class MosaicDefinitionTransaction extends Transaction {

    private final MosaicNonce mosaicNonce;
    private final MosaicId mosaicId;
    private final MosaicFlags mosaicFlags;
    /**
     * The divisibility determines up to what decimal place the mosaic can be divided into. Thus a
     * divisibility of 3 means that a mosaic can be divided into smallest parts of 0.001 mosaics
     * i.e. milli mosaics is the smallest sub-unit. When transferring mosaics via a transfer
     * transaction the quantity transferred is given in multiples of those smallest parts. The
     * divisibility must be in the range of 0 and 6. The default value is "0".
     */
    private final int divisibility;
    /**
     * The duration in blocks a mosaic will be available. After the duration finishes mosaic is
     * inactive and can be renewed. Duration is required when defining the mosaic
     */
    private final BlockDuration blockDuration;

    /**
     * Constructor.
     *
     * @param factory {@link MosaicDefinitionTransactionFactory}
     */
    public MosaicDefinitionTransaction(MosaicDefinitionTransactionFactory factory) {
        super(factory);
        this.mosaicNonce = factory.getMosaicNonce();
        this.mosaicId = factory.getMosaicId();
        this.mosaicFlags = factory.getMosaicFlags();
        this.divisibility = factory.getDivisibility();
        this.blockDuration = factory.getBlockDuration();
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
     * Returns mosaic flags defining mosaic.
     *
     * @return {@link MosaicFlags}
     */
    public MosaicFlags getMosaicFlags() {
        return mosaicFlags;
    }

    /**
     * Returns the number of blocks from height it will be active
     *
     * @return the number of blocks from height it will be active
     */
    public BlockDuration getBlockDuration() {
        return blockDuration;
    }

    /**
     * Returns the mosaic divisibility.
     *
     * @return mosaic divisibility
     */
    public int getDivisibility() {
        return divisibility;
    }

    /**
     * Gets the serialized bytes.
     *
     * @return Serialized bytes
     */
    byte[] generateBytes() {
        // Add place holders to the signer and signature until actually signed
        final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
        final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

        MosaicDefinitionTransactionBuilder txBuilder =
            MosaicDefinitionTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                getEntityTypeDto(),
                new AmountDto(getMaxFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                new MosaicNonceDto(getMosaicNonce().getNonceAsInt()),
                new MosaicIdDto(getMosaicId().getId().longValue()),
                getMosaicFlagsEnumSet(),
                (byte) getDivisibility(),
                new BlockDurationDto(getBlockDuration().getDuration()));
        return txBuilder.serialize();
    }

    /**
     * Gets the embedded tx bytes.
     *
     * @return Embedded tx bytes
     */
    byte[] generateEmbeddedBytes() {
        EmbeddedMosaicDefinitionTransactionBuilder txBuilder =
            EmbeddedMosaicDefinitionTransactionBuilder.create(
                new KeyDto(getRequiredSignerBytes()),
                getNetworkVersion(),
                getEntityTypeDto(),
                new MosaicNonceDto(getMosaicNonce().getNonceAsInt()),
                new MosaicIdDto(getMosaicId().getId().longValue()),
                getMosaicFlagsEnumSet(),
                (byte) getDivisibility(),
                new BlockDurationDto(getBlockDuration().getDuration()));
        return txBuilder.serialize();
    }

    /**
     * Get the mosaic flags.
     *
     * @return Mosaic flags
     */
    private EnumSet<MosaicFlagsDto> getMosaicFlagsEnumSet() {
        EnumSet<MosaicFlagsDto> mosaicFlagsBuilder = EnumSet.of(MosaicFlagsDto.NONE);
        if (getMosaicFlags().isSupplyMutable()) {
            mosaicFlagsBuilder.add(MosaicFlagsDto.SUPPLY_MUTABLE);
        }
        if (getMosaicFlags().isTransferable()) {
            mosaicFlagsBuilder.add(MosaicFlagsDto.TRANSFERABLE);
        }
        if (getMosaicFlags().isRestrictable()) {
            mosaicFlagsBuilder.add(MosaicFlagsDto.RESTRICTABLE);
        }
        return mosaicFlagsBuilder;
    }
}
