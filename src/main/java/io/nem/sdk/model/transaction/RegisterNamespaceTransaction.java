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
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceType;
import org.apache.commons.lang3.Validate;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Accounts can rent a namespace for an amount of blocks and after a this renew the contract.
 * This is done via a RegisterNamespaceTransaction.
 *
 * @since 1.0
 */
public class RegisterNamespaceTransaction extends Transaction {
	private final String namespaceName;
	private final NamespaceId namespaceId;
	private final Optional<BigInteger> duration;
	private final Optional<NamespaceId> parentId;
	private final NamespaceType namespaceType;

	public RegisterNamespaceTransaction(final NetworkType networkType, final Integer version, final Deadline deadline, final BigInteger fee,
										final String namespaceName, final NamespaceId namespaceId, final NamespaceType namespaceType,
										final Optional<BigInteger> duration, final Optional<NamespaceId> parentId, final String signature,
										final PublicAccount signer, final TransactionInfo transactionInfo) {
		this(networkType, version, deadline, fee, namespaceName, namespaceId, namespaceType, duration, parentId, Optional.of(signature),
				Optional.of(signer), Optional.of(transactionInfo));
	}

	private RegisterNamespaceTransaction(final NetworkType networkType, final Integer version, final Deadline deadline,
										 final BigInteger fee, final String namespaceName, final NamespaceId namespaceId,
										 final NamespaceType namespaceType, final Optional<BigInteger> duration,
										 final Optional<NamespaceId> parentId) {
		this(networkType, version, deadline, fee, namespaceName, namespaceId, namespaceType, duration, parentId, Optional.empty(),
				Optional.empty(), Optional.empty());
	}

	private RegisterNamespaceTransaction(final NetworkType networkType, final Integer version, final Deadline deadline,
										 final BigInteger fee, final String namespaceName, final NamespaceId namespaceId,
										 final NamespaceType namespaceType, final Optional<BigInteger> duration,
										 final Optional<NamespaceId> parentId, final Optional<String> signature,
										 final Optional<PublicAccount> signer, final Optional<TransactionInfo> transactionInfo) {
		super(TransactionType.REGISTER_NAMESPACE, networkType, version, deadline, fee, signature, signer, transactionInfo);
		Validate.notNull(namespaceName, "NamespaceName must not be null");
		Validate.notNull(namespaceType, "NamespaceType must not be null");
		Validate.notNull(namespaceId, "NamespaceId must not be null");
		if (namespaceType == NamespaceType.RootNamespace) {
			Validate.notNull(duration, "Duration must not be null");
		} else {
			Validate.notNull(parentId, "ParentId must not be null");
		}
		this.namespaceName = namespaceName;
		this.namespaceType = namespaceType;
		this.namespaceId = namespaceId;
		this.duration = duration;
		this.parentId = parentId;
	}

	/**
	 * Creates a root namespace object.
	 *
	 * @param deadline      Deadline to complete the transaction.
	 * @param fee           Fee for the transaction.
	 * @param namespaceName Namespace name.
	 * @param duration      Duration of the namespace.
	 * @param networkType   Network type.
	 * @return Register namespace transaction.
	 */
	public static RegisterNamespaceTransaction createRootNamespace(final Deadline deadline, final BigInteger fee,
																   final String namespaceName, final BigInteger duration,
																   final NetworkType networkType) {
		Validate.notNull(namespaceName, "NamespaceName must not be null");
		NamespaceId namespaceId = new NamespaceId(IdGenerator.generateNamespaceId(namespaceName));
		return new RegisterNamespaceTransaction(networkType, TransactionVersion.REGISTER_NAMESPACE.getValue(), deadline, fee, namespaceName,
				namespaceId, NamespaceType.RootNamespace, Optional.of(duration), Optional.empty());
	}

	/**
	 * Creates a sub namespace object.
	 *
	 * @param deadline            Deadline to include the transaction.
	 * @param fee                 Fee for the namespace.
	 * @param namespaceName       Namespace name.
	 * @param parentNamespaceName Parent namespace name.
	 * @param networkType         Network type.
	 * @return instance of RegisterNamespaceTransaction
	 */
	public static RegisterNamespaceTransaction createSubNamespace(final Deadline deadline, final BigInteger fee, final String namespaceName,
																  final String parentNamespaceName,
																  final NetworkType networkType) {
		Validate.notNull(namespaceName, "NamespaceName must not be null");
		Validate.notNull(parentNamespaceName, "ParentNamespaceName must not be null");
		NamespaceId parentId = new NamespaceId(parentNamespaceName);
		return RegisterNamespaceTransaction.createSubNamespace(deadline, fee, namespaceName, parentId, networkType);
	}

