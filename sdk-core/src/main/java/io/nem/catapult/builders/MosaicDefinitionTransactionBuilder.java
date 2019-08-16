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
import java.util.EnumSet;

/**
 * Binary layout for a non-embedded mosaic definition transaction.
 */
public final class MosaicDefinitionTransactionBuilder extends TransactionBuilder {

    /**
     * Mosaic definition transaction body.
     */
    private final MosaicDefinitionTransactionBodyBuilder mosaicDefinitionTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected MosaicDefinitionTransactionBuilder(final DataInput stream) {
        super(stream);
        this.mosaicDefinitionTransactionBody = MosaicDefinitionTransactionBodyBuilder
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
     * @param nonce Mosaic nonce.
     * @param id Mosaic identifier.
     * @param flags Mosaic flags.
     * @param divisibility Mosaic divisibility.
     * @param duration Mosaic duration.
     */
    protected MosaicDefinitionTransactionBuilder(final SignatureDto signature, final KeyDto signer,
        final short version, final EntityTypeDto type, final AmountDto fee,
        final TimestampDto deadline, final MosaicNonceDto nonce, final MosaicIdDto id,
        final EnumSet<MosaicFlagsDto> flags, final byte divisibility,
        final BlockDurationDto duration) {
        super(signature, signer, version, type, fee, deadline);
        this.mosaicDefinitionTransactionBody = MosaicDefinitionTransactionBodyBuilder
            .create(nonce, id, flags, divisibility, duration);
    }

    /**
     * Creates an instance of MosaicDefinitionTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param nonce Mosaic nonce.
     * @param id Mosaic identifier.
     * @param flags Mosaic flags.
     * @param divisibility Mosaic divisibility.
     * @param duration Mosaic duration.
     * @return Instance of MosaicDefinitionTransactionBuilder.
     */
    public static MosaicDefinitionTransactionBuilder create(final SignatureDto signature,
        final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee,
        final TimestampDto deadline, final MosaicNonceDto nonce, final MosaicIdDto id,
        final EnumSet<MosaicFlagsDto> flags, final byte divisibility,
        final BlockDurationDto duration) {
        return new MosaicDefinitionTransactionBuilder(signature, signer, version, type, fee,
            deadline, nonce, id, flags, divisibility, duration);
    }

    /**
     * Creates an instance of MosaicDefinitionTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of MosaicDefinitionTransactionBuilder.
     */
    public static MosaicDefinitionTransactionBuilder loadFromBinary(final DataInput stream) {
        return new MosaicDefinitionTransactionBuilder(stream);
    }

    /**
     * Gets mosaic nonce.
     *
     * @return Mosaic nonce.
     */
    public MosaicNonceDto getNonce() {
        return this.mosaicDefinitionTransactionBody.getNonce();
    }

    /**
     * Gets mosaic identifier.
     *
     * @return Mosaic identifier.
     */
    public MosaicIdDto getId() {
        return this.mosaicDefinitionTransactionBody.getId();
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
     * Gets mosaic duration.
     *
     * @return Mosaic duration.
     */
    public BlockDurationDto getDuration() {
        return this.mosaicDefinitionTransactionBody.getDuration();
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
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] superBytes = super.serialize();
            dataOutputStream.write(superBytes, 0, superBytes.length);
            final byte[] mosaicDefinitionTransactionBodyBytes = this.mosaicDefinitionTransactionBody
                .serialize();
            dataOutputStream.write(mosaicDefinitionTransactionBodyBytes, 0,
                mosaicDefinitionTransactionBodyBytes.length);
        });
    }
}
