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

package io.nem.sdk.model.transaction;

import io.nem.core.utils.ByteUtils;
import io.nem.core.utils.HexEncoder;
import org.apache.commons.lang.ArrayUtils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * UInt64 data model to enable numeric representation up to 64 bits.
 * A UInt64 object is composed of two 32-bit numbers: lower and higher.
 */
public class UInt64 {

    /**
     * Static method to convert from BigInteger to UInt64 integer array [lower,higher]
     *
     * @param input BigInteger
     * @return integer array
     */
    public static int[] fromBigInteger(BigInteger input) {
        byte[] bytes = input.toByteArray();
        ArrayUtils.reverse(bytes);
        int lower = 0, higher = 0;
        byte[] lowerBound = new byte[4];
        int size = 4;
        if (bytes.length < 4) {
            size = bytes.length;
        }
        System.arraycopy(bytes, 0, lowerBound, 0, size);
        lower = ByteBuffer.wrap(lowerBound).order(ByteOrder.LITTLE_ENDIAN).getInt();
        if (bytes.length > 4) {
            byte[] higherBound = new byte[4];
            size = 4;
            if (bytes.length - 4 < 4) {
                size = bytes.length - 4;
            }
            System.arraycopy(bytes, 4, higherBound, 0, size);
            higher = ByteBuffer.wrap(higherBound).order(ByteOrder.LITTLE_ENDIAN).getInt();
        }
        return new int[]{lower, higher};
    }

    /**
     * Static method to convert from integer array [lower,higher] to BigInteger
     *
     * @param input integer array
     * @return BigInteger
     */
    public static BigInteger fromIntArray(int[] input) {
        if (input.length != 2) {
            throw new IllegalArgumentException("input must have length 2");
        }
        ArrayUtils.reverse(input);
        byte[] array = new byte[input.length * 4];
        ByteBuffer bbuf = ByteBuffer.wrap(array);
        IntBuffer ibuf = bbuf.asIntBuffer();
        ibuf.put(input);
        return new BigInteger(array);
    }

    /**
     * Static method to convert from lower and higher to BigInteger
     *
     * @param lower long
     * @param higher long
     * @return BigInteger
     */
    public static BigInteger fromLowerAndHigher(long lower, long higher) {
        int[] array = new int[]{(int) lower, (int) higher};
        return UInt64.fromIntArray(array);
    }

    /**
     * Static method to convert from lower and higher to BigInteger
     *
     * @param lower Number
     * @param higher Number
     * @return BigInteger
     */
    public static BigInteger fromLowerAndHigher(Number lower, Number higher) {
        int[] array = new int[] {lower.intValue(), higher.intValue()};
        return UInt64.fromIntArray(array);
    }

    /**
     * Static method to convert from BigInteger to Hexadecimal string
     *
     * @param input BigInteger
     * @return String
     */
    public static String bigIntegerToHex(BigInteger input) {
        byte[] bytes = ByteUtils.bigIntToBytes(input);
        return HexEncoder.getString(bytes);
    }
}
