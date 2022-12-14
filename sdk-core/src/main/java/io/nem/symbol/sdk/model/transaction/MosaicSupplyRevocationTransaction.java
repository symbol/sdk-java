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

/**
 * Creators of a revokable mosaic will be able to recall any and all balances from any holders.
 * Holders of these mosaics implicitly place trust in the issuer. The mosaic issuer can revoke and
 * recall balances using this transaction.
 */
public class MosaicSupplyRevocationTransaction extends Transaction {

  private final UnresolvedAddress sourceAddress;
  private final Mosaic mosaic;

  MosaicSupplyRevocationTransaction(MosaicSupplyRevocationTransactionFactory factory) {
    super(factory);
    this.sourceAddress = factory.getSourceAddress();
    this.mosaic = factory.getMosaic();
  }

  /**
   * Returns address from which tokens should be revoked
   *
   * @return UnresolvedAddress
   */
  public UnresolvedAddress getSourceAddress() {
    return sourceAddress;
  }

  /**
   * Returns mosaic and amount revoked
   *
   * @return Mosaic
   */
  public Mosaic getMosaic() {
    return mosaic;
  }
}
