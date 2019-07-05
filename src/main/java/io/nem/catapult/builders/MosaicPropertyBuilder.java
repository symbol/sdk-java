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

/** Mosaic property compose of an id and a value. */
public class MosaicPropertyBuilder {
  /** Mosaic property id. */
  private final MosaicPropertyIdDto id;
  /** Mosaic property value. */
  private final long value;

  /**
   * Constructor - Creates an object from stream.
   *
   * @param stream Byte stream to use to serialize the object.
   */
  protected MosaicPropertyBuilder(final DataInput stream) {
    try {
      this.id = MosaicPropertyIdDto.loadFromBinary(stream);
      this.value = Long.reverseBytes(stream.readLong());
    } catch (Exception e) {
      throw GeneratorUtils.getExceptionToPropagate(e);
    }
  }

  /**
   * Constructor.
   *
   * @param id Mosaic property id.
   * @param value Mosaic property value.
   */
  protected MosaicPropertyBuilder(final MosaicPropertyIdDto id, final long value) {
    GeneratorUtils.notNull(id, "id is null");
    this.id = id;
    this.value = value;
  }

  /**
   * Creates an instance of MosaicPropertyBuilder.
   *
   * @param id Mosaic property id.
   * @param value Mosaic property value.
   * @return Instance of MosaicPropertyBuilder.
   */
  public static MosaicPropertyBuilder create(final MosaicPropertyIdDto id, final long value) {
    return new MosaicPropertyBuilder(id, value);
  }

  /**
   * Gets mosaic property id.
   *
   * @return Mosaic property id.
   */
  public MosaicPropertyIdDto getId() {
    return this.id;
  }

  /**
   * Gets mosaic property value.
   *
   * @return Mosaic property value.
   */
  public long getValue() {
    return this.value;
  }

  /**
   * Gets the size of the object.
   *
   * @return Size in bytes.
   */
  public int getSize() {
    int size = 0;
    size += this.id.getSize();
    size += 8; // value
    return size;
  }

  /**
   * Creates an instance of MosaicPropertyBuilder from a stream.
   *
   * @param stream Byte stream to use to serialize the object.
   * @return Instance of MosaicPropertyBuilder.
   */
  public static MosaicPropertyBuilder loadFromBinary(final DataInput stream) {
    return new MosaicPropertyBuilder(stream);
  }

  /**
   * Serializes an object to bytes.
   *
   * @return Serialized bytes.
   */
  public byte[] serialize() {
    return GeneratorUtils.serialize(
        dataOutputStream -> {
          final byte[] idBytes = this.id.serialize();
          dataOutputStream.write(idBytes, 0, idBytes.length);
          dataOutputStream.writeLong(Long.reverseBytes(this.getValue()));
        });
  }
}
