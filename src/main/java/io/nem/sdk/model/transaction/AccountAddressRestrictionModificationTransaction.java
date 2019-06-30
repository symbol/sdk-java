/**
 *** Copyright (c) 2016-present,
 *** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
 ***
 *** This file is part of Catapult.
 ***
 *** Catapult is free software: you can redistribute it and/or modify
 *** it under the terms of the GNU Lesser General Public License as published by
 *** the Free Software Foundation, either version 3 of the License, or
 *** (at your option) any later version.
 ***
 *** Catapult is distributed in the hope that it will be useful,
 *** but WITHOUT ANY WARRANTY; without even the implied warranty of
 *** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *** GNU Lesser General Public License for more details.
 ***
 *** You should have received a copy of the GNU Lesser General Public License
 *** along with Catapult. If not, see <http://www.gnu.org/licenses/>.
 **/
package io.nem.sdk.model.transaction;

import io.nem.catapult.builders.*;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import org.apache.commons.lang.Validate;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountAddressRestrictionModificationTransaction extends Transaction {
    private final AccountRestrictionType restrictionType;
    private final List<AccountRestrictionModification<Address>> modifications;

    /**
     * public constructor
     * @param networkType
     * @param version
     * @param deadline
     * @param fee
     * @param restrictionType
     * @param modifications
     * @param signature
     * @param signer
     * @param transactionInfo
     */
    public AccountAddressRestrictionModificationTransaction(final NetworkType networkType, final Integer version, final Deadline deadline, final BigInteger fee,
                                                            final AccountRestrictionType restrictionType, final List<AccountRestrictionModification<Address>> modifications,
                                                            final String signature, final PublicAccount signer, final TransactionInfo transactionInfo) {
        this(networkType, version, deadline, fee, restrictionType, modifications, Optional.of(signature),
                Optional.of(signer), Optional.of(transactionInfo));
    }

    /**
     * private constructor
     * @param networkType
     * @param version
     * @param deadline
     * @param fee
     * @param restrictionType
     * @param modifications
     */
    private AccountAddressRestrictionModificationTransaction(final NetworkType networkType, final Integer version, final Deadline deadline, final BigInteger fee,
                                                             final AccountRestrictionType restrictionType, final List<AccountRestrictionModification<Address>> modifications) {
        this(networkType, version, deadline, fee, restrictionType, modifications, Optional.empty(), Optional.empty(),
                Optional.empty());
    }

    /**
     * private constructor
     * @param networkType
     * @param version
     * @param deadline
     * @param fee
     * @param restrictionType
     * @param modifications
     * @param signature
     * @param signer
     * @param transactionInfo
     */
    private AccountAddressRestrictionModificationTransaction(final NetworkType networkType, final Integer version, final Deadline deadline, final BigInteger fee,
                                                             final AccountRestrictionType restrictionType, final List<AccountRestrictionModification<Address>> modifications,
                                                             final Optional<String> signature, final Optional<PublicAccount> signer,
                                                             final Optional<TransactionInfo> transactionInfo) {
        super(TransactionType.ACCOUNT_PROPERTIES_ADDRESS, networkType, version, deadline, fee, signature, signer, transactionInfo);
        Validate.notNull(restrictionType, "RestrictionType must not be null");
        Validate.notNull(modifications, "Modifications must not be null");
        this.restrictionType = restrictionType;
        this.modifications = modifications;
    }

    /**
     * Create account address restriction transaction object
     * @param deadline
     * @param restrictionType
     * @param modifications
     * @param networkType
     * @return {@link AccountAddressRestrictionModificationTransaction}
     */
    public static AccountAddressRestrictionModificationTransaction create(Deadline deadline,
                                                                          AccountRestrictionType restrictionType,
                                                                          List<AccountRestrictionModification<Address>> modifications,
                                                                          NetworkType networkType) {
        return new AccountAddressRestrictionModificationTransaction(networkType, TransactionVersion.ACCOUNT_PROPERTIES_ADDRESS.getValue(), deadline,
                        BigInteger.ZERO, restrictionType, modifications);
    }

    /**
     * Get account restriction type
     * @return {@linke AccountRestrictionType}
     */
    public AccountRestrictionType getRestrictionType() {
        return this.restrictionType;
    }

    /**
     * Get account address restriction modifications
     * @return {@link List<AccountRestrictionModification<Address>>}
     */
    public List<AccountRestrictionModification<Address>> getModifications() {
        return this.modifications;
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

        AccountAddressRestrictionTransactionBuilder txBuilder =
                AccountAddressRestrictionTransactionBuilder.create(new SignatureDto(signatureBuffer),
                        new KeyDto(signerBuffer), getNetworkVersion(),
                        EntityTypeDto.ACCOUNT_ADDRESS_RESTRICTION_TRANSACTION,
                        new AmountDto(getFee().longValue()), new TimestampDto(getDeadline().getInstant()),
                        AccountRestrictionTypeDto.rawValueOf(this.restrictionType.getValue()),
                        getModificationBuilder());
        return txBuilder.serialize();
    }

    /**
     * Gets the embedded tx bytes.
     *
     * @return Embedded tx bytes
     */
    byte[] generateEmbeddedBytes() {
        EmbeddedAccountAddressRestrictionTransactionBuilder txBuilder =
                EmbeddedAccountAddressRestrictionTransactionBuilder.create(new KeyDto(getSignerBytes().get()), getNetworkVersion(),
                        EntityTypeDto.ACCOUNT_ADDRESS_RESTRICTION_TRANSACTION,
                        AccountRestrictionTypeDto.rawValueOf(this.restrictionType.getValue()), getModificationBuilder());
        return txBuilder.serialize();
    }

    /**
     * Gets account restriction modification.
     * @return account restriction modification.
     */
    private ArrayList<AccountAddressRestrictionModificationBuilder> getModificationBuilder() {
        final ArrayList<AccountAddressRestrictionModificationBuilder> modificationBuilder = new ArrayList<>(modifications.size());
        for (AccountRestrictionModification<Address> accountRestrictionModification : modifications) {
            final ByteBuffer addressByteBuffer =
                    accountRestrictionModification.getValue().getByteBuffer();
            final AccountAddressRestrictionModificationBuilder builder =
                    AccountAddressRestrictionModificationBuilder.create(
                            AccountRestrictionModificationTypeDto.rawValueOf(accountRestrictionModification.getModificationType().getValue()),
                            new AddressDto(addressByteBuffer)
                    );
            modificationBuilder.add(builder);
        }
        return modificationBuilder;
    }
}
