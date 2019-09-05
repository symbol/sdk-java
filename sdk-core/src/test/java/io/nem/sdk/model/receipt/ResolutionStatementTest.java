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

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.AddressAlias;
import io.nem.sdk.model.namespace.MosaicAlias;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ResolutionStatementTest {

    static Address address;
    static MosaicId mosaicId;
    static AddressAlias addressAlias;
    static MosaicAlias mosaicAlias;
    static ReceiptSource receiptSource;
    static ResolutionEntry<AddressAlias> addressAliasResolutionEntry;
    static ResolutionEntry<MosaicAlias> mosaicAliasResolutionEntry;

    @BeforeAll
    public static void setup() {
        address = new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26",
            NetworkType.MIJIN_TEST);
        mosaicId = new MosaicId("85BBEA6CC462B244");
        addressAlias = new AddressAlias(address);
        mosaicAlias = new MosaicAlias(mosaicId);
        receiptSource = new ReceiptSource(1, 1);
        addressAliasResolutionEntry =
            new ResolutionEntry(addressAlias, receiptSource, ReceiptType.ADDRESS_ALIAS_RESOLUTION);
        mosaicAliasResolutionEntry =
            new ResolutionEntry(mosaicAlias, receiptSource, ReceiptType.MOSAIC_ALIAS_RESOLUTION);
    }

    @Test
    void shouldCreateAddressResolutionStatement() {
        List<ResolutionEntry<AddressAlias>> resolutionEntries = new ArrayList<>();
        resolutionEntries.add(addressAliasResolutionEntry);
        ResolutionStatement<Address> resolutionStatement =
            new ResolutionStatement(BigInteger.TEN, address, resolutionEntries);
        assertEquals(BigInteger.TEN, resolutionStatement.getHeight());
        assertEquals(resolutionStatement.getUnresolved(), address);
        assertEquals(resolutionStatement.getResolutionEntries(), resolutionEntries);
    }

    @Test
    void shouldCreateMosaicResolutionStatement() {
        List<ResolutionEntry<MosaicAlias>> resolutionEntries = new ArrayList<>();
        resolutionEntries.add(mosaicAliasResolutionEntry);
        ResolutionStatement<Address> resolutionStatement =
            new ResolutionStatement(BigInteger.TEN, mosaicId, resolutionEntries);
        assertEquals(BigInteger.TEN, resolutionStatement.getHeight());
        assertEquals(resolutionStatement.getUnresolved(), mosaicId);
        assertEquals(resolutionStatement.getResolutionEntries(), resolutionEntries);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithWrongUnResolvedType() {
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                List<ResolutionEntry<MosaicAlias>> resolutionEntries = new ArrayList<>();
                resolutionEntries.add(mosaicAliasResolutionEntry);
                new ResolutionStatement(BigInteger.TEN, "", resolutionEntries);
            });
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWithMismatchedUnresolvedAndResolvedType() {
        assertThrows(
            IllegalArgumentException.class,
            () -> {
                List<ResolutionEntry<MosaicAlias>> resolutionEntries = new ArrayList<>();
                resolutionEntries.add(mosaicAliasResolutionEntry);
                new ResolutionStatement(BigInteger.TEN, address, resolutionEntries);
            });
    }
}
