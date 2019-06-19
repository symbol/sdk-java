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

/** Binary layout for a non-embedded secret lock transaction. */
public final class SecretLockTransactionBuilder extends TransactionBuilder {
    /** Secret lock transaction body. */
    private final SecretLockTransactionBodyBuilder secretLockTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected SecretLockTransactionBuilder(final DataInput stream) {
        super(stream);
        this.secretLockTransactionBody = SecretLockTransactionBodyBuilder.loadFromBinary(stream);
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
     * @param mosaic Lock mosaic.
     * @param duration Number of blocks for which a lock should be valid.
     * @param hashAlgorithm Hash alghoritm.
     * @param secret Secret.
     * @param recipient Recipient of the locked mosaic.
     */
    protected SecretLockTransactionBuilder(final SignatureDto signature, final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final UnresolvedMosaicBuilder mosaic, final BlockDurationDto duration, final LockHashAlgorithmDto hashAlgorithm, final Hash256Dto secret, final UnresolvedAddressDto recipient) {
        super(signature, signer, version, type, fee, deadline);
        this.secretLockTransactionBody = SecretLockTransactionBodyBuilder.create(mosaic, duration, hashAlgorithm, secret, recipient);
    }

    /**
     * Creates an instance of SecretLockTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param mosaic Lock mosaic.
     * @param duration Number of blocks for which a lock should be valid.
     * @param hashAlgorithm Hash alghoritm.
     * @param secret Secret.
     * @param recipient Recipient of the locked mosaic.
     * @return Instance of SecretLockTransactionBuilder.
     */
    public static SecretLockTransactionBuilder create(final SignatureDto signature, final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final UnresolvedMosaicBuilder mosaic, final BlockDurationDto duration, final LockHashAlgorithmDto hashAlgorithm, final Hash256Dto secret, final UnresolvedAddressDto recipient) {
        return new SecretLockTransactionBuilder(signature, signer, version, type, fee, deadline, mosaic, duration, hashAlgorithm, secret, recipient);
    }

    /**
     * Gets lock mosaic.
     *
     * @return Lock mosaic.
     */
    public UnresolvedMosaicBuilder getMosaic() {
        return this.secretLockTransactionBody.getMosaic();
    }

    /**
     * Gets number of blocks for which a lock should be valid.
     *
     * @return Number of blocks for which a lock should be valid.
     */
    public BlockDurationDto getDuration() {
        return this.secretLockTransactionBody.getDuration();
    }

    /**
     * Gets hash alghoritm.
     *
     * @return Hash alghoritm.
     */
    public LockHashAlgorithmDto getHashAlgorithm() {
        return this.secretLockTransactionBody.getHashAlgorithm();
    }

    /**
     * Gets secret.
     *
     * @return Secret.
     */
    public Hash256Dto getSecret() {
        return this.secretLockTransactionBody.getSecret();
    }

    /**
     * Gets recipient of the locked mosaic.
     *
     * @return Recipient of the locked mosaic.
     */
    public UnresolvedAddressDto getRecipient() {
        return this.secretLockTransactionBody.getRecipient();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.secretLockTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of SecretLockTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of SecretLockTransactionBuilder.
     */
    public static SecretLockTransactionBuilder loadFromBinary(final DataInput stream) {
        return new SecretLockTransactionBuilder(stream);
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
            final byte[] secretLockTransactionBodyBytes = this.secretLockTransactionBody.serialize();
            dataOutputStream.write(secretLockTransactionBodyBytes, 0, secretLockTransactionBodyBytes.length);
        });
    }
}
