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

import io.nem.catapult.builders.*;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.mosaic.MosaicProperties;
import org.apache.commons.lang.Validate;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
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


	public MosaicDefinitionTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee,
									   MosaicNonce mosaicNonce, MosaicId mosaicId, MosaicProperties mosaicProperties,
									   String signature, PublicAccount signer, TransactionInfo transactionInfo) {
		this(networkType, version, deadline, fee, mosaicNonce, mosaicId, mosaicProperties, Optional.of(signature),
				Optional.of(signer), Optional.of(transactionInfo));
	}

	public MosaicDefinitionTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee,
									   MosaicNonce mosaicNonce, MosaicId mosaicId, MosaicProperties mosaicProperties) {
		this(networkType, version, deadline, fee, mosaicNonce, mosaicId, mosaicProperties, Optional.empty(),
				Optional.empty(), Optional.empty());
	}

	private MosaicDefinitionTransaction(NetworkType networkType, Integer version, Deadline deadline, BigInteger fee,
										MosaicNonce mosaicNonce, MosaicId mosaicId, MosaicProperties mosaicProperties,
										Optional<String> signature, Optional<PublicAccount> signer,
										Optional<TransactionInfo> transactionInfo) {
		super(TransactionType.MOSAIC_DEFINITION, networkType, version, deadline, fee, signature, signer,
				transactionInfo);
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
	 * @param maxFee           Max fee.
	 * @param mosaicNonce      The mosaicNonce
	 * @param mosaicId         The mosaicId.
	 * @param mosaicProperties The mosaic properties.
	 * @param networkType      The network type.
	 * @return {@link MosaicDefinitionTransaction}
	 */
	public static MosaicDefinitionTransaction create(Deadline deadline, BigInteger maxFee, MosaicNonce mosaicNonce, MosaicId mosaicId,
													 MosaicProperties mosaicProperties, NetworkType networkType) {
		Validate.notNull(mosaicNonce, "MosaicNonce must not be null");
		Validate.notNull(mosaicId, "MosaicId must not be null");
		return new MosaicDefinitionTransaction(networkType,
				TransactionVersion.MOSAIC_DEFINITION.getValue(),
				deadline,
				maxFee,
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
				MosaicDefinitionTransactionBuilder.create(new SignatureDto(signatureBuffer),
						new KeyDto(signerBuffer), getNetworkVersion(),
						EntityTypeDto.MOSAIC_DEFINITION_TRANSACTION,
						new AmountDto(getFee().longValue()), new TimestampDto(getDeadline().getInstant()),
						new MosaicNonceDto(getMosaicNonce().getNonceAsInt()),
						new MosaicIdDto(getMosaicId().getId().longValue()),
						getMosaicFlags(), (byte) getMosaicProperties().getDivisibility(),
						getProperties());
		return txBuilder.serialize();
	}

	/**
	 * Gets the embedded tx bytes.
	 *
	 * @return Embedded tx bytes
	 */
	byte[] generateEmbeddedBytes() {
		EmbeddedMosaicDefinitionTransactionBuilder txBuilder =
				EmbeddedMosaicDefinitionTransactionBuilder.create(new KeyDto(getSignerBytes().get()), getNetworkVersion(),
						EntityTypeDto.MOSAIC_DEFINITION_TRANSACTION,
						new MosaicNonceDto(getMosaicNonce().getNonceAsInt()),
						new MosaicIdDto(getMosaicId().getId().longValue()),
						getMosaicFlags(), (byte) getMosaicProperties().getDivisibility(),
						getProperties());
		return txBuilder.serialize();
	}

	/**
	 * Get the mosaic flags.
	 *
	 * @return Mosaic flags
	 */
	private EnumSet<MosaicFlagsDto> getMosaicFlags() {
		EnumSet<MosaicFlagsDto> mosaicFlagsBuilder = EnumSet.of(MosaicFlagsDto.NONE);
		if (getMosaicProperties().isSupplyMutable()) {
			mosaicFlagsBuilder.add(MosaicFlagsDto.SUPPLY_MUTABLE);
		}
		if (getMosaicProperties().isTransferable()) {
			mosaicFlagsBuilder.add(MosaicFlagsDto.TRANSFERABLE);
		}
		return mosaicFlagsBuilder;
	}

	/**
	 * Gets a list of properties.
	 *
	 * @return List of mosaic properties.
	 */
	private ArrayList<MosaicPropertyBuilder> getProperties() {
		final ArrayList<MosaicPropertyBuilder> properties = new ArrayList<>();
		if (mosaicProperties.getDuration().isPresent()) {
			properties.add(MosaicPropertyBuilder.create(MosaicPropertyIdDto.DURATION,
					mosaicProperties.getDuration().get().longValue()));
		}
		return properties;
	}
}
