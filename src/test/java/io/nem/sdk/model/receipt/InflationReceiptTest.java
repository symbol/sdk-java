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

import io.nem.sdk.model.mosaic.MosaicId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InflationReceiptTest {
    static MosaicId mosaicId;

    @BeforeAll
    public static void setup() {
        mosaicId = new MosaicId("85BBEA6CC462B244");
    }

    @Test
    void shouldCreateInflationReceipt() {

        InflationReceipt inflationReceipt =
                new InflationReceipt(mosaicId, BigInteger.valueOf(10), ReceiptType.Inflation, ReceiptVersion.INFLATION_RECEIPT);
        assertEquals(inflationReceipt.getType(), ReceiptType.Inflation);
        assertEquals(inflationReceipt.getSize(), null);
        assertEquals(inflationReceipt.getVersion(), ReceiptVersion.INFLATION_RECEIPT);
        assertEquals(inflationReceipt.getMosaicId().getIdAsHex().toUpperCase(), "85BBEA6CC462B244");
        assertEquals(inflationReceipt.getAmount(), BigInteger.TEN);
    }

    @Test
    void shouldCreateInflationReceiptWithSize() {

        InflationReceipt inflationReceipt =
                new InflationReceipt(mosaicId, BigInteger.valueOf(10), ReceiptType.Inflation, ReceiptVersion.INFLATION_RECEIPT, Optional.of(100));
        assertEquals(inflationReceipt.getType(), ReceiptType.Inflation);
        assertEquals(inflationReceipt.getSize(), Optional.of(100));
        assertEquals(inflationReceipt.getVersion(), ReceiptVersion.INFLATION_RECEIPT);
        assertEquals(inflationReceipt.getMosaicId().getIdAsHex().toUpperCase(), "85BBEA6CC462B244");
        assertEquals(inflationReceipt.getAmount(), BigInteger.TEN);
        assertEquals(inflationReceipt.getSize().get().intValue(), 100);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithWrongReceiptType() {
        assertThrows(IllegalArgumentException.class, () -> {
            new InflationReceipt(mosaicId, BigInteger.valueOf(10), ReceiptType.Namespace_Rental_Fee, ReceiptVersion.INFLATION_RECEIPT);
        });
    }
}
