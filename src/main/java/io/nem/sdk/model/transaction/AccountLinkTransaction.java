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
import org.apache.commons.lang.Validate;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Optional;

public class AccountLinkTransaction extends Transaction {

	private final PublicAccount remoteAccount;
	private final AccountLinkAction linkAction;

	public AccountLinkTransaction(final NetworkType networkType, final Integer version, final Deadline deadline, final BigInteger maxFee,
								  final PublicAccount remoteAccount, final AccountLinkAction linkAction, final String signature,
								  final PublicAccount signer, final TransactionInfo transactionInfo) {
		this(networkType, version, deadline, maxFee, remoteAccount, linkAction, Optional.of(signature), Optional.of(signer),
				Optional.of(transactionInfo));
	}

	public AccountLinkTransaction(final NetworkType networkType, final Integer version, final Deadline deadline, final BigInteger maxFee,
								  final PublicAccount remoteAccount, final AccountLinkAction linkAction) {
		this(networkType, version, deadline, maxFee, remoteAccount, linkAction, Optional.empty(), Optional.empty(), Optional.empty());
	}

	private AccountLinkTransaction(final NetworkType networkType, final Integer version, final Deadline deadline, final BigInteger maxFee,
								   final PublicAccount remoteAccount,
								   final AccountLinkAction linkAction, final Optional<String> signature, final Optional<PublicAccount> signer,
								   final Optional<TransactionInfo> transactionInfo) {
		super(TransactionType.ACCOUNT_LINK, networkType, version, deadline, maxFee, signature, signer, transactionInfo);
		Validate.notNull(remoteAccount, "remoteAccount must not be null");
		Validate.notNull(linkAction, "linkAction must not be null");
		this.remoteAccount = remoteAccount;
		this.linkAction = linkAction;
	}

	/**
	 * Creates an account link transaction.
	 *
	 * @param deadline         Deadline to include the transaction.
	 * @param maxFee           Max fee defined by the sender.
	 * @param remoteAccountKey Remote account key.
	 * @param linkAction       Link action.
	 * @param networkType      Network type.
	 * @return Account link transaction
	 */
	public static AccountLinkTransaction create(final Deadline deadline, final BigInteger maxFee, final PublicAccount remoteAccountKey,
												final AccountLinkAction linkAction, final NetworkType networkType) {
		return new AccountLinkTransaction(networkType, TransactionVersion.ACCOUNT_LINK.getValue(), deadline, maxFee,
				remoteAccountKey, linkAction);
	}

	/**
	 * Gets the public key.
	 *
	 * @return Public key.
	 */
	public PublicAccount getRemoteAccount() {
		return remoteAccount;
	}

	/**
	 * Gets the link action.
	 *
	 * @return Link action.
	 */
	public AccountLinkAction getLinkAction() {
		return linkAction;
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

		final AccountLinkTransactionBuilder txBuilder =
				AccountLinkTransactionBuilder.create(new SignatureDto(signatureBuffer),
						new KeyDto(signerBuffer), getNetworkVersion(),
						EntityTypeDto.ACCOUNT_LINK_TRANSACTION,
						new AmountDto(getFee().longValue()), new TimestampDto(getDeadline().getInstant()),
						new KeyDto(getRemoteAccount().getPublicKey().getByteBuffer()),
						AccountLinkActionDto.rawValueOf(getLinkAction().getValue()));
		return txBuilder.serialize();
	}

	/**
	 * Serialized the transaction to embedded bytes.
	 *
	 * @return bytes of the transaction.
	 */
	@Override
	byte[] generateEmbeddedBytes() {
		final EmbeddedAccountLinkTransactionBuilder txBuilder =
				EmbeddedAccountLinkTransactionBuilder.create(new KeyDto(getSignerBytes().get()), getNetworkVersion(),
						EntityTypeDto.ADDRESS_ALIAS_TRANSACTION, new KeyDto(getRemoteAccount().getPublicKey().getByteBuffer()),
						AccountLinkActionDto.rawValueOf(getLinkAction().getValue()));
		return txBuilder.serialize();
	}
}
