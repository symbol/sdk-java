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

        import io.nem.sdk.model.account.Account;
        import io.nem.sdk.model.account.Address;
        import io.nem.sdk.model.blockchain.NetworkType;
        import io.nem.sdk.model.mosaic.MosaicId;
        import io.nem.sdk.model.namespace.NamespaceId;
        import org.junit.jupiter.api.BeforeAll;
        import org.junit.jupiter.api.Test;

        import java.math.BigInteger;

        import static org.junit.jupiter.api.Assertions.assertEquals;
        import static org.junit.jupiter.api.Assertions.assertThrows;

public class BalanceTransferReceiptTest {
    static Account account;
    static MosaicId mosaicId;
    static Address recipientAddress;
    static NamespaceId recipientAddressAlias;

    @BeforeAll
    public static void setup() {
        account = new Account("787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d", NetworkType.MIJIN_TEST);
        mosaicId = new MosaicId("85BBEA6CC462B244");
        recipientAddress = new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN_TEST);
        recipientAddressAlias = new NamespaceId(new BigInteger("-8884663987180930485"));
    }

    @Test
    void shouldCreateMosaicRentalFeeReceipt() {

        BalanceTransferReceipt<Address> balanceTransferReceipt =
                new BalanceTransferReceipt(account.getPublicAccount(), recipientAddress, mosaicId, BigInteger.valueOf(10), ReceiptType.Mosaic_Rental_Fee, ReceiptVersion.BALANCE_TRANSFER);
        assertEquals(balanceTransferReceipt.getType(), ReceiptType.Mosaic_Rental_Fee);
        assertEquals(balanceTransferReceipt.getSize(), null);
        assertEquals(balanceTransferReceipt.getVersion(), ReceiptVersion.BALANCE_TRANSFER);
        assertEquals(balanceTransferReceipt.getSender().getPublicKey().toUpperCase(), account.getPublicKey().toUpperCase());
        assertEquals(balanceTransferReceipt.getRecipient().pretty(), "SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26");
        assertEquals(balanceTransferReceipt.getMosaicId().getIdAsHex().toUpperCase(), "85BBEA6CC462B244");
        assertEquals(balanceTransferReceipt.getAmount(), BigInteger.TEN);
    }

    @Test
    void shouldCreateMosaicRentalFeeReceiptWithAlias() {

        BalanceTransferReceipt<NamespaceId> balanceTransferReceipt =
                new BalanceTransferReceipt(account.getPublicAccount(), recipientAddressAlias, mosaicId, BigInteger.valueOf(10), ReceiptType.Mosaic_Rental_Fee, ReceiptVersion.BALANCE_TRANSFER);
        assertEquals(balanceTransferReceipt.getType(), ReceiptType.Mosaic_Rental_Fee);
        assertEquals(balanceTransferReceipt.getSize(), null);
        assertEquals(balanceTransferReceipt.getVersion(), ReceiptVersion.BALANCE_TRANSFER);
        assertEquals(balanceTransferReceipt.getSender().getPublicKey().toUpperCase(), account.getPublicKey().toUpperCase());
        assertEquals(balanceTransferReceipt.getRecipient().getId(), new BigInteger("-8884663987180930485"));
        assertEquals(balanceTransferReceipt.getMosaicId().getIdAsHex().toUpperCase(), "85BBEA6CC462B244");
        assertEquals(balanceTransferReceipt.getAmount(), BigInteger.TEN);
    }

    @Test
    void shouldCreateNamespaceRentalFeeReceipt() {

        BalanceTransferReceipt<Address> balanceTransferReceipt =
                new BalanceTransferReceipt(account.getPublicAccount(), recipientAddress, mosaicId, BigInteger.valueOf(10), ReceiptType.Namespace_Rental_Fee, ReceiptVersion.BALANCE_TRANSFER);
        assertEquals(balanceTransferReceipt.getType(), ReceiptType.Namespace_Rental_Fee);
        assertEquals(balanceTransferReceipt.getSize(), null);
        assertEquals(balanceTransferReceipt.getVersion(), ReceiptVersion.BALANCE_TRANSFER);
        assertEquals(balanceTransferReceipt.getSender().getPublicKey().toUpperCase(), account.getPublicKey().toUpperCase());
        assertEquals(balanceTransferReceipt.getRecipient().pretty(), "SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26");
        assertEquals(balanceTransferReceipt.getMosaicId().getIdAsHex().toUpperCase(), "85BBEA6CC462B244");
        assertEquals(balanceTransferReceipt.getAmount(), BigInteger.TEN);
    }

    @Test
    void shouldCreateNamespaceRentalFeeReceiptWithAlias() {

        BalanceTransferReceipt<NamespaceId> balanceTransferReceipt =
                new BalanceTransferReceipt(account.getPublicAccount(), recipientAddressAlias, mosaicId, BigInteger.valueOf(10), ReceiptType.Namespace_Rental_Fee, ReceiptVersion.BALANCE_TRANSFER);
        assertEquals(balanceTransferReceipt.getType(), ReceiptType.Namespace_Rental_Fee);
        assertEquals(balanceTransferReceipt.getSize(), null);
        assertEquals(balanceTransferReceipt.getVersion(), ReceiptVersion.BALANCE_TRANSFER);
        assertEquals(balanceTransferReceipt.getSender().getPublicKey().toUpperCase(), account.getPublicKey().toUpperCase());
        assertEquals(balanceTransferReceipt.getRecipient().getId(), new BigInteger("-8884663987180930485"));
        assertEquals(balanceTransferReceipt.getMosaicId().getIdAsHex().toUpperCase(), "85BBEA6CC462B244");
        assertEquals(balanceTransferReceipt.getAmount(), BigInteger.TEN);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithWrongReceiptType() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BalanceChangeReceipt(account.getPublicAccount(), mosaicId, BigInteger.valueOf(10), ReceiptType.Namespace_Expired, ReceiptVersion.BALANCE_TRANSFER);
        });
    }
}
