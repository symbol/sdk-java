/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountRestrictions;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountAddressRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountMosaicRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountOperationRestrictionTransaction;
import io.nem.sdk.model.transaction.AccountOperationRestrictionTransactionFactory;
import io.nem.sdk.model.transaction.AccountRestrictionModification;
import io.nem.sdk.model.transaction.AccountRestrictionModificationAction;
import io.nem.sdk.model.transaction.AccountRestrictionType;
import io.nem.sdk.model.transaction.TransactionType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountRestrictionIntegrationTest extends BaseIntegrationTest {


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void addAndRemoveTransactionRestriction(RepositoryType type) {

        AccountRestrictionType restrictionType = AccountRestrictionType.BLOCK_OUTGOING_TRANSACTION_TYPE;
        TransactionType transactionType = TransactionType.SECRET_PROOF;

        Account testAccount = getTestAccount();

        Assertions.assertNotNull(get(getRepositoryFactory(type).createAccountRepository()
            .getAccountInfo(testAccount.getAddress())));

        if (hasRestriction(type, testAccount, restrictionType, transactionType)) {
            System.out.println("Removing existing transaction restriction!");
            sendAccountRestrictionTransaction(type, AccountRestrictionModification
                    .createForTransactionType(AccountRestrictionModificationAction.REMOVE,
                        transactionType),
                restrictionType);
            Assertions
                .assertFalse(hasRestriction(type, testAccount, restrictionType, transactionType));
        }

        System.out.println("Adding transaction restriction");
        sendAccountRestrictionTransaction(type, AccountRestrictionModification
                .createForTransactionType(AccountRestrictionModificationAction.ADD,
                    transactionType),
            restrictionType);

        Assertions.assertTrue(hasRestriction(type, testAccount, restrictionType, transactionType));

        System.out.println("Removing transaction restriction");
        sendAccountRestrictionTransaction(type, AccountRestrictionModification
                .createForTransactionType(AccountRestrictionModificationAction.REMOVE,
                    transactionType),
            restrictionType);

        Assertions.assertFalse(hasRestriction(type, testAccount, restrictionType, transactionType));

    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void addAndRemoveMosaicRestriction(RepositoryType type) {

        AccountRestrictionType restrictionType = AccountRestrictionType.ALLOW_INCOMING_MOSAIC;

        Account testAccount = getTestAccount();

        MosaicNonce nonce = MosaicNonce.createRandom();
        MosaicId mosaicId = MosaicId.createFromNonce(nonce, testAccount.getPublicAccount());

        Assertions.assertNotNull(get(getRepositoryFactory(type).createAccountRepository()
            .getAccountInfo(testAccount.getAddress())));

        if (hasRestriction(type, testAccount, restrictionType, mosaicId)) {
            System.out.println("Removing existing mosaic restriction!");
            sendAccountRestrictionMosaic(type, AccountRestrictionModification
                    .createForMosaic(AccountRestrictionModificationAction.REMOVE,
                        mosaicId),
                restrictionType);
            Assertions.assertFalse(hasRestriction(type, testAccount, restrictionType, mosaicId));
        }

        System.out.println("Adding mosaic restriction");
        sendAccountRestrictionMosaic(type, AccountRestrictionModification
                .createForMosaic(AccountRestrictionModificationAction.ADD,
                    mosaicId),
            restrictionType);

        Assertions.assertTrue(hasRestriction(type, testAccount, restrictionType, mosaicId));

        System.out.println("Removing mosaic restriction");
        sendAccountRestrictionMosaic(type, AccountRestrictionModification
                .createForMosaic(AccountRestrictionModificationAction.REMOVE,
                    mosaicId),
            restrictionType);

        Assertions.assertFalse(hasRestriction(type, testAccount, restrictionType, mosaicId));

    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void addAndRemoveAddressRestriction(RepositoryType type) {

        AccountRestrictionType restrictionType = AccountRestrictionType.ALLOW_OUTGOING_ADDRESS;
        Address address = getRecipient();

        Account testAccount = getTestAccount();

        Assertions.assertNotNull(get(getRepositoryFactory(type).createAccountRepository()
            .getAccountInfo(testAccount.getAddress())));

        if (hasRestriction(type, testAccount, restrictionType, address)) {
            System.out.println("Removing existing address restriction!");
            sendAccountRestrictionAddress(type, AccountRestrictionModification
                    .createForAddress(AccountRestrictionModificationAction.REMOVE,
                        address),
                restrictionType);
            Assertions.assertFalse(hasRestriction(type, testAccount, restrictionType, address));
        }

        System.out.println("Adding address restriction");
        sendAccountRestrictionAddress(type, AccountRestrictionModification
                .createForAddress(AccountRestrictionModificationAction.ADD,
                    address),
            restrictionType);

        Assertions.assertTrue(hasRestriction(type, testAccount, restrictionType,
            address));

        System.out.println("Removing address restriction");
        sendAccountRestrictionAddress(type, AccountRestrictionModification
                .createForAddress(AccountRestrictionModificationAction.REMOVE,
                    address),
            restrictionType);

        Assertions.assertFalse(hasRestriction(type, testAccount, restrictionType, address));

    }


    private boolean hasRestriction(RepositoryType type, Account testAccount,
        AccountRestrictionType restrictionType, Object value) {
        try {
            Thread.sleep(2000);//Need to wait?
            AccountRestrictions restrictions = get(
                getRepositoryFactory(type).createRestrictionRepository()
                    .getAccountRestrictions(testAccount.getAddress()));
            Assertions.assertEquals(testAccount.getAddress(), restrictions.getAddress());

            System.out.println("Current Restrictions: " + jsonHelper().print(restrictions));
            return restrictions.getRestrictions().stream().anyMatch(
                r -> r.getRestrictionType()
                    .equals(restrictionType) && r.getValues()
                    .contains(value));
        } catch (RepositoryCallException | InterruptedException e) {
            //If it fails, it's because is a new account.
            Assertions.assertEquals(
                "ApiException: Not Found - 404 - ResourceNotFound - no resource exists with id '"
                    + testAccount.getAddress().plain() + "'", e.getMessage());
            return false;
        }


    }

    private void sendAccountRestrictionTransaction(RepositoryType type,
        AccountRestrictionModification<TransactionType> modification,
        AccountRestrictionType accountRestrictionType) {

        Account testAccount = getTestAccount();
        List<AccountRestrictionModification<TransactionType>> modifications = new ArrayList<>();
        modifications.add(modification);
        AccountOperationRestrictionTransaction transaction =
            new AccountOperationRestrictionTransactionFactory(
                getNetworkType(),
                accountRestrictionType
                , modifications
            ).build();

        AccountOperationRestrictionTransaction processedTransaction = announceAndValidate(type,
            testAccount, transaction);

        Assertions.assertEquals(accountRestrictionType, processedTransaction.getRestrictionType());
        Assertions.assertEquals(modification.getModificationAction(),
            processedTransaction.getModifications().get(0).getModificationAction());
        Assertions.assertEquals(modification.getValue(),
            processedTransaction.getModifications().get(0).getValue());
    }

    private void sendAccountRestrictionMosaic(RepositoryType type,
        AccountRestrictionModification<MosaicId> modification,
        AccountRestrictionType accountRestrictionType) {

        Account testAccount = getTestAccount();
        List<AccountRestrictionModification<MosaicId>> modifications = new ArrayList<>();
        modifications.add(modification);
        AccountMosaicRestrictionTransaction transaction =
            new AccountMosaicRestrictionTransactionFactory(
                getNetworkType(),
                accountRestrictionType
                , modifications
            ).build();

        AccountMosaicRestrictionTransaction processedTransaction = announceAndValidate(type,
            testAccount, transaction);

        Assertions.assertEquals(accountRestrictionType, processedTransaction.getRestrictionType());
        Assertions.assertEquals(modification.getModificationAction(),
            processedTransaction.getModifications().get(0).getModificationAction());
        Assertions.assertEquals(modification.getValue(),
            processedTransaction.getModifications().get(0).getValue());
    }

    private void sendAccountRestrictionAddress(RepositoryType type,
        AccountRestrictionModification<Address> modification,
        AccountRestrictionType accountRestrictionType) {

        Account testAccount = getTestAccount();
        List<AccountRestrictionModification<Address>> modifications = new ArrayList<>();
        modifications.add(modification);
        AccountAddressRestrictionTransaction transaction =
            new AccountAddressRestrictionTransactionFactory(
                getNetworkType(),
                accountRestrictionType
                , modifications
            ).build();

        AccountAddressRestrictionTransaction processedTransaction = announceAndValidate(type,
            testAccount, transaction);

        Assertions.assertEquals(accountRestrictionType, processedTransaction.getRestrictionType());
        Assertions.assertEquals(modification.getModificationAction(),
            processedTransaction.getModifications().get(0).getModificationAction());
        Assertions.assertEquals(modification.getValue(),
            processedTransaction.getModifications().get(0).getValue());

    }


}
