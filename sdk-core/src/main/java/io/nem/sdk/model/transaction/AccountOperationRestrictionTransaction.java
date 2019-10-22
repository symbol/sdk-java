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

import java.util.List;

public class AccountOperationRestrictionTransaction extends Transaction {

    private final AccountRestrictionType restrictionType;
    private final List<AccountRestrictionModification<TransactionType>> modifications;

    AccountOperationRestrictionTransaction(
        AccountOperationRestrictionTransactionFactory factory) {
        super(factory);
        this.restrictionType = factory.getRestrictionType();
        this.modifications = factory.getModifications();
    }

    /**
     * Get account restriction type
     *
     * @return {@link AccountRestrictionType}
     */
    public AccountRestrictionType getRestrictionType() {
        return this.restrictionType;
    }

    /**
     * Get account operation restriction modifications
     *
     * @return list of {@link AccountRestrictionModification}
     */
    public List<AccountRestrictionModification<TransactionType>> getModifications() {
        return this.modifications;
    }
}
