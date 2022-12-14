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

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.NamespaceMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.openapi.vertx.model.NamespaceMetadataTransactionDTO;
import java.math.BigInteger;

/** Namespace metadata transaction mapper. */
class NamespaceMetadataTransactionMapper
    extends AbstractTransactionMapper<
        NamespaceMetadataTransactionDTO, NamespaceMetadataTransaction> {

  public NamespaceMetadataTransactionMapper(JsonHelper jsonHelper) {
    super(jsonHelper, TransactionType.NAMESPACE_METADATA, NamespaceMetadataTransactionDTO.class);
  }

  @Override
  protected NamespaceMetadataTransactionFactory createFactory(
      NetworkType networkType, Deadline deadline, NamespaceMetadataTransactionDTO transaction) {
    UnresolvedAddress targetAddress =
        MapperUtils.toUnresolvedAddress(transaction.getTargetAddress());
    Integer valueSizeDelta = transaction.getValueSizeDelta();
    BigInteger scopedMetaDataKey =
        MapperUtils.fromHexToBigInteger(transaction.getScopedMetadataKey());
    byte[] value = ConvertUtils.fromHexToBytes(transaction.getValue());
    NamespaceId targetNamespace = MapperUtils.toNamespaceId(transaction.getTargetNamespaceId());
    NamespaceMetadataTransactionFactory factory =
        NamespaceMetadataTransactionFactory.create(
            networkType, deadline, targetAddress, targetNamespace, scopedMetaDataKey, value);
    factory.valueSizeDelta(valueSizeDelta);
    Long valueSize = transaction.getValueSize();
    if (valueSize != null) {
      factory.valueSize(valueSize);
    }
    return factory;
  }

  @Override
  protected void copyToDto(
      NamespaceMetadataTransaction transaction, NamespaceMetadataTransactionDTO dto) {
    dto.setTargetAddress(transaction.getTargetAddress().encoded(transaction.getNetworkType()));
    dto.setTargetNamespaceId(MapperUtils.getIdAsHex(transaction.getTargetNamespaceId()));
    dto.setScopedMetadataKey(MapperUtils.fromBigIntegerToHex(transaction.getScopedMetadataKey()));
    dto.setValue(ConvertUtils.toHex(transaction.getValue()));
    dto.setValueSizeDelta(transaction.getValueSizeDelta());
    dto.setValueSize(transaction.getValueSize());
  }
}
