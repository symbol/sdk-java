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
import io.nem.sdk.api.RestrictionRepository;
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
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.sdk.model.transaction.TransactionType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;


/**
 * {@link RestrictionRepository} integration tests.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestrictionRepositoryIntegrationTest extends BaseIntegrationTest {

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void getAccountRestrictions(RepositoryType type) {

        RestrictionRepository restrictionRepository = getRepositoryFactory(type)
            .createRestrictionRepository();

        AccountRestrictions accountRestrictions1 = get(
            restrictionRepository.getAccountRestrictions(getTestAccountAddress()));

        Assertions.assertNotNull(accountRestrictions1);

        List<AccountRestrictions> list = get(
            restrictionRepository.getAccountsRestrictions(Arrays.asList(getTestAccountAddress())));

        Assertions.assertEquals(1, list.size());

        AccountRestrictions accountRestrictions2 = list.get(0);

        Assertions.assertNotNull(accountRestrictions2);

        Assertions.assertEquals(jsonHelper().print(accountRestrictions2),
            jsonHelper().print(accountRestrictions1));
    }


}
