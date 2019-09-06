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

import java.util.Arrays;

/**
 * Enum containing transaction type constants.
 */
public enum TransactionType {

    /**
     * RESERVED
     */
    RESERVED(0),

    // Mosaic
    /**
     * Mosaic definition transaction type.
     */
    MOSAIC_DEFINITION(0x414D),

    /**
     * Mosaic supply change transaction.
     */
    MOSAIC_SUPPLY_CHANGE(0x424D),

    // Namespace
    /**
     * Register namespace transaction type.
     */
    REGISTER_NAMESPACE(0x414E),

    /**
     * Address alias transaction type.
     */
    ADDRESS_ALIAS(0x424E),

    /**
     * Mosaic alias transaction type.
     */
    MOSAIC_ALIAS(0x434E),

    // Transfer
    /**
     * Transfer Transaction transaction type.
     */
    TRANSFER(0x4154),

    // Multisignature
    /**
     * Modify multisig account transaction type.
     */
    MODIFY_MULTISIG_ACCOUNT(0x4155),

    /**
     * Aggregate complete transaction type.
     */
    AGGREGATE_COMPLETE(0x4141),

    /**
     * Aggregate bonded transaction type
     */
    AGGREGATE_BONDED(0x4241),

    /**
     * Hash Lock transaction type
     */
    LOCK(0x4148),

    // Account filters
    /**
     * Account properties address transaction type
     */
    ACCOUNT_PROPERTIES_ADDRESS(0x4150),

    /**
     * Account properties mosaic transaction type
     */
    ACCOUNT_PROPERTIES_MOSAIC(0x4250),

    /**
     * Account properties entity type transaction type
     */
    ACCOUNT_PROPERTIES_ENTITY_TYPE(0x4350),

    // Cross-chain swaps
    /**
     * Secret Lock Transaction type
     */
    SECRET_LOCK(0x4152),

    /**
     * Secret Proof transaction type
     */
    SECRET_PROOF(0x4252),

    /**
     * Account link transaction type
     */
    ACCOUNT_LINK(0x414C);

    private final int value;

    TransactionType(int value) {
        this.value = value;
    }

    /**
     * Static constructor converting transaction type raw value to enum instance.
     *
     * @return {@link TransactionType}
     */
    public static TransactionType rawValueOf(int value) {
        return Arrays.stream(values()).filter(e -> e.value == value).findFirst()
            .orElseThrow(() -> new IllegalArgumentException(value + " is not a valid value"));
    }

    /**
     * Returns enum value.
     *
     * @return enum value
     */
    public int getValue() {
        return this.value;
    }
}
