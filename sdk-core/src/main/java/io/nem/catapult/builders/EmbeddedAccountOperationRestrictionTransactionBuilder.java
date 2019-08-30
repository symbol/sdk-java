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

/** Binary layout for an embedded account operation restriction transaction. */
public final class EmbeddedAccountOperationRestrictionTransactionBuilder extends EmbeddedTransactionBuilder {
    /** Account operation restriction transaction body. */
    private final AccountOperationRestrictionTransactionBodyBuilder accountOperationRestrictionTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected EmbeddedAccountOperationRestrictionTransactionBuilder(final DataInput stream) {
        super(stream);
        this.accountOperationRestrictionTransactionBody = AccountOperationRestrictionTransactionBodyBuilder.loadFromBinary(stream);
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
    protected EmbeddedAccountOperationRestrictionTransactionBuilder(final KeyDto signer, final short version, final EntityTypeDto type, final AccountRestrictionTypeDto restrictionType, final ArrayList<AccountOperationRestrictionModificationBuilder> modifications) {
        super(signer, version, type);
        this.accountOperationRestrictionTransactionBody = AccountOperationRestrictionTransactionBodyBuilder.create(restrictionType, modifications);
    }

    /**
     * Creates an instance of EmbeddedAccountOperationRestrictionTransactionBuilder.
     *
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param restrictionType Account restriction type.
     * @param modifications Account restriction modifications.
     * @return Instance of EmbeddedAccountOperationRestrictionTransactionBuilder.
     */
    public static EmbeddedAccountOperationRestrictionTransactionBuilder create(final KeyDto signer, final short version, final EntityTypeDto type, final AccountRestrictionTypeDto restrictionType, final ArrayList<AccountOperationRestrictionModificationBuilder> modifications) {
        return new EmbeddedAccountOperationRestrictionTransactionBuilder(signer, version, type, restrictionType, modifications);
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
     * Creates an instance of EmbeddedAccountOperationRestrictionTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EmbeddedAccountOperationRestrictionTransactionBuilder.
     */
    public static EmbeddedAccountOperationRestrictionTransactionBuilder loadFromBinary(final DataInput stream) {
        return new EmbeddedAccountOperationRestrictionTransactionBuilder(stream);
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
            final byte[] accountOperationRestrictionTransactionBodyBytes = this.accountOperationRestrictionTransactionBody.serialize();
            dataOutputStream.write(accountOperationRestrictionTransactionBodyBytes, 0, accountOperationRestrictionTransactionBodyBytes.length);
        });
    }
}
