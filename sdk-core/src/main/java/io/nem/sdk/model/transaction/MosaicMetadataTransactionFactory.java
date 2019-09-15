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

import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

/**
 * Factory of {@link MosaicMetadataTransaction}
 */
public class MosaicMetadataTransactionFactory extends
    TransactionFactory<MosaicMetadataTransaction> {

    /**
     * Metadata target public key.
     */
    private final PublicAccount targetAccount;
    /**
     * Metadata target mosaic id.
     */
    private final MosaicId targetMosaicId;

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

    public MosaicMetadataTransactionFactory(
        NetworkType networkType,
        PublicAccount targetAccount,
        MosaicId targetMosaicId,
        BigInteger scopedMetadataKey,
        int valueSizeDelta,
        int valueSize,
        String value) {
        super(TransactionType.MOSAIC_METADATA_TRANSACTION, networkType);

        Validate.notNull(targetAccount, "TargetAccount must not be null");
        Validate.notNull(targetMosaicId, "TargetMosaicId must not be null");
        Validate.notNull(scopedMetadataKey, "ScopedMetadataKey must not be null");
        Validate.notNull(valueSizeDelta, "ValueSizeDelta must not be null");
        Validate.notNull(valueSize, "ValueSize must not be null");
        Validate.notNull(value, "Value must not be null");

        this.targetAccount = targetAccount;
        this.targetMosaicId = targetMosaicId;
        this.scopedMetadataKey = scopedMetadataKey;
        this.valueSizeDelta = valueSizeDelta;
        this.valueSize = valueSize;
        this.value = value;
    }

    public PublicAccount getTargetAccount() {
        return targetAccount;
    }

    public MosaicId getTargetMosaicId() {
        return targetMosaicId;
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
    public MosaicMetadataTransaction build() {
        return new MosaicMetadataTransaction(this);
    }
}
