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
import java.util.ArrayList;

/**
 * Binary layout for an embedded multisig account modification transaction.
 */
public final class EmbeddedMultisigAccountModificationTransactionBuilder extends
    EmbeddedTransactionBuilder {

    /**
     * Multisig account modification transaction body.
     */
    private final MultisigAccountModificationTransactionBodyBuilder multisigAccountModificationTransactionBody;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected EmbeddedMultisigAccountModificationTransactionBuilder(final DataInput stream) {
        super(stream);
        this.multisigAccountModificationTransactionBody = MultisigAccountModificationTransactionBodyBuilder
            .loadFromBinary(stream);
    }

    /**
     * Constructor.
     *
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param minRemovalDelta Relative change of the minimal number of cosignatories required when
     * removing an account.
     * @param minApprovalDelta Relative change of the minimal number of cosignatories required when
     * approving a transaction.
     * @param modifications Attached cosignatory modifications.
     */
    protected EmbeddedMultisigAccountModificationTransactionBuilder(final KeyDto signer,
        final short version, final EntityTypeDto type, final byte minRemovalDelta,
        final byte minApprovalDelta,
        final ArrayList<CosignatoryModificationBuilder> modifications) {
        super(signer, version, type);
        this.multisigAccountModificationTransactionBody = MultisigAccountModificationTransactionBodyBuilder
            .create(minRemovalDelta, minApprovalDelta, modifications);
    }

    /**
     * Creates an instance of EmbeddedMultisigAccountModificationTransactionBuilder.
     *
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param minRemovalDelta Relative change of the minimal number of cosignatories required when
     * removing an account.
     * @param minApprovalDelta Relative change of the minimal number of cosignatories required when
     * approving a transaction.
     * @param modifications Attached cosignatory modifications.
     * @return Instance of EmbeddedMultisigAccountModificationTransactionBuilder.
     */
    public static EmbeddedMultisigAccountModificationTransactionBuilder create(final KeyDto signer,
        final short version, final EntityTypeDto type, final byte minRemovalDelta,
        final byte minApprovalDelta,
        final ArrayList<CosignatoryModificationBuilder> modifications) {
        return new EmbeddedMultisigAccountModificationTransactionBuilder(signer, version, type,
            minRemovalDelta, minApprovalDelta, modifications);
    }

    /**
     * Creates an instance of EmbeddedMultisigAccountModificationTransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of EmbeddedMultisigAccountModificationTransactionBuilder.
     */
    public static EmbeddedMultisigAccountModificationTransactionBuilder loadFromBinary(
        final DataInput stream) {
        return new EmbeddedMultisigAccountModificationTransactionBuilder(stream);
    }

    /**
     * Gets relative change of the minimal number of cosignatories required when removing an
     * account.
     *
     * @return Relative change of the minimal number of cosignatories required when removing an
     * account.
     */
    public byte getMinRemovalDelta() {
        return this.multisigAccountModificationTransactionBody.getMinRemovalDelta();
    }

    /**
     * Gets relative change of the minimal number of cosignatories required when approving a
     * transaction.
     *
     * @return Relative change of the minimal number of cosignatories required when approving a
     * transaction.
     */
    public byte getMinApprovalDelta() {
        return this.multisigAccountModificationTransactionBody.getMinApprovalDelta();
    }

    /**
     * Gets attached cosignatory modifications.
     *
     * @return Attached cosignatory modifications.
     */
    public ArrayList<CosignatoryModificationBuilder> getModifications() {
        return this.multisigAccountModificationTransactionBody.getModifications();
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    @Override
    public int getSize() {
        int size = super.getSize();
        size += this.multisigAccountModificationTransactionBody.getSize();
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
            final byte[] multisigAccountModificationTransactionBodyBytes = this.multisigAccountModificationTransactionBody
                .serialize();
            dataOutputStream.write(multisigAccountModificationTransactionBodyBytes, 0,
                multisigAccountModificationTransactionBodyBytes.length);
        });
    }
}
