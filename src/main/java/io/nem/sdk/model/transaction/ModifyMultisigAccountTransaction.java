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

import io.nem.catapult.builders.*;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import org.apache.commons.lang3.Validate;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Modify multisig account transactions are part of the NEM's multisig account system.
 * A modify multisig account transaction holds an array of multisig cosignatory modifications, min number of signatures to approve a transaction and a min number of signatures to remove a cosignatory.
 *
 * @since 1.0
 */
public class ModifyMultisigAccountTransaction extends Transaction {
	private final byte minApprovalDelta;
	private final byte minRemovalDelta;
	private final List<MultisigCosignatoryModification> modifications;

	public ModifyMultisigAccountTransaction(NetworkType networkType, Integer version, Deadline deadline,
											BigInteger fee, byte minApprovalDelta, byte minRemovalDelta,
											List<MultisigCosignatoryModification> modifications, String signature,
											PublicAccount signer, TransactionInfo transactionInfo) {
		this(networkType, version, deadline, fee, minApprovalDelta, minRemovalDelta, modifications,
				Optional.of(signature), Optional.of(signer), Optional.of(transactionInfo));
	}

	public ModifyMultisigAccountTransaction(NetworkType networkType, Integer version, Deadline deadline,
											BigInteger fee, byte minApprovalDelta, byte minRemovalDelta,
											List<MultisigCosignatoryModification> modifications) {
		this(networkType, version, deadline, fee, minApprovalDelta, minRemovalDelta, modifications, Optional.empty(),
				Optional.empty(), Optional.empty());
	}

	private ModifyMultisigAccountTransaction(NetworkType networkType, Integer version, Deadline deadline,
											 BigInteger fee, byte minApprovalDelta, byte minRemovalDelta,
											 List<MultisigCosignatoryModification> modifications,
											 Optional<String> signature, Optional<PublicAccount> signer,
											 Optional<TransactionInfo> transactionInfo) {
		super(TransactionType.MODIFY_MULTISIG_ACCOUNT, networkType, version, deadline, fee, signature, signer,
				transactionInfo);
		Validate.notNull(modifications, "Modifications must not be null");
		this.minApprovalDelta = minApprovalDelta;
		this.minRemovalDelta = minRemovalDelta;
		this.modifications = modifications;
	}

	/**
	 * Create a modify multisig account transaction object.
	 *
	 * @param deadline         The deadline to include the transaction.
	 * @param minApprovalDelta The min approval relative change.
	 * @param minRemovalDelta  The min removal relative change.
	 * @param modifications    The list of modifications.
	 * @param networkType      The network type.
	 * @return {@link ModifyMultisigAccountTransaction}
	 */

	public static ModifyMultisigAccountTransaction create(Deadline deadline, byte minApprovalDelta,
														  byte minRemovalDelta,
														  List<MultisigCosignatoryModification> modifications,
														  NetworkType networkType) {
		return new ModifyMultisigAccountTransaction(networkType, TransactionVersion.MODIFY_MULTISIG_ACCOUNT.getValue(), deadline,
				BigInteger.valueOf(0), minApprovalDelta,
				minRemovalDelta, modifications);
	}

	/**
	 * Return number of signatures needed to approve a transaction.
	 * If we are modifying and existing multi-signature account this indicates
	 * the relative change of the minimum cosignatories.
	 *
	 * @return byte
	 */
	public byte getMinApprovalDelta() {
		return minApprovalDelta;
	}

	/**
	 * Return number of signatures needed to remove a cosignatory.
	 * If we are modifying and existing multi-signature account this indicates
	 * the relative change of the minimum cosignatories.
	 *
	 * @return byte
	 */
	public byte getMinRemovalDelta() {
		return minRemovalDelta;
	}

	/**
	 * The List of cosigner accounts added or removed from the multi-signature account.
	 *
	 * @return List<{ @ link MultisigCosignatoryModification }>
	 */
	public List<MultisigCosignatoryModification> getModifications() {
		return modifications;
	}

	/**
	 * Serialized the transaction.
	 *
	 * @return bytes of the transaction.
	 */
	byte[] generateBytes() {
		// Add place holders to the signer and signature until actually signed
		final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
		final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

		ModifyMultisigAccountTransactionBuilder txBuilder =
				ModifyMultisigAccountTransactionBuilder.create(new SignatureDto(signatureBuffer),
						new KeyDto(signerBuffer), getNetworkVersion(),
						EntityTypeDto.MODIFY_MULTISIG_ACCOUNT_TRANSACTION,
						new AmountDto(getFee().longValue()), new TimestampDto(getDeadline().getInstant()),
						getMinRemovalDelta(), getMinApprovalDelta(), getModificationBuilder());
		return txBuilder.serialize();
	}

	/**
	 * Gets the embedded tx bytes.
	 *
	 * @return Embedded tx bytes
	 */
	byte[] generateEmbeddedBytes() {
		EmbeddedModifyMultisigAccountTransactionBuilder txBuilder =
				EmbeddedModifyMultisigAccountTransactionBuilder.create(new KeyDto(getSignerBytes().get()), getNetworkVersion(),
						EntityTypeDto.MODIFY_MULTISIG_ACCOUNT_TRANSACTION,
						getMinRemovalDelta(), getMinApprovalDelta(), getModificationBuilder());
		return txBuilder.serialize();
	}

	/**
	 * Gets cosignatory modification.
	 * @return Cosignatory modification.
	 */
	private ArrayList<CosignatoryModificationBuilder> getModificationBuilder() {
		final ArrayList<CosignatoryModificationBuilder> modificationBuilder = new ArrayList<>(modifications.size());
		for (MultisigCosignatoryModification multisigCosignatoryModification : modifications) {
			final byte[] byteCosignatoryPublicKey =
					multisigCosignatoryModification.getCosignatoryPublicAccount().getPublicKey().getBytes();
			final ByteBuffer keyBuffer = ByteBuffer.wrap(byteCosignatoryPublicKey);
			final CosignatoryModificationBuilder cosignatoryModificationBuilder =
					CosignatoryModificationBuilder.create(CosignatoryModificationTypeDto
									.rawValueOf((byte) multisigCosignatoryModification.getType().getValue()),
							new KeyDto(keyBuffer));
			modificationBuilder.add(cosignatoryModificationBuilder);
		}
		return modificationBuilder;
	}
}
