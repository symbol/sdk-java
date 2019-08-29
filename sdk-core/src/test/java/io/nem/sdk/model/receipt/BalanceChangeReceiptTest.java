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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BalanceChangeReceiptTest {

    static Account account;
    static MosaicId mosaicId;

    @BeforeAll
    public static void setup() {
        account =
            new Account(
                "787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d",
                NetworkType.MIJIN_TEST);
        mosaicId = new MosaicId("85BBEA6CC462B244");
    }

    @Test
    void shouldCreateHarvestFeeReceipt() {

        BalanceChangeReceipt balanceChangeReceipt =
            new BalanceChangeReceipt(
                account.getPublicAccount(),
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.HARVEST_FEE,
                ReceiptVersion.BALANCE_CHANGE);
        assertEquals(balanceChangeReceipt.getType(), ReceiptType.HARVEST_FEE);
        assertEquals(balanceChangeReceipt.getSize(), null);
        assertEquals(balanceChangeReceipt.getVersion(), ReceiptVersion.BALANCE_CHANGE);
        assertEquals(
            balanceChangeReceipt.getAccount().getPublicKey().toString().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals(balanceChangeReceipt.getMosaicId().getIdAsHex().toUpperCase(),
            "85BBEA6CC462B244");
        assertEquals(balanceChangeReceipt.getAmount(), BigInteger.TEN);
    }

    @Test
    void shouldCreateLockHashCreatedReceipt() {

        BalanceChangeReceipt balanceChangeReceipt =
            new BalanceChangeReceipt(
                account.getPublicAccount(),
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.LOCK_HASH_CREATED,
                ReceiptVersion.BALANCE_CHANGE);
        assertEquals(balanceChangeReceipt.getType(), ReceiptType.LOCK_HASH_CREATED);
        assertEquals(balanceChangeReceipt.getSize(), null);
        assertEquals(balanceChangeReceipt.getVersion(), ReceiptVersion.BALANCE_CHANGE);
        assertEquals(
            balanceChangeReceipt.getAccount().getPublicKey().toString().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals(balanceChangeReceipt.getMosaicId().getIdAsHex().toUpperCase(),
            "85BBEA6CC462B244");
        assertEquals(balanceChangeReceipt.getAmount(), BigInteger.TEN);
    }

    @Test
    void shouldCreateLockHashExpiredReceipt() {

        BalanceChangeReceipt balanceChangeReceipt =
            new BalanceChangeReceipt(
                account.getPublicAccount(),
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.LOCK_HASH_EXPIRED,
                ReceiptVersion.BALANCE_CHANGE);
        assertEquals(balanceChangeReceipt.getType(), ReceiptType.LOCK_HASH_EXPIRED);
        assertEquals(balanceChangeReceipt.getSize(), null);
        assertEquals(balanceChangeReceipt.getVersion(), ReceiptVersion.BALANCE_CHANGE);
        assertEquals(
            balanceChangeReceipt.getAccount().getPublicKey().toString().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals(balanceChangeReceipt.getMosaicId().getIdAsHex().toUpperCase(),
            "85BBEA6CC462B244");
        assertEquals(balanceChangeReceipt.getAmount(), BigInteger.TEN);
    }

    @Test
    void shouldCreateLockHashCompletedReceipt() {

        BalanceChangeReceipt balanceChangeReceipt =
            new BalanceChangeReceipt(
                account.getPublicAccount(),
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.LOCK_HASH_COMPLETED,
                ReceiptVersion.BALANCE_CHANGE);
        assertEquals(balanceChangeReceipt.getType(), ReceiptType.LOCK_HASH_COMPLETED);
        assertEquals(balanceChangeReceipt.getSize(), null);
        assertEquals(balanceChangeReceipt.getVersion(), ReceiptVersion.BALANCE_CHANGE);
        assertEquals(
            balanceChangeReceipt.getAccount().getPublicKey().toString().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals(balanceChangeReceipt.getMosaicId().getIdAsHex().toUpperCase(),
            "85BBEA6CC462B244");
        assertEquals(balanceChangeReceipt.getAmount(), BigInteger.TEN);
    }

    @Test
    void shouldCreateLockSecretCreatedReceipt() {

        BalanceChangeReceipt balanceChangeReceipt =
            new BalanceChangeReceipt(
                account.getPublicAccount(),
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.LOCK_SECRET_CREATED,
                ReceiptVersion.BALANCE_CHANGE);
        assertEquals(balanceChangeReceipt.getType(), ReceiptType.LOCK_SECRET_CREATED);
        assertEquals(balanceChangeReceipt.getSize(), null);
        assertEquals(balanceChangeReceipt.getVersion(), ReceiptVersion.BALANCE_CHANGE);
        assertEquals(
            balanceChangeReceipt.getAccount().getPublicKey().toString().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals(balanceChangeReceipt.getMosaicId().getIdAsHex().toUpperCase(),
            "85BBEA6CC462B244");
        assertEquals(balanceChangeReceipt.getAmount(), BigInteger.TEN);
    }

    @Test
    void shouldCreateLockSecretExpiredReceipt() {

        BalanceChangeReceipt balanceChangeReceipt =
            new BalanceChangeReceipt(
                account.getPublicAccount(),
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.LOCK_SECRET_EXPIRED,
                ReceiptVersion.BALANCE_CHANGE);
        assertEquals(balanceChangeReceipt.getType(), ReceiptType.LOCK_SECRET_EXPIRED);
        assertEquals(balanceChangeReceipt.getSize(), null);
        assertEquals(balanceChangeReceipt.getVersion(), ReceiptVersion.BALANCE_CHANGE);
        assertEquals(
            balanceChangeReceipt.getAccount().getPublicKey().toString().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals(balanceChangeReceipt.getMosaicId().getIdAsHex().toUpperCase(),
            "85BBEA6CC462B244");
        assertEquals(balanceChangeReceipt.getAmount(), BigInteger.TEN);
    }

    @Test
    void shouldCreateLockSecretCompletedReceipt() {

        BalanceChangeReceipt balanceChangeReceipt =
            new BalanceChangeReceipt(
                account.getPublicAccount(),
                mosaicId,
                BigInteger.valueOf(10),
                ReceiptType.LOCK_SECRET_COMPLETED,
                ReceiptVersion.BALANCE_CHANGE);
        assertEquals(balanceChangeReceipt.getType(), ReceiptType.LOCK_SECRET_COMPLETED);
        assertEquals(balanceChangeReceipt.getSize(), null);
        assertEquals(balanceChangeReceipt.getVersion(), ReceiptVersion.BALANCE_CHANGE);
        assertEquals(
            balanceChangeReceipt.getAccount().getPublicKey().toString().toUpperCase(),
            account.getPublicKey().toUpperCase());
        assertEquals(balanceChangeReceipt.getMosaicId().getIdAsHex().toUpperCase(),
            "85BBEA6CC462B244");
        assertEquals(balanceChangeReceipt.getAmount(), BigInteger.TEN);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithWrongReceiptType() {
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                new BalanceChangeReceipt(
                    account.getPublicAccount(),
                    mosaicId,
                    BigInteger.valueOf(10),
                    ReceiptType.NAMESPACE_EXPIRED,
                    ReceiptVersion.BALANCE_CHANGE);
            });
    }
}
