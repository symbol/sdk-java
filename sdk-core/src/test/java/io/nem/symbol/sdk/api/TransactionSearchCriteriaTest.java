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
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.TransactionGroup;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Test of {@link TransactionSearchCriteria} */
class TransactionSearchCriteriaTest {

  @Test
  void shouldCreate() {
    TransactionSearchCriteria criteria = new TransactionSearchCriteria(TransactionGroup.CONFIRMED);
    Assertions.assertNull(criteria.getId());
    Assertions.assertNull(criteria.getOrder());
    Assertions.assertNull(criteria.getPageSize());
    Assertions.assertNull(criteria.getPageNumber());
    Assertions.assertNull(criteria.getTransactionTypes());
    Assertions.assertNull(criteria.getAddress());
    Assertions.assertNull(criteria.getEmbedded());
    Assertions.assertEquals(TransactionGroup.CONFIRMED, criteria.getGroup());
    Assertions.assertNull(criteria.getHeight());
    Assertions.assertNull(criteria.getFromHeight());
    Assertions.assertNull(criteria.getToHeight());
    Assertions.assertNull(criteria.getOffset());
    Assertions.assertNull(criteria.getRecipientAddress());
    Assertions.assertNull(criteria.getSignerPublicKey());
    Assertions.assertNull(criteria.getTransferMosaicId());
    Assertions.assertNull(criteria.getFromTransferAmount());
    Assertions.assertNull(criteria.getToTransferAmount());
  }

  @Test
  void shouldSetValues() {

    Address address1 = Address.generateRandom(NetworkType.TEST_NET);
    Address address2 = Address.generateRandom(NetworkType.TEST_NET);
    PublicKey signerPublicKey = PublicKey.fromHexString("227F");
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);

    TransactionSearchCriteria criteria =
        new TransactionSearchCriteria(TransactionGroup.UNCONFIRMED);

    criteria.setId("theId");
    criteria.setOrder(OrderBy.DESC);
    criteria.setPageSize(10);
    criteria.setTransactionTypes(
        Collections.singletonList(TransactionType.MOSAIC_GLOBAL_RESTRICTION));
    criteria.setPageNumber(5);
    criteria.setAddress(address1);
    criteria.setRecipientAddress(address2);
    criteria.setEmbedded(true);
    criteria.setHeight(BigInteger.ONE);
    criteria.setFromHeight(BigInteger.valueOf(2));
    criteria.setToHeight(BigInteger.valueOf(3));
    criteria.setOffset("offset1");
    criteria.setSignerPublicKey(signerPublicKey);

    criteria.setFromTransferAmount(BigInteger.valueOf(10));
    criteria.setToTransferAmount(BigInteger.valueOf(20));
    criteria.setTransferMosaicId(mosaicId);
    criteria.setSignerPublicKey(signerPublicKey);

    Assertions.assertEquals("theId", criteria.getId());
    Assertions.assertEquals(OrderBy.DESC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals(
        Collections.singletonList(TransactionType.MOSAIC_GLOBAL_RESTRICTION),
        criteria.getTransactionTypes());

    Assertions.assertEquals(address1, criteria.getAddress());
    Assertions.assertEquals(address2, criteria.getRecipientAddress());
    Assertions.assertEquals(true, criteria.getEmbedded());
    Assertions.assertEquals("offset1", criteria.getOffset());
    Assertions.assertEquals(BigInteger.ONE, criteria.getHeight());
    Assertions.assertEquals(BigInteger.valueOf(2), criteria.getFromHeight());
    Assertions.assertEquals(BigInteger.valueOf(3), criteria.getToHeight());
    Assertions.assertEquals(signerPublicKey, criteria.getSignerPublicKey());

    Assertions.assertEquals(BigInteger.valueOf(10), criteria.getFromTransferAmount());
    Assertions.assertEquals(BigInteger.valueOf(20), criteria.getToTransferAmount());
    Assertions.assertEquals(mosaicId, criteria.getTransferMosaicId());
  }

