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

import io.nem.catapult.builders.AccountOperationRestrictionModificationBuilder;
import io.nem.catapult.builders.AccountOperationRestrictionTransactionBuilder;
import io.nem.catapult.builders.AccountRestrictionModificationActionDto;
import io.nem.catapult.builders.AccountRestrictionTypeDto;
import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.EmbeddedAccountOperationRestrictionTransactionBuilder;
import io.nem.catapult.builders.EntityTypeDto;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

public class AccountOperationRestrictionModificationTransaction extends Transaction {

    private final AccountRestrictionType restrictionType;
    private final List<AccountRestrictionModification<TransactionType>> modifications;

    /**
     * public constructor
     */
    @SuppressWarnings("squid:S00107")
    public AccountOperationRestrictionModificationTransaction(
        final NetworkType networkType,
        final Integer version,
        final Deadline deadline,
        final BigInteger fee,
        final AccountRestrictionType restrictionType,
        final List<AccountRestrictionModification<TransactionType>> modifications,
        final String signature,
        final PublicAccount signer,
        final TransactionInfo transactionInfo) {
        this(
            networkType,
            version,
            deadline,
            fee,
            restrictionType,
            modifications,
            Optional.of(signature),
            Optional.of(signer),
            Optional.of(transactionInfo));
    }

    /**
     * private constructor
     */
    private AccountOperationRestrictionModificationTransaction(
        final NetworkType networkType,
        final Integer version,
        final Deadline deadline,
        final BigInteger fee,
        final AccountRestrictionType restrictionType,
        final List<AccountRestrictionModification<TransactionType>> modifications) {
        this(
            networkType,
            version,
            deadline,
            fee,
            restrictionType,
            modifications,
            Optional.empty(),
            Optional.empty(),
            Optional.empty());
    }

    /**
     * private constructor
     */
    @SuppressWarnings("squid:S00107")
    private AccountOperationRestrictionModificationTransaction(
        final NetworkType networkType,
        final Integer version,
        final Deadline deadline,
        final BigInteger fee,
        final AccountRestrictionType restrictionType,
        final List<AccountRestrictionModification<TransactionType>> modifications,
        final Optional<String> signature,
        final Optional<PublicAccount> signer,
        final Optional<TransactionInfo> transactionInfo) {
        super(
            TransactionType.ACCOUNT_PROPERTIES_ENTITY_TYPE,
            networkType,
            version,
            deadline,
            fee,
            signature,
            signer,
            transactionInfo);
        Validate.notNull(restrictionType, "RestrictionType must not be null");
        Validate.notNull(modifications, "Modifications must not be null");
        this.restrictionType = restrictionType;
        this.modifications = modifications;
    }

    /**
     * Create account operation restriction transaction object
     *
     * @return {@link AccountOperationRestrictionModificationTransaction}
     */
    public static AccountOperationRestrictionModificationTransaction create(
        Deadline deadline,
        AccountRestrictionType restrictionType,
        List<AccountRestrictionModification<TransactionType>> modifications,
        NetworkType networkType) {
        return new AccountOperationRestrictionModificationTransaction(
            networkType,
            TransactionVersion.ACCOUNT_PROPERTIES_ENTITY_TYPE.getValue(),
            deadline,
            BigInteger.ZERO,
            restrictionType,
            modifications);
    }

    /**
     * Get account restriction type
     *
     * @return {@linke AccountRestrictionType}
     */
    public AccountRestrictionType getRestrictionType() {
        return this.restrictionType;
    }

    /**
     * Get account operation restriction modifications
     *
     * @return {@link List<AccountRestrictionModification<TransactionType>>}
     */
    public List<AccountRestrictionModification<TransactionType>> getModifications() {
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

        AccountOperationRestrictionTransactionBuilder txBuilder =
            AccountOperationRestrictionTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                EntityTypeDto.ACCOUNT_OPERATION_RESTRICTION_TRANSACTION,
                new AmountDto(getFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
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
        EmbeddedAccountOperationRestrictionTransactionBuilder txBuilder =
            EmbeddedAccountOperationRestrictionTransactionBuilder.create(
                new KeyDto(getRequiredSignerBytes()),
                getNetworkVersion(),
                EntityTypeDto.ACCOUNT_OPERATION_RESTRICTION_TRANSACTION,
                AccountRestrictionTypeDto.rawValueOf(this.restrictionType.getValue()),
                getModificationBuilder());
        return txBuilder.serialize();
    }

    /**
     * Gets account restriction modification.
     *
     * @return account restriction modification.
     */
    private ArrayList<AccountOperationRestrictionModificationBuilder> getModificationBuilder() {
        final ArrayList<AccountOperationRestrictionModificationBuilder> modificationBuilder =
            new ArrayList<>(modifications.size());
        for (AccountRestrictionModification<TransactionType> accountRestrictionModification :
            modifications) {
            final AccountOperationRestrictionModificationBuilder builder =
                AccountOperationRestrictionModificationBuilder.create(
                    AccountRestrictionModificationActionDto.rawValueOf(
                        accountRestrictionModification.getModificationType().getValue()),
                    EntityTypeDto.rawValueOf(
                        (short) accountRestrictionModification.getValue().getValue()));
            modificationBuilder.add(builder);
        }
        return modificationBuilder;
    }
}
