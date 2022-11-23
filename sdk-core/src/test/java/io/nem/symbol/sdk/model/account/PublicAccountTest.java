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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import io.nem.symbol.sdk.model.network.NetworkType;
import java.util.HashSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PublicAccountTest {

  private final String plain = "TBE5JFS6AG2RBQVJE7R3IZV4B4RCXULIXY77ZZQ";
  private final String pretty = "TBE5JF-S6AG2R-BQVJE7-R3IZV4-B4RCXU-LIXY77-ZZQ";
  private final String encoded = "9849D4965E01B510C2A927E3B466BC0F222BD168BE3FFCE6";
  private final String publicKey =
      "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456332";
  private final NetworkType networkType = NetworkType.TEST_NET;

  @Test
  void shouldCreatePublicAccountViaConstructor() {
    PublicAccount publicAccount = new PublicAccount(publicKey, NetworkType.TEST_NET);
    assertEquals(publicKey.toUpperCase(), publicAccount.getPublicKey().toHex());
    assertEquals(plain, publicAccount.getAddress().plain());
  }

  @Test
  void shouldCreatePublicAccountViaStaticConstructor() {
    PublicAccount publicAccount =
        PublicAccount.createFromPublicKey(publicKey, NetworkType.TEST_NET);
    assertEquals(publicKey.toUpperCase(), publicAccount.getPublicKey().toHex());
    assertEquals(plain, publicAccount.getAddress().plain());
  }

  @Test
  void equalityIsBasedOnPublicKeyAndNetwork() {
    PublicAccount publicAccount = new PublicAccount(publicKey, NetworkType.TEST_NET);
    PublicAccount publicAccount2 = new PublicAccount(publicKey, NetworkType.TEST_NET);
    assertEquals(publicAccount, publicAccount2);
  }

  @Test
  void equalityReturnsFalseIfNetworkIsDifferent() {
    PublicAccount publicAccount = new PublicAccount(publicKey, NetworkType.TEST_NET);
    PublicAccount publicAccount2 = new PublicAccount(publicKey, NetworkType.MAIN_NET);
    assertNotEquals(publicAccount, publicAccount2);
  }

  @Test
  public void testAddresses() {
    assertAddress(Address.createFromRawAddress(plain));
    assertAddress(Address.createFromRawAddress(pretty));
    assertAddress(Address.createFromPublicKey(publicKey, networkType));
    assertAddress(Address.createFromEncoded(encoded));
  }

  private void assertAddress(Address address) {
    assertEquals(encoded, address.encoded());
    assertEquals(plain, address.plain());
    assertEquals(pretty, address.pretty());
    Assertions.assertEquals(networkType, address.getNetworkType());
  }

  @Test
  void shouldBeEquals() {
    PublicAccount account1 =
        PublicAccount.createFromPublicKey(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            NetworkType.TEST_NET);

    PublicAccount account2 =
        PublicAccount.createFromPublicKey(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            NetworkType.TEST_NET);

    PublicAccount account3 =
        PublicAccount.createFromPublicKey(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            NetworkType.MAIN_NET);

    assertEquals(account1, account2);
    assertEquals(account1.hashCode(), account2.hashCode());
    assertNotEquals(account1, account3);

    assertNotEquals(account1, new HashSet<>());
  }
}
