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

/** Binary layout for an embedded account properties mosaic transaction. */
public final class EmbeddedMosaicPropertyTransactionBuilder extends EmbeddedTransactionBuilder {
    /** Mosaic property transaction body. */
    private final MosaicPropertyTransactionBodyBuilder mosaicPropertyTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected EmbeddedMosaicPropertyTransactionBuilder(final DataInput stream) {
        super(stream);
        this.mosaicPropertyTransactionBody = MosaicPropertyTransactionBodyBuilder.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param propertyType Property type.
     * @param modifications Property modifications.
     */
    protected EmbeddedMosaicPropertyTransactionBuilder(final KeyDto signer, final short version, final EntityTypeDto type, final PropertyTypeDto propertyType, final ArrayList<MosaicPropertyModificationBuilder> modifications) {
        super(signer, version, type);
        this.mosaicPropertyTransactionBody = MosaicPropertyTransactionBodyBuilder.create(propertyType, modifications);
    }

    /**
     * Creates an instance of EmbeddedMosaicPropertyTransactionBuilder.
     *
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param propertyType Property type.
     * @param modifications Property modifications.
     * @return Instance of EmbeddedMosaicPropertyTransactionBuilder.
     */
    public static EmbeddedMosaicPropertyTransactionBuilder create(final KeyDto signer, final short version, final EntityTypeDto type, final PropertyTypeDto propertyType, final ArrayList<MosaicPropertyModificationBuilder> modifications) {
        return new EmbeddedMosaicPropertyTransactionBuilder(signer, version, type, propertyType, modifications);
    }

    /**
     * Gets property type.
     *
     * @return Property type.
     */
    public PropertyTypeDto getPropertyType() {
        return this.mosaicPropertyTransactionBody.getPropertyType();
    }

    /**
     * Gets property modifications.
     *
     * @return Property modifications.
     */
    public ArrayList<MosaicPropertyModificationBuilder> getModifications() {
        return this.mosaicPropertyTransactionBody.getModifications();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.mosaicPropertyTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of EmbeddedMosaicPropertyTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EmbeddedMosaicPropertyTransactionBuilder.
     */
    public static EmbeddedMosaicPropertyTransactionBuilder loadFromBinary(final DataInput stream) {
        return new EmbeddedMosaicPropertyTransactionBuilder(stream);
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
            final byte[] mosaicPropertyTransactionBodyBytes = this.mosaicPropertyTransactionBody.serialize();
            dataOutputStream.write(mosaicPropertyTransactionBodyBytes, 0, mosaicPropertyTransactionBodyBytes.length);
        });
    }
}
