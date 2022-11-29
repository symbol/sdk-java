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
package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyRevocationTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyRevocationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MosaicRevocationTransactionIntegrationTest extends BaseIntegrationTest {

  private Account account;

  @BeforeEach
  void setup() {
    account = config().getDefaultAccount();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void standaloneMosaicSupplyRevocationTransaction(RepositoryType type) {

    Address recipient = Address.generateRandom(NetworkType.TEST_NET);
    MosaicId mosaicId = helper().createMosaic(account, type, BigInteger.valueOf(1000), null);
    doMosaicTransfer(type, recipient, mosaicId, BigInteger.valueOf(100));

    MosaicSupplyRevocationTransaction mosaicSupplyRevocationTransaction =
        MosaicSupplyRevocationTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                recipient,
                new Mosaic(mosaicId, BigInteger.valueOf(100)))
            .maxFee(maxFee)
            .build();

    announceAndValidate(type, account, mosaicSupplyRevocationTransaction);
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void aggregateMosaicSupplyRevocationTransaction(RepositoryType type) {

    Address recipient = Address.generateRandom(NetworkType.TEST_NET);
    MosaicId mosaicId = helper().createMosaic(account, type, BigInteger.valueOf(1000), null);
    doMosaicTransfer(type, recipient, mosaicId, BigInteger.valueOf(100));

    MosaicSupplyRevocationTransaction mosaicSupplyRevocationTransaction =
        MosaicSupplyRevocationTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                recipient,
                new Mosaic(mosaicId, BigInteger.valueOf(50)))
            .maxFee(maxFee)
            .build();

    announceAggregateAndValidate(type, mosaicSupplyRevocationTransaction, account);
  }

  private void doMosaicTransfer(
      RepositoryType type, Address recipient, MosaicId mosaicId, BigInteger amount) {
    TransferTransaction transferTransaction =
        TransferTransactionFactory.create(
                getNetworkType(),
                getDeadline(),
                recipient,
                Collections.singletonList(new Mosaic(mosaicId, amount)))
            .maxFee(maxFee)
            .build();
    announceAndValidate(type, account, transferTransaction);
  }
}
