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

import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.receipt.ReceiptType;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransactionStatementSearchCriteriaTest {

  @Test
  void shouldCreate() {
    TransactionStatementSearchCriteria criteria = new TransactionStatementSearchCriteria();
    Assertions.assertNull(criteria.getOrder());
    Assertions.assertNull(criteria.getPageSize());
    Assertions.assertNull(criteria.getPageNumber());
    Assertions.assertNull(criteria.getOffset());
    Assertions.assertNull(criteria.getHeight());
    Assertions.assertNull(criteria.getReceiptTypes());
    Assertions.assertNull(criteria.getArtifactId());
    Assertions.assertNull(criteria.getTargetAddress());
    Assertions.assertNull(criteria.getRecipientAddress());
  }

  @Test
  void shouldSetValues() {

    Address recipientAddress = Address.generateRandom(NetworkType.TEST_NET);
    Address targetAddress = Address.generateRandom(NetworkType.TEST_NET);
    Address senderAddress = Address.generateRandom(NetworkType.TEST_NET);

    TransactionStatementSearchCriteria criteria = new TransactionStatementSearchCriteria();

    criteria.setOrder(OrderBy.DESC);
    criteria.setPageSize(10);
    criteria.setPageNumber(5);
    criteria.setOffset("abc");
    criteria.setHeight(BigInteger.ONE);
    criteria.setFromHeight(BigInteger.valueOf(2));
    criteria.setToHeight(BigInteger.valueOf(3));
    criteria.setReceiptTypes(Collections.singletonList(ReceiptType.MOSAIC_ALIAS_RESOLUTION));
    criteria.setRecipientAddress(recipientAddress);
    criteria.setTargetAddress(targetAddress);
    criteria.setSenderAddress(senderAddress);
    criteria.setArtifactId("artifactid");

    Assertions.assertEquals(OrderBy.DESC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals(BigInteger.ONE, criteria.getHeight());
    Assertions.assertEquals(BigInteger.valueOf(2), criteria.getFromHeight());
    Assertions.assertEquals(BigInteger.valueOf(3), criteria.getToHeight());
    Assertions.assertEquals("abc", criteria.getOffset());

    Assertions.assertEquals(
        Collections.singletonList(ReceiptType.MOSAIC_ALIAS_RESOLUTION), criteria.getReceiptTypes());
    Assertions.assertEquals("artifactid", criteria.getArtifactId());
    Assertions.assertEquals(recipientAddress, criteria.getRecipientAddress());
    Assertions.assertEquals(targetAddress, criteria.getTargetAddress());
    Assertions.assertEquals(senderAddress, criteria.getSenderAddress());
  }

  @Test
  void shouldUseBuilderMethods() {

    Address recipientAddress = Address.generateRandom(NetworkType.TEST_NET);
    Address targetAddress = Address.generateRandom(NetworkType.TEST_NET);
    Address senderAddress = Address.generateRandom(NetworkType.TEST_NET);

    TransactionStatementSearchCriteria criteria =
        new TransactionStatementSearchCriteria().height(BigInteger.ONE);
    criteria.fromHeight(BigInteger.valueOf(2));
    criteria.toHeight(BigInteger.valueOf(3));
    criteria.order(OrderBy.ASC).pageSize(10).pageNumber(5);
    criteria.receiptTypes(Collections.singletonList(ReceiptType.MOSAIC_ALIAS_RESOLUTION));
    criteria.recipientAddress(recipientAddress);
    criteria.targetAddress(targetAddress);
    criteria.senderAddress(senderAddress);
    criteria.artifactId("artifactid");

    criteria.offset("abc");
    Assertions.assertEquals(OrderBy.ASC, criteria.getOrder());
    Assertions.assertEquals(10, criteria.getPageSize());
    Assertions.assertEquals(5, criteria.getPageNumber());
    Assertions.assertEquals(BigInteger.ONE, criteria.getHeight());
    Assertions.assertEquals(BigInteger.valueOf(2), criteria.getFromHeight());
    Assertions.assertEquals(BigInteger.valueOf(3), criteria.getToHeight());
    Assertions.assertEquals("abc", criteria.getOffset());

    Assertions.assertEquals(
        Collections.singletonList(ReceiptType.MOSAIC_ALIAS_RESOLUTION), criteria.getReceiptTypes());
    Assertions.assertEquals("artifactid", criteria.getArtifactId());
    Assertions.assertEquals(recipientAddress, criteria.getRecipientAddress());
    Assertions.assertEquals(targetAddress, criteria.getTargetAddress());
    Assertions.assertEquals(senderAddress, criteria.getSenderAddress());
  }

  @Test
  void shouldBeEquals() {

    Address recipientAddress = Address.generateRandom(NetworkType.TEST_NET);
    Address targetAddress = Address.generateRandom(NetworkType.TEST_NET);
    Address senderAddress = Address.generateRandom(NetworkType.TEST_NET);

    TransactionStatementSearchCriteria criteria1 =
        new TransactionStatementSearchCriteria()
            .order(OrderBy.ASC)
            .pageSize(10)
            .pageNumber(5)
            .height(BigInteger.ONE);
    criteria1.offset("abc");
    criteria1.receiptTypes(Collections.singletonList(ReceiptType.MOSAIC_ALIAS_RESOLUTION));
    criteria1.recipientAddress(recipientAddress);
    criteria1.targetAddress(targetAddress);
    criteria1.senderAddress(senderAddress);
    criteria1.artifactId("artifactid");

    TransactionStatementSearchCriteria criteria2 =
        new TransactionStatementSearchCriteria()
            .order(OrderBy.ASC)
            .pageSize(10)
            .pageNumber(5)
            .height(BigInteger.ONE);
    criteria2.offset("abc");
    criteria2.receiptTypes(Collections.singletonList(ReceiptType.MOSAIC_ALIAS_RESOLUTION));
    criteria2.recipientAddress(recipientAddress);
    criteria2.targetAddress(targetAddress);
    criteria2.senderAddress(senderAddress);
    criteria2.artifactId("artifactid");

    Assertions.assertEquals(
        new TransactionStatementSearchCriteria(), new TransactionStatementSearchCriteria());
    Assertions.assertEquals(criteria1, criteria2);
    Assertions.assertEquals(criteria1, criteria1);
    Assertions.assertEquals(criteria1.hashCode(), criteria2.hashCode());

    criteria1.setHeight(BigInteger.TEN);
    Assertions.assertNotEquals(criteria1, criteria2);
    Assertions.assertNotEquals(criteria1.hashCode(), criteria2.hashCode());

    Assertions.assertNotEquals("ABC", criteria2);
  }
}
