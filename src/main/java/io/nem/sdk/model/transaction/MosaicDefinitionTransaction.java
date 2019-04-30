/*
 * Copyright 2018 NEM
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

import com.google.flatbuffers.FlatBufferBuilder;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.mosaic.MosaicProperties;
import io.nem.sdk.model.namespace.NamespaceId;

import org.apache.commons.lang.Validate;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Before a mosaic can be created or transferred, a corresponding definition of the mosaic has to be created and published to the network.
 * This is done via a mosaic definition transaction.
 *
 * @since 1.0
 */
public class MosaicDefinitionTransaction extends Transaction {
    private final MosaicNonce mosaicNonce;
    private final MosaicId mosaicId;
    private final MosaicProperties mosaicProperties;
    private final Schema schema = new MosaicDefinitionTransactionSchema();


    public MosaicDefinitionTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee, MosaicNonce mosaicNonce, MosaicId mosaicId, MosaicProperties mosaicProperties, String signature, PublicAccount signer, TransactionInfo transactionInfo) {
        this(networkType, version, deadline, fee, mosaicNonce, mosaicId, mosaicProperties, Optional.of(signature), Optional.of(signer), Optional.of(transactionInfo));
    }

    public MosaicDefinitionTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee, MosaicNonce mosaicNonce, MosaicId mosaicId, MosaicProperties mosaicProperties) {
        this(networkType, version, deadline, fee, mosaicNonce, mosaicId, mosaicProperties, Optional.empty(), Optional.empty(), Optional.empty());
    }

    private MosaicDefinitionTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee, MosaicNonce mosaicNonce, MosaicId mosaicId, MosaicProperties mosaicProperties, Optional<String> signature, Optional<PublicAccount> signer, Optional<TransactionInfo> transactionInfo) {
        super(TransactionType.MOSAIC_DEFINITION, networkType, version, deadline, fee, signature, signer, transactionInfo);
        Validate.notNull(mosaicNonce, "MosaicNonce must not be null");
        Validate.notNull(mosaicId, "MosaicId must not be null");
        Validate.notNull(mosaicProperties, "MosaicProperties must not be null");
        this.mosaicNonce = mosaicNonce;
        this.mosaicId = mosaicId;
        this.mosaicProperties = mosaicProperties;
    }

    /**
     * Create a mosaic creation transaction object.
     *
     * @param deadline         The deadline to include the transaction.
     * @param mosaicNonce      The mosaicNonce
     * @param mosaicId         The mosaicId.
     * @param mosaicProperties The mosaic properties.
     * @param networkType      The network type.
     * @return {@link MosaicDefinitionTransaction}
     */
    public static MosaicDefinitionTransaction create(Deadline deadline, MosaicNonce mosaicNonce, MosaicId mosaicId, MosaicProperties mosaicProperties, NetworkType networkType) {
        Validate.notNull(mosaicNonce, "MosaicNonce must not be null");
        Validate.notNull(mosaicId, "MosaicId must not be null");
        return new MosaicDefinitionTransaction(networkType,
                TransactionVersion.MOSAIC_DEFINITION.getValue(),
                deadline,
                BigInteger.valueOf(0),
                mosaicNonce,
                mosaicId,
                mosaicProperties);
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

    byte[] generateBytes() {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        BigInteger deadlineBigInt = BigInteger.valueOf(getDeadline().getInstant());
        int[] fee = new int[]{0, 0};
        int version = (int) Long.parseLong(Integer.toHexString(getNetworkType().getValue()) + "0" + Integer.toHexString(getVersion()), 16);

        int flags = 0;

        if (mosaicProperties.isSupplyMutable()) {
            flags += 1;
        }

        if (mosaicProperties.isTransferable()) {
            flags += 2;
        }

        if (mosaicProperties.isLevyMutable()) {
            flags += 4;
        }

        // Create Vectors
        int signatureVector = MosaicDefinitionCreationTransactionBuffer.createSignatureVector(builder, new byte[64]);
        int signerVector = MosaicDefinitionCreationTransactionBuffer.createSignerVector(builder, new byte[32]);
        int deadlineVector = MosaicDefinitionCreationTransactionBuffer.createDeadlineVector(builder, UInt64.fromBigInteger(deadlineBigInt));
        int feeVector = MosaicDefinitionCreationTransactionBuffer.createFeeVector(builder, fee);
        int mosaicIdVector = MosaicDefinitionCreationTransactionBuffer.createParentIdVector(builder, UInt64.fromBigInteger(mosaicId.getId()));
        int durationVector = MosaicDefinitionCreationTransactionBuffer.createDurationVector(builder, UInt64.fromBigInteger(mosaicProperties.getDuration()));

        int fixSize = 149; // replace by the all numbers sum or add a comment explaining this

        //int name = builder.createString(mosaicName);

        MosaicDefinitionCreationTransactionBuffer.startMosaicDefinitionCreationTransactionBuffer(builder);
        //MosaicDefinitionCreationTransactionBuffer.addSize(builder, fixSize + mosaicName.length());
        MosaicDefinitionCreationTransactionBuffer.addSignature(builder, signatureVector);
        MosaicDefinitionCreationTransactionBuffer.addSigner(builder, signerVector);
        MosaicDefinitionCreationTransactionBuffer.addVersion(builder, version);
        MosaicDefinitionCreationTransactionBuffer.addType(builder, getType().getValue());
        MosaicDefinitionCreationTransactionBuffer.addFee(builder, feeVector);
        MosaicDefinitionCreationTransactionBuffer.addDeadline(builder, deadlineVector);

        MosaicDefinitionCreationTransactionBuffer.addMosaicId(builder, mosaicIdVector);
        //MosaicDefinitionCreationTransactionBuffer.addMosaicNameLength(builder, mosaicName.length());
        MosaicDefinitionCreationTransactionBuffer.addNumOptionalProperties(builder, 1);
        MosaicDefinitionCreationTransactionBuffer.addFlags(builder, flags);

        MosaicDefinitionCreationTransactionBuffer.addDivisibility(builder, mosaicProperties.getDivisibility());

        //MosaicDefinitionCreationTransactionBuffer.addMosaicName(builder, name);
        MosaicDefinitionCreationTransactionBuffer.addIndicateDuration(builder, 2);
        MosaicDefinitionCreationTransactionBuffer.addDuration(builder, durationVector);

        int codedTransaction = MosaicDefinitionCreationTransactionBuffer.endMosaicDefinitionCreationTransactionBuffer(builder);
        builder.finish(codedTransaction);

        return schema.serialize(builder.sizedByteArray());
    }
}
