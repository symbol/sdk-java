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

/**
 * Values of restriction for Catbuffer. Values can be composed.
 *
 * For example, Block (0X80) + Outgoing (0x40) + Operation Type (0x04) = 0xC4
 *
 *
 * Restriction type is an address. ADDRESS((byte)1),
 *
 * Restriction type is a mosaic identifier. MOSAIC_ID((byte)2),
 *
 * Restriction type is a transaction type. TRANSACTION_TYPE((byte)4),
 *
 * Restriction is interpreted as outgoing. OUTGOING((byte)64),
 *
 * Restriction is interpreted as blocking operation. BLOCK((byte)128);
 */

class AccountRestrictionTypeValueOptions {

    private AccountRestrictionTypeValueOptions() {
    }

    static final int ADDRESS_VALUE = 0x01;
    static final int MOSAIC_VALUE = 0x02;
    static final int TRANSACTION_TYPE_VALUE = 0x04;
    static final int SENTINEL_VALUE = 0x05;
    static final int OUTGOING_VALUE = 0x40;
    static final int BLOCK_VALUE = 0x80;
}