	/**
	 * Create a sub namespace object.
	 *
	 * @param deadline      Deadline to include the transaction.
	 * @param fee           Fee for the namespace.
	 * @param namespaceName Namespace name.
	 * @param parentId      Parent id name.
	 * @param networkType   Network type.
	 * @return instance of RegisterNamespaceTransaction
	 */
	public static RegisterNamespaceTransaction createSubNamespace(final Deadline deadline, final BigInteger fee, final String namespaceName,
																  final NamespaceId parentId,
																  final NetworkType networkType) {
		Validate.notNull(namespaceName, "NamespaceName must not be null");
		Validate.notNull(parentId, "ParentId must not be null");
		NamespaceId namespaceId = new NamespaceId(IdGenerator.generateNamespaceId(namespaceName, parentId.getId()));
		return new RegisterNamespaceTransaction(networkType, TransactionVersion.REGISTER_NAMESPACE.getValue(), deadline,
				fee, namespaceName, namespaceId, NamespaceType.SubNamespace, Optional.empty(), Optional.of(parentId));
	}


	/**
	 * Returns namespace name.
	 *
	 * @return namespace name
	 */
	public String getNamespaceName() {
		return namespaceName;
	}

	/**
	 * Returns id of the namespace derived from namespaceName.
	 * When creating a sub namespace the namespaceId is derived from namespaceName and parentId.
	 *
	 * @return namespace id
	 */
	public NamespaceId getNamespaceId() {
		return namespaceId;
	}

	/**
	 * Returns number of blocks a namespace is active.
	 *
	 * @return namespace renting duration
	 */
	public Optional<BigInteger> getDuration() {
		return duration;
	}

	/**
	 * The id of the parent sub namespace.
	 *
	 * @return sub namespace
	 */
	public Optional<NamespaceId> getParentId() {
		return parentId;
	}

	/**
	 * Returns namespace type either RootNamespace or SubNamespace.
	 *
	 * @return namespace type
	 */
	public NamespaceType getNamespaceType() {
		return namespaceType;
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

		RegisterNamespaceTransactionBuilder txBuilder;
		if (namespaceType == NamespaceType.RootNamespace) {
			txBuilder = RegisterNamespaceTransactionBuilder.create(new SignatureDto(signatureBuffer),
					new KeyDto(signerBuffer), getNetworkVersion(),
					EntityTypeDto.REGISTER_NAMESPACE_TRANSACTION,
					new AmountDto(getFee().longValue()),
					new TimestampDto(getDeadline().getInstant()),
					new BlockDurationDto(getDuration().get().longValue()),
					new NamespaceIdDto(getNamespaceId().getId().longValue()),
					getNameBuffer());

		} else {
			txBuilder = RegisterNamespaceTransactionBuilder.create(new SignatureDto(signatureBuffer),
					new KeyDto(signerBuffer), getNetworkVersion(),
					EntityTypeDto.REGISTER_NAMESPACE_TRANSACTION,
					new AmountDto(getFee().longValue()),
					new TimestampDto(getDeadline().getInstant()),
					new NamespaceIdDto(getParentId().get().getId().longValue()),
					new NamespaceIdDto(getNamespaceId().getId().longValue()),
					getNameBuffer());
		}
		return txBuilder.serialize();
	}

	/**
	 * Gets the embedded tx bytes.
	 *
	 * @return Embedded tx bytes
	 */
	byte[] generateEmbeddedBytes() {
		EmbeddedRegisterNamespaceTransactionBuilder txBuilder;
		if (namespaceType == NamespaceType.RootNamespace) {
			txBuilder = EmbeddedRegisterNamespaceTransactionBuilder.create(new KeyDto(getSignerBytes().get()), getNetworkVersion(),
					EntityTypeDto.REGISTER_NAMESPACE_TRANSACTION,
					new BlockDurationDto(getDuration().get().longValue()),
					new NamespaceIdDto(getNamespaceId().getId().longValue()),
					getNameBuffer());

		} else {
			txBuilder = EmbeddedRegisterNamespaceTransactionBuilder.create(new KeyDto(getSignerBytes().get()), getNetworkVersion(),
					EntityTypeDto.REGISTER_NAMESPACE_TRANSACTION,
					new NamespaceIdDto(getParentId().get().getId().longValue()),
					new NamespaceIdDto(getNamespaceId().getId().longValue()),
					getNameBuffer());
		}
		return txBuilder.serialize();
	}

	/**
	 * Gets namespace name buffer.
	 *
	 * @return Name buffer.
	 */
	private ByteBuffer getNameBuffer() {
		final byte[] nameBytes = namespaceName.getBytes(StandardCharsets.UTF_8);
		final ByteBuffer nameBuffer = ByteBuffer.wrap(nameBytes);
		return nameBuffer;
	}
}
