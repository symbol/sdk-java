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
package io.nem.symbol.sdk.api;

import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.network.NetworkType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test of {@link BlockSearchCriteria} */
class BlockSearchCriteriaTest {

  private final Account account1 = Account.generateNewAccount(NetworkType.TEST_NET);
  private final Account account2 = Account.generateNewAccount(NetworkType.TEST_NET);
  private final PublicKey publicKey2 = account2.getKeyPair().getPublicKey();

  @Test
  void shouldCreate() {
    BlockSearchCriteria criteria = new BlockSearchCriteria();
    Assertions.assertNull(criteria.getOrder());
    Assertions.assertNull(criteria.getPageSize());
    Assertions.assertNull(criteria.getPageNumber());
    Assertions.assertNull(criteria.getOffset());
    Assertions.assertNull(criteria.getBeneficiaryAddress());
    Assertions.assertNull(criteria.getSignerPublicKey());
    Assertions.assertNull(criteria.getOrderBy());
  }

  @Test
  void shouldSetValues() {

    BlockSearchCriteria criteria = new BlockSearchCriteria();
    criteria.setOrder(OrderBy.DESC);
    criteria.setPageSize(10);
    criteria.setPageNumber(5);
    criteria.setOffset("abc");
    criteria.setBeneficiaryAddress(account1.getAddress());
    criteria.setSignerPublicKey(publicKey2);
    criteria.setOrderBy(BlockOrderBy.HEIGHT);

    Assertions.assertEquals(OrderBy.DESC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals("abc", criteria.getOffset());
    Assertions.assertEquals(account1.getAddress(), criteria.getBeneficiaryAddress());
    Assertions.assertEquals(publicKey2, criteria.getSignerPublicKey());
    Assertions.assertEquals(BlockOrderBy.HEIGHT, criteria.getOrderBy());
  }

  @Test
  void shouldUseBuilderMethods() {

    BlockSearchCriteria criteria =
        new BlockSearchCriteria()
            .order(OrderBy.ASC)
            .pageSize(10)
            .pageNumber(5)
            .beneficiaryAddress(account1.getAddress())
            .signerPublicKey(publicKey2)
            .orderBy(BlockOrderBy.HEIGHT);

    criteria.offset("abc");
    Assertions.assertEquals(OrderBy.ASC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals("abc", criteria.getOffset());
    Assertions.assertEquals(account1.getAddress(), criteria.getBeneficiaryAddress());
    Assertions.assertEquals(publicKey2, criteria.getSignerPublicKey());
    Assertions.assertEquals(BlockOrderBy.HEIGHT, criteria.getOrderBy());
  }

  @Test
  void shouldBeEquals() {

    BlockSearchCriteria criteria1 =
        new BlockSearchCriteria()
            .order(OrderBy.ASC)
            .pageSize(10)
            .pageNumber(5)
            .beneficiaryAddress(account1.getAddress())
            .signerPublicKey(publicKey2)
            .orderBy(BlockOrderBy.HEIGHT);
    criteria1.offset("abc");

    BlockSearchCriteria criteria2 =
        new BlockSearchCriteria()
            .order(OrderBy.ASC)
            .pageSize(10)
            .pageNumber(5)
            .beneficiaryAddress(account1.getAddress())
            .signerPublicKey(publicKey2)
            .orderBy(BlockOrderBy.HEIGHT);
    criteria2.offset("abc");

    Assertions.assertEquals(new BlockSearchCriteria(), new BlockSearchCriteria());
    Assertions.assertEquals(criteria1, criteria2);
    Assertions.assertEquals(criteria1, criteria1);
    Assertions.assertEquals(criteria1.hashCode(), criteria2.hashCode());

    criteria1.pageNumber(30);
    Assertions.assertNotEquals(criteria1, criteria2);
    Assertions.assertNotEquals(criteria1.hashCode(), criteria2.hashCode());

    criteria2.pageNumber(100);
    Assertions.assertNotEquals(criteria1, criteria2);
    Assertions.assertNotEquals(criteria1.hashCode(), criteria2.hashCode());

    Assertions.assertNotEquals("ABC", criteria2);
  }
}
