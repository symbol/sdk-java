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
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Binary layout for a non-embedded transfer transaction.
 */
public final class TransferTransactionBuilder extends TransactionBuilder {

    /**
     * Transfer transaction body.
     */
    private final TransferTransactionBodyBuilder transferTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected TransferTransactionBuilder(final DataInput stream) {
        super(stream);
        this.transferTransactionBody = TransferTransactionBodyBuilder.loadFromBinary(stream);
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
     * @param recipient Transaction recipient.
     * @param message Transaction message.
     * @param mosaics Attached mosaics.
     */
    protected TransferTransactionBuilder(final SignatureDto signature, final KeyDto signer,
        final short version, final EntityTypeDto type, final AmountDto fee,
        final TimestampDto deadline, final UnresolvedAddressDto recipient, final ByteBuffer message,
        final ArrayList<UnresolvedMosaicBuilder> mosaics) {
        super(signature, signer, version, type, fee, deadline);
        this.transferTransactionBody = TransferTransactionBodyBuilder
            .create(recipient, message, mosaics);
    }

    /**
     * Creates an instance of TransferTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param recipient Transaction recipient.
     * @param message Transaction message.
     * @param mosaics Attached mosaics.
     * @return Instance of TransferTransactionBuilder.
     */
    public static TransferTransactionBuilder create(final SignatureDto signature,
        final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee,
        final TimestampDto deadline, final UnresolvedAddressDto recipient, final ByteBuffer message,
        final ArrayList<UnresolvedMosaicBuilder> mosaics) {
        return new TransferTransactionBuilder(signature, signer, version, type, fee, deadline,
            recipient, message, mosaics);
    }

    /**
     * Creates an instance of TransferTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of TransferTransactionBuilder.
     */
    public static TransferTransactionBuilder loadFromBinary(final DataInput stream) {
        return new TransferTransactionBuilder(stream);
    }

    /**
     * Gets transaction recipient.
     *
     * @return Transaction recipient.
     */
    public UnresolvedAddressDto getRecipient() {
        return this.transferTransactionBody.getRecipient();
    }

    /**
     * Gets transaction message.
     *
     * @return Transaction message.
     */
    public ByteBuffer getMessage() {
        return this.transferTransactionBody.getMessage();
    }

    /**
     * Gets attached mosaics.
     *
     * @return Attached mosaics.
     */
    public ArrayList<UnresolvedMosaicBuilder> getMosaics() {
        return this.transferTransactionBody.getMosaics();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.transferTransactionBody.getSize();
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
            final byte[] transferTransactionBodyBytes = this.transferTransactionBody.serialize();
            dataOutputStream
                .write(transferTransactionBodyBytes, 0, transferTransactionBodyBytes.length);
        });
    }
}
