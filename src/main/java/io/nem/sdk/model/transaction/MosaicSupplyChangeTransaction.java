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
import io.nem.catapult.builders.EmbeddedMosaicSupplyChangeTransactionBuilder;
import io.nem.catapult.builders.EntityTypeDto;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.MosaicSupplyChangeActionDto;
import io.nem.catapult.builders.MosaicSupplyChangeTransactionBuilder;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicSupplyType;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

/**
 * In case a mosaic has the flag 'supplyMutable' set to true, the creator of the mosaic can change
 * the supply, i.e. increase or decrease the supply.
 *
 * @since 1.0
 */
public class MosaicSupplyChangeTransaction extends Transaction {

    private final MosaicId mosaicId;
    private final MosaicSupplyType mosaicSupplyType;
    private final BigInteger delta;

    public MosaicSupplyChangeTransaction(
        NetworkType networkType,
        Integer version,
        Deadline deadline,
        BigInteger fee,
        MosaicId mosaicId,
        MosaicSupplyType mosaicSupplyType,
        BigInteger delta,
        String signature,
        PublicAccount signer,
        TransactionInfo transactionInfo) {
        this(
            networkType,
            version,
            deadline,
            fee,
            mosaicId,
            mosaicSupplyType,
            delta,
            Optional.of(signature),
            Optional.of(signer),
            Optional.of(transactionInfo));
    }

    public MosaicSupplyChangeTransaction(
        NetworkType networkType,
        Integer version,
        Deadline deadline,
        BigInteger fee,
        MosaicId mosaicId,
        MosaicSupplyType mosaicSupplyType,
        BigInteger delta) {
        this(
            networkType,
            version,
            deadline,
            fee,
            mosaicId,
            mosaicSupplyType,
            delta,
            Optional.empty(),
            Optional.empty(),
            Optional.empty());
    }

    private MosaicSupplyChangeTransaction(
        NetworkType networkType,
        Integer version,
        Deadline deadline,
        BigInteger fee,
        MosaicId mosaicId,
        MosaicSupplyType mosaicSupplyType,
        BigInteger delta,
        Optional<String> signature,
        Optional<PublicAccount> signer,
        Optional<TransactionInfo> transactionInfo) {
        super(
            TransactionType.MOSAIC_SUPPLY_CHANGE,
            networkType,
            version,
            deadline,
            fee,
            signature,
            signer,
            transactionInfo);
        Validate.notNull(mosaicId, "MosaicId must not be null");
        Validate.notNull(mosaicSupplyType, "MosaicSupplyType must not be null");
        Validate.notNull(delta, "Delta must not be null");
        this.mosaicId = mosaicId;
        this.mosaicSupplyType = mosaicSupplyType;
        this.delta = delta;
    }

    /**
     * Create a mosaic supply change transaction object.
     *
     * @param deadline The deadline to include the transaction.
     * @param mosaicId The mosaic id.
     * @param mosaicSupplyType The supply type.
     * @param delta The supply change in units for the mosaic.
     * @param networkType The network type.
     * @return {@link MosaicSupplyChangeTransaction}
     */
    public static MosaicSupplyChangeTransaction create(
        Deadline deadline,
        MosaicId mosaicId,
        MosaicSupplyType mosaicSupplyType,
        BigInteger delta,
        NetworkType networkType) {
        return new MosaicSupplyChangeTransaction(
            networkType,
            TransactionVersion.MOSAIC_SUPPLY_CHANGE.getValue(),
            deadline,
            BigInteger.valueOf(0),
            mosaicId,
            mosaicSupplyType,
            delta);
    }

    /**
     * Returns mosaic id.
     *
     * @return BigInteger
     */
    public MosaicId getMosaicId() {
        return mosaicId;
    }

    /**
     * Returns mosaic supply type.
     *
     * @return {@link MosaicSupplyType}
     */
    public MosaicSupplyType getMosaicSupplyType() {
        return mosaicSupplyType;
    }

    /**
     * Returns amount of mosaics added or removed.
     *
     * @return BigInteger
     */
    public BigInteger getDelta() {
        return delta;
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

        MosaicSupplyChangeTransactionBuilder txBuilder =
            MosaicSupplyChangeTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                EntityTypeDto.MOSAIC_SUPPLY_CHANGE_TRANSACTION,
                new AmountDto(getFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                new UnresolvedMosaicIdDto(getMosaicId().getId().longValue()),
                MosaicSupplyChangeActionDto.rawValueOf((byte) getMosaicSupplyType().getValue()),
                new AmountDto(getDelta().longValue()));
        return txBuilder.serialize();
    }

    /**
     * Gets the embedded tx bytes.
     *
     * @return Embedded tx bytes
     */
    byte[] generateEmbeddedBytes() {
        EmbeddedMosaicSupplyChangeTransactionBuilder txBuilder =
            EmbeddedMosaicSupplyChangeTransactionBuilder.create(
                new KeyDto(getSignerBytes().get()),
                getNetworkVersion(),
                EntityTypeDto.MOSAIC_SUPPLY_CHANGE_TRANSACTION,
                new UnresolvedMosaicIdDto(getMosaicId().getId().longValue()),
                MosaicSupplyChangeActionDto.rawValueOf((byte) getMosaicSupplyType().getValue()),
                new AmountDto(getDelta().longValue()));
        return txBuilder.serialize();
    }
}
