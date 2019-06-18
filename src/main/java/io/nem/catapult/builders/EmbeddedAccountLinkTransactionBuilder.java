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

/** Binary layout for an embedded account link transaction. */
public final class EmbeddedAccountLinkTransactionBuilder extends EmbeddedTransactionBuilder {
    /** Account link transaction body. */
    private final AccountLinkTransactionBodyBuilder accountLinkTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected EmbeddedAccountLinkTransactionBuilder(final DataInput stream) {
        super(stream);
        this.accountLinkTransactionBody = AccountLinkTransactionBodyBuilder.loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param remoteAccountKey Remote account key.
     * @param linkAction Account link action.
     */
    protected EmbeddedAccountLinkTransactionBuilder(final KeyDto signer, final short version, final EntityTypeDto type, final KeyDto remoteAccountKey, final AccountLinkActionDto linkAction) {
        super(signer, version, type);
        this.accountLinkTransactionBody = AccountLinkTransactionBodyBuilder.create(remoteAccountKey, linkAction);
    }

    /**
     * Creates an instance of EmbeddedAccountLinkTransactionBuilder.
     *
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param remoteAccountKey Remote account key.
     * @param linkAction Account link action.
     * @return Instance of EmbeddedAccountLinkTransactionBuilder.
     */
    public static EmbeddedAccountLinkTransactionBuilder create(final KeyDto signer, final short version, final EntityTypeDto type, final KeyDto remoteAccountKey, final AccountLinkActionDto linkAction) {
        return new EmbeddedAccountLinkTransactionBuilder(signer, version, type, remoteAccountKey, linkAction);
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
     * Creates an instance of EmbeddedAccountLinkTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EmbeddedAccountLinkTransactionBuilder.
     */
    public static EmbeddedAccountLinkTransactionBuilder loadFromBinary(final DataInput stream) {
        return new EmbeddedAccountLinkTransactionBuilder(stream);
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
