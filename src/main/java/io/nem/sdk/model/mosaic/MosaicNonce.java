package io.nem.sdk.model.mosaic;

import io.nem.core.utils.ByteUtils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Mosaic nonce class
 */
public class MosaicNonce {
	/**
	 * Mosaic nonce
	 */
	private final byte[] nonce;

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
		final byte NO_OF_RANDOM_BYTES = 4;
		byte[] bytes = new byte[NO_OF_RANDOM_BYTES]; // the array to be filled in with random bytes
		try {
			SecureRandom.getInstanceStrong().nextBytes(bytes);
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalIdentifierException("NoSuchAlgorithmException:" + e);
		}
		return new MosaicNonce(bytes);
	}

	/**
	 * Create a MosaicNonce from hexadecimal notation.
	 *
	 * @param hex
	 * @return MosaicNonce
	 * @throws IllegalIdentifierException
	 */
	public static MosaicNonce createFromHex(String hex) {
		try {
			final byte[] bytes = Hex.decodeHex(hex);
			if (bytes.length != 4) {
				throw new IllegalIdentifierException("Expected 4 bytes for Nonce but got " + bytes.length + " instead.");
			}
			return new MosaicNonce(bytes);
		}
		catch (DecoderException e) {
			throw new IllegalIdentifierException("DecoderException:" + e);
		}
	}

	/**
	 * Create a MosaicNonce from a BigInteger.
	 *
	 * @param number
	 * @return MosaicNonce
	 */
	public static MosaicNonce createFromBigInteger(BigInteger number) {
		return new MosaicNonce(ByteUtils.bigIntToBytesOfSize(number, 4));
	}

	/**
	 * @return nonce
	 */
	public byte[] getNonce() {
		return nonce;
	}

	/**
	 * @return nonce int
	 */
	public int getNonceAsInt() {
		return ByteUtils.bytesToInt(this.nonce);
	}
}
