package io.nem.catapult.builders;

import java.io.DataInput;

/**
 * a cosignature.
 */
public class CosignatureBuilder {
	/**
	 * cosigner public key.
	 */
	private final KeyDto signer;
	/**
	 * cosigner signature.
	 */
	private final SignatureDto signature;

	/**
	 * Constructor - Create object from stream.
	 *
	 * @param stream Byte stream to use to serialize the object.
	 */
	protected CosignatureBuilder(final DataInput stream) {
		this.signer = KeyDto.loadFromBinary(stream);
		this.signature = SignatureDto.loadFromBinary(stream);
	}

	/**
	 * Constructor.
	 *
	 * @param signer    cosigner public key.
	 * @param signature cosigner signature.
	 */
	protected CosignatureBuilder(final KeyDto signer, final SignatureDto signature) {
		GeneratorUtils.notNull(signer, "signer is null");
		GeneratorUtils.notNull(signature, "signature is null");
		this.signer = signer;
		this.signature = signature;
	}

	/**
	 * Create an instance of CosignatureBuilder.
	 *
	 * @param signer    cosigner public key.
	 * @param signature cosigner signature.
	 * @return An instance of CosignatureBuilder.
	 */
	public static CosignatureBuilder create(final KeyDto signer, final SignatureDto signature) {
		return new CosignatureBuilder(signer, signature);
	}

	/**
	 * loadFromBinary - Create an instance of CosignatureBuilder from a stream.
	 *
	 * @param stream Byte stream to use to serialize the object.
	 * @return An instance of CosignatureBuilder.
	 * @throws Exception failed to deserialize from stream.
	 */
	public static CosignatureBuilder loadFromBinary(final DataInput stream) throws Exception {
		return new CosignatureBuilder(stream);
	}

	/**
	 * Get cosigner public key.
	 *
	 * @return cosigner public key.
	 */
	public KeyDto getSigner() {
		return this.signer;
	}

	/**
	 * Get cosigner signature.
	 *
	 * @return cosigner signature.
	 */
	public SignatureDto getSignature() {
		return this.signature;
	}

	/**
	 * Get the size of the object.
	 *
	 * @return Size in bytes.
	 */
	public int getSize() {
		int size = 0;
		size += this.signer.getSize();
		size += this.signature.getSize();
		return size;
	}

	/**
	 * Serialize the object to bytes.
	 *
	 * @return Serialized bytes.
	 */
	public byte[] serialize() {
		return GeneratorUtils.serialize(dataOutputStream -> {
			final byte[] signerBytes = this.signer.serialize();
			dataOutputStream.write(signerBytes, 0, signerBytes.length);
			final byte[] signatureBytes = this.signature.serialize();
			dataOutputStream.write(signatureBytes, 0, signatureBytes.length);
		});
	}
}
