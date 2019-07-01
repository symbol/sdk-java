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

package io.nem.core.utils;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

public class StringEncoderTest {

    private static final byte[] ENCODED_SIGMA_BYTES = new byte[]{
            0x53, 0x69, 0x67, 0x6D, 0x61
    };

    private static final byte[] ENCODED_CURRENCY_SYMBOLS_BYTES = new byte[]{
            0x24, (byte) 0xC2, (byte) 0xA2, (byte) 0xE2, (byte) 0x82, (byte) 0xAC
    };

    private static final byte[] ENCODED_ADDRESS_BYTES = new byte[]{
            83, 68, 85, 80, 53, 80, 76, 72, 68, 88, 75, 66, 88, 51, 85, 85, 53, 81, 53, 50, 76, 65, 89, 52, 87, 89, 69, 75, 71, 69, 87, 67, 54, 73, 66, 51, 86, 66, 70, 77
    };

    @Test
    public void stringCanBeConvertedToByteArray() {
        // Assert:
        Assert.assertThat(StringEncoder.getBytes("Sigma"), IsEqual.equalTo(ENCODED_SIGMA_BYTES));
        Assert.assertThat(StringEncoder.getBytes("$¢€"), IsEqual.equalTo(ENCODED_CURRENCY_SYMBOLS_BYTES));
    }

    @Test
    public void byteArrayCanBeConvertedToString() {
        // Assert:
        Assert.assertThat(StringEncoder.getString(ENCODED_SIGMA_BYTES), IsEqual.equalTo("Sigma"));
        Assert.assertThat(StringEncoder.getString(ENCODED_CURRENCY_SYMBOLS_BYTES), IsEqual.equalTo("$¢€"));
    }

    @Test
    public void stringCanBeConvertedToByteBuffer() {
        // Assert:
        Assert.assertThat(StringEncoder.getByteBuffer("Sigma").array(), IsEqual.equalTo(ENCODED_SIGMA_BYTES));
        Assert.assertThat(StringEncoder.getByteBuffer("$¢€").array(), IsEqual.equalTo(ENCODED_CURRENCY_SYMBOLS_BYTES));
    }

    @Test
    public void stringCanBeConvertedToByteArray2() {
        // Assert:
        byte[] bytes = StringEncoder.getBytes("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM");
        System.out.println(ByteUtils.unsignedBytesToString(bytes));
        Assert.assertThat(StringEncoder.getBytes("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"), IsEqual.equalTo(ENCODED_ADDRESS_BYTES));
        Assert.assertThat(StringEncoder.getByteBuffer("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM").array(), IsEqual.equalTo(ENCODED_ADDRESS_BYTES));
    }
}
