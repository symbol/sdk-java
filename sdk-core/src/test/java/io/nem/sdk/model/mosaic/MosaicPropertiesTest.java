/*
 * Copyright 2018 NEM
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

package io.nem.sdk.model.mosaic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class MosaicPropertiesTest {

    @Test
    void shouldCreateMosaicPropertiesViaConstructor() {
        MosaicProperties mosaicProperties =
            MosaicProperties.create(true, true, 1, true, BigInteger.valueOf(1000));
        assertTrue(mosaicProperties.isSupplyMutable());
        assertTrue(mosaicProperties.isTransferable());
        assertEquals(1, mosaicProperties.getDivisibility());
        assertTrue(mosaicProperties.isRestrictable());
        assertEquals(
            BigInteger.valueOf(1000).intValue(), mosaicProperties.getDuration().longValue());
    }

    @Test
    void shouldCreateMosaicPropertiesWithDefaultValuesViaConstructor() {
        MosaicProperties mosaicProperties =
            MosaicProperties.create(true, true, 1, BigInteger.valueOf(1000));
        assertTrue(mosaicProperties.isSupplyMutable());
        assertTrue(mosaicProperties.isTransferable());
        assertEquals(1, mosaicProperties.getDivisibility());
        assertTrue(!mosaicProperties.isRestrictable());
        assertEquals(
            BigInteger.valueOf(1000).intValue(), mosaicProperties.getDuration().longValue());
    }
}
