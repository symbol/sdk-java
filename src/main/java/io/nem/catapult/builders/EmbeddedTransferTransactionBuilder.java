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
 * Binary layout for an embedded transfer transaction.
 */
public final class EmbeddedTransferTransactionBuilder extends EmbeddedTransactionBuilder {

    /**
     * Transfer transaction body.
     */
    private final TransferTransactionBodyBuilder transferTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected EmbeddedTransferTransactionBuilder(final DataInput stream) {
        super(stream);
        this.transferTransactionBody = TransferTransactionBodyBuilder.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param recipient Transaction recipient.
     * @param message Transaction message.
     * @param mosaics Attached mosaics.
     */
    protected EmbeddedTransferTransactionBuilder(final KeyDto signer, final short version,
        final EntityTypeDto type, final UnresolvedAddressDto recipient, final ByteBuffer message,
        final ArrayList<UnresolvedMosaicBuilder> mosaics) {
        super(signer, version, type);
        this.transferTransactionBody = TransferTransactionBodyBuilder
            .create(recipient, message, mosaics);
    }

    /**
     * Creates an instance of EmbeddedTransferTransactionBuilder.
     *
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param recipient Transaction recipient.
     * @param message Transaction message.
     * @param mosaics Attached mosaics.
     * @return Instance of EmbeddedTransferTransactionBuilder.
     */
    public static EmbeddedTransferTransactionBuilder create(final KeyDto signer,
        final short version, final EntityTypeDto type, final UnresolvedAddressDto recipient,
        final ByteBuffer message, final ArrayList<UnresolvedMosaicBuilder> mosaics) {
        return new EmbeddedTransferTransactionBuilder(signer, version, type, recipient, message,
            mosaics);
    }

    /**
     * Creates an instance of EmbeddedTransferTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EmbeddedTransferTransactionBuilder.
     */
    public static EmbeddedTransferTransactionBuilder loadFromBinary(final DataInput stream) {
        return new EmbeddedTransferTransactionBuilder(stream);
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
