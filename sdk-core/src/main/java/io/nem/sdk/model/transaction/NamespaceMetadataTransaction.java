/*
 * Copyright 2019. NEM
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */

package io.nem.sdk.model.transaction;

import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.EmbeddedNamespaceMetadataTransactionBuilder;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.NamespaceIdDto;
import io.nem.catapult.builders.NamespaceMetadataTransactionBuilder;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.namespace.NamespaceId;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Announce an NameMetadataTransaction to associate a key-value state to an namespace.
 */
public class NamespaceMetadataTransaction extends Transaction {

    /**
     * Metadata target public key.
     */
    private final PublicAccount targetAccount;
    /**
     * Metadata target Namespace id.
     */
    private final NamespaceId targetNamespaceId;

    /**
     * Metadata key scoped to source, target and type.
     */
    private final BigInteger scopedMetadataKey;
    /**
     * Change in value size in bytes.
     */
    private final int valueSizeDelta;

    /**
     * Value size in bytes.
     */
    private final int valueSize;

    /**
     * When there is an existing value, the new value is calculated as xor(previous-value, value).
     */
    private final String value;

    /**
     * Constructor
     *
     * @param factory the factory with the configured data.
     */
    NamespaceMetadataTransaction(NamespaceMetadataTransactionFactory factory) {
        super(factory);
        this.targetAccount = factory.getTargetAccount();
        this.targetNamespaceId = factory.getTargetNamespaceId();
        this.scopedMetadataKey = factory.getScopedMetadataKey();
        this.valueSizeDelta = factory.getValueSizeDelta();
        this.valueSize = factory.getValueSize();
        this.value = factory.getValue();
    }

    public PublicAccount getTargetAccount() {
        return targetAccount;
    }

    public NamespaceId getTargetNamespaceId() {
        return targetNamespaceId;
    }

    public BigInteger getScopedMetadataKey() {
        return scopedMetadataKey;
    }

    public int getValueSizeDelta() {
        return valueSizeDelta;
    }

    public int getValueSize() {
        return valueSize;
    }

    public String getValue() {
        return value;
    }

    @Override
    byte[] generateBytes() {
        // Add place holders to the signer and signature until actually signed
        final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
        final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

        NamespaceMetadataTransactionBuilder txBuilder =
            NamespaceMetadataTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                getEntityTypeDto(),
                new AmountDto(getMaxFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                new KeyDto(this.getTargetAccount().getPublicKey().getByteBuffer()),
                this.getScopedMetadataKey().longValue(),
                new NamespaceIdDto(getTargetNamespaceId().getId().longValue()),
                (short) getValueSizeDelta(),
                getValueBuffer()
            );
        return txBuilder.serialize();
    }

    @Override
    byte[] generateEmbeddedBytes() {
        EmbeddedNamespaceMetadataTransactionBuilder txBuilder =
            EmbeddedNamespaceMetadataTransactionBuilder.create(
                new KeyDto(getRequiredSignerBytes()),
                getNetworkVersion(),
                getEntityTypeDto(),
                new KeyDto(this.getTargetAccount().getPublicKey().getByteBuffer()),
                this.getScopedMetadataKey().longValue(),
                new NamespaceIdDto(getTargetNamespaceId().getId().longValue()),
                (short) getValueSizeDelta(),
                getValueBuffer()
            );
        return txBuilder.serialize();
    }

    /**
     * Gets value buffer
     *
     * @return Value buffer.
     */
    private ByteBuffer getValueBuffer() {
        return ByteBuffer.wrap(value.getBytes());
    }
}
