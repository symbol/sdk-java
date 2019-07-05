/*
 * Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.model.transaction;

import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.EmbeddedSecretProofTransactionBuilder;
import io.nem.catapult.builders.EntityTypeDto;
import io.nem.catapult.builders.Hash256Dto;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.LockHashAlgorithmDto;
import io.nem.catapult.builders.SecretProofTransactionBuilder;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.catapult.builders.UnresolvedAddressDto;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Optional;
import org.apache.commons.lang3.Validate;
import org.bouncycastle.util.encoders.Hex;

/**
 * Secret proof transaction.
 */
public class SecretProofTransaction extends Transaction {

    private final HashType hashType;
    private final String secret;
    private final String proof;
    private final Address recipient;

    /**
     * Constructor.
     *
     * @param networkType Network type.
     * @param version Transaction version.
     * @param deadline Deadline to include the transaction.
     * @param maxFee Max fee the sender will pay.
     * @param hashType Hash algorithm secret is generated with.
     * @param recipient Address of recipient.
     * @param secret Seed proof hashed.
     * @param proof Seed proof.
     * @param signature Transaction Signature.
     * @param signer Signer of the transaction.
     * @param transactionInfo Transaction info.
     */
    public SecretProofTransaction(
        final NetworkType networkType,
        final Integer version,
        final Deadline deadline,
        final BigInteger maxFee,
        final HashType hashType,
        final Address recipient,
        final String secret,
        final String proof,
        final String signature,
        final PublicAccount signer,
        final TransactionInfo transactionInfo) {
        this(
            networkType,
            version,
            deadline,
            maxFee,
            hashType,
            recipient,
            secret,
            proof,
            Optional.of(signature),
            Optional.of(signer),
            Optional.of(transactionInfo));
    }

    /**
     * Constructor.
     *
     * @param networkType Network type.
     * @param version Transaction version.
     * @param deadline Deadline to include the transaction.
     * @param maxFee Max fee the sender will pay.
     * @param hashType Hash algorithm secret is generated with.
     * @param recipient Address of recipient.
     * @param secret Seed proof hashed.
     * @param proof Seed proof.
     */
    public SecretProofTransaction(
        final NetworkType networkType,
        final Integer version,
        final Deadline deadline,
        final BigInteger maxFee,
        final HashType hashType,
        final Address recipient,
        final String secret,
        final String proof) {
        this(
            networkType,
            version,
            deadline,
            maxFee,
            hashType,
            recipient,
            secret,
            proof,
            Optional.empty(),
            Optional.empty(),
            Optional.empty());
    }

    /**
     * Constructor.
     *
     * @param networkType Network type.
     * @param version Transaction version.
     * @param deadline Deadline to include the transaction.
     * @param maxFee Max fee the sender will pay.
     * @param hashType Hash algorithm secret is generated with.
     * @param recipient Address of recipient.
     * @param secret Seed proof hashed.
     * @param proof Seed proof.
     * @param signature Transaction Signature.
     * @param signer Signer of the transaction.
     * @param transactionInfo Transaction info.
     */
    public SecretProofTransaction(
        final NetworkType networkType,
        final Integer version,
        final Deadline deadline,
        final BigInteger maxFee,
        final HashType hashType,
        final Address recipient,
        final String secret,
        final String proof,
        final Optional<String> signature,
        final Optional<PublicAccount> signer,
        final Optional<TransactionInfo> transactionInfo) {
        super(
            TransactionType.SECRET_PROOF,
            networkType,
            version,
            deadline,
            maxFee,
            signature,
            signer,
            transactionInfo);
        Validate.notNull(secret, "Secret must not be null.");
        Validate.notNull(proof, "Proof must not be null.");
        Validate.notNull(recipient, "Recipient must not be null.");
        if (!HashType.Validator(hashType, secret)) {
            throw new IllegalArgumentException(
                "HashType and Secret have incompatible length or not hexadecimal string");
        }
        this.hashType = hashType;
        this.secret = secret;
        this.proof = proof;
        this.recipient = recipient;
    }

    /**
     * Create a secret proof transaction object.
     *
     * @param deadline Deadline to include the transaction.
     * @param maxFee Max fee the sender will pay.
     * @param hashType Hash algorithm secret is generated with.
     * @param recipient Address of recipient.
     * @param secret Seed proof hashed.
     * @param proof Seed proof.
     * @param networkType Network type.
     * @return a SecretLockTransaction instance
     */
    public static SecretProofTransaction create(
        final Deadline deadline,
        final BigInteger maxFee,
        final HashType hashType,
        final Address recipient,
        final String secret,
        final String proof,
        final NetworkType networkType) {
        return new SecretProofTransaction(
            networkType,
            TransactionVersion.SECRET_PROOF.getValue(),
            deadline,
            maxFee,
            hashType,
            recipient,
            secret,
            proof);
    }

    /**
     * Returns the hash algorithm secret is generated with.
     *
     * @return the hash algorithm secret is generated with.
     */
    public HashType getHashType() {
        return hashType;
    }

    /**
     * Returns the proof hashed.
     *
     * @return the proof hashed.
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Returns proof.
     *
     * @return proof.
     */
    public String getProof() {
        return proof;
    }

    /**
     * Serialized the transaction.
     *
     * @return bytes of the transaction.
     */
    @Override
    byte[] generateBytes() {
        // Add place holders to the signer and signature until actually signed
        final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
        final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

        SecretProofTransactionBuilder txBuilder =
            SecretProofTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                EntityTypeDto.SECRET_PROOF_TRANSACTION,
                new AmountDto(getFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                LockHashAlgorithmDto.rawValueOf((byte) hashType.getValue()),
                new Hash256Dto(getSecretBuffer()),
                new UnresolvedAddressDto(this.recipient.getByteBuffer()),
                getProofBuffer());
        return txBuilder.serialize();
    }

    /**
     * Gets the embedded tx bytes.
     *
     * @return Embedded tx bytes
     */
    @Override
    byte[] generateEmbeddedBytes() {
        EmbeddedSecretProofTransactionBuilder txBuilder =
            EmbeddedSecretProofTransactionBuilder.create(
                new KeyDto(getSignerBytes().get()),
                getNetworkVersion(),
                EntityTypeDto.SECRET_PROOF_TRANSACTION,
                LockHashAlgorithmDto.rawValueOf((byte) hashType.getValue()),
                new Hash256Dto(getSecretBuffer()),
                new UnresolvedAddressDto(this.recipient.getByteBuffer()),
                getProofBuffer());
        return txBuilder.serialize();
    }

    private ByteBuffer getSecretBuffer() {
        final ByteBuffer secretBuffer = ByteBuffer.allocate(32);
        secretBuffer.put(Hex.decode(secret));
        return secretBuffer;
    }

    /**
     * Gets proof buffer
     *
     * @return Proof buffer.
     */
    private ByteBuffer getProofBuffer() {
        final byte[] proofBytes = Hex.decode(proof);
        final ByteBuffer proofBuffer = ByteBuffer.wrap(proofBytes);
        return proofBuffer;
    }
}
