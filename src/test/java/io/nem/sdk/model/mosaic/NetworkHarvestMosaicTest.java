package io.nem.sdk.model.mosaic;

import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.UInt64;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NetworkHarvestMosaicTest {

    @Test
    void shouldCreateNetworkHarvestMosaicViaConstructor() {
        NetworkHarvestMosaic currency = new NetworkHarvestMosaic(BigInteger.valueOf(0));
        assertEquals(BigInteger.valueOf(0), currency.getAmount());
        assertEquals(NetworkHarvestMosaic.NAMESPACEID, currency.getId());
        assertEquals("941299b2b7e1291c", currency.getIdAsHex());
    }

    @Test
    void shouldCreateRelativeNetworkHarvestMosaic() {
        NetworkHarvestMosaic currency = NetworkHarvestMosaic.createRelative(BigInteger.valueOf(1000));
        assertEquals(BigInteger.valueOf(1000 * 1000), currency.getAmount());
        assertEquals(NetworkHarvestMosaic.NAMESPACEID, currency.getId());
        assertEquals("941299b2b7e1291c", currency.getIdAsHex());
    }

    @Test
    void shouldCreateAbsoluteNetworkHarvestMosaic() {
        NetworkHarvestMosaic currency = NetworkHarvestMosaic.createAbsolute(BigInteger.valueOf(1));
        assertEquals(BigInteger.valueOf(1), currency.getAmount());
        assertEquals(NetworkHarvestMosaic.NAMESPACEID, currency.getId());
        assertEquals("941299b2b7e1291c", currency.getIdAsHex());
    }

    @Test
    void shouldCompareNamespaceIdsForEquality() {
        NamespaceId namespaceId = new NamespaceId(UInt64.fromLowerAndHigher(3084986652L, 2484246962L));
        assertEquals(-7776984613647210212L, namespaceId.getIdAsLong());
        assertEquals(NetworkHarvestMosaic.NAMESPACEID.getIdAsLong(), namespaceId.getIdAsLong());
        assertEquals(NetworkHarvestMosaic.NAMESPACEID.getIdAsHex(), namespaceId.getIdAsHex());
    }

    @Test
    void shouldHaveValidStatics() {
        assertEquals(NetworkHarvestMosaic.DIVISIBILITY, 3);
        assertEquals(NetworkHarvestMosaic.INITIALSUPPLY, new BigInteger("15000000"));
        assertEquals(NetworkHarvestMosaic.TRANSFERABLE, true);
        assertEquals(NetworkHarvestMosaic.SUPPLYMUTABLE, true);
        assertEquals(NetworkHarvestMosaic.LEVYMUTABLE, false);
    }
}
