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

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;

public class AccountRestrictionModification<T> {
    private final AccountRestrictionModificationType modificationType;
    private final T value;

    private AccountRestrictionModification(AccountRestrictionModificationType modificationType, T value) {
        this.modificationType = modificationType;
        this.value = value;
    }

    /**
     *
     * @param modificationType
     * @param address
     * @return AccountRestrictionModification<Address>
     */
    public static AccountRestrictionModification<Address> createForAddress(AccountRestrictionModificationType modificationType, Address address) {
        return new AccountRestrictionModification(modificationType, address);
    }

    /**
     *
     * @param modificationType
     * @param mosaicId
     * @return AccountRestrictionModification<MosaicId>
     */
    public static AccountRestrictionModification<Address> createForMosaic(AccountRestrictionModificationType modificationType, MosaicId mosaicId) {
        return new AccountRestrictionModification(modificationType, mosaicId);
    }

    /**
     *
     * @param modificationType
     * @param transactionType
     * @return AccountRestrictionModification<TransactionType>
     */
    public static AccountRestrictionModification<TransactionType> createForEntityType(AccountRestrictionModificationType modificationType, TransactionType transactionType) {
        return new AccountRestrictionModification(modificationType, transactionType);
    }

    /**
     * Get modification value
     * @return value
     */
    public T getValue() {
        return value;
    }

    /**
     * Get modification type
     * @return AccountRestrictionModificationType
     */
    public AccountRestrictionModificationType getModificationType() {
        return this.modificationType;
    }
}
