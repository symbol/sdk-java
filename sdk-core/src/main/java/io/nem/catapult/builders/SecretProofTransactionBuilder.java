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

/**
 * Binary layout for a non-embedded secret proof transaction.
 */
public final class SecretProofTransactionBuilder extends TransactionBuilder {

    /**
     * Secret proof transaction body.
     */
    private final SecretProofTransactionBodyBuilder secretProofTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected SecretProofTransactionBuilder(final DataInput stream) {
        super(stream);
        this.secretProofTransactionBody = SecretProofTransactionBodyBuilder.loadFromBinary(stream);
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
     * @param hashAlgorithm Hash algorithm.
     * @param secret Secret.
     * @param recipient Recipient.
     * @param proof Proof data.
     */
    protected SecretProofTransactionBuilder(final SignatureDto signature, final KeyDto signer,
        final short version, final EntityTypeDto type, final AmountDto fee,
        final TimestampDto deadline, final LockHashAlgorithmDto hashAlgorithm,
        final Hash256Dto secret, final UnresolvedAddressDto recipient, final ByteBuffer proof) {
        super(signature, signer, version, type, fee, deadline);
        this.secretProofTransactionBody = SecretProofTransactionBodyBuilder
            .create(hashAlgorithm, secret, recipient, proof);
    }

    /**
     * Creates an instance of SecretProofTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param hashAlgorithm Hash algorithm.
     * @param secret Secret.
     * @param recipient Recipient.
     * @param proof Proof data.
     * @return Instance of SecretProofTransactionBuilder.
     */
    public static SecretProofTransactionBuilder create(final SignatureDto signature,
        final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee,
        final TimestampDto deadline, final LockHashAlgorithmDto hashAlgorithm,
        final Hash256Dto secret, final UnresolvedAddressDto recipient, final ByteBuffer proof) {
        return new SecretProofTransactionBuilder(signature, signer, version, type, fee, deadline,
            hashAlgorithm, secret, recipient, proof);
    }

    /**
     * Creates an instance of SecretProofTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of SecretProofTransactionBuilder.
     */
    public static SecretProofTransactionBuilder loadFromBinary(final DataInput stream) {
        return new SecretProofTransactionBuilder(stream);
    }

    /**
     * Gets hash algorithm.
     *
     * @return Hash algorithm.
     */
    public LockHashAlgorithmDto getHashAlgorithm() {
        return this.secretProofTransactionBody.getHashAlgorithm();
    }

    /**
     * Gets secret.
     *
     * @return Secret.
     */
    public Hash256Dto getSecret() {
        return this.secretProofTransactionBody.getSecret();
    }

    /**
     * Gets recipient.
     *
     * @return Recipient.
     */
    public UnresolvedAddressDto getRecipient() {
        return this.secretProofTransactionBody.getRecipient();
    }

    /**
     * Gets proof data.
     *
     * @return Proof data.
     */
    public ByteBuffer getProof() {
        return this.secretProofTransactionBody.getProof();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.secretProofTransactionBody.getSize();
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
            final byte[] secretProofTransactionBodyBytes = this.secretProofTransactionBody
                .serialize();
            dataOutputStream
                .write(secretProofTransactionBodyBytes, 0, secretProofTransactionBodyBytes.length);
        });
    }
}
