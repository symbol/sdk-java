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
package io.nem.symbol.sdk.model.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.symbol.catapult.builders.AccountRestrictionsBuilder;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.infrastructure.SerializationUtils;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.AccountOperationRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountRestrictionTest {

  @Test
  void shouldCreateAccountRestrictionViaConstructor() {
    Address address = Address.createFromEncoded("983FA2882A4A7F13A781112BD7C6693D52DA44F762B85ED7");
    AccountRestriction accountRestrictionOperation =
        new AccountRestriction(
            AccountOperationRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE,
            Arrays.asList(TransactionType.TRANSFER, TransactionType.MOSAIC_METADATA));
    assertEquals(
        AccountOperationRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE,
        accountRestrictionOperation.getRestrictionFlags());
    assertEquals(2, accountRestrictionOperation.getValues().size());

    AccountRestrictions accountRestrictions =
        new AccountRestrictions(1, address, Collections.singletonList(accountRestrictionOperation));

    byte[] serializedState = accountRestrictions.serialize();
    String expectedHex =
        "0100983FA2882A4A7F13A781112BD7C6693D52DA44F762B85ED701000000000000000440020000000000000054414442";
    assertEquals(expectedHex, ConvertUtils.toHex(serializedState));

    AccountRestrictionsBuilder builder =
        AccountRestrictionsBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    byte[] serialize = builder.getRestrictions().get(0).serialize();
    System.out.println(ConvertUtils.toHex(serialize));

    Assertions.assertEquals(
        ConvertUtils.toHex(serializedState), ConvertUtils.toHex(builder.serialize()));
  }

  @Test
  void shouldCreateAccountRestrictionsInfoViaConstructor() {
    String metaId = "12345";
    Address address = Address.createFromEncoded("983FA2882A4A7F13A781112BD7C6693D52DA44F762B85ED7");
    AccountRestriction accountRestriction =
        new AccountRestriction(
            AccountAddressRestrictionFlags.ALLOW_OUTGOING_ADDRESS,
            Collections.singletonList("TBEM3LTBAHSDOXONNOKAVIGIZJLUCCPIBWY7WEA"));
    AccountRestrictions accountRestrictions =
        new AccountRestrictions(1, address, Arrays.asList(accountRestriction));
    AccountPropertiesInfo accountPropertiesInfo =
        new AccountPropertiesInfo(metaId, accountRestrictions);

    assertEquals(metaId, accountPropertiesInfo.getMetaId());
    assertEquals(address, accountPropertiesInfo.getAccountRestrictions().getAddress());
    assertEquals(1, accountPropertiesInfo.getAccountRestrictions().getRestrictions().size());
    assertEquals(
        AccountAddressRestrictionFlags.ALLOW_OUTGOING_ADDRESS,
        accountPropertiesInfo
            .getAccountRestrictions()
            .getRestrictions()
            .get(0)
            .getRestrictionFlags());

    byte[] serializedState = accountRestrictions.serialize();
    String expectedHex =
        "0100983FA2882A4A7F13A781112BD7C6693D52DA44F762B85ED7010000000000000001400000000000000000";
    assertEquals(expectedHex, ConvertUtils.toHex(serializedState));

    AccountRestrictionsBuilder builder =
        AccountRestrictionsBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(
        ConvertUtils.toHex(serializedState), ConvertUtils.toHex(builder.serialize()));
  }

  @Test
  void shouldCreateAccountRestrictionsViaConstructorEmpty() {
    Address address = Address.createFromEncoded("983FA2882A4A7F13A781112BD7C6693D52DA44F762B85ED7");

    AccountRestrictions accountRestrictions =
        new AccountRestrictions(1, address, Collections.emptyList());

    assertEquals(address, accountRestrictions.getAddress());
    assertEquals(0, accountRestrictions.getRestrictions().size());

    byte[] serializedState = accountRestrictions.serialize();
    String expectedHex = "0100983FA2882A4A7F13A781112BD7C6693D52DA44F762B85ED70000000000000000";
    assertEquals(expectedHex, ConvertUtils.toHex(serializedState));

    AccountRestrictionsBuilder builder =
        AccountRestrictionsBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(
        ConvertUtils.toHex(serializedState), ConvertUtils.toHex(builder.serialize()));

    Assertions.assertEquals(
        accountRestrictions.getRestrictions().size(), builder.getRestrictions().size());
  }

  @Test
  void shouldCreateAccountRestrictionsViaConstructor() {
    Address address = Address.createFromEncoded("983FA2882A4A7F13A781112BD7C6693D52DA44F762B85ED7");
    AccountRestriction accountAllowIncomingRestriction =
        new AccountRestriction(
            AccountAddressRestrictionFlags.ALLOW_INCOMING_ADDRESS,
            Arrays.asList("TBEM3LTBAHSDOXONNOKAVIGIZJLUCCPIBWY7WEA"));
    AccountRestrictions accountRestrictions =
        new AccountRestrictions(1, address, Arrays.asList(accountAllowIncomingRestriction));

    assertEquals(address, accountRestrictions.getAddress());
    assertEquals(1, accountRestrictions.getRestrictions().size());
    assertEquals(
        AccountAddressRestrictionFlags.ALLOW_INCOMING_ADDRESS,
        accountRestrictions.getRestrictions().get(0).getRestrictionFlags());

    byte[] serializedState = accountRestrictions.serialize();
    String expectedHex =
        "0100983FA2882A4A7F13A781112BD7C6693D52DA44F762B85ED7010000000000000001000000000000000000";
    assertEquals(expectedHex, ConvertUtils.toHex(serializedState));

    AccountRestrictionsBuilder builder =
        AccountRestrictionsBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(
        ConvertUtils.toHex(serializedState), ConvertUtils.toHex(builder.serialize()));

    Assertions.assertEquals(
        accountRestrictions.getRestrictions().size(), builder.getRestrictions().size());
  }

  @Test
  void shouldCreateAccountRestrictionViaConstructorMultipleTypes() {
    Address address = Address.createFromEncoded("983FA2882A4A7F13A781112BD7C6693D52DA44F762B85ED7");
    AccountRestriction accountRestrictionOperation =
        new AccountRestriction(
            AccountOperationRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE,
            Arrays.asList(TransactionType.TRANSFER, TransactionType.MOSAIC_METADATA));
    assertEquals(
        AccountOperationRestrictionFlags.ALLOW_OUTGOING_TRANSACTION_TYPE,
        accountRestrictionOperation.getRestrictionFlags());
    assertEquals(2, accountRestrictionOperation.getValues().size());

    AccountRestriction accountAllowIncomingRestriction =
        new AccountRestriction(
            AccountAddressRestrictionFlags.ALLOW_INCOMING_ADDRESS,
            Arrays.asList("TBEM3LTBAHSDOXONNOKAVIGIZJLUCCPIBWY7WEA"));

    AccountRestrictions accountRestrictions =
        new AccountRestrictions(
            1,
            address,
            Arrays.asList(accountRestrictionOperation, accountAllowIncomingRestriction));

    byte[] serializedState = accountRestrictions.serialize();
    String expectedHex =
        "0100983FA2882A4A7F13A781112BD7C6693D52DA44F762B85ED70200000000000000044002000000000000005441444201000000000000000000";
    assertEquals(expectedHex, ConvertUtils.toHex(serializedState));

    AccountRestrictionsBuilder builder =
        AccountRestrictionsBuilder.loadFromBinary(SerializationUtils.toDataInput(serializedState));

    Assertions.assertEquals(
        ConvertUtils.toHex(serializedState), ConvertUtils.toHex(builder.serialize()));
  }
}
