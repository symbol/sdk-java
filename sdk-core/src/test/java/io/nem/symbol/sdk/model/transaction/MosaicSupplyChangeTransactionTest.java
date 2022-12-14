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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicSupplyChangeActionType;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MosaicSupplyChangeTransactionTest extends AbstractTransactionTester {

  @Test
  void createAMosaicSupplyChangeTransactionViaConstructor() {

    Duration epochAdjustment = Duration.ofSeconds(100);
    MosaicSupplyChangeTransaction mosaicSupplyChangeTx =
        MosaicSupplyChangeTransactionFactory.create(
                NetworkType.TEST_NET,
                Deadline.create(epochAdjustment),
                new MosaicId(new BigInteger("6300565133566699912")),
                MosaicSupplyChangeActionType.INCREASE,
                BigInteger.valueOf(10))
            .build();

    assertEquals(NetworkType.TEST_NET, mosaicSupplyChangeTx.getNetworkType());
    assertEquals(1, mosaicSupplyChangeTx.getVersion());
    assertTrue(
        LocalDateTime.now()
            .isBefore(mosaicSupplyChangeTx.getDeadline().getLocalDateTime(epochAdjustment)));
    assertEquals(BigInteger.valueOf(0), mosaicSupplyChangeTx.getMaxFee());
    assertEquals(new BigInteger("6300565133566699912"), mosaicSupplyChangeTx.getMosaicId().getId());
    assertEquals(MosaicSupplyChangeActionType.INCREASE, mosaicSupplyChangeTx.getAction());
    assertEquals(BigInteger.valueOf(10), mosaicSupplyChangeTx.getDelta());
  }

  @Test
  @DisplayName("Serialization")
  void serialization() {
    String expected =
        "91000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001984D42000000000000000001000000000000008869746E9B1A70570A0000000000000001";

    MosaicSupplyChangeTransaction transaction =
        MosaicSupplyChangeTransactionFactory.create(
                NetworkType.TEST_NET,
                new Deadline(BigInteger.ONE),
                new MosaicId(new BigInteger("6300565133566699912")),
                MosaicSupplyChangeActionType.INCREASE,
                BigInteger.valueOf(10))
            .build();

    byte[] actual = transaction.serialize();
    assertEquals(expected, ConvertUtils.toHex(actual));

    assertSerialization(expected, transaction);
  }
}
