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
    public static AccountRestrictionModification<TransactionType> createForAddress(AccountRestrictionModificationType modificationType, TransactionType transactionType) {
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
