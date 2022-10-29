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

import io.nem.symbol.sdk.infrastructure.TransactionMapper;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.TransactionType;

/** Aggregate transaction mapper. */
class AggregateTransactionV1Mapper extends AggregateTransactionMapper {

  public AggregateTransactionV1Mapper(
      JsonHelper jsonHelper, TransactionType transactionType, TransactionMapper transactionMapper) {
    super(jsonHelper, transactionType, transactionMapper);
  }

  @Override
  public int getVersion() {
    return 1;
  }
}
