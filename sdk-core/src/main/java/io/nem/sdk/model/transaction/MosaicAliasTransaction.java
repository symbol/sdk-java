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

import io.nem.catapult.builders.AliasActionDto;
import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.EmbeddedMosaicAliasTransactionBuilder;
import io.nem.catapult.builders.EntityTypeDto;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.MosaicAliasTransactionBuilder;
import io.nem.catapult.builders.MosaicIdDto;
import io.nem.catapult.builders.NamespaceIdDto;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

/**
 * Mosaic alias transaction.
 */
public class MosaicAliasTransaction extends Transaction {

    private final AliasAction aliasAction;
    private final NamespaceId namespaceId;
    private final MosaicId mosaicId;

    /**
     * @param networkType Network type.
     * @param version Transaction version.
     * @param deadline Deadline to include the transaction.
     * @param maxFee Max fee defined by the sender.
     * @param aliasAction Alias action.
     * @param namespaceId Namespace id.
     * @param mosaicId Mosaic id.
     * @param signature Signature.
     * @param signer Signer for the transaction.
     * @param transactionInfo Transaction info.
     */
    @SuppressWarnings("squid:S00107")
    public MosaicAliasTransaction(
        final NetworkType networkType,
        final int version,
        final Deadline deadline,
        final BigInteger maxFee,
        final AliasAction aliasAction,
        final NamespaceId namespaceId,
        final MosaicId mosaicId,
        final Optional<String> signature,
        final Optional<PublicAccount> signer,
        final Optional<TransactionInfo> transactionInfo) {
        super(
            TransactionType.MOSAIC_ALIAS,
            networkType,
            version,
            deadline,
            maxFee,
            signature,
            signer,
            transactionInfo);
        Validate.notNull(namespaceId, "namespaceId must not be null");
        Validate.notNull(mosaicId, "mosaicId must not be null");

        this.aliasAction = aliasAction;
        this.namespaceId = namespaceId;
        this.mosaicId = mosaicId;
    }

    /**
     * Create a mosaic alias transaction object
     *
     * @param deadline Deadline to include the transaction.
     * @param maxFee Max fee defined by the sender.
     * @param aliasAction Alias action.
     * @param namespaceId Namespace id.
     * @param mosaicId Mosaic id.
     * @param networkType Network type.
     * @return Mosaic alias transaction.
     */
    public static MosaicAliasTransaction create(
        final Deadline deadline,
        final BigInteger maxFee,
        final AliasAction aliasAction,
        final NamespaceId namespaceId,
        final MosaicId mosaicId,
        final NetworkType networkType) {
        return new MosaicAliasTransaction(
            networkType,
            TransactionVersion.MOSAIC_ALIAS.getValue(),
            deadline,
            maxFee,
            aliasAction,
            namespaceId,
            mosaicId,
            Optional.empty(),
            Optional.empty(),
            Optional.empty());
    }

    /**
     * Gets the alias action.
     *
     * @return Alias Action.
     */
    public AliasAction getAliasAction() {
        return this.aliasAction;
    }

    /**
     * Gets the namespace id.
     *
     * @return Namespace id.
     */
    public NamespaceId getNamespaceId() {
        return this.namespaceId;
    }

    /**
     * Gets the mosiac id.
     *
     * @return Mosaic id.
     */
    public MosaicId getMosaicId() {
        return this.mosaicId;
    }

    /**
     * Serialized the transaction.
     *
     * @return bytes of the transaction.
     */
    @Override
    byte[] generateBytes() {
        // Add place holders to the signer and signature until actually signed
        final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
        final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

        final MosaicAliasTransactionBuilder txBuilder =
            MosaicAliasTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                EntityTypeDto.MOSAIC_ALIAS_TRANSACTION,
                new AmountDto(getFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                AliasActionDto.rawValueOf(getAliasAction().getValue()),
                new NamespaceIdDto(getNamespaceId().getIdAsLong()),
                new MosaicIdDto(getMosaicId().getIdAsLong()));
        return txBuilder.serialize();
    }

    /**
     * Serialized the transaction to embedded bytes.
     *
     * @return bytes of the transaction.
     */
    @Override
    byte[] generateEmbeddedBytes() {
        final EmbeddedMosaicAliasTransactionBuilder txBuilder =
            EmbeddedMosaicAliasTransactionBuilder.create(
                new KeyDto(getRequiredSignerBytes()),
                getNetworkVersion(),
                EntityTypeDto.MOSAIC_ALIAS_TRANSACTION,
                AliasActionDto.rawValueOf(getAliasAction().getValue()),
                new NamespaceIdDto(getNamespaceId().getIdAsLong()),
                new MosaicIdDto(getMosaicId().getIdAsLong()));
        return txBuilder.serialize();
    }
}
