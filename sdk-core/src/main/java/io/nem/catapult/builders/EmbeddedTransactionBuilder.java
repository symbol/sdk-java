/**
 * ** Copyright (c) 2016-present, ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights
 * reserved. ** ** This file is part of Catapult. ** ** Catapult is free software: you can
 * redistribute it and/or modify ** it under the terms of the GNU Lesser General Public License as
 * published by ** the Free Software Foundation, either version 3 of the License, or ** (at your
 * option) any later version. ** ** Catapult is distributed in the hope that it will be useful, **
 * but WITHOUT ANY WARRANTY; without even the implied warranty of ** MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the ** GNU Lesser General Public License for more details. ** ** You
 * should have received a copy of the GNU Lesser General Public License ** along with Catapult. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package io.nem.catapult.builders;

import java.io.DataInput;

/** Binary layout for an embedded transaction. */
public class EmbeddedTransactionBuilder {
  /** Entity size. */
  private int size;
  /** Entity signer's public key. */
  private final KeyDto signer;
  /** Entity version. */
  private final short version;
  /** Entity type. */
  private final EntityTypeDto type;

  /**
   * Constructor - Creates an object from stream.
   *
   * @param stream Byte stream to use to serialize the object.
   */
  protected EmbeddedTransactionBuilder(final DataInput stream) {
    try {
      this.size = Integer.reverseBytes(stream.readInt());
      this.signer = KeyDto.loadFromBinary(stream);
      this.version = Short.reverseBytes(stream.readShort());
      this.type = EntityTypeDto.loadFromBinary(stream);
    } catch (Exception e) {
      throw GeneratorUtils.getExceptionToPropagate(e);
    }
  }

  /**
   * Constructor.
   *
   * @param signer Entity signer's public key.
   * @param version Entity version.
   * @param type Entity type.
   */
  protected EmbeddedTransactionBuilder(
      final KeyDto signer, final short version, final EntityTypeDto type) {
    GeneratorUtils.notNull(signer, "signer is null");
    GeneratorUtils.notNull(type, "type is null");
    this.signer = signer;
    this.version = version;
    this.type = type;
  }

  /**
   * Creates an instance of EmbeddedTransactionBuilder.
   *
   * @param signer Entity signer's public key.
   * @param version Entity version.
   * @param type Entity type.
   * @return Instance of EmbeddedTransactionBuilder.
   */
  public static EmbeddedTransactionBuilder create(
      final KeyDto signer, final short version, final EntityTypeDto type) {
    return new EmbeddedTransactionBuilder(signer, version, type);
  }

  /**
   * Gets the size if created from a stream otherwise zero.
   *
   * @return Object size from stream.
   */
  protected int getStreamSize() {
    return this.size;
  }

  /**
   * Gets entity signer's public key.
   *
   * @return Entity signer's public key.
   */
  public KeyDto getSigner() {
    return this.signer;
  }

  /**
   * Gets entity version.
   *
   * @return Entity version.
   */
  public short getVersion() {
    return this.version;
  }

  /**
   * Gets entity type.
   *
   * @return Entity type.
   */
  public EntityTypeDto getType() {
    return this.type;
  }

  /**
   * Gets the size of the object.
   *
   * @return Size in bytes.
   */
  public int getSize() {
    int size = 0;
    size += 4; // size
    size += this.signer.getSize();
    size += 2; // version
    size += this.type.getSize();
    return size;
  }

  /**
   * Creates an instance of EmbeddedTransactionBuilder from a stream.
   *
   * @param stream Byte stream to use to serialize the object.
   * @return Instance of EmbeddedTransactionBuilder.
   */
  public static EmbeddedTransactionBuilder loadFromBinary(final DataInput stream) {
    return new EmbeddedTransactionBuilder(stream);
  }

  /**
   * Serializes an object to bytes.
   *
   * @return Serialized bytes.
   */
  public byte[] serialize() {
    return GeneratorUtils.serialize(
        dataOutputStream -> {
          dataOutputStream.writeInt(Integer.reverseBytes(this.getSize()));
          final byte[] signerBytes = this.signer.serialize();
          dataOutputStream.write(signerBytes, 0, signerBytes.length);
          dataOutputStream.writeShort(Short.reverseBytes(this.getVersion()));
          final byte[] typeBytes = this.type.serialize();
          dataOutputStream.write(typeBytes, 0, typeBytes.length);
        });
  }
}
