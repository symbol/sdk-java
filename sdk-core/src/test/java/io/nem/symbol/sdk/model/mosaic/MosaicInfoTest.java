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
package io.nem.symbol.sdk.model.mosaic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.catapult.builders.MosaicEntryBuilder;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MosaicInfoTest {

  @Test
  void createAMosaicInfoViaConstructor() {
    MosaicFlags mosaicFlags = MosaicFlags.create(true, true, true, true);
    MosaicId mosaicId = new MosaicId(new BigInteger("-3087871471161192663"));

    Address address = Address.createFromRawAddress("TBEM3LTBAHSDOXONNOKAVIGIZJLUCCPIBWY7WEA");
    MosaicInfo info =
        new MosaicInfo(
            "abc",
            1,
            mosaicId,
            new BigInteger("100"),
            new BigInteger("200"),
            address,
            1L,
            mosaicFlags,
            3,
            BigInteger.valueOf(10));

    assertEquals(mosaicId, info.getMosaicId());
    assertEquals(new BigInteger("100"), info.getSupply());
    assertEquals(new BigInteger("200"), info.getStartHeight());
    assertEquals(address, info.getOwnerAddress());
    assertTrue(info.isSupplyMutable());
    assertTrue(info.isTransferable());
    assertTrue(info.isRestrictable());
    assertTrue(info.isRevokable());
    assertEquals(1L, info.getRevision());
    assertEquals(3, info.getDivisibility());
    assertEquals(BigInteger.valueOf(10), info.getDuration());
    assertEquals("abc", info.getRecordId().get());

    byte[] serializedState = info.serialize();
    String expectedHex =
        "010029CF5FD941AD25D56400000000000000C8000000000000009848CDAE6101E4375DCD6B940AA0C8CA574109E80DB1FB10010000000F030A00000000000000";
    Assertions.assertEquals(expectedHex, ConvertUtils.toHex(serializedState));
    MosaicEntryBuilder builder =
        MosaicEntryBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(expectedHex, ConvertUtils.toHex(builder.serialize()));
  }

  @Test
  void toNetworkCurrency() {
    MosaicFlags mosaicFlags = MosaicFlags.create(true, true, true, true);
    MosaicId mosaicId = new MosaicId(new BigInteger("-3087871471161192663"));

    Address address = Account.generateNewAccount(NetworkType.TEST_NET).getAddress();
    MosaicInfo mosaicInfo =
        new MosaicInfo(
            "abc",
            1,
            mosaicId,
            new BigInteger("100"),
            new BigInteger("0"),
            address,
            1L,
            mosaicFlags,
            3,
            BigInteger.valueOf(10));

    Currency currency = mosaicInfo.toCurrency();
    assertEquals(mosaicId, currency.getMosaicId().get());
    assertFalse(currency.getNamespaceId().isPresent());
    assertTrue(currency.isSupplyMutable());
    assertTrue(currency.isTransferable());
    assertTrue(currency.isRestrictable());
    assertTrue(currency.isRevokable());
    assertEquals(3, currency.getDivisibility());
  }

  @Test
  void shouldReturnSupplyMutableTrue() {
    MosaicInfo mosaicInfo = buildMosaicInfo(MosaicFlags.create(true, false, false, false));
    assertTrue(mosaicInfo.isSupplyMutable());
  }

  @Test
  void shouldReturnSupplyMutableFalse() {
    MosaicInfo mosaicInfo = buildMosaicInfo(MosaicFlags.create(false, true, true, true));
    assertFalse(mosaicInfo.isSupplyMutable());
  }

  @Test
  void shouldReturnTransferableTrue() {
    MosaicInfo mosaicInfo = buildMosaicInfo(MosaicFlags.create(false, true, false, false));
    assertTrue(mosaicInfo.isTransferable());
  }

  @Test
  void shouldReturnTransferableFalse() {
    MosaicInfo mosaicInfo = buildMosaicInfo(MosaicFlags.create(true, false, true, true));
    assertFalse(mosaicInfo.isTransferable());
  }

  @Test
  void shouldReturnRestrictableTrue() {
    MosaicInfo mosaicInfo = buildMosaicInfo(MosaicFlags.create(false, false, true, false));
    assertTrue(mosaicInfo.isRestrictable());
  }

  @Test
  void shouldReturnRestrictableFalse() {
    MosaicInfo mosaicInfo = buildMosaicInfo(MosaicFlags.create(true, true, false, true));
    assertFalse(mosaicInfo.isRestrictable());
  }

  @Test
  void shouldReturnRevokableTrue() {
    MosaicInfo mosaicInfo = buildMosaicInfo(MosaicFlags.create(false, false, false, true));
    assertTrue(mosaicInfo.isRevokable());
  }

  @Test
  void shouldReturnRevokableFalse() {
    MosaicInfo mosaicInfo = buildMosaicInfo(MosaicFlags.create(true, true, true, false));
    assertFalse(mosaicInfo.isRevokable());
  }

  private MosaicInfo buildMosaicInfo(MosaicFlags mosaicFlags) {
    return new MosaicInfo(
        "abc",
        1,
        new MosaicId(new BigInteger("-3087871471161192663")),
        new BigInteger("100"),
        new BigInteger("0"),
        Account.generateNewAccount(NetworkType.TEST_NET).getAddress(),
        1L,
        mosaicFlags,
        3,
        BigInteger.valueOf(10));
  }
}
