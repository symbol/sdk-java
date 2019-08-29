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

package io.nem.sdk.model.receipt;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Enum containing receipt type constants.
 *
 * @see <a https://github.com/nemtech/catapult-server/blob/master/src/catapult/model/ReceiptType.h
 * />
 * @see <a https://github.com/nemtech/catapult-server/blob/master/src/catapult/model/ReceiptType.cpp
 * />
 */
public enum ReceiptType {
    /**
     * The recipient, account and amount of fees received for harvesting a block. It is recorded
     * when a block is harvested.
     */
    HARVEST_FEE(0x2143),
    /**
     * The unresolved and resolved alias. It is recorded when a transaction indicates a valid
     * address alias instead of an address.
     */
    ADDRESS_ALIAS_RESOLUTION(0xF143),
    /**
     * The unresolved and resolved alias. It is recorded when a transaction indicates a valid mosaic
     * alias instead of a mosaicId.
     */
    MOSAIC_ALIAS_RESOLUTION(0xF243),
    /**
     * A collection of state changes for a given source. It is recorded when a state change receipt
     * is issued.
     */
    TRANSACTION_GROUP(0xE143),
    /**
     * The mosaicId expiring in this block. It is recorded when a mosaic expires.
     */
    MOSAIC_EXPIRED(0x414D),
    /**
     * The sender and recipient of the levied mosaic, the mosaicId and amount. It is recorded when a
     * transaction has a levied mosaic.
     */
    MOSAIC_LEVY(0x124D),
    /**
     * The sender and recipient of the mosaicId and amount representing the cost of registering the
     * mosaic. It is recorded when a mosaic is registered.
     */
    MOSAIC_RENTAL_FEE(0x134D),
    /**
     * The namespaceId expiring in this block. It is recorded when a namespace expires.
     */
    NAMESPACE_EXPIRED(0x414E),
    /**
     * The sender and recipient of the mosaicId and amount representing the cost of extending the
     * namespace. It is recorded when a namespace is registered or its duration is extended.
     */
    NAMESPACE_RENTAL_FEE(0x124E),
    /**
     * The lockhash sender, mosaicId and amount locked. It is recorded when a valid
     * HashLockTransaction is announced.
     */
    LOCK_HASH_CREATED(0x3148),
    /**
     * The haslock sender, mosaicId and amount locked that is returned. It is recorded when an
     * aggregate bonded transaction linked to the hash completes.
     */
    LOCK_HASH_COMPLETED(0x2248),
    /**
     * The account receiving the locked mosaic, the mosaicId and the amount. It is recorded when a
     * lock hash expires.
     */
    LOCK_HASH_EXPIRED(0x2348),
    /**
     * The secretlock sender, mosaicId and amount locked. It is recorded when a valid
     * SecretLockTransaction is announced.
     */
    LOCK_SECRET_CREATED(0x3152),
    /**
     * The secretlock sender, mosaicId and amount locked. It is recorded when a secretlock is
     * proved.
     */
    LOCK_SECRET_COMPLETED(0x2252),
    /**
     * The account receiving the locked mosaic, the mosaicId and the amount. It is recorded when a
     * secretlock expires
     */
    LOCK_SECRET_EXPIRED(0x2352),

    /**
     * The amount of native currency mosaics created. The receipt is recorded when the network has
     * inflation configured, and a new block triggers the creation of currency mosaics.
     */
    INFLATION(0x5143);

    public static final Set<ReceiptType> ARTIFACT_EXPIRY = Collections.unmodifiableSet(EnumSet
        .of(MOSAIC_EXPIRED, NAMESPACE_EXPIRED));

    public static final Set<ReceiptType> BALANCE_CHANGE = Collections.unmodifiableSet(EnumSet.of(
        HARVEST_FEE,
        LOCK_HASH_COMPLETED,
        LOCK_HASH_CREATED,
        LOCK_HASH_CREATED,
        LOCK_HASH_EXPIRED,
        LOCK_SECRET_COMPLETED,
        LOCK_SECRET_CREATED,
        LOCK_SECRET_EXPIRED));

    public static final Set<ReceiptType> BALANCE_TRANSFER =
        Collections.unmodifiableSet(EnumSet.of(MOSAIC_RENTAL_FEE, NAMESPACE_RENTAL_FEE));

    public static final Set<ReceiptType> RESOLUTION_STATEMENT =
        Collections.unmodifiableSet(EnumSet.of(ADDRESS_ALIAS_RESOLUTION, MOSAIC_ALIAS_RESOLUTION));

    private final int value;

    ReceiptType(int value) {
        this.value = value;
    }

    /**
     * Static constructor converting receipt type raw value to enum instance.
     *
     * @return {@link ReceiptType}
     */
    public static ReceiptType rawValueOf(int value) {
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
