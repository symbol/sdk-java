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

import io.nem.core.utils.ByteUtils;
import io.nem.sdk.model.blockchain.NetworkType;

import java.io.DataInput;

/** Binary layout for a transaction. */
public class TransactionBuilder {
    /** Entity size. */
    private int size;
    /** Entity signature. */
    private final SignatureDto signature;
    /** Entity signer's public key. */
    private final KeyDto signer;
    /** Entity version. */
    private final short version;
    /** Entity type. */
    private final EntityTypeDto type;
    /** Transaction fee. */
    private final AmountDto fee;
    /** Transaction deadline. */
    private final TimestampDto deadline;

    private Integer transactionVersion;
    private NetworkType networkType;


    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected TransactionBuilder(final DataInput stream) {
        try {
            this.size = Integer.reverseBytes(stream.readInt());
            this.signature = SignatureDto.loadFromBinary(stream);
            this.signer = KeyDto.loadFromBinary(stream);
            this.version = Short.reverseBytes(stream.readShort());
            this.type = EntityTypeDto.loadFromBinary(stream);
            this.fee = AmountDto.loadFromBinary(stream);
            this.deadline = TimestampDto.loadFromBinary(stream);
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
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
     */
    protected TransactionBuilder(final SignatureDto signature, final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline) {
        GeneratorUtils.notNull(signature, "signature is null");
        GeneratorUtils.notNull(signer, "signer is null");
        GeneratorUtils.notNull(type, "type is null");
        GeneratorUtils.notNull(fee, "fee is null");
        GeneratorUtils.notNull(deadline, "deadline is null");
        this.signature = signature;
        this.signer = signer;
        this.version = version;
        this.type = type;
        this.fee = fee;
        this.deadline = deadline;
    }

    /**
     * Creates an instance of TransactionBuilder.
     *
     * @param signature Entity signature.
     * @param signer Entity signer's public key.
     * @param version Entity version.
     * @param type Entity type.
     * @param fee Transaction fee.
     * @param deadline Transaction deadline.
     * @return Instance of TransactionBuilder.
     */
    public static TransactionBuilder create(final SignatureDto signature, final KeyDto signer, final short version, final EntityTypeDto type, final AmountDto fee, final TimestampDto deadline) {
        return new TransactionBuilder(signature, signer, version, type, fee, deadline);
    }

    /**
     * Gets the size if created from a stream otherwise zero.
     *
     * @return Object size from stream.
     */
    protected int getStreamSize() {
        return this.size;
    }

    /**
     * Gets entity signature.
     *
     * @return Entity signature.
     */
    public SignatureDto getSignature() {
        return this.signature;
    }

    /**
     * Gets entity signer's public key.
     *
     * @return Entity signer's public key.
     */
    public KeyDto getSigner() {
        return this.signer;
    }

    /**
     * Gets entity version.
     *
     * @return Entity version.
     */
    public short getVersion() { return this.version; }

    /**
     * Gets entity type.
     *
     * @return Entity type.
     */
    public EntityTypeDto getType() {
        return this.type;
    }

    /**
     * Gets transaction fee..
     *
     * @return Transaction fee.
     */
    public AmountDto getFee() {
        return this.fee;
    }

    /**
     * Gets transaction deadline.
     *
     * @return Transaction deadline.
     */
    public TimestampDto getDeadline() {
        return this.deadline;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += 4; // size
        size += this.signature.getSize();
        size += this.signer.getSize();
        size += 2; // version
        size += this.type.getSize();
        size += this.fee.getSize();
        size += this.deadline.getSize();
        return size;
    }

    /**
     * Creates an instance of TransactionBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of TransactionBuilder.
     */
    public static TransactionBuilder loadFromBinary(final DataInput stream) {
        return new TransactionBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.writeInt(Integer.reverseBytes(this.getSize()));
            final byte[] signatureBytes = this.signature.serialize();
            dataOutputStream.write(signatureBytes, 0, signatureBytes.length);
            final byte[] signerBytes = this.signer.serialize();
            dataOutputStream.write(signerBytes, 0, signerBytes.length);
            dataOutputStream.writeShort(Short.reverseBytes(this.getVersion()));
            final byte[] typeBytes = this.type.serialize();
            dataOutputStream.write(typeBytes, 0, typeBytes.length);
            final byte[] feeBytes = this.fee.serialize();
            dataOutputStream.write(feeBytes, 0, feeBytes.length);
            final byte[] deadlineBytes = this.deadline.serialize();
            dataOutputStream.write(deadlineBytes, 0, deadlineBytes.length);
        });
    }

    public String asString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nSize: "+this.getSize());
        sb.append("\nSignature: "+this.getSignature().asString());
        sb.append("\nSigner: "+this.getSigner().asString());
        sb.append("\nTransactionVersion: "+this.getTransactionVersion());
        sb.append("\nNetworkType: "+this.getNetworkType());
        sb.append("\nType: "+this.getType().asString());
        sb.append("\nFee: "+this.getFee().asString());
        sb.append("\nDeadline: "+this.getDeadline().asString());

        return sb.toString();
    }

    /**
     * Gets transaction version by lazy initialization.
     *
     * @return Integer
     */
    public Integer getTransactionVersion() {
        if (this.transactionVersion == null) this.setTransactionVersionAndNetworkType(this.version);
        return this.transactionVersion;
    }

    /**
     * Gets network type by lazy initialization.
     *
     * @return NetworkType
     */
    public NetworkType getNetworkType() {
        if (this.networkType == null) this.setTransactionVersionAndNetworkType(this.version);
        return this.networkType;
    }

    /**
     * Extract and set transaction version and network type from version.
     *
     * @param version
     */
    private void setTransactionVersionAndNetworkType(short version) {
        byte[] bytes = ByteUtils.shortToBytes(version);
        this.transactionVersion = Byte.toUnsignedInt(bytes[1]);
        this.networkType = NetworkType.rawValueOf(Byte.toUnsignedInt(bytes[0]));
    }
}
