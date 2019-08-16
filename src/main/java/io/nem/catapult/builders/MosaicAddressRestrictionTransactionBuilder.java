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

/**
 * Binary layout for a non-embedded mosaic address restriction transaction.
 */
public final class MosaicAddressRestrictionTransactionBuilder extends TransactionBuilder {

    /**
     * Mosaic address restriction transaction body.
     */
    private final MosaicAddressRestrictionTransactionBodyBuilder mosaicAddressRestrictionTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected MosaicAddressRestrictionTransactionBuilder(final DataInput stream) {
        super(stream);
        this.mosaicAddressRestrictionTransactionBody = MosaicAddressRestrictionTransactionBodyBuilder
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
     * @param mosaicId Identifier of the mosaic to which the restriction applies.
     * @param restrictionKey Restriction key.
     * @param targetAddress Address being restricted.
     * @param previousRestrictionValue Previous restriction value.
     * @param newRestrictionValue New restriction value.
     */
    protected MosaicAddressRestrictionTransactionBuilder(final SignatureDto signature,
        final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee,
        final TimestampDto deadline, final UnresolvedMosaicIdDto mosaicId,
        final long restrictionKey, final UnresolvedAddressDto targetAddress,
        final long previousRestrictionValue, final long newRestrictionValue) {
        super(signature, signer, version, type, fee, deadline);
        this.mosaicAddressRestrictionTransactionBody = MosaicAddressRestrictionTransactionBodyBuilder
            .create(mosaicId, restrictionKey, targetAddress, previousRestrictionValue,
                newRestrictionValue);
    }

    /**
     * Creates an instance of MosaicAddressRestrictionTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param mosaicId Identifier of the mosaic to which the restriction applies.
     * @param restrictionKey Restriction key.
     * @param targetAddress Address being restricted.
     * @param previousRestrictionValue Previous restriction value.
     * @param newRestrictionValue New restriction value.
     * @return Instance of MosaicAddressRestrictionTransactionBuilder.
     */
    public static MosaicAddressRestrictionTransactionBuilder create(final SignatureDto signature,
        final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee,
        final TimestampDto deadline, final UnresolvedMosaicIdDto mosaicId,
        final long restrictionKey, final UnresolvedAddressDto targetAddress,
        final long previousRestrictionValue, final long newRestrictionValue) {
        return new MosaicAddressRestrictionTransactionBuilder(signature, signer, version, type, fee,
            deadline, mosaicId, restrictionKey, targetAddress, previousRestrictionValue,
            newRestrictionValue);
    }

    /**
     * Creates an instance of MosaicAddressRestrictionTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MosaicAddressRestrictionTransactionBuilder.
     */
    public static MosaicAddressRestrictionTransactionBuilder loadFromBinary(
        final DataInput stream) {
        return new MosaicAddressRestrictionTransactionBuilder(stream);
    }

    /**
     * Gets identifier of the mosaic to which the restriction applies.
     *
     * @return Identifier of the mosaic to which the restriction applies.
     */
    public UnresolvedMosaicIdDto getMosaicId() {
        return this.mosaicAddressRestrictionTransactionBody.getMosaicId();
    }

    /**
     * Gets restriction key.
     *
     * @return Restriction key.
     */
    public long getRestrictionKey() {
        return this.mosaicAddressRestrictionTransactionBody.getRestrictionKey();
    }

    /**
     * Gets address being restricted.
     *
     * @return Address being restricted.
     */
    public UnresolvedAddressDto getTargetAddress() {
        return this.mosaicAddressRestrictionTransactionBody.getTargetAddress();
    }

    /**
     * Gets previous restriction value.
     *
     * @return Previous restriction value.
     */
    public long getPreviousRestrictionValue() {
        return this.mosaicAddressRestrictionTransactionBody.getPreviousRestrictionValue();
    }

    /**
     * Gets new restriction value.
     *
     * @return New restriction value.
     */
    public long getNewRestrictionValue() {
        return this.mosaicAddressRestrictionTransactionBody.getNewRestrictionValue();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.mosaicAddressRestrictionTransactionBody.getSize();
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
            final byte[] mosaicAddressRestrictionTransactionBodyBytes = this.mosaicAddressRestrictionTransactionBody
                .serialize();
            dataOutputStream.write(mosaicAddressRestrictionTransactionBodyBytes, 0,
                mosaicAddressRestrictionTransactionBodyBytes.length);
        });
    }
}
