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

/** Account restriction basic modification. */
public class AccountRestrictionModificationBuilder {
  /** Modification type. */
  private final AccountRestrictionModificationTypeDto modificationType;

  /**
   * Constructor - Creates an object from stream.
   *
   * @param stream Byte stream to use to serialize the object.
   */
  protected AccountRestrictionModificationBuilder(final DataInput stream) {
    this.modificationType = AccountRestrictionModificationTypeDto.loadFromBinary(stream);
  }

  /**
   * Constructor.
   *
   * @param modificationType Modification type.
   */
  protected AccountRestrictionModificationBuilder(
      final AccountRestrictionModificationTypeDto modificationType) {
    GeneratorUtils.notNull(modificationType, "modificationType is null");
    this.modificationType = modificationType;
  }

  /**
   * Creates an instance of AccountRestrictionModificationBuilder.
   *
   * @param modificationType Modification type.
   * @return Instance of AccountRestrictionModificationBuilder.
   */
  public static AccountRestrictionModificationBuilder create(
      final AccountRestrictionModificationTypeDto modificationType) {
    return new AccountRestrictionModificationBuilder(modificationType);
  }

  /**
   * Gets Modification type.
   *
   * @return Modification type.
   */
  public AccountRestrictionModificationTypeDto getModificationType() {
    return this.modificationType;
  }

  /**
   * Gets the size of the object.
   *
   * @return Size in bytes.
   */
  public int getSize() {
    int size = 0;
    size += this.modificationType.getSize();
    return size;
  }

  /**
   * Creates an instance of AccountRestrictionModificationBuilder from a stream.
   *
   * @param stream Byte stream to use to serialize the object.
   * @return Instance of AccountRestrictionModificationBuilder.
   */
  public static AccountRestrictionModificationBuilder loadFromBinary(final DataInput stream) {
    return new AccountRestrictionModificationBuilder(stream);
  }

  /**
   * Serializes an object to bytes.
   *
   * @return Serialized bytes.
   */
  public byte[] serialize() {
    return GeneratorUtils.serialize(
        dataOutputStream -> {
          final byte[] modificationTypeBytes = this.modificationType.serialize();
          dataOutputStream.write(modificationTypeBytes, 0, modificationTypeBytes.length);
        });
  }
}
