/**
 * ** Copyright (c) 2016-present, ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights
 * reserved. ** ** This file is part of Catapult. ** ** Catapult is free software: you can
 * redistribute it and/or modify ** it under the terms of the GNU Lesser General Public License as
 * published by ** the Free Software Foundation, either version 3 of the License, or ** (at your
 * option) any later version. ** ** Catapult is distributed in the hope that it will be useful, **
 * but WITHOUT ANY WARRANTY; without even the implied warranty of ** MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the ** GNU Lesser General Public License for more details. ** ** You
 * should have received a copy of the GNU Lesser General Public License ** along with Catapult. If
 * not, see <http://www.gnu.org/licenses/>.
 **/

package io.nem.catapult.builders;

import java.io.DataInput;
import java.util.ArrayList;

/**
 * Binary layout for a non-embedded account operation restriction transaction.
 */
public final class AccountOperationRestrictionTransactionBuilder extends TransactionBuilder {

    /**
     * Account operation restriction transaction body.
     */
    private final AccountOperationRestrictionTransactionBodyBuilder accountOperationRestrictionTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected AccountOperationRestrictionTransactionBuilder(final DataInput stream) {
        super(stream);
        this.accountOperationRestrictionTransactionBody = AccountOperationRestrictionTransactionBodyBuilder
            .loadFromBinary(stream);
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
    protected AccountOperationRestrictionTransactionBuilder(final SignatureDto signature,
        final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee,
        final TimestampDto deadline, final AccountRestrictionTypeDto restrictionType,
        final ArrayList<AccountOperationRestrictionModificationBuilder> modifications) {
        super(signature, signer, version, type, fee, deadline);
        this.accountOperationRestrictionTransactionBody = AccountOperationRestrictionTransactionBodyBuilder
            .create(restrictionType, modifications);
    }

    /**
     * Creates an instance of AccountOperationRestrictionTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param restrictionType Account restriction type.
     * @param modifications Account restriction modifications.
     * @return Instance of AccountOperationRestrictionTransactionBuilder.
     */
    public static AccountOperationRestrictionTransactionBuilder create(final SignatureDto signature,
        final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee,
        final TimestampDto deadline, final AccountRestrictionTypeDto restrictionType,
        final ArrayList<AccountOperationRestrictionModificationBuilder> modifications) {
        return new AccountOperationRestrictionTransactionBuilder(signature, signer, version, type,
            fee, deadline, restrictionType, modifications);
    }

    /**
     * Creates an instance of AccountOperationRestrictionTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of AccountOperationRestrictionTransactionBuilder.
     */
    public static AccountOperationRestrictionTransactionBuilder loadFromBinary(
        final DataInput stream) {
        return new AccountOperationRestrictionTransactionBuilder(stream);
    }

    /**
     * Gets account restriction type.
     *
     * @return Account restriction type.
     */
    public AccountRestrictionTypeDto getRestrictionType() {
        return this.accountOperationRestrictionTransactionBody.getRestrictionType();
    }

    /**
     * Gets account restriction modifications.
     *
     * @return Account restriction modifications.
     */
    public ArrayList<AccountOperationRestrictionModificationBuilder> getModifications() {
        return this.accountOperationRestrictionTransactionBody.getModifications();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.accountOperationRestrictionTransactionBody.getSize();
        return size;
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
            final byte[] accountOperationRestrictionTransactionBodyBytes = this.accountOperationRestrictionTransactionBody
                .serialize();
            dataOutputStream.write(accountOperationRestrictionTransactionBodyBytes, 0,
                accountOperationRestrictionTransactionBodyBytes.length);
        });
    }
}
