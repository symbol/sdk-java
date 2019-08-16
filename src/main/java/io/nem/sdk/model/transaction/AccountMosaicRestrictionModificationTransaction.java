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

import io.nem.catapult.builders.AccountMosaicRestrictionModificationBuilder;
import io.nem.catapult.builders.AccountMosaicRestrictionTransactionBuilder;
import io.nem.catapult.builders.AccountRestrictionModificationActionDto;
import io.nem.catapult.builders.AccountRestrictionTypeDto;
import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.EmbeddedAccountMosaicRestrictionTransactionBuilder;
import io.nem.catapult.builders.EntityTypeDto;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang.Validate;

public class AccountMosaicRestrictionModificationTransaction extends Transaction {

    private final AccountRestrictionType restrictionType;
    private final List<AccountRestrictionModification<MosaicId>> modifications;

    /**
     * public constructor
     */
    public AccountMosaicRestrictionModificationTransaction(
        final NetworkType networkType,
        final Integer version,
        final Deadline deadline,
        final BigInteger fee,
        final AccountRestrictionType restrictionType,
        final List<AccountRestrictionModification<MosaicId>> modifications,
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
    private AccountMosaicRestrictionModificationTransaction(
        final NetworkType networkType,
        final Integer version,
        final Deadline deadline,
        final BigInteger fee,
        final AccountRestrictionType restrictionType,
        final List<AccountRestrictionModification<MosaicId>> modifications) {
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
    private AccountMosaicRestrictionModificationTransaction(
        final NetworkType networkType,
        final Integer version,
        final Deadline deadline,
        final BigInteger fee,
        final AccountRestrictionType restrictionType,
        final List<AccountRestrictionModification<MosaicId>> modifications,
        final Optional<String> signature,
        final Optional<PublicAccount> signer,
        final Optional<TransactionInfo> transactionInfo) {
        super(
            TransactionType.ACCOUNT_PROPERTIES_MOSAIC,
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
     * Create account mosaic restriction transaction object
     *
     * @return {@link AccountMosaicRestrictionModificationTransaction}
     */
    public static AccountMosaicRestrictionModificationTransaction create(
        Deadline deadline,
        AccountRestrictionType restrictionType,
        List<AccountRestrictionModification<MosaicId>> modifications,
        NetworkType networkType) {
        return new AccountMosaicRestrictionModificationTransaction(
            networkType,
            TransactionVersion.ACCOUNT_PROPERTIES_MOSAIC.getValue(),
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
     * Get account mosaic restriction modifications
     *
     * @return {@link List<AccountRestrictionModification<MosaicId>>}
     */
    public List<AccountRestrictionModification<MosaicId>> getModifications() {
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

        AccountMosaicRestrictionTransactionBuilder txBuilder =
            AccountMosaicRestrictionTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                EntityTypeDto.ACCOUNT_MOSAIC_RESTRICTION_TRANSACTION,
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
        EmbeddedAccountMosaicRestrictionTransactionBuilder txBuilder =
            EmbeddedAccountMosaicRestrictionTransactionBuilder.create(
                new KeyDto(getSignerBytes().get()),
                getNetworkVersion(),
                EntityTypeDto.ACCOUNT_MOSAIC_RESTRICTION_TRANSACTION,
                AccountRestrictionTypeDto.rawValueOf(this.restrictionType.getValue()),
                getModificationBuilder());
        return txBuilder.serialize();
    }

    /**
     * Gets account restriction modification.
     *
     * @return account restriction modification.
     */
    private ArrayList<AccountMosaicRestrictionModificationBuilder> getModificationBuilder() {
        final ArrayList<AccountMosaicRestrictionModificationBuilder> modificationBuilder =
            new ArrayList<>(modifications.size());
        for (AccountRestrictionModification<MosaicId> accountRestrictionModification : modifications) {
            final AccountMosaicRestrictionModificationBuilder builder =
                AccountMosaicRestrictionModificationBuilder.create(
                    AccountRestrictionModificationActionDto.rawValueOf(
                        accountRestrictionModification.getModificationType().getValue()),
                    new UnresolvedMosaicIdDto(
                        accountRestrictionModification.getValue().getIdAsLong()));
            modificationBuilder.add(builder);
        }
        return modificationBuilder;
    }
}
