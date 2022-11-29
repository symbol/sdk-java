/*
 * Copyright 2020 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.nem.symbol.sdk.model.transaction;

import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.network.NetworkType;
import org.apache.commons.lang3.Validate;

/** Factory of {@link MosaicSupplyRevocationTransaction} */
public class MosaicSupplyRevocationTransactionFactory
    extends TransactionFactory<MosaicSupplyRevocationTransaction> {

  private final UnresolvedAddress sourceAddress;
  private final Mosaic mosaic;

  private MosaicSupplyRevocationTransactionFactory(
      NetworkType networkType, Deadline deadline, UnresolvedAddress sourceAddress, Mosaic mosaic) {
    super(TransactionType.MOSAIC_SUPPLY_REVOCATION, networkType, deadline);
    Validate.notNull(sourceAddress, "Source address must not be null");
    Validate.notNull(mosaic, "Mosaic must not be null");
    this.sourceAddress = sourceAddress;
    this.mosaic = mosaic;
  }

  /**
   * Static create method for factory.
   *
   * @param networkType Network type.
   * @param deadline the deadline
   * @param sourceAddress UnresolvedAddress.
   * @param mosaic Mosaic.
   * @return Mosaic supply revoke transaction.
   */
  public static MosaicSupplyRevocationTransactionFactory create(
      NetworkType networkType, Deadline deadline, UnresolvedAddress sourceAddress, Mosaic mosaic) {
    return new MosaicSupplyRevocationTransactionFactory(
        networkType, deadline, sourceAddress, mosaic);
  }

  /**
   * Returns address from which tokens should be revoked
   *
   * @return UnresolvedAddess
   */
  public UnresolvedAddress getSourceAddress() {
    return sourceAddress;
  }

  /**
   * Returns mosaic.
   *
   * @return Mosaic
   */
  public Mosaic getMosaic() {
    return mosaic;
  }

  @Override
  public MosaicSupplyRevocationTransaction build() {
    return new MosaicSupplyRevocationTransaction(this);
  }
}
