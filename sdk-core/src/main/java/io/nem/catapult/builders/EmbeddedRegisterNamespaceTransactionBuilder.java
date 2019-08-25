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
import java.nio.ByteBuffer;

/** Binary layout for an embedded register namespace transaction. */
public final class EmbeddedRegisterNamespaceTransactionBuilder extends EmbeddedTransactionBuilder {
  /** Register namespace transaction body. */
  private final RegisterNamespaceTransactionBodyBuilder registerNamespaceTransactionBody;

  /**
   * Constructor - Creates an object from stream.
   *
   * @param stream Byte stream to use to serialize the object.
   */
  protected EmbeddedRegisterNamespaceTransactionBuilder(final DataInput stream) {
    super(stream);
    this.registerNamespaceTransactionBody =
        RegisterNamespaceTransactionBodyBuilder.loadFromBinary(stream);
  }

  /**
   * Constructor.
   *
   * @param signer Entity signer's public key.
   * @param version Entity version.
   * @param type Entity type.
   * @param duration Namespace duration.
   * @param namespaceId Id of the namespace.
   * @param name Namespace name.
   */
  protected EmbeddedRegisterNamespaceTransactionBuilder(
      final KeyDto signer,
      final short version,
      final EntityTypeDto type,
      final BlockDurationDto duration,
      final NamespaceIdDto namespaceId,
      final ByteBuffer name) {
    super(signer, version, type);
    this.registerNamespaceTransactionBody =
        RegisterNamespaceTransactionBodyBuilder.create(duration, namespaceId, name);
  }

  /**
   * Constructor.
   *
   * @param signer Entity signer's public key.
   * @param version Entity version.
   * @param type Entity type.
   * @param parentId Id of the parent namespace.
   * @param namespaceId Id of the namespace.
   * @param name Namespace name.
   */
  protected EmbeddedRegisterNamespaceTransactionBuilder(
      final KeyDto signer,
      final short version,
      final EntityTypeDto type,
      final NamespaceIdDto parentId,
      final NamespaceIdDto namespaceId,
      final ByteBuffer name) {
    super(signer, version, type);
    this.registerNamespaceTransactionBody =
        RegisterNamespaceTransactionBodyBuilder.create(parentId, namespaceId, name);
  }

  /**
   * Creates an instance of EmbeddedRegisterNamespaceTransactionBuilder.
   *
   * @param signer Entity signer's public key.
   * @param version Entity version.
   * @param type Entity type.
   * @param duration Namespace duration.
   * @param namespaceId Id of the namespace.
   * @param name Namespace name.
   * @return Instance of EmbeddedRegisterNamespaceTransactionBuilder.
   */
  public static EmbeddedRegisterNamespaceTransactionBuilder create(
      final KeyDto signer,
      final short version,
      final EntityTypeDto type,
      final BlockDurationDto duration,
      final NamespaceIdDto namespaceId,
      final ByteBuffer name) {
    return new EmbeddedRegisterNamespaceTransactionBuilder(
        signer, version, type, duration, namespaceId, name);
  }

  /**
   * Creates an instance of EmbeddedRegisterNamespaceTransactionBuilder.
   *
   * @param signer Entity signer's public key.
   * @param version Entity version.
   * @param type Entity type.
   * @param parentId Id of the parent namespace.
   * @param namespaceId Id of the namespace.
   * @param name Namespace name.
   * @return Instance of EmbeddedRegisterNamespaceTransactionBuilder.
   */
  public static EmbeddedRegisterNamespaceTransactionBuilder create(
      final KeyDto signer,
      final short version,
      final EntityTypeDto type,
      final NamespaceIdDto parentId,
      final NamespaceIdDto namespaceId,
      final ByteBuffer name) {
    return new EmbeddedRegisterNamespaceTransactionBuilder(
        signer, version, type, parentId, namespaceId, name);
  }

  /**
   * Gets type of the registered namespace.
   *
   * @return Type of the registered namespace.
   */
  public NamespaceTypeDto getNamespaceType() {
    return this.registerNamespaceTransactionBody.getNamespaceType();
  }

  /**
   * Gets namespace duration.
   *
   * @return Namespace duration.
   */
  public BlockDurationDto getDuration() {
    return this.registerNamespaceTransactionBody.getDuration();
  }

  /**
   * Gets id of the parent namespace.
   *
   * @return Id of the parent namespace.
   */
  public NamespaceIdDto getParentId() {
    return this.registerNamespaceTransactionBody.getParentId();
  }

  /**
   * Gets id of the namespace.
   *
   * @return Id of the namespace.
   */
  public NamespaceIdDto getNamespaceId() {
    return this.registerNamespaceTransactionBody.getNamespaceId();
  }

  /**
   * Gets namespace name.
   *
   * @return Namespace name.
   */
  public ByteBuffer getName() {
    return this.registerNamespaceTransactionBody.getName();
  }

  /**
   * Gets the size of the object.
   *
   * @return Size in bytes.
   */
  @Override
  public int getSize() {
    int size = super.getSize();
    size += this.registerNamespaceTransactionBody.getSize();
    return size;
  }

  /**
   * Creates an instance of EmbeddedRegisterNamespaceTransactionBuilder from a stream.
   *
   * @param stream Byte stream to use to serialize the object.
   * @return Instance of EmbeddedRegisterNamespaceTransactionBuilder.
   */
  public static EmbeddedRegisterNamespaceTransactionBuilder loadFromBinary(final DataInput stream) {
    return new EmbeddedRegisterNamespaceTransactionBuilder(stream);
  }

  /**
   * Serializes an object to bytes.
   *
   * @return Serialized bytes.
   */
  public byte[] serialize() {
    return GeneratorUtils.serialize(
        dataOutputStream -> {
          final byte[] superBytes = super.serialize();
          dataOutputStream.write(superBytes, 0, superBytes.length);
          final byte[] registerNamespaceTransactionBodyBytes =
              this.registerNamespaceTransactionBody.serialize();
          dataOutputStream.write(
              registerNamespaceTransactionBodyBytes,
              0,
              registerNamespaceTransactionBodyBytes.length);
        });
  }
}
