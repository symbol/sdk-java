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

/** Binary layout for a non-embedded mosaic supply change transaction. */
public final class MosaicSupplyChangeTransactionBuilder extends TransactionBuilder {
    /** Mosaic supply change transaction body. */
    private final MosaicSupplyChangeTransactionBodyBuilder mosaicSupplyChangeTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected MosaicSupplyChangeTransactionBuilder(final DataInput stream) {
        super(stream);
        this.mosaicSupplyChangeTransactionBody = MosaicSupplyChangeTransactionBodyBuilder.loadFromBinary(stream);
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
     * @param mosaicId Affected mosaic identifier.
     * @param action Supply change action.
     * @param delta Change amount.
     */
    protected MosaicSupplyChangeTransactionBuilder(final SignatureDto signature, final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final UnresolvedMosaicIdDto mosaicId, final MosaicSupplyChangeActionDto action, final AmountDto delta) {
        super(signature, signer, version, type, fee, deadline);
        this.mosaicSupplyChangeTransactionBody = MosaicSupplyChangeTransactionBodyBuilder.create(mosaicId, action, delta);
    }

    /**
     * Creates an instance of MosaicSupplyChangeTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param mosaicId Affected mosaic identifier.
     * @param action Supply change action.
     * @param delta Change amount.
     * @return Instance of MosaicSupplyChangeTransactionBuilder.
     */
    public static MosaicSupplyChangeTransactionBuilder create(final SignatureDto signature, final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final UnresolvedMosaicIdDto mosaicId, final MosaicSupplyChangeActionDto action, final AmountDto delta) {
        return new MosaicSupplyChangeTransactionBuilder(signature, signer, version, type, fee, deadline, mosaicId, action, delta);
    }

    /**
     * Gets affected mosaic identifier.
     *
     * @return Affected mosaic identifier.
     */
    public UnresolvedMosaicIdDto getMosaicId() {
        return this.mosaicSupplyChangeTransactionBody.getMosaicId();
    }

    /**
     * Gets supply change action.
     *
     * @return Supply change action.
     */
    public MosaicSupplyChangeActionDto getAction() {
        return this.mosaicSupplyChangeTransactionBody.getAction();
    }

    /**
     * Gets change amount.
     *
     * @return Change amount.
     */
    public AmountDto getDelta() {
        return this.mosaicSupplyChangeTransactionBody.getDelta();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.mosaicSupplyChangeTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of MosaicSupplyChangeTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MosaicSupplyChangeTransactionBuilder.
     */
    public static MosaicSupplyChangeTransactionBuilder loadFromBinary(final DataInput stream) {
        return new MosaicSupplyChangeTransactionBuilder(stream);
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
            final byte[] mosaicSupplyChangeTransactionBodyBytes = this.mosaicSupplyChangeTransactionBody.serialize();
            dataOutputStream.write(mosaicSupplyChangeTransactionBodyBytes, 0, mosaicSupplyChangeTransactionBodyBytes.length);
        });
    }
}
