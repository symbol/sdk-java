package io.nem.catapult.builders;

import java.io.DataInput;

/** a detached cosignature. */
public final class DetachedCosignatureBuilder extends CosignatureBuilder {
  /** hash of the corresponding parent. */
  private final Hash256Dto parentHash;

  /**
   * Constructor - Create object from stream.
   *
   * @param stream Byte stream to use to serialize the object.
   */
  protected DetachedCosignatureBuilder(final DataInput stream) {
    super(stream);
    this.parentHash = Hash256Dto.loadFromBinary(stream);
  }

  /**
   * Constructor.
   *
   * @param signer cosigner public key.
   * @param signature cosigner signature.
   * @param parentHash hash of the corresponding parent.
   */
  protected DetachedCosignatureBuilder(
      final KeyDto signer, final SignatureDto signature, final Hash256Dto parentHash) {
    super(signer, signature);
    GeneratorUtils.notNull(parentHash, "parentHash is null");
    this.parentHash = parentHash;
  }

  /**
   * Create an instance of DetachedCosignatureBuilder.
   *
   * @param signer cosigner public key.
   * @param signature cosigner signature.
   * @param parentHash hash of the corresponding parent.
   * @return An instance of DetachedCosignatureBuilder.
   */
  public static DetachedCosignatureBuilder create(
      final KeyDto signer, final SignatureDto signature, final Hash256Dto parentHash) {
    return new DetachedCosignatureBuilder(signer, signature, parentHash);
  }

  /**
   * loadFromBinary - Create an instance of DetachedCosignatureBuilder from a stream.
   *
   * @param stream Byte stream to use to serialize the object.
   * @return An instance of DetachedCosignatureBuilder.
   * @throws Exception failed to deserialize from stream.
   */
  public static DetachedCosignatureBuilder loadFromBinary(final DataInput stream) throws Exception {
    return new DetachedCosignatureBuilder(stream);
  }

  /**
   * Get hash of the corresponding parent.
   *
   * @return hash of the corresponding parent.
   */
  public Hash256Dto getParentHash() {
    return this.parentHash;
  }

  /**
   * Get the size of the object.
   *
   * @return Size in bytes.
   */
  @Override
  public int getSize() {
    int size = super.getSize();
    size += this.parentHash.getSize();
    return size;
  }

  /**
   * Serialize the object to bytes.
   *
   * @return Serialized bytes.
   */
  public byte[] serialize() {
    return GeneratorUtils.serialize(
        dataOutputStream -> {
          final byte[] superBytes = super.serialize();
          dataOutputStream.write(superBytes, 0, superBytes.length);
          final byte[] parentHashBytes = this.parentHash.serialize();
          dataOutputStream.write(parentHashBytes, 0, parentHashBytes.length);
        });
  }
}
