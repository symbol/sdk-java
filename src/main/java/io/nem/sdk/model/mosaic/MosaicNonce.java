package io.nem.sdk.model.mosaic;

import io.nem.core.utils.ByteUtils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class MosaicNonce {
    /**
     * Mosaic nonce
     */
    private final byte[] nonce;

    /**
     *
     * @return nonce
     */
    public byte[] getNonce() {
        return nonce;
    }

    /**
     *
     * @return nonce long
     */
    public int getNonceAsInt() {
        return ByteUtils.bytesToInt(this.nonce);
    }

    /**
     * Create MosaicNonce from byte array
     *
     * @param nonce
     */
    public MosaicNonce(byte[] nonce) {
        this.nonce = nonce;
    }

    /**
     * Create a random MosaicNonce
     *
     * @return MosaicNonce nonce
     */
    public static MosaicNonce createRandom() {
        byte NO_OF_RANDOM_BYTES = 4;
        byte[] bytes = new byte[NO_OF_RANDOM_BYTES]; // the array to be filled in with random bytes
        try {
            SecureRandom.getInstanceStrong().nextBytes(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalIdentifierException("NoSuchAlgorithmException:" + e);
        }
        return new MosaicNonce(bytes);
    }

    /**
     * Create a MosaicNonce from hexadecimal notation.
     *
     * @param   hex
     * @throws IllegalIdentifierException
     * @return MosaicNonce
     */
    public static MosaicNonce createFromHex(String hex) {
        byte[] bytes;
        try {
            bytes = Hex.decodeHex(hex);
            if (bytes.length != 4) {
                throw new IllegalIdentifierException("Expected 4 bytes for Nonce but got " + bytes.length + " instead.");
            }
        } catch (DecoderException e) {
            throw new IllegalIdentifierException("DecoderException:" + e);
        }
        return new MosaicNonce(bytes);
    }

    /**
     * Create a MosaicNonce from a BigInteger.
     *
     * @param number
     * @return MosaicNonce
     */
    public static MosaicNonce createFromBigInteger(BigInteger number) {
        /*byte[] bytes = number.toByteArray();
        int size = bytes.length;
        bytes = Arrays.copyOfRange(bytes, (size <= 4) ? 0 : size - 4, (size <= 4) ? 4 : size);
        return new MosaicNonce(bytes);*/

        return new MosaicNonce(ByteUtils.bigIntToBytesOfSize(number, 4));
    }
}
