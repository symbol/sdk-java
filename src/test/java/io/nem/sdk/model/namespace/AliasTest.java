package io.nem.sdk.model.namespace;

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.UInt64;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class AliasTest {

    Address address = Address.createFromRawAddress("SCTVW23D2MN5VE4AQ4TZIDZENGNOZXPRPRLIKCF2");
    Address address2 = Address.createFromRawAddress("SARNASAS2BIAB6LMFA3FPMGBPGIJGK6IJETM3ZSP");
    MosaicId mosaicId = new MosaicId(UInt64.fromLowerAndHigher(481110499, 231112638));
    MosaicId mosaicId2 = new MosaicId(UInt64.fromLowerAndHigher(481110498, 231112637));

    @Test
    void shouldCreateAEmptyAlias() {
        Alias alias = new EmptyAlias();
        assertEquals(AliasType.None, alias.getType());
    }

    @Test
    void shouldCreateAAddressAlias() {
        AddressAlias addressAlias = new AddressAlias(address);
        assertEquals(AliasType.Address, addressAlias.getType());
        assertEquals(address, addressAlias.getAddress());
    }

    @Test
    void shouldCreateAMosaicAlias() {
        MosaicAlias mosaicAlias = new MosaicAlias(mosaicId);
        assertEquals(AliasType.Mosaic, mosaicAlias.getType());
        assertEquals(mosaicId, mosaicAlias.getMosaicId());
    }

    @Test
    void shouldCompareAddressInAdressAlias() {
        AddressAlias addressAlias = new AddressAlias(address);
        AddressAlias addressAlias1 = new AddressAlias(address);
        AddressAlias addressAlias2 = new AddressAlias(address2);
        assertEquals(addressAlias.getAddress(), addressAlias1.getAddress());
        assertNotEquals(addressAlias.getAddress(), addressAlias2.getAddress());
    }

    @Test
    void shouldCompareMosaicIdInMosaicAlias() {
        MosaicAlias mosaicAlias = new MosaicAlias(mosaicId);
        MosaicAlias mosaicAlias1 = new MosaicAlias(mosaicId);
        MosaicAlias mosaicAlias2 = new MosaicAlias(mosaicId2);
        assertEquals(mosaicAlias.getMosaicId(), mosaicAlias1.getMosaicId());
        assertNotEquals(mosaicAlias.getMosaicId(), mosaicAlias2.getMosaicId());
    }
}
