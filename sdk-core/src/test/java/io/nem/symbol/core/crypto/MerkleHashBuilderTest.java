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
package io.nem.symbol.core.crypto;

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.RandomUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Testing of {@link MerkleHashBuilder} */
public class MerkleHashBuilderTest {
  byte[] calculateMerkleHash(Stream<byte[]> hashes) {
    MerkleHashBuilder builder = new MerkleHashBuilder();
    hashes.forEach(embeddedHash -> builder.update(embeddedHash));
    return builder.getRootHash();
  }

  void assertMerkleHash(String expectedHash, String[] hashes) {
    // Act:
    byte[] calculatedHash =
        calculateMerkleHash(Stream.of(hashes).map(ConvertUtils::fromHexToBytes));

    // Assert:
    Assertions.assertEquals(expectedHash, ConvertUtils.toHex(calculatedHash));
  }

  @Test
  public void testZero() {
    this.assertMerkleHash(
        "0000000000000000000000000000000000000000000000000000000000000000", new String[] {});
  }

  @Test
  public void testOne() {
    String randomHash = ConvertUtils.toHex(RandomUtils.generateRandomBytes(32));
    this.assertMerkleHash(randomHash, new String[] {randomHash});
  }

  @Test
  public void testCanBuildBalancedTree() {
    this.assertMerkleHash(
        "7D853079F5F9EE30BDAE49C4956AF20CDF989647AFE971C069AC263DA1FFDF7E",
        new String[] {
          "36C8213162CDBC78767CF43D4E06DDBE0D3367B6CEAEAEB577A50E2052441BC8",
          "8A316E48F35CDADD3F827663F7535E840289A16A43E7134B053A86773E474C28",
          "6D80E71F00DFB73B358B772AD453AEB652AE347D3E098AE269005A88DA0B84A7",
          "2AE2CA59B5BB29721BFB79FE113929B6E52891CAA29CBF562EBEDC46903FF681",
          "421D6B68A6DF8BB1D5C9ACF7ED44515E77945D42A491BECE68DA009B551EE6CE",
          "7A1711AF5C402CFEFF87F6DA4B9C738100A7AC3EDAD38D698DF36CA3FE883480",
          "1E6516B2CC617E919FAE0CF8472BEB2BFF598F19C7A7A7DC260BC6715382822C",
          "410330530D04A277A7C96C1E4F34184FDEB0FFDA63563EFD796C404D7A6E5A20"
        });
  }

  @Test
  public void testCanBuildFromUnbalancedTree() {
    this.assertMerkleHash(
        "DEFB4BF7ACF2145500087A02C88F8D1FCF27B8DEF4E0FDABE09413D87A3F0D09",
        new String[] {
          "36C8213162CDBC78767CF43D4E06DDBE0D3367B6CEAEAEB577A50E2052441BC8",
          "8A316E48F35CDADD3F827663F7535E840289A16A43E7134B053A86773E474C28",
          "6D80E71F00DFB73B358B772AD453AEB652AE347D3E098AE269005A88DA0B84A7",
          "2AE2CA59B5BB29721BFB79FE113929B6E52891CAA29CBF562EBEDC46903FF681",
          "421D6B68A6DF8BB1D5C9ACF7ED44515E77945D42A491BECE68DA009B551EE6CE"
        });
  }

  @Test
  public void testChangingSubHashOrderChangesMerkleHash() {
    // Arrange:
    ArrayList<byte[]> seed1 = new ArrayList<byte[]>();
    for (int i = 0; i < 8; ++i) seed1.add(RandomUtils.generateRandomBytes(32));

    List<byte[]> seed2 =
        Arrays.asList(
            seed1.get(0),
            seed1.get(1),
            seed1.get(2),
            seed1.get(5),
            seed1.get(4),
            seed1.get(3),
            seed1.get(6),
            seed1.get(7));

    // Act:
    byte[] rootHash1 = this.calculateMerkleHash(seed1.stream());
    byte[] rootHash2 = this.calculateMerkleHash(seed2.stream());

    // Assert:
    Assertions.assertNotEquals(ConvertUtils.toHex(rootHash1), ConvertUtils.toHex(rootHash2));
  }

  @Test
  public void testChangingSubHashChangesMerkleHash() {
    // Arrange:
    ArrayList<byte[]> seed1 = new ArrayList<byte[]>();
    for (int i = 0; i < 8; ++i) seed1.add(RandomUtils.generateRandomBytes(32));

    List<byte[]> seed2 =
        Arrays.asList(
            seed1.get(0),
            seed1.get(1),
            seed1.get(2),
            seed1.get(3),
            RandomUtils.generateRandomBytes(32),
            seed1.get(5),
            seed1.get(6),
            seed1.get(7));

    // Act:
    byte[] rootHash1 = this.calculateMerkleHash(seed1.stream());
    byte[] rootHash2 = this.calculateMerkleHash(seed2.stream());

    // Assert:
    Assertions.assertNotEquals(ConvertUtils.toHex(rootHash1), ConvertUtils.toHex(rootHash2));
  }
}
