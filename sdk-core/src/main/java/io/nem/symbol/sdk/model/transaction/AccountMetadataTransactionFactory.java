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
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;

/** Factory of {@link AccountMetadataTransaction} */
public class AccountMetadataTransactionFactory
    extends MetadataTransactionFactory<AccountMetadataTransaction> {

  private AccountMetadataTransactionFactory(
      NetworkType networkType,
      Deadline deadline,
      UnresolvedAddress targetAddress,
      BigInteger scopedMetadataKey,
      byte[] value) {
    super(
        TransactionType.ACCOUNT_METADATA,
        networkType,
        deadline,
        targetAddress,
        scopedMetadataKey,
        value);
  }

  /**
   * Static create method for factory.
   *
   * @param networkType Network type.
   * @param deadline the deadline
   * @param targetAddress Target address.
   * @param scopedMetadataKey Scoped metadata key.
   * @param value Value.
   * @return Account metadata transaction.
   */
  public static AccountMetadataTransactionFactory create(
      NetworkType networkType,
      Deadline deadline,
      UnresolvedAddress targetAddress,
      BigInteger scopedMetadataKey,
      byte[] value) {
    return new AccountMetadataTransactionFactory(
        networkType, deadline, targetAddress, scopedMetadataKey, value);
  }

  @Override
  public AccountMetadataTransaction build() {
    return new AccountMetadataTransaction(this);
  }
}
