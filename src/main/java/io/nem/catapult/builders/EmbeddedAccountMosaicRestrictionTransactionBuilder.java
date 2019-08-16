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
 * Binary layout for an embedded account mosaic restriction transaction.
 */
public final class EmbeddedAccountMosaicRestrictionTransactionBuilder extends
    EmbeddedTransactionBuilder {

    /**
     * Account mosaic restriction transaction body.
     */
    private final AccountMosaicRestrictionTransactionBodyBuilder accountMosaicRestrictionTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected EmbeddedAccountMosaicRestrictionTransactionBuilder(final DataInput stream) {
        super(stream);
        this.accountMosaicRestrictionTransactionBody = AccountMosaicRestrictionTransactionBodyBuilder
            .loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param restrictionType Account restriction type.
     * @param modifications Account restriction modifications.
     */
    protected EmbeddedAccountMosaicRestrictionTransactionBuilder(final KeyDto signer,
        final short version, final EntityTypeDto type,
        final AccountRestrictionTypeDto restrictionType,
        final ArrayList<AccountMosaicRestrictionModificationBuilder> modifications) {
        super(signer, version, type);
        this.accountMosaicRestrictionTransactionBody = AccountMosaicRestrictionTransactionBodyBuilder
            .create(restrictionType, modifications);
    }

    /**
     * Creates an instance of EmbeddedAccountMosaicRestrictionTransactionBuilder.
     *
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param restrictionType Account restriction type.
     * @param modifications Account restriction modifications.
     * @return Instance of EmbeddedAccountMosaicRestrictionTransactionBuilder.
     */
    public static EmbeddedAccountMosaicRestrictionTransactionBuilder create(final KeyDto signer,
        final short version, final EntityTypeDto type,
        final AccountRestrictionTypeDto restrictionType,
        final ArrayList<AccountMosaicRestrictionModificationBuilder> modifications) {
        return new EmbeddedAccountMosaicRestrictionTransactionBuilder(signer, version, type,
            restrictionType, modifications);
    }

    /**
     * Creates an instance of EmbeddedAccountMosaicRestrictionTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EmbeddedAccountMosaicRestrictionTransactionBuilder.
     */
    public static EmbeddedAccountMosaicRestrictionTransactionBuilder loadFromBinary(
        final DataInput stream) {
        return new EmbeddedAccountMosaicRestrictionTransactionBuilder(stream);
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
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] superBytes = super.serialize();
            dataOutputStream.write(superBytes, 0, superBytes.length);
            final byte[] accountMosaicRestrictionTransactionBodyBytes = this.accountMosaicRestrictionTransactionBody
                .serialize();
            dataOutputStream.write(accountMosaicRestrictionTransactionBodyBytes, 0,
                accountMosaicRestrictionTransactionBodyBytes.length);
        });
    }
}
