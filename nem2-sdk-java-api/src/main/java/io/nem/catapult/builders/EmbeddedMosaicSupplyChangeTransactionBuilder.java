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

/** Binary layout for an embedded mosaic supply change transaction. */
public final class EmbeddedMosaicSupplyChangeTransactionBuilder extends EmbeddedTransactionBuilder {
  /** Mosaic supply change transaction body. */
  private final MosaicSupplyChangeTransactionBodyBuilder mosaicSupplyChangeTransactionBody;

  /**
   * Constructor - Creates an object from stream.
   *
   * @param stream Byte stream to use to serialize the object.
   */
  protected EmbeddedMosaicSupplyChangeTransactionBuilder(final DataInput stream) {
    super(stream);
    this.mosaicSupplyChangeTransactionBody =
        MosaicSupplyChangeTransactionBodyBuilder.loadFromBinary(stream);
  }

  /**
   * Constructor.
   *
   * @param signer Entity signer's public key.
   * @param version Entity version.
   * @param type Entity type.
   * @param mosaicId Id of the affected mosaic.
   * @param direction Supply change direction.
   * @param delta Amount of the change.
   */
  protected EmbeddedMosaicSupplyChangeTransactionBuilder(
      final KeyDto signer,
      final short version,
      final EntityTypeDto type,
      final UnresolvedMosaicIdDto mosaicId,
      final MosaicSupplyChangeDirectionDto direction,
      final AmountDto delta) {
    super(signer, version, type);
    this.mosaicSupplyChangeTransactionBody =
        MosaicSupplyChangeTransactionBodyBuilder.create(mosaicId, direction, delta);
  }

  /**
   * Creates an instance of EmbeddedMosaicSupplyChangeTransactionBuilder.
   *
   * @param signer Entity signer's public key.
   * @param version Entity version.
   * @param type Entity type.
   * @param mosaicId Id of the affected mosaic.
   * @param direction Supply change direction.
   * @param delta Amount of the change.
   * @return Instance of EmbeddedMosaicSupplyChangeTransactionBuilder.
   */
  public static EmbeddedMosaicSupplyChangeTransactionBuilder create(
      final KeyDto signer,
      final short version,
      final EntityTypeDto type,
      final UnresolvedMosaicIdDto mosaicId,
      final MosaicSupplyChangeDirectionDto direction,
      final AmountDto delta) {
    return new EmbeddedMosaicSupplyChangeTransactionBuilder(
        signer, version, type, mosaicId, direction, delta);
  }

  /**
   * Gets id of the affected mosaic.
   *
   * @return Id of the affected mosaic.
   */
  public UnresolvedMosaicIdDto getMosaicId() {
    return this.mosaicSupplyChangeTransactionBody.getMosaicId();
  }

  /**
   * Gets supply change direction.
   *
   * @return Supply change direction.
   */
  public MosaicSupplyChangeDirectionDto getDirection() {
    return this.mosaicSupplyChangeTransactionBody.getDirection();
  }

  /**
   * Gets amount of the change.
   *
   * @return Amount of the change.
   */
  public AmountDto getDelta() {
    return this.mosaicSupplyChangeTransactionBody.getDelta();
  }

  /**
   * Gets the size of the object.
   *
   * @return Size in bytes.
   */
  @Override
  public int getSize() {
    int size = super.getSize();
    size += this.mosaicSupplyChangeTransactionBody.getSize();
    return size;
  }

  /**
   * Creates an instance of EmbeddedMosaicSupplyChangeTransactionBuilder from a stream.
   *
   * @param stream Byte stream to use to serialize the object.
   * @return Instance of EmbeddedMosaicSupplyChangeTransactionBuilder.
   */
  public static EmbeddedMosaicSupplyChangeTransactionBuilder loadFromBinary(
      final DataInput stream) {
    return new EmbeddedMosaicSupplyChangeTransactionBuilder(stream);
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
          final byte[] mosaicSupplyChangeTransactionBodyBytes =
              this.mosaicSupplyChangeTransactionBody.serialize();
          dataOutputStream.write(
              mosaicSupplyChangeTransactionBodyBytes,
              0,
              mosaicSupplyChangeTransactionBodyBytes.length);
        });
  }
}
