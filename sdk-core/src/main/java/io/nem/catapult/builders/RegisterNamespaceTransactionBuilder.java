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

/** Binary layout for a non-embedded register namespace transaction. */
public final class RegisterNamespaceTransactionBuilder extends TransactionBuilder {
  /** Register namespace transaction body. */
  private final RegisterNamespaceTransactionBodyBuilder registerNamespaceTransactionBody;

  /**
   * Constructor - Creates an object from stream.
   *
   * @param stream Byte stream to use to serialize the object.
   */
  protected RegisterNamespaceTransactionBuilder(final DataInput stream) {
    super(stream);
    this.registerNamespaceTransactionBody =
        RegisterNamespaceTransactionBodyBuilder.loadFromBinary(stream);
  }

  /**
   * Constructor.
   *
   * @param signature Entity signature.
   * @param signer Entity signer's public key.
   * @param version Entity version.
   * @param type Entity type.
   * @param fee Transaction fee.
   * @param deadline Transaction deadline.
   * @param duration Namespace duration.
   * @param namespaceId Id of the namespace.
   * @param name Namespace name.
   */
  protected RegisterNamespaceTransactionBuilder(
      final SignatureDto signature,
      final KeyDto signer,
      final short version,
      final EntityTypeDto type,
      final AmountDto fee,
      final TimestampDto deadline,
      final BlockDurationDto duration,
      final NamespaceIdDto namespaceId,
      final ByteBuffer name) {
    super(signature, signer, version, type, fee, deadline);
    this.registerNamespaceTransactionBody =
        RegisterNamespaceTransactionBodyBuilder.create(duration, namespaceId, name);
  }

  /**
   * Constructor.
   *
   * @param signature Entity signature.
   * @param signer Entity signer's public key.
   * @param version Entity version.
   * @param type Entity type.
   * @param fee Transaction fee.
   * @param deadline Transaction deadline.
   * @param parentId Id of the parent namespace.
   * @param namespaceId Id of the namespace.
   * @param name Namespace name.
   */
  protected RegisterNamespaceTransactionBuilder(
      final SignatureDto signature,
      final KeyDto signer,
      final short version,
      final EntityTypeDto type,
      final AmountDto fee,
      final TimestampDto deadline,
      final NamespaceIdDto parentId,
      final NamespaceIdDto namespaceId,
      final ByteBuffer name) {
    super(signature, signer, version, type, fee, deadline);
    this.registerNamespaceTransactionBody =
        RegisterNamespaceTransactionBodyBuilder.create(parentId, namespaceId, name);
  }

  /**
   * Creates an instance of RegisterNamespaceTransactionBuilder.
   *
   * @param signature Entity signature.
   * @param signer Entity signer's public key.
   * @param version Entity version.
   * @param type Entity type.
   * @param fee Transaction fee.
   * @param deadline Transaction deadline.
   * @param duration Namespace duration.
   * @param namespaceId Id of the namespace.
   * @param name Namespace name.
   * @return Instance of RegisterNamespaceTransactionBuilder.
   */
  public static RegisterNamespaceTransactionBuilder create(
      final SignatureDto signature,
      final KeyDto signer,
      final short version,
      final EntityTypeDto type,
      final AmountDto fee,
      final TimestampDto deadline,
      final BlockDurationDto duration,
      final NamespaceIdDto namespaceId,
      final ByteBuffer name) {
    return new RegisterNamespaceTransactionBuilder(
        signature, signer, version, type, fee, deadline, duration, namespaceId, name);
  }

  /**
   * Creates an instance of RegisterNamespaceTransactionBuilder.
   *
   * @param signature Entity signature.
   * @param signer Entity signer's public key.
   * @param version Entity version.
   * @param type Entity type.
   * @param fee Transaction fee.
   * @param deadline Transaction deadline.
   * @param parentId Id of the parent namespace.
   * @param namespaceId Id of the namespace.
   * @param name Namespace name.
   * @return Instance of RegisterNamespaceTransactionBuilder.
   */
  public static RegisterNamespaceTransactionBuilder create(
      final SignatureDto signature,
      final KeyDto signer,
      final short version,
      final EntityTypeDto type,
      final AmountDto fee,
      final TimestampDto deadline,
      final NamespaceIdDto parentId,
      final NamespaceIdDto namespaceId,
      final ByteBuffer name) {
    return new RegisterNamespaceTransactionBuilder(
        signature, signer, version, type, fee, deadline, parentId, namespaceId, name);
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
   * Creates an instance of RegisterNamespaceTransactionBuilder from a stream.
   *
   * @param stream Byte stream to use to serialize the object.
   * @return Instance of RegisterNamespaceTransactionBuilder.
   */
  public static RegisterNamespaceTransactionBuilder loadFromBinary(final DataInput stream) {
    return new RegisterNamespaceTransactionBuilder(stream);
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
