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

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test of {@link SerializationUtils} */
public class SerializationUtilsTest {

  @Test
  void toUnresolvedAddressFromNamespaceId() {

    NamespaceId namespaceId = NamespaceId.createFromName("this.currency");

    Assertions.assertEquals("BDED68013AAEE068", namespaceId.getIdAsHex());

    Assertions.assertEquals(
        "9968E0AE3A0168EDBD000000000000000000000000000000",
        ConvertUtils.toHex(
            SerializationUtils.fromUnresolvedAddressToByteBuffer(namespaceId, NetworkType.TEST_NET)
                .array()));

    Assertions.assertEquals(
        "6968E0AE3A0168EDBD000000000000000000000000000000",
        ConvertUtils.toHex(
            SerializationUtils.fromUnresolvedAddressToByteBuffer(namespaceId, NetworkType.MAIN_NET)
                .array()));
  }

  @Test
  void intToUnsignedLong() {
    Assertions.assertEquals(
        ((long) Integer.MAX_VALUE) + 1,
        SerializationUtils.intToUnsignedLong(Integer.MAX_VALUE + 1));

    Assertions.assertEquals(10, SerializationUtils.intToUnsignedLong(10));

    Assertions.assertEquals(4294967295L, SerializationUtils.intToUnsignedLong(-1));
  }
}
