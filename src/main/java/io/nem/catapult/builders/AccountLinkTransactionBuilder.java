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

/** Binary layout for a non-embedded account link transaction. */
public final class AccountLinkTransactionBuilder extends TransactionBuilder {
    /** Account link transaction body. */
    private final AccountLinkTransactionBodyBuilder accountLinkTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected AccountLinkTransactionBuilder(final DataInput stream) {
        super(stream);
        this.accountLinkTransactionBody = AccountLinkTransactionBodyBuilder.loadFromBinary(stream);
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
     * @param remoteAccountKey Remote account key.
     * @param linkAction Account link action.
     */
    protected AccountLinkTransactionBuilder(final SignatureDto signature, final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final KeyDto remoteAccountKey, final AccountLinkActionDto linkAction) {
        super(signature, signer, version, type, fee, deadline);
        this.accountLinkTransactionBody = AccountLinkTransactionBodyBuilder.create(remoteAccountKey, linkAction);
    }

    /**
     * Creates an instance of AccountLinkTransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @param remoteAccountKey Remote account key.
     * @param linkAction Account link action.
     * @return Instance of AccountLinkTransactionBuilder.
     */
    public static AccountLinkTransactionBuilder create(final SignatureDto signature, final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline, final KeyDto remoteAccountKey, final AccountLinkActionDto linkAction) {
        return new AccountLinkTransactionBuilder(signature, signer, version, type, fee, deadline, remoteAccountKey, linkAction);
    }

    /**
     * Gets remote account key.
     *
     * @return Remote account key.
     */
    public KeyDto getRemoteAccountKey() {
        return this.accountLinkTransactionBody.getRemoteAccountKey();
    }

    /**
     * Gets account link action.
     *
     * @return Account link action.
     */
    public AccountLinkActionDto getLinkAction() {
        return this.accountLinkTransactionBody.getLinkAction();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.accountLinkTransactionBody.getSize();
        return size;
    }

    /**
     * Creates an instance of AccountLinkTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of AccountLinkTransactionBuilder.
     */
    public static AccountLinkTransactionBuilder loadFromBinary(final DataInput stream) {
        return new AccountLinkTransactionBuilder(stream);
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
            final byte[] accountLinkTransactionBodyBytes = this.accountLinkTransactionBody.serialize();
            dataOutputStream.write(accountLinkTransactionBodyBytes, 0, accountLinkTransactionBodyBytes.length);
        });
    }
}
