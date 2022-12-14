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
package io.nem.symbol.sdk.model.namespace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.network.NetworkType;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NamespaceInfoTest {

  @Test
  void createANamespaceInfoViaConstructor() {

    NamespaceId namespaceId = NamespaceId.createFromId(new BigInteger("-8884663987180930485"));
    Address address = Address.createFromRawAddress("TBEM3LTBAHSDOXONNOKAVIGIZJLUCCPIBWY7WEA");
    NamespaceInfo namespaceInfo =
        new NamespaceInfo(
            "abc",
            1,
            true,
            0,
            NamespaceRegistrationType.ROOT_NAMESPACE,
            1,
            Arrays.asList(namespaceId),
            NamespaceId.createFromId(new BigInteger("0")),
            address,
            new BigInteger("1"),
            new BigInteger("-1"),
            new MosaicAlias(new MosaicId(new BigInteger("100"))));
    assertEquals("abc", namespaceInfo.getRecordId().get());
    assertTrue(namespaceInfo.isActive());
    assertEquals(0, (int) namespaceInfo.getIndex());
    assertSame(namespaceInfo.getRegistrationType(), NamespaceRegistrationType.ROOT_NAMESPACE);
    assertEquals(1, (int) namespaceInfo.getDepth());
    assertEquals(namespaceId, namespaceInfo.getLevels().get(0));
    Assertions.assertEquals(address, namespaceInfo.getOwnerAddress());
    assertEquals(new BigInteger("1"), namespaceInfo.getStartHeight());
    assertEquals(new BigInteger("-1"), namespaceInfo.getEndHeight());
    assertEquals(AliasType.MOSAIC, namespaceInfo.getAlias().getType());
    assertEquals(
        new BigInteger("100"), ((MosaicId) namespaceInfo.getAlias().getAliasValue()).getId());

    byte[] serialize = namespaceInfo.serialize(Arrays.asList(createSubNamespaceInfo(namespaceId)));
    assertEquals(
        "01004BFA5F372D55B3849848CDAE6101E4375DCD6B940AA0C8CA574109E80DB1FB100100000000000000FFFFFFFFFFFFFFFF01640000000000000001000000000000000129CF2728A91AE7F0016400000000000000",
        ConvertUtils.toHex(serialize));
  }

  @Test
  void createANamespaceInfoViaConstructorEmptyAlias() {

    NamespaceId namespaceId = NamespaceId.createFromId(new BigInteger("-8884663987180930485"));
    Address address = Address.createFromRawAddress("TBEM3LTBAHSDOXONNOKAVIGIZJLUCCPIBWY7WEA");
    NamespaceInfo namespaceInfo =
        new NamespaceInfo(
            "abc",
            1,
            true,
            0,
            NamespaceRegistrationType.ROOT_NAMESPACE,
            1,
            Arrays.asList(namespaceId),
            NamespaceId.createFromId(new BigInteger("0")),
            address,
            new BigInteger("1"),
            new BigInteger("-1"),
            new EmptyAlias());
    assertEquals("abc", namespaceInfo.getRecordId().get());
    assertTrue(namespaceInfo.isActive());
    assertEquals(0, (int) namespaceInfo.getIndex());
    assertSame(namespaceInfo.getRegistrationType(), NamespaceRegistrationType.ROOT_NAMESPACE);
    assertEquals(1, (int) namespaceInfo.getDepth());
    assertEquals(namespaceId, namespaceInfo.getLevels().get(0));
    Assertions.assertEquals(address, namespaceInfo.getOwnerAddress());
    assertEquals(new BigInteger("1"), namespaceInfo.getStartHeight());
    assertEquals(new BigInteger("-1"), namespaceInfo.getEndHeight());
    assertEquals(AliasType.NONE, namespaceInfo.getAlias().getType());

    byte[] serialize = namespaceInfo.serialize(Arrays.asList(createSubNamespaceInfo(namespaceId)));
    assertEquals(
        "01004BFA5F372D55B3849848CDAE6101E4375DCD6B940AA0C8CA574109E80DB1FB100100000000000000FFFFFFFFFFFFFFFF0001000000000000000129CF2728A91AE7F0016400000000000000",
        ConvertUtils.toHex(serialize));
  }

  @Test
  void shouldReturnRootNamespaceId() {
    NamespaceInfo namespaceInfo = createRootNamespaceInfo();
    assertEquals(new BigInteger("9562080086528621131"), namespaceInfo.getId().getId());
  }

  @Test
  void shouldReturnSubNamespaceId() {
    NamespaceId parentId = NamespaceId.createFromId(new BigInteger("-3087871471161192663"));
    NamespaceInfo namespaceInfo = createSubNamespaceInfo(parentId);
    assertEquals(new BigInteger("17358872602548358953"), namespaceInfo.getId().getId());
  }

  @Test
  void shouldReturnRootTrueWhenNamespaceInfoIsFromRootNamespace() {
    NamespaceInfo namespaceInfo = createRootNamespaceInfo();
    assertTrue(namespaceInfo.isRoot());
  }

  @Test
  void shouldReturnRootFalseWhenNamespaceInfoIsFromSubNamespace() {
    NamespaceId parentId = NamespaceId.createFromId(new BigInteger("-3087871471161192663"));
    NamespaceInfo namespaceInfo = createSubNamespaceInfo(parentId);
    assertFalse(namespaceInfo.isRoot());
  }

  @Test
  void shouldReturnSubNamespaceFalseWhenNamespaceInfoIsFromRootNamespace() {
    NamespaceInfo namespaceInfo = createRootNamespaceInfo();
    assertFalse(namespaceInfo.isSubnamespace());
  }

  @Test
  void shouldReturnSubNamespaceTrueWhenNamespaceInfoIsFromSubNamespace() {
    NamespaceId parentId = NamespaceId.createFromId(new BigInteger("-3087871471161192663"));
    NamespaceInfo namespaceInfo = createSubNamespaceInfo(parentId);
    assertTrue(namespaceInfo.isSubnamespace());
  }

  @Test
  void shouldReturnParentNamespaceIdWhenNamespaceInfoIsFromSubNamespace() {
    NamespaceId parentId = NamespaceId.createFromId(new BigInteger("-3087871471161192663"));
    NamespaceInfo namespaceInfo = createSubNamespaceInfo(parentId);
    assertEquals(parentId, namespaceInfo.parentNamespaceId());
  }

  @Test
  void shouldParentNamespaceIdThrowErrorWhenNamespaceInfoIsFromRootNamespace() {
    NamespaceInfo namespaceInfo = createRootNamespaceInfo();
    assertThrows(
        IllegalStateException.class,
        () -> {
          namespaceInfo.parentNamespaceId();
        },
        "Is A Root Namespace");
  }

  NamespaceInfo createRootNamespaceInfo() {
    return new NamespaceInfo(
        "abc",
        1,
        true,
        0,
        NamespaceRegistrationType.ROOT_NAMESPACE,
        1,
        Collections.singletonList(NamespaceId.createFromId(new BigInteger("-8884663987180930485"))),
        NamespaceId.createFromId(new BigInteger("0")),
        new PublicAccount(
                "B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF",
                NetworkType.TEST_NET)
            .getAddress(),
        new BigInteger("1"),
        new BigInteger("-1"),
        new MosaicAlias(new MosaicId(new BigInteger("100"))));
  }

  NamespaceInfo createSubNamespaceInfo(NamespaceId parentId) {
    NamespaceId level0 = NamespaceId.createFromId(new BigInteger("17358872602548358953"));
    NamespaceId level1 = NamespaceId.createFromId(new BigInteger("-1087871471161192663"));
    return new NamespaceInfo(
        "bcd",
        1,
        true,
        0,
        NamespaceRegistrationType.SUB_NAMESPACE,
        1,
        Arrays.asList(level0, level1),
        parentId,
        new PublicAccount(
                "B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF",
                NetworkType.TEST_NET)
            .getAddress(),
        new BigInteger("1"),
        new BigInteger("-1"),
        new MosaicAlias(new MosaicId(new BigInteger("100"))));
  }
}
