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

/** Binary layout for an embedded mosaic alias transaction. */
public final class EmbeddedMosaicAliasTransactionBuilder extends EmbeddedTransactionBuilder {
  /** Mosaic alias transaction body. */
  private final MosaicAliasTransactionBodyBuilder mosaicAliasTransactionBody;

  /**
   * Constructor - Creates an object from stream.
   *
   * @param stream Byte stream to use to serialize the object.
   */
  protected EmbeddedMosaicAliasTransactionBuilder(final DataInput stream) {
    super(stream);
    this.mosaicAliasTransactionBody = MosaicAliasTransactionBodyBuilder.loadFromBinary(stream);
  }

  /**
   * Constructor.
   *
   * @param signer Entity signer's public key.
   * @param version Entity version.
   * @param type Entity type.
   * @param aliasAction Alias action.
   * @param namespaceId Id of a namespace that will become an alias.
   * @param mosaicId Aliased mosaic id.
   */
  protected EmbeddedMosaicAliasTransactionBuilder(
      final KeyDto signer,
      final short version,
      final EntityTypeDto type,
      final AliasActionDto aliasAction,
      final NamespaceIdDto namespaceId,
      final MosaicIdDto mosaicId) {
    super(signer, version, type);
    this.mosaicAliasTransactionBody =
        MosaicAliasTransactionBodyBuilder.create(aliasAction, namespaceId, mosaicId);
  }

  /**
   * Creates an instance of EmbeddedMosaicAliasTransactionBuilder.
   *
   * @param signer Entity signer's public key.
   * @param version Entity version.
   * @param type Entity type.
   * @param aliasAction Alias action.
   * @param namespaceId Id of a namespace that will become an alias.
   * @param mosaicId Aliased mosaic id.
   * @return Instance of EmbeddedMosaicAliasTransactionBuilder.
   */
  public static EmbeddedMosaicAliasTransactionBuilder create(
      final KeyDto signer,
      final short version,
      final EntityTypeDto type,
      final AliasActionDto aliasAction,
      final NamespaceIdDto namespaceId,
      final MosaicIdDto mosaicId) {
    return new EmbeddedMosaicAliasTransactionBuilder(
        signer, version, type, aliasAction, namespaceId, mosaicId);
  }

  /**
   * Gets alias action.
   *
   * @return Alias action.
   */
  public AliasActionDto getAliasAction() {
    return this.mosaicAliasTransactionBody.getAliasAction();
  }

  /**
   * Gets id of a namespace that will become an alias.
   *
   * @return Id of a namespace that will become an alias.
   */
  public NamespaceIdDto getNamespaceId() {
    return this.mosaicAliasTransactionBody.getNamespaceId();
  }

  /**
   * Gets aliased mosaic id.
   *
   * @return Aliased mosaic id.
   */
  public MosaicIdDto getMosaicId() {
    return this.mosaicAliasTransactionBody.getMosaicId();
  }

  /**
   * Gets the size of the object.
   *
   * @return Size in bytes.
   */
  @Override
  public int getSize() {
    int size = super.getSize();
    size += this.mosaicAliasTransactionBody.getSize();
    return size;
  }

  /**
   * Creates an instance of EmbeddedMosaicAliasTransactionBuilder from a stream.
   *
   * @param stream Byte stream to use to serialize the object.
   * @return Instance of EmbeddedMosaicAliasTransactionBuilder.
   */
  public static EmbeddedMosaicAliasTransactionBuilder loadFromBinary(final DataInput stream) {
    return new EmbeddedMosaicAliasTransactionBuilder(stream);
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
          final byte[] mosaicAliasTransactionBodyBytes =
              this.mosaicAliasTransactionBody.serialize();
          dataOutputStream.write(
              mosaicAliasTransactionBodyBytes, 0, mosaicAliasTransactionBodyBytes.length);
        });
  }
}
