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

/** Binary layout for an account properties transaction type transaction. */
final class TransactionTypePropertyTransactionBodyBuilder {
    /** Property type. */
    private final PropertyTypeDto propertyType;
    /** Property modifications. */
    private final ArrayList<TransactionTypePropertyModificationBuilder> modifications;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected TransactionTypePropertyTransactionBodyBuilder(final DataInput stream) {
        try {
            this.propertyType = PropertyTypeDto.loadFromBinary(stream);
            final byte modificationsCount = stream.readByte();
            this.modifications = new java.util.ArrayList<>(modificationsCount);
            for (int i = 0; i < modificationsCount; i++) {
                modifications.add(TransactionTypePropertyModificationBuilder.loadFromBinary(stream));
            }
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Constructor.
     *
     * @param propertyType Property type.
     * @param modifications Property modifications.
     */
    protected TransactionTypePropertyTransactionBodyBuilder(final PropertyTypeDto propertyType, final ArrayList<TransactionTypePropertyModificationBuilder> modifications) {
        GeneratorUtils.notNull(propertyType, "propertyType is null");
        GeneratorUtils.notNull(modifications, "modifications is null");
        this.propertyType = propertyType;
        this.modifications = modifications;
    }

    /**
     * Creates an instance of TransactionTypePropertyTransactionBodyBuilder.
     *
     * @param propertyType Property type.
     * @param modifications Property modifications.
     * @return Instance of TransactionTypePropertyTransactionBodyBuilder.
     */
    public static TransactionTypePropertyTransactionBodyBuilder create(final PropertyTypeDto propertyType, final ArrayList<TransactionTypePropertyModificationBuilder> modifications) {
        return new TransactionTypePropertyTransactionBodyBuilder(propertyType, modifications);
    }

    /**
     * Gets property type.
     *
     * @return Property type.
     */
    public PropertyTypeDto getPropertyType() {
        return this.propertyType;
    }

    /**
     * Gets property modifications.
     *
     * @return Property modifications.
     */
    public ArrayList<TransactionTypePropertyModificationBuilder> getModifications() {
        return this.modifications;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.propertyType.getSize();
        size += 1; // modificationsCount
        size += this.modifications.stream().mapToInt(o -> o.getSize()).sum();
        return size;
    }

    /**
     * Creates an instance of TransactionTypePropertyTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of TransactionTypePropertyTransactionBodyBuilder.
     */
    public static TransactionTypePropertyTransactionBodyBuilder loadFromBinary(final DataInput stream) {
        return new TransactionTypePropertyTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] propertyTypeBytes = this.propertyType.serialize();
            dataOutputStream.write(propertyTypeBytes, 0, propertyTypeBytes.length);
            dataOutputStream.writeByte((byte) this.modifications.size());
            for (int i = 0; i < this.modifications.size(); i++) {
                final byte[] modificationsBytes = this.modifications.get(i).serialize();
                dataOutputStream.write(modificationsBytes, 0, modificationsBytes.length);
            }
        });
    }
}
