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

package io.nem.sdk.infrastructure;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.model.transaction.UInt64;
import java.math.BigInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class UInt64Test {

    static Stream<Arguments> provider() {
        return Stream.of(
            Arguments.of(new int[]{100, 0}, BigInteger.valueOf(100)),
            Arguments.of(new int[]{1000, 0}, BigInteger.valueOf(1000)),
            Arguments.of(new int[]{100000, 0}, BigInteger.valueOf(100000)),
            Arguments.of(new int[]{12345, 99999}, new BigInteger("429492434645049")),
            Arguments.of(new int[]{1111, 2222}, new BigInteger("9543417332823")),
            Arguments.of(new int[]{373240754, -467074897}, new BigInteger("-2006071407024327758")),
            Arguments.of(
                new int[]{373240754, new Long("3827892399").intValue()},
                new BigInteger("-2006071407024327758")),
            Arguments.of(new int[]{373240754, 1680408751}, new BigInteger("7217300629830448050")));
    }

    static Stream<Arguments> provider2() {
        return Stream.of(
            Arguments.of("84b3552d375ffa4b", new BigInteger("-8884663987180930485")),
            Arguments.of("0A96B3A44615B62F", new BigInteger("762994705017714223")),
            Arguments.of("5CE4E38B09F1423D", new BigInteger("6693725132486165053")));
    }

    @Test
    void zeroShouldReturnAnArrayOfTwoWithTwoZeros() {
        int[] result = UInt64.fromBigInteger(BigInteger.valueOf(0));
        assertArrayEquals(new int[]{0, 0}, result);
    }

    @Test
    void oneShouldBeReturnedAsIntArray() {
        int[] result = UInt64.fromBigInteger(BigInteger.valueOf(1));
        assertArrayEquals(new int[]{1, 0}, result);
    }

    @Test
    void bigIntegerNEMToHex() {
        String result = UInt64.bigIntegerToHex(new BigInteger("-8884663987180930485"));
        assertEquals("84b3552d375ffa4b", result);
    }

    @Test
    void bigIntegerSUBNEMToHex() {
        String result = UInt64.bigIntegerToHex(new BigInteger("7217300629830448050"));
        assertEquals("642900af163f33b2", result);
    }

    @ParameterizedTest
    @MethodSource("provider2")
    void testBigIntegerToHex(String expected, BigInteger input) {
        String result = UInt64.bigIntegerToHex(input);
        assertEquals(expected.toLowerCase(), result);
    }

    @ParameterizedTest
    @MethodSource("provider")
    void testUInt64FromBigInteger(int[] expected, BigInteger input) {
        int[] result = UInt64.fromBigInteger(input);
        assertArrayEquals(expected, result);
    }

    @ParameterizedTest
    @MethodSource("provider")
    void testUInt64FromIntArray(int[] input, BigInteger expected) {
        BigInteger result = UInt64.fromIntArray(input);
        assertEquals(expected, result);
    }
}
