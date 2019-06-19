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
import java.util.EnumSet;
import java.util.ArrayList;

/** Binary layout for an embedded mosaic definition transaction. */
public final class EmbeddedMosaicDefinitionTransactionBuilder extends EmbeddedTransactionBuilder {
    /** Mosaic definition transaction body. */
    private final MosaicDefinitionTransactionBodyBuilder mosaicDefinitionTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected EmbeddedMosaicDefinitionTransactionBuilder(final DataInput stream) {
        super(stream);
        this.mosaicDefinitionTransactionBody = MosaicDefinitionTransactionBodyBuilder.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param mosaicNonce Mosaic nonce.
     * @param mosaicId Id of the mosaic.
     * @param flags Mosaic flags.
     * @param divisibility Mosaic divisibility.
     * @param properties Optional properties.
     */
    protected EmbeddedMosaicDefinitionTransactionBuilder(final KeyDto signer, final short version, final EntityTypeDto type, final MosaicNonceDto mosaicNonce, final MosaicIdDto mosaicId, final EnumSet<MosaicFlagsDto> flags, final byte divisibility, final ArrayList<MosaicPropertyBuilder> properties) {
        super(signer, version, type);
        this.mosaicDefinitionTransactionBody = MosaicDefinitionTransactionBodyBuilder.create(mosaicNonce, mosaicId, flags, divisibility, properties);
    }

    /**
     * Creates an instance of EmbeddedMosaicDefinitionTransactionBuilder.
     *
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param mosaicNonce Mosaic nonce.
     * @param mosaicId Id of the mosaic.
     * @param flags Mosaic flags.
     * @param divisibility Mosaic divisibility.
     * @param properties Optional properties.
     * @return Instance of EmbeddedMosaicDefinitionTransactionBuilder.
     */
    public static EmbeddedMosaicDefinitionTransactionBuilder create(final KeyDto signer, final short version, final EntityTypeDto type, final MosaicNonceDto mosaicNonce, final MosaicIdDto mosaicId, final EnumSet<MosaicFlagsDto> flags, final byte divisibility, final ArrayList<MosaicPropertyBuilder> properties) {
        return new EmbeddedMosaicDefinitionTransactionBuilder(signer, version, type, mosaicNonce, mosaicId, flags, divisibility, properties);
    }

    /**
     * Gets mosaic nonce.
     *
     * @return Mosaic nonce.
     */
    public MosaicNonceDto getMosaicNonce() {
        return this.mosaicDefinitionTransactionBody.getMosaicNonce();
    }

    /**
     * Gets id of the mosaic.
     *
     * @return Id of the mosaic.
     */
    public MosaicIdDto getMosaicId() {
        return this.mosaicDefinitionTransactionBody.getMosaicId();
    }

    /**
     * Gets mosaic flags.
     *
     * @return Mosaic flags.
     */
    public EnumSet<MosaicFlagsDto> getFlags() {
        return this.mosaicDefinitionTransactionBody.getFlags();
    }

    /**
     * Gets mosaic divisibility.
     *
     * @return Mosaic divisibility.
     */
    public byte getDivisibility() {
        return this.mosaicDefinitionTransactionBody.getDivisibility();
    }

    /**
     * Gets optional properties.
     *
     * @return Optional properties.
     */
    public ArrayList<MosaicPropertyBuilder> getProperties() {
        return this.mosaicDefinitionTransactionBody.getProperties();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.mosaicDefinitionTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of EmbeddedMosaicDefinitionTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EmbeddedMosaicDefinitionTransactionBuilder.
     */
    public static EmbeddedMosaicDefinitionTransactionBuilder loadFromBinary(final DataInput stream) {
        return new EmbeddedMosaicDefinitionTransactionBuilder(stream);
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
            final byte[] mosaicDefinitionTransactionBodyBytes = this.mosaicDefinitionTransactionBody.serialize();
            dataOutputStream.write(mosaicDefinitionTransactionBodyBytes, 0, mosaicDefinitionTransactionBodyBytes.length);
        });
    }
}