  @Test
  void shouldUseBuilderMethods() {

    Address address1 = Address.generateRandom(NetworkType.TEST_NET);
    Address address2 = Address.generateRandom(NetworkType.TEST_NET);
    PublicKey signerPublicKey = PublicKey.fromHexString("227F");
    MosaicId mosaicId = new MosaicId(BigInteger.TEN);

    TransactionSearchCriteria criteria =
        new TransactionSearchCriteria(TransactionGroup.UNCONFIRMED)
            .id("theId")
            .order(OrderBy.ASC)
            .pageSize(10)
            .pageNumber(5)
            .transactionTypes(Collections.singletonList(TransactionType.MOSAIC_GLOBAL_RESTRICTION));
    criteria.address(address1);
    criteria.recipientAddress(address2);
    criteria.embedded(true);
    criteria.height(BigInteger.ONE);
    criteria.offset("offset1");
    criteria.signerPublicKey(signerPublicKey);
    criteria.fromTransferAmount(BigInteger.valueOf(10));
    criteria.toTransferAmount(BigInteger.valueOf(20));
    criteria.transferMosaicId(mosaicId);

    Assertions.assertEquals("theId", criteria.getId());
    Assertions.assertEquals(OrderBy.ASC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals(
        Collections.singletonList(TransactionType.MOSAIC_GLOBAL_RESTRICTION),
        criteria.getTransactionTypes());

    Assertions.assertEquals(address1, criteria.getAddress());
    Assertions.assertEquals(address2, criteria.getRecipientAddress());
    Assertions.assertEquals(true, criteria.getEmbedded());
    Assertions.assertEquals("offset1", criteria.getOffset());
    Assertions.assertEquals(BigInteger.ONE, criteria.getHeight());
    Assertions.assertEquals(signerPublicKey, criteria.getSignerPublicKey());

    Assertions.assertEquals(BigInteger.valueOf(10), criteria.getFromTransferAmount());
    Assertions.assertEquals(BigInteger.valueOf(20), criteria.getToTransferAmount());
    Assertions.assertEquals(mosaicId, criteria.getTransferMosaicId());
  }

  @Test
  void shouldBeEquals() {

    Address address1 = Address.generateRandom(NetworkType.TEST_NET);
    Address address2 = Address.generateRandom(NetworkType.TEST_NET);
    PublicKey signerPublicKey = PublicKey.fromHexString("227F");

    TransactionSearchCriteria criteria1 =
        new TransactionSearchCriteria(TransactionGroup.UNCONFIRMED)
            .id("theId")
            .order(OrderBy.ASC)
            .pageSize(10)
            .pageNumber(5)
            .transactionTypes(Collections.singletonList(TransactionType.MOSAIC_GLOBAL_RESTRICTION));
    criteria1.address(address1);
    criteria1.recipientAddress(address2);
    criteria1.embedded(true);
    criteria1.height(BigInteger.ONE);
    criteria1.offset("offset1");
    criteria1.setSignerPublicKey(signerPublicKey);
    criteria1.setFromHeight(BigInteger.valueOf(2));
    criteria1.setToHeight(BigInteger.valueOf(3));

    TransactionSearchCriteria criteria2 =
        new TransactionSearchCriteria(TransactionGroup.UNCONFIRMED)
            .id("theId")
            .order(OrderBy.ASC)
            .pageSize(10)
            .pageNumber(5)
            .transactionTypes(Collections.singletonList(TransactionType.MOSAIC_GLOBAL_RESTRICTION));
    criteria2.address(address1);
    criteria2.recipientAddress(address2);
    criteria2.embedded(true);
    criteria2.height(BigInteger.ONE);
    criteria2.offset("offset1");
    criteria2.setSignerPublicKey(signerPublicKey);
    criteria2.setFromHeight(BigInteger.valueOf(2));
    criteria2.setToHeight(BigInteger.valueOf(3));

    Assertions.assertEquals(
        new TransactionSearchCriteria(TransactionGroup.UNCONFIRMED),
        new TransactionSearchCriteria(TransactionGroup.UNCONFIRMED));
    Assertions.assertEquals(criteria1, criteria2);
    Assertions.assertEquals(criteria1, criteria1);
    Assertions.assertEquals(criteria1.hashCode(), criteria2.hashCode());

    criteria2.embedded(false);
    Assertions.assertNotEquals(criteria1, criteria2);
    Assertions.assertNotEquals(criteria1.hashCode(), criteria2.hashCode());

    criteria2.pageNumber(100);
    criteria2.embedded(true);
    Assertions.assertNotEquals(criteria1, criteria2);
    Assertions.assertNotEquals(criteria1.hashCode(), criteria2.hashCode());

    Assertions.assertNotEquals("ABC", criteria2);
  }
}
