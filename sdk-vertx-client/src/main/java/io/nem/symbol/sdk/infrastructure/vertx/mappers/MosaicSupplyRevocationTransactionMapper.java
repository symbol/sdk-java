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
package io.nem.symbol.sdk.infrastructure.vertx.mappers;

import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyRevocationTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyRevocationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.MosaicSupplyRevocationTransactionDTO;

/** Mosaic supply revocation transaction mapper. */
class MosaicSupplyRevocationTransactionMapper
    extends AbstractTransactionMapper<
        MosaicSupplyRevocationTransactionDTO, MosaicSupplyRevocationTransaction> {

  public MosaicSupplyRevocationTransactionMapper(JsonHelper jsonHelper) {
    super(
        jsonHelper,
        TransactionType.MOSAIC_SUPPLY_REVOCATION,
        MosaicSupplyRevocationTransactionDTO.class);
  }

  @Override
  protected TransactionFactory<MosaicSupplyRevocationTransaction> createFactory(
      NetworkType networkType,
      Deadline deadline,
      MosaicSupplyRevocationTransactionDTO transaction) {
    return MosaicSupplyRevocationTransactionFactory.create(
        networkType,
        deadline,
        MapperUtils.toUnresolvedAddress(transaction.getSourceAddress()),
        getMosaic(transaction));
  }

  @Override
  protected void copyToDto(
      MosaicSupplyRevocationTransaction transaction, MosaicSupplyRevocationTransactionDTO dto) {
    dto.setMosaicId(MapperUtils.getIdAsHex(transaction.getMosaic().getId()));
    dto.setAmount(transaction.getMosaic().getAmount());
    dto.setSourceAddress(transaction.getSourceAddress().encoded(transaction.getNetworkType()));
  }

  private Mosaic getMosaic(MosaicSupplyRevocationTransactionDTO transaction) {
    return new Mosaic(
        MapperUtils.toUnresolvedMosaicId(transaction.getMosaicId()), transaction.getAmount());
  }
}
