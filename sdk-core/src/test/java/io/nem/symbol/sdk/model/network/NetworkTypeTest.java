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
package io.nem.symbol.sdk.model.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NetworkTypeTest {

  @Test
  void MAIN_NETIs0x68() {
    Assertions.assertEquals(0x68, NetworkType.MAIN_NET.getValue());
    assertEquals(104, NetworkType.MAIN_NET.getValue());
  }

  @Test
  void TEST_NETIs0x96() {
    assertEquals(0x98, NetworkType.TEST_NET.getValue());
    assertEquals(152, NetworkType.TEST_NET.getValue());
  }

  @Test
  void rawValueOfNetworkName() {
    assertEquals(NetworkType.MAIN_NET, NetworkType.rawValueOf("mainnet"));
    assertEquals(NetworkType.TEST_NET, NetworkType.rawValueOf("testnet"));
  }

  @Test
  void rawValueOfValue() {
    Arrays.stream(NetworkType.values())
        .forEach(
            networkType ->
                assertEquals(networkType, NetworkType.rawValueOf(networkType.getValue())));
  }

  @Test
  void rawValueOfInvalidName() {
    assertEquals(
        "NotANetworkName is not a valid network name",
        assertThrows(
                IllegalArgumentException.class, () -> NetworkType.rawValueOf("NotANetworkName"))
            .getMessage());
  }

  @Test
  void rawValueOfInvalidValue() {
    assertEquals(
        "10 is not a valid value",
        assertThrows(IllegalArgumentException.class, () -> NetworkType.rawValueOf(10))
            .getMessage());
  }
}
