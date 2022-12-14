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
package io.nem.symbol.core.utils;

import java.math.BigInteger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConvertUtilsTest {

  // region getBytes

  private static void assertGetBytesConversion(final String input, final byte[] expectedOutput) {
    // Act:
    final byte[] output = ConvertUtils.getBytes(input);

    // Assert:
    MatcherAssert.assertThat(output, IsEqual.equalTo(expectedOutput));
  }

  private static void assertGetStringConversion(final byte[] input, final String expectedOutput) {
    // Act:
    final String output = ConvertUtils.toHex(input);

    // Assert:
    MatcherAssert.assertThat(output, IsEqual.equalTo(expectedOutput));
    MatcherAssert.assertThat(input, IsEqual.equalTo(ConvertUtils.fromHexToBytes(output)));
  }

  @Test
  void getBytesCannotConvertMalformedStringToByteArray() {
    // Act:
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> ConvertUtils.getBytes("4e454g465457"));
  }

  @Test
  void getBytesCanConvertValidStringToByteArray() {
    // Assert:
    assertGetBytesConversion("4e454d465457", new byte[] {0x4e, 0x45, 0x4d, 0x46, 0x54, 0x57});
  }

  // endregion

  // region tryGetBytes

  @Test
  void getBytesCanConvertValidStringWithOddLengthToByteArray() {
    // Assert:
    assertGetBytesConversion("e454d465457", new byte[] {0x0e, 0x45, 0x4d, 0x46, 0x54, 0x57});
  }

  @Test
  void getBytesCanConvertValidStringWithLeadingZerosToByteArray() {
    // Assert:
    assertGetBytesConversion("00000d465457", new byte[] {0x00, 0x00, 0x0d, 0x46, 0x54, 0x57});
  }

  @Test
  void tryGetBytesCanConvertValidStringToByteArray() {
    // Assert:
    assertGetBytesConversion("4e454d465457", new byte[] {0x4e, 0x45, 0x4d, 0x46, 0x54, 0x57});
  }

  @Test
  void tryGetBytesCanConvertValidStringWithOddLengthToByteArray() {
    // Assert:
    assertGetBytesConversion("e454d465457", new byte[] {0x0e, 0x45, 0x4d, 0x46, 0x54, 0x57});
  }

  @Test
  void tryGetBytesCanConvertValidStringWithLeadingZerosToByteArray() {
    // Assert:
    assertGetBytesConversion("00000d465457", new byte[] {0x00, 0x00, 0x0d, 0x46, 0x54, 0x57});
  }

  @Test
  void tryGetBytesCannotConvertMalformedStringToByteArray() {
    // Assert:
    IllegalArgumentException exception =
        Assertions.assertThrows(
            IllegalArgumentException.class, () -> assertGetBytesConversion("4e454g465457", null));
    Assertions.assertEquals(
        "org.apache.commons.codec.DecoderException: Illegal hexadecimal character g at index 5",
        exception.getMessage());
  }

  @Test
  void getStringCanConvertBytesToHexString() {
    // Assert:
    assertGetStringConversion(new byte[] {0x4e, 0x45, 0x4d, 0x46, 0x54, 0x57}, "4E454D465457");
  }

  @Test
  void getStringCanConvertBytesWithLeadingZerosToHexString() {
    // Assert:
    assertGetStringConversion(new byte[] {0x00, 0x00, 0x0d, 0x46, 0x54, 0x57}, "00000D465457");
  }

  @Test
  void getStringCanConvertEmptyBytesToHexString() {
    // Assert:
    assertGetStringConversion(new byte[] {}, "");
  }

  @Test
  void fromStringToHexToString() {
    // Assert:
    String message = "Some message ??????";

    Assertions.assertEquals(
        message, ConvertUtils.fromHexToString(ConvertUtils.fromStringToHex(message)));

    Assertions.assertNull(ConvertUtils.fromHexToString(null));
    Assertions.assertNull(ConvertUtils.fromStringToHex(null));
  }

  @Test
  void fromHex() {
    // Assert:
    String message = "This is the message for this account! ??????89664";

    Assertions.assertEquals(
        message,
        ConvertUtils.fromHexToString(
            "5468697320697320746865206D65737361676520666F722074686973206163636F756E742120E6B189E5AD973839363634"));
  }

  @Test
  void toSize16Hex() {
    Assertions.assertEquals("000000000000000A", ConvertUtils.toSize16Hex(BigInteger.TEN));

    Assertions.assertEquals(
        "00000000000186A0", ConvertUtils.toSize16Hex(BigInteger.valueOf(100000)));

    Assertions.assertEquals("0000000000000000", ConvertUtils.toSize16Hex(BigInteger.ZERO));

    IllegalArgumentException error =
        Assertions.assertThrows(
            IllegalArgumentException.class, () -> ConvertUtils.toSize16Hex(BigInteger.valueOf(-5)));
    Assertions.assertEquals("BigInteger '-5' must not be negative", error.getMessage());
  }

  @Test
  void reverseHexString() {
    Assertions.assertEquals("FF75", ConvertUtils.reverseHexString("75FF"));

    Assertions.assertEquals(
        "FF66FF66FF66FF66FF66FF66FF66FF75FF75FF75FF75FF75FF75",
        ConvertUtils.reverseHexString("75FF75FF75FF75FF75FF75FF66FF66FF66FF66FF66FF66FF66FF"));
  }

  @Test
  void testToBigIntegerFromLong() {

    BigInteger biggerThanLongInteger = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.TEN);

    Assertions.assertEquals("9223372036854775817", biggerThanLongInteger.toString());

    // It overflows.
    Assertions.assertEquals(-9223372036854775799L, biggerThanLongInteger.longValue());

    // Then, BigInteger is negative.
    Assertions.assertEquals(
        "-9223372036854775799", BigInteger.valueOf(biggerThanLongInteger.longValue()).toString());

    // It doesn't overflow.
    Assertions.assertEquals(
        "9223372036854775817",
        ConvertUtils.toUnsignedBigInteger(biggerThanLongInteger.longValue()).toString());
  }

  @Test
  void testIsHexString() {
    Assertions.assertTrue(ConvertUtils.isHexString("026ee415fc15"));
    Assertions.assertTrue(ConvertUtils.isHexString("abcdef0123456789ABCDEF"));
    Assertions.assertFalse(ConvertUtils.isHexString("abcdef012345G789ABCDEF")); // G char
    Assertions.assertFalse(ConvertUtils.isHexString("026ee415fc1")); // ODD
    Assertions.assertFalse(ConvertUtils.isHexString(null)); // Null
  }

  @Test
  void validateIsHexString() {
    assertIsHexString("026ee415fc15", null, null);
    assertIsHexString("abcdef0123456789ABCDEF", null, null);

    assertIsHexString("026ee415fc15", 12, null);
    assertIsHexString("abcdef0123456789ABCDEF", 22, null);

    assertIsHexString("026ee415fc15", 4, "026ee415fc15 is not an hex of size 4");
    assertIsHexString(
        "abcdef0123456789ABCDEF", 4, "abcdef0123456789ABCDEF is not an hex of size 4");

    assertIsHexString("abcdef012345G789ABCDEF", null, "abcdef012345G789ABCDEF is not a valid hex");
    assertIsHexString("026ee415fc1", null, "026ee415fc1 is not a valid hex");
    assertIsHexString(null, null, "Null is not a valid hex");

    assertIsHexString("abcdef012345G789ABCDEF", 2, "abcdef012345G789ABCDEF is not a valid hex");
    assertIsHexString("026ee415fc1", 2, "026ee415fc1 is not a valid hex");
    assertIsHexString(null, 2, "Null is not a valid hex");
  }

  @Test
  void reverseHex() {
    String hex = "D9E338F78767ED95";
    String reverseHex = ConvertUtils.reverseHexString(hex);
    Assertions.assertEquals("95ED6787F738E3D9", reverseHex);
    Assertions.assertEquals(hex, ConvertUtils.reverseHexString(reverseHex));
  }

  @Test
  void padHex() {
    Assertions.assertEquals(
        "F8CBC85DAC0BA67AD8E3196814F5ABED2149B41D000000000000000000000000",
        ConvertUtils.padHex("F8CBC85DAC0BA67AD8E3196814F5ABED2149B41D", 64));

    Assertions.assertEquals(
        "F8CBC85DAC0BA67AD8E3196814F5ABED2149B41D",
        ConvertUtils.padHex("F8CBC85DAC0BA67AD8E3196814F5ABED2149B41D", 40));

    IllegalArgumentException exception =
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> ConvertUtils.padHex("F8CBC85DAC0BA67AD8E3196814F5ABED2149B41D", 20));
    Assertions.assertEquals(
        "Hex F8CBC85DAC0BA67AD8E3196814F5ABED2149B41D size must be at least 20",
        exception.getMessage());
  }

  private void assertIsHexString(String input, Integer size, String errorMessage) {
    try {
      if (size == null) {
        ConvertUtils.validateIsHexString(input);
      } else {
        ConvertUtils.validateIsHexString(input, size);
      }
      Assertions.assertNull(errorMessage);
    } catch (IllegalArgumentException e) {
      Assertions.assertEquals(errorMessage, e.getMessage());
    }
  }
}
