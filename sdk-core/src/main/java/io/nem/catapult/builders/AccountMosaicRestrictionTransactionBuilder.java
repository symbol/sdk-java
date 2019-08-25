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

package io.nem.catapult.builders;

import java.io.DataInput;
import java.util.ArrayList;

/** Binary layout for a non-embedded account mosaic restriction transaction. */
public final class AccountMosaicRestrictionTransactionBuilder extends TransactionBuilder {
    /** Account mosaic restriction transaction body. */
    private final AccountMosaicRestrictionTransactionBodyBuilder accountMosaicRestrictionTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected AccountMosaicRestrictionTransactionBuilder(final DataInput stream) {
        super(stream);
        this.accountMosaicRestrictionTransactionBody = AccountMosaicRestrictionTransactionBodyBuilder.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signature Entity signature.
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param restrictionType Account restriction type.
     * @param modifications Account restriction modifications.
     */
    protected AccountMosaicRestrictionTransactionBuilder(final SignatureDto signature, final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final AccountRestrictionTypeDto restrictionType, final ArrayList<AccountMosaicRestrictionModificationBuilder> modifications) {
        super(signature, signer, version, type, fee, deadline);
        this.accountMosaicRestrictionTransactionBody = AccountMosaicRestrictionTransactionBodyBuilder.create(restrictionType, modifications);
    }

    /**
     * Creates an instance of AccountMosaicRestrictionTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param restrictionType Account restriction type.
     * @param modifications Account restriction modifications.
     * @return Instance of AccountMosaicRestrictionTransactionBuilder.
     */
    public static AccountMosaicRestrictionTransactionBuilder create(final SignatureDto signature, final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final AccountRestrictionTypeDto restrictionType, final ArrayList<AccountMosaicRestrictionModificationBuilder> modifications) {
        return new AccountMosaicRestrictionTransactionBuilder(signature, signer, version, type, fee, deadline, restrictionType, modifications);
    }

    /**
     * Gets account restriction type.
     *
     * @return Account restriction type.
     */
    public AccountRestrictionTypeDto getRestrictionType() {
        return this.accountMosaicRestrictionTransactionBody.getRestrictionType();
    }

    /**
     * Gets account restriction modifications.
     *
     * @return Account restriction modifications.
     */
    public ArrayList<AccountMosaicRestrictionModificationBuilder> getModifications() {
        return this.accountMosaicRestrictionTransactionBody.getModifications();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.accountMosaicRestrictionTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of AccountMosaicRestrictionTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of AccountMosaicRestrictionTransactionBuilder.
     */
    public static AccountMosaicRestrictionTransactionBuilder loadFromBinary(final DataInput stream) {
        return new AccountMosaicRestrictionTransactionBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] superBytes = super.serialize();
            dataOutputStream.write(superBytes, 0, superBytes.length);
            final byte[] accountMosaicRestrictionTransactionBodyBytes = this.accountMosaicRestrictionTransactionBody.serialize();
            dataOutputStream.write(accountMosaicRestrictionTransactionBodyBytes, 0, accountMosaicRestrictionTransactionBodyBytes.length);
        });
    }
}
