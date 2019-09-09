/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.core.utils;

import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MapperUtilsTest {

    @Test
    void shouldMapToNamespaceId() {
        Assertions.assertNull(MapperUtils.toNamespaceId(null));
        Assertions
            .assertEquals(BigInteger.valueOf(1194684), MapperUtils.toNamespaceId("123ABC").getId());
    }

    @Test
    void shouldMapToAddress() {
        Assertions.assertNull(MapperUtils.toAddress(null));
        Assertions.assertNotNull(MapperUtils.toAddress("SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX"));
    }

    @Test
    void shouldMapToMosaicId() {
        Assertions.assertNull(MapperUtils.toMosaicId(null));
        Assertions
            .assertEquals(BigInteger.valueOf(1194684), MapperUtils.toMosaicId("123ABC").getId());
    }

}
