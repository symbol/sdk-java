/**
 * ** Copyright (c) 2016-present,
 * ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
 * **
 * ** This file is part of Catapult.
 * **
 * ** Catapult is free software: you can redistribute it and/or modify
 * ** it under the terms of the GNU Lesser General Public License as published by
 * ** the Free Software Foundation, either version 3 of the License, or
 * ** (at your option) any later version.
 * **
 * ** Catapult is distributed in the hope that it will be useful,
 * ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 * ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * ** GNU Lesser General Public License for more details.
 * **
 * ** You should have received a copy of the GNU Lesser General Public License
 * ** along with Catapult. If not, see <http://www.gnu.org/licenses/>.
 **/

package io.nem.sdk.model.transaction;

import io.nem.catapult.builders.*;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import org.apache.commons.lang3.Validate;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Optional;

/**
 * Address alias transaction.
 */
public class AddressAliasTransaction extends Transaction {
	final private AliasAction aliasAction;
	final private NamespaceId namespaceId;
	final private Address address;

	/**
	 * @param networkType     Network type.
	 * @param version         Transaction version.
	 * @param deadline        Deadline to include the transaction.
	 * @param maxFee          Max fee defined by the sender.
	 * @param aliasAction     Alias action.
	 * @param namespaceId     Namespace id.
	 * @param address         Address of the account.
	 * @param signature       Signature.
	 * @param signer          Signer for the transaction.
	 * @param transactionInfo Transaction info.
	 */
	public AddressAliasTransaction(final NetworkType networkType, final int version, final Deadline deadline, final BigInteger maxFee,
								   final AliasAction aliasAction, final NamespaceId namespaceId, final Address address,
								   final Optional<String> signature, final Optional<PublicAccount> signer,
								   final Optional<TransactionInfo> transactionInfo) {
		super(TransactionType.MOSAIC_ALIAS, networkType, version, deadline, maxFee, signature, signer, transactionInfo);
		Validate.notNull(namespaceId, "namespaceId must not be null");
		Validate.notNull(address, "address must not be null");

		this.aliasAction = aliasAction;
		this.namespaceId = namespaceId;
		this.address = address;
	}

	/**
	 * Create a mosaic alias transaction object
	 *
	 * @param deadline    Deadline to include the transaction.
	 * @param maxFee      Max fee defined by the sender.
	 * @param aliasAction Alias action.
	 * @param namespaceId Namespace id.
	 * @param address     Address of the account.
	 * @param networkType Network type.
	 * @returns Address alias transaction.
	 */
	public static AddressAliasTransaction create(final Deadline deadline, final BigInteger maxFee, final AliasAction aliasAction,
												 final NamespaceId namespaceId, final Address address, final NetworkType networkType) {
		return new AddressAliasTransaction(networkType, TransactionVersion.ADDRESS_ALIAS.getValue(), deadline, maxFee, aliasAction,
				namespaceId, address, Optional.empty(), Optional.empty(), Optional.empty());
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
	 * Gets the address.
	 *
	 * @return Address of the account.
	 */
	public Address getAddress() {
		return this.address;
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

		final AddressAliasTransactionBuilder txBuilder =
				AddressAliasTransactionBuilder.create(new SignatureDto(signatureBuffer),
						new KeyDto(signerBuffer), getNetworkVersion(),
						EntityTypeDto.ADDRESS_ALIAS_TRANSACTION,
						new AmountDto(getFee().longValue()), new TimestampDto(getDeadline().getInstant()),
						AliasActionDto.rawValueOf(getAliasAction().getValue()),
						new NamespaceIdDto(getNamespaceId().getIdAsLong()), new AddressDto(getAddress().getByteBuffer()));
		return txBuilder.serialize();
	}

	/**
	 * Serialized the transaction to embedded bytes.
	 *
	 * @return bytes of the transaction.
	 */
	@Override
	byte[] generateEmbeddedBytes() {
		final EmbeddedAddressAliasTransactionBuilder txBuilder =
				EmbeddedAddressAliasTransactionBuilder.create(new KeyDto(getSignerBytes().get()), getNetworkVersion(),
						EntityTypeDto.ADDRESS_ALIAS_TRANSACTION, AliasActionDto.rawValueOf(getAliasAction().getValue()),
						new NamespaceIdDto(getNamespaceId().getIdAsLong()), new AddressDto(getAddress().getByteBuffer()));
		return txBuilder.serialize();
	}
}
