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

/** Binary layout for a non-embedded account properties transaction type transaction. */
public final class TransactionTypePropertyTransactionBuilder extends TransactionBuilder {
    /** Transaction type property transaction body. */
    private final TransactionTypePropertyTransactionBodyBuilder transactionTypePropertyTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected TransactionTypePropertyTransactionBuilder(final DataInput stream) {
        super(stream);
        this.transactionTypePropertyTransactionBody = TransactionTypePropertyTransactionBodyBuilder.loadFromBinary(stream);
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
     * @param propertyType Property type.
     * @param modifications Property modifications.
     */
    protected TransactionTypePropertyTransactionBuilder(final SignatureDto signature, final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final PropertyTypeDto propertyType, final ArrayList<TransactionTypePropertyModificationBuilder> modifications) {
        super(signature, signer, version, type, fee, deadline);
        this.transactionTypePropertyTransactionBody = TransactionTypePropertyTransactionBodyBuilder.create(propertyType, modifications);
    }

    /**
     * Creates an instance of TransactionTypePropertyTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param propertyType Property type.
     * @param modifications Property modifications.
     * @return Instance of TransactionTypePropertyTransactionBuilder.
     */
    public static TransactionTypePropertyTransactionBuilder create(final SignatureDto signature, final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final PropertyTypeDto propertyType, final ArrayList<TransactionTypePropertyModificationBuilder> modifications) {
        return new TransactionTypePropertyTransactionBuilder(signature, signer, version, type, fee, deadline, propertyType, modifications);
    }

    /**
     * Gets property type.
     *
     * @return Property type.
     */
    public PropertyTypeDto getPropertyType() {
        return this.transactionTypePropertyTransactionBody.getPropertyType();
    }

    /**
     * Gets property modifications.
     *
     * @return Property modifications.
     */
    public ArrayList<TransactionTypePropertyModificationBuilder> getModifications() {
        return this.transactionTypePropertyTransactionBody.getModifications();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.transactionTypePropertyTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of TransactionTypePropertyTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of TransactionTypePropertyTransactionBuilder.
     */
    public static TransactionTypePropertyTransactionBuilder loadFromBinary(final DataInput stream) {
        return new TransactionTypePropertyTransactionBuilder(stream);
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
            final byte[] transactionTypePropertyTransactionBodyBytes = this.transactionTypePropertyTransactionBody.serialize();
            dataOutputStream.write(transactionTypePropertyTransactionBodyBytes, 0, transactionTypePropertyTransactionBodyBytes.length);
        });
    }
}
