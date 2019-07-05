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
import io.nem.catapult.builders.BlockDurationDto;
import io.nem.catapult.builders.EmbeddedSecretLockTransactionBuilder;
import io.nem.catapult.builders.EntityTypeDto;
import io.nem.catapult.builders.Hash256Dto;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.LockHashAlgorithmDto;
import io.nem.catapult.builders.SecretLockTransactionBuilder;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.catapult.builders.UnresolvedAddressDto;
import io.nem.catapult.builders.UnresolvedMosaicBuilder;
import io.nem.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Optional;
import org.apache.commons.lang3.Validate;
import org.bouncycastle.util.encoders.Hex;

public class SecretLockTransaction extends Transaction {

    private final Mosaic mosaic;
    private final BigInteger duration;
    private final HashType hashType;
    private final String secret;
    private final Address recipient;

    public SecretLockTransaction(
        NetworkType networkType,
        Integer version,
        Deadline deadline,
        BigInteger fee,
        Mosaic mosaic,
        BigInteger duration,
        HashType hashType,
        String secret,
        Address recipient,
        String signature,
        PublicAccount signer,
        TransactionInfo transactionInfo) {
        this(
            networkType,
            version,
            deadline,
            fee,
            mosaic,
            duration,
            hashType,
            secret,
            recipient,
            Optional.of(signature),
            Optional.of(signer),
            Optional.of(transactionInfo));
    }

    public SecretLockTransaction(
        NetworkType networkType,
        Integer version,
        Deadline deadline,
        BigInteger fee,
        Mosaic mosaic,
        BigInteger duration,
        HashType hashType,
        String secret,
        Address recipient) {
        this(
            networkType,
            version,
            deadline,
            fee,
            mosaic,
            duration,
            hashType,
            secret,
            recipient,
            Optional.empty(),
            Optional.empty(),
            Optional.empty());
    }

    public SecretLockTransaction(
        NetworkType networkType,
        Integer version,
        Deadline deadline,
        BigInteger fee,
        Mosaic mosaic,
        BigInteger duration,
        HashType hashType,
        String secret,
        Address recipient,
        Optional<String> signature,
        Optional<PublicAccount> signer,
        Optional<TransactionInfo> transactionInfo) {
        super(
            TransactionType.SECRET_LOCK,
            networkType,
            version,
            deadline,
            fee,
            signature,
            signer,
            transactionInfo);
        Validate.notNull(mosaic, "Mosaic must not be null");
        Validate.notNull(duration, "Duration must not be null");
        Validate.notNull(secret, "Secret must not be null");
        Validate.notNull(recipient, "Recipient must not be null");
        if (!HashType.Validator(hashType, secret)) {
            throw new IllegalArgumentException(
                "HashType and Secret have incompatible length or not hexadecimal string");
        }
        this.mosaic = mosaic;
        this.duration = duration;
        this.hashType = hashType;
        this.secret = secret;
        this.recipient = recipient;
    }

    /**
     * Create a secret lock transaction object.
     *
     * @param deadline The deadline to include the transaction.
     * @param mosaic The locked mosaic.
     * @param duration The duration for the funds to be released or returned.
     * @param hashType The hash algorithm secret is generated with.
     * @param secret The proof hashed.
     * @param recipient The recipient of the funds.
     * @param networkType The network type.
     * @return a SecretLockTransaction instance
     */
    public static SecretLockTransaction create(
        Deadline deadline,
        Mosaic mosaic,
        BigInteger duration,
        HashType hashType,
        String secret,
        Address recipient,
        NetworkType networkType) {
        return new SecretLockTransaction(
            networkType,
            TransactionVersion.SECRET_LOCK.getValue(),
            deadline,
            BigInteger.valueOf(0),
            mosaic,
            duration,
            hashType,
            secret,
            recipient);
    }

    /**
     * Returns locked mosaic.
     *
     * @return locked mosaic.
     */
    public Mosaic getMosaic() {
        return mosaic;
    }

    /**
     * Returns duration for the funds to be released or returned.
     *
     * @return duration for the funds to be released or returned.
     */
    public BigInteger getDuration() {
        return duration;
    }

    /**
     * Returns the hash algorithm, secret is generated with.
     *
     * @return the hash algorithm, secret is generated with.
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
     * Returns the recipient of the funds.
     *
     * @return the recipient of the funds.
     */
    public Address getRecipient() {
        return recipient;
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

        SecretLockTransactionBuilder txBuilder =
            SecretLockTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                EntityTypeDto.SECRET_LOCK_TRANSACTION,
                new AmountDto(getFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                UnresolvedMosaicBuilder.create(
                    new UnresolvedMosaicIdDto(mosaic.getId().getId().longValue()),
                    new AmountDto(mosaic.getAmount().longValue())),
                new BlockDurationDto(duration.longValue()),
                LockHashAlgorithmDto.rawValueOf((byte) hashType.getValue()),
                new Hash256Dto(getSecretBuffer()),
                new UnresolvedAddressDto(getRecipient().getByteBuffer()));
        return txBuilder.serialize();
    }

    /**
     * Gets the embedded tx bytes.
     *
     * @return Embedded tx bytes
     */
    @Override
    byte[] generateEmbeddedBytes() {
        EmbeddedSecretLockTransactionBuilder txBuilder =
            EmbeddedSecretLockTransactionBuilder.create(
                new KeyDto(getSignerBytes().get()),
                getNetworkVersion(),
                EntityTypeDto.SECRET_LOCK_TRANSACTION,
                UnresolvedMosaicBuilder.create(
                    new UnresolvedMosaicIdDto(mosaic.getId().getId().longValue()),
                    new AmountDto(mosaic.getAmount().longValue())),
                new BlockDurationDto(duration.longValue()),
                LockHashAlgorithmDto.rawValueOf((byte) hashType.getValue()),
                new Hash256Dto(getSecretBuffer()),
                new UnresolvedAddressDto(getRecipient().getByteBuffer()));
        return txBuilder.serialize();
    }

    /**
     * Gets secret buffer.
     *
     * @return Secret buffer.
     */
    private ByteBuffer getSecretBuffer() {
        final ByteBuffer secretBuffer = ByteBuffer.allocate(32);
        secretBuffer.put(Hex.decode(secret));
        return secretBuffer;
    }
}
