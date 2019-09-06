package io.nem.sdk.model.mosaic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class MosaicNonceTest {

    @Test
    void createRandomNonce() {
        MosaicNonce nonce = MosaicNonce.createRandom();
        assertNotNull(nonce.getNonce());
    }

    @Test
    void createRandomNonceTwiceNotTheSame() {
        MosaicNonce nonce1 = MosaicNonce.createRandom();
        MosaicNonce nonce2 = MosaicNonce.createRandom();
        assertNotNull(nonce1.getNonce());
        assertNotNull(nonce2.getNonce());
        assertFalse(Arrays.equals(nonce1.getNonce(), nonce2.getNonce()));
    }

    @Test
    void createNonceFromHexadecimalString() {
        MosaicNonce nonce = MosaicNonce.createFromHex("00000000");
        assertNotNull(nonce.getNonce());
        assertTrue(Arrays.equals(nonce.getNonce(), new byte[]{0x0, 0x0, 0x0, 0x0}));
    }

    @Test
    void createNonceFromHexadecimalStringTwiceNotTheSame() {
        MosaicNonce nonce1 = MosaicNonce.createFromHex("00000000");
        MosaicNonce nonce2 = MosaicNonce.createFromHex("FFFFFFFF");
        assertNotNull(nonce1.getNonce());
        assertNotNull(nonce2.getNonce());
        assertTrue(Arrays.equals(nonce1.getNonce(), new byte[]{0x0, 0x0, 0x0, 0x0}));
        assertTrue(Arrays.equals(nonce2.getNonce(), new byte[]{-0x1, -0x1, -0x1, -0x1}));
        assertFalse(Arrays.equals(nonce1.getNonce(), nonce2.getNonce()));
    }

    @Test
    void createNonceFromBigInteger() {
        MosaicNonce nonce1 = MosaicNonce.createFromBigInteger(new BigInteger("0"));
        MosaicNonce nonce2 = MosaicNonce.createFromBigInteger(new BigInteger("4294967295"));
        assertNotNull(nonce1.getNonce());
        assertNotNull(nonce2.getNonce());
        assertEquals(0, nonce1.getNonceAsInt());
        assertEquals(nonce2.getNonceAsInt(), new BigInteger("4294967295").intValue());
        assertTrue(
            Arrays.equals(nonce1.getNonce(), MosaicNonce.createFromHex("00000000").getNonce()));
        assertTrue(
            Arrays.equals(nonce2.getNonce(), MosaicNonce.createFromHex("FFFFFFFF").getNonce()));
    }
}
