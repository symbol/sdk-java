package io.nem.core.model;

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fernando on 27/07/19.
 *
 * @author Fernando Boucquez
 */
public class AddressTest {


    private String plain = "SAKQRU2RTNWMBE3KAQRTA46T3EB6DX567FO4EBFL";
    private String pretty = "SAKQRU-2RTNWM-BE3KAQ-RTA46T-3EB6DX-567FO4-EBFL";
    private String encoded = "901508D3519B6CC0936A04233073D3D903E1DFBEF95DC204AB";
    private String publicKey = "089E931203F63EECF695DB94957B03E1A6B7941532069B687386D6D4A7B6BE4A";
    private NetworkType networkType = NetworkType.MIJIN_TEST;

    @Test
    public void testAddresses() {
        assertAddress(Address.createFromRawAddress(plain));
        assertAddress(Address.createFromRawAddress(pretty));
        assertAddress(Address.createFromEncoded(encoded));
        assertAddress(Address.createFromPublicKey(publicKey, networkType));
    }

    private void assertAddress(Address address) {
        Assert.assertEquals(plain, address.plain());
        Assert.assertEquals(pretty, address.pretty());
        Assert.assertEquals(networkType, address.getNetworkType());
    }


    @Test
    public void testAddresses2() {
        Address address = Address
            .createFromPublicKey("B630EFDDFADCC4A2077AB8F1EC846B08FEE2D2972EACF95BBAC6BFAC3D31834C",
                networkType);
        Assert.assertEquals("SCUNEE-EE4FON-6N3DKD-FZUM6U-V7AU26-ZFTWT5-6NUX", address.pretty());
        Assert.assertEquals("SCUNEEEE4FON6N3DKDFZUM6UV7AU26ZFTWT56NUX", address.plain());
    }
}




