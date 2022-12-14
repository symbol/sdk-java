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
package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.sdk.api.MosaicRestrictionSearchCriteria;
import io.nem.symbol.sdk.api.MosaicRestrictionTransactionService;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.RestrictionMosaicRepository;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.restriction.MosaicGlobalRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicRestriction;
import io.nem.symbol.sdk.model.restriction.MosaicRestrictionEntryType;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MosaicRestrictionServiceIntegrationTest extends BaseIntegrationTest {

  private final BigInteger restrictionKey = BigInteger.valueOf(11111);
  private Account testAccount;

  @BeforeAll
  void setup() {
    testAccount = helper().getTestAccount(TestHelper.DEFAULT_REPOSITORY_TYPE).getLeft();
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void createUpdateMosaicGlobalRestrictionTransactionFactory(RepositoryType type) {

    // 1) Create a new mosaic
    MosaicId mosaicId = createMosaic(testAccount, type, null, null);

    // 2) Create a restriction on the mosaic

    MosaicRestrictionTransactionService restrictionService =
        getMosaicRestrictionTransactionService(type);

    BigInteger originalValue = BigInteger.valueOf(20);
    MosaicRestrictionType originalRestrictionType = MosaicRestrictionType.GE;
    MosaicGlobalRestrictionTransaction createTransaction =
        get(restrictionService.createMosaicGlobalRestrictionTransactionFactory(
                mosaicId, restrictionKey, originalValue, originalRestrictionType))
            .maxFee(maxFee)
            .build();

    // 3) Announce the create restriction transaction
    MosaicGlobalRestrictionTransaction processedCreateTransaction =
        announceAndValidate(type, testAccount, createTransaction);
    // 4) Validate that the received processedCreateTransaction and the create transaction are the
    // same
    assertTransaction(createTransaction, processedCreateTransaction);

    // 5) Validate the data from the endpoints

    waitForIndexing();

    RestrictionMosaicRepository restrictionRepository =
        getRepositoryFactory(type).createRestrictionMosaicRepository();

    assertMosaicGlobalRestriction(
        createTransaction, getMosaicGlobalRestriction(restrictionRepository, mosaicId));

    // 6) Modifying the restriction by sending a new transaction with the previous values.
    MosaicGlobalRestrictionTransaction updateTransaction =
        get(restrictionService.createMosaicGlobalRestrictionTransactionFactory(
                mosaicId, restrictionKey, BigInteger.valueOf(40), MosaicRestrictionType.EQ))
            .maxFee(maxFee)
            .build();

    // 7) Announcing the update restriction transaction and checking the processed one.
    MosaicGlobalRestrictionTransaction processedUpdateTransaction =
        announceAndValidate(type, testAccount, updateTransaction);

    assertTransaction(updateTransaction, processedUpdateTransaction);

    // 8) Validating that the endpoints show the new value and type.

    waitForIndexing();

    assertMosaicGlobalRestriction(
        updateTransaction, getMosaicGlobalRestriction(restrictionRepository, mosaicId));
  }

  private MosaicGlobalRestriction getMosaicGlobalRestriction(
      RestrictionMosaicRepository restrictionRepository, MosaicId mosaicId) {
    return (MosaicGlobalRestriction)
        get(restrictionRepository.search(
                new MosaicRestrictionSearchCriteria()
                    .mosaicId(mosaicId)
                    .entryType(MosaicRestrictionEntryType.GLOBAL)))
            .getData()
            .get(0);
  }

  private void assertTransaction(
      MosaicGlobalRestrictionTransaction expectedTransaction,
      MosaicGlobalRestrictionTransaction processedTransaction) {

    Assertions.assertEquals(
        expectedTransaction.getMosaicId().getId(), processedTransaction.getMosaicId().getId());

    Assertions.assertEquals(
        expectedTransaction.getReferenceMosaicId(), processedTransaction.getReferenceMosaicId());

    Assertions.assertEquals(
        expectedTransaction.getNewRestrictionType(), processedTransaction.getNewRestrictionType());

    Assertions.assertEquals(
        expectedTransaction.getPreviousRestrictionType(),
        processedTransaction.getPreviousRestrictionType());

    Assertions.assertEquals(
        expectedTransaction.getNewRestrictionValue(),
        processedTransaction.getNewRestrictionValue());

    Assertions.assertEquals(
        expectedTransaction.getPreviousRestrictionValue(),
        processedTransaction.getPreviousRestrictionValue());

    Assertions.assertEquals(
        expectedTransaction.getRestrictionKey(), processedTransaction.getRestrictionKey());
  }

  private void assertMosaicGlobalRestriction(
      MosaicGlobalRestrictionTransaction mosaicGlobalRestrictionTransaction,
      MosaicGlobalRestriction mosaicGlobalRestriction) {

    BigInteger restrictionKey = mosaicGlobalRestrictionTransaction.getRestrictionKey();
    BigInteger newRestrictionValue = mosaicGlobalRestrictionTransaction.getNewRestrictionValue();

    Assertions.assertEquals(restrictionKey, mosaicGlobalRestrictionTransaction.getRestrictionKey());

    Assertions.assertEquals(1, mosaicGlobalRestriction.getRestrictions().size());

    Assertions.assertEquals(
        Collections.singleton(restrictionKey), mosaicGlobalRestriction.getRestrictions().keySet());

    Assertions.assertEquals(
        newRestrictionValue,
        mosaicGlobalRestriction.getRestrictions().get(restrictionKey).getRestrictionValue());

    Assertions.assertEquals(
        mosaicGlobalRestrictionTransaction.getNewRestrictionType(),
        mosaicGlobalRestriction.getRestrictions().get(restrictionKey).getRestrictionType());

    Assertions.assertEquals(
        new MosaicId(BigInteger.ZERO),
        mosaicGlobalRestriction.getRestrictions().get(restrictionKey).getReferenceMosaicId());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void createUpdateMosaicAddressRestrictionTransactionFactory(RepositoryType type) {

    // 1) Create a new mosaic
    MosaicId mosaicId = createMosaic(testAccount, type, null, null);

    // 2) Create a restriction on the mosaic

    MosaicRestrictionTransactionService restrictionService =
        getMosaicRestrictionTransactionService(type);

    BigInteger originalValue = BigInteger.valueOf(20);
    MosaicRestrictionType originalRestrictionType = MosaicRestrictionType.GE;
    MosaicGlobalRestrictionTransaction createTransaction =
        get(restrictionService.createMosaicGlobalRestrictionTransactionFactory(
                mosaicId, restrictionKey, originalValue, originalRestrictionType))
            .maxFee(maxFee)
            .build();

    // 3) Announce the create restriction transaction
    MosaicGlobalRestrictionTransaction processedCreateTransaction =
        announceAndValidate(type, testAccount, createTransaction);
    // 4) Validate that the received processedCreateTransaction and the create transaction are the
    // same
    assertTransaction(createTransaction, processedCreateTransaction);

    // 5) Validate the data from the endpoints

    RestrictionMosaicRepository restrictionRepository =
        getRepositoryFactory(type).createRestrictionMosaicRepository();

    waitForIndexing();

    assertMosaicGlobalRestriction(
        createTransaction, getMosaicGlobalRestriction(restrictionRepository, mosaicId));

    // 6) Create the restriction by sending a new transaction
    MosaicAddressRestrictionTransaction createAddressTransaction =
        get(restrictionService.createMosaicAddressRestrictionTransactionFactory(
                mosaicId, restrictionKey, testAccount.getAddress(), BigInteger.valueOf(40)))
            .maxFee(maxFee)
            .build();

    announceAndValidate(type, testAccount, createAddressTransaction);

    // 7) Announcing the update restriction transaction and checking the processed one.

    waitForIndexing();

    MosaicAddressRestrictionTransaction updateAddressTransaction =
        get(restrictionService.createMosaicAddressRestrictionTransactionFactory(
                mosaicId, restrictionKey, testAccount.getAddress(), BigInteger.valueOf(50)))
            .maxFee(maxFee)
            .build();

    MosaicAddressRestrictionTransaction finalTransaction =
        announceAndValidate(type, testAccount, updateAddressTransaction);

    Assertions.assertEquals(BigInteger.valueOf(50), finalTransaction.getNewRestrictionValue());
    Assertions.assertEquals(BigInteger.valueOf(40), finalTransaction.getPreviousRestrictionValue());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void createUpdateMosaicAddressRestrictionTransactionFactoryUsingAlias(RepositoryType type) {

    // 1) Create mosaic and alias
    String mosaicAliasName =
        "createUpdateMosaicAddressRestrictionAlias".toLowerCase()
            + RandomUtils.generateRandomInt(100000);
    NamespaceId mosaicAlias = NamespaceId.createFromName(mosaicAliasName);
    MosaicId mosaicId = createMosaic(testAccount, type, null, mosaicAliasName);

    // 2) Create a restriction on the mosaic

    waitForIndexing();

    MosaicRestrictionTransactionService restrictionService =
        getMosaicRestrictionTransactionService(type);

    BigInteger originalValue = BigInteger.valueOf(20);
    MosaicRestrictionType originalRestrictionType = MosaicRestrictionType.GE;
    MosaicGlobalRestrictionTransaction createTransaction =
        get(restrictionService.createMosaicGlobalRestrictionTransactionFactory(
                mosaicAlias, restrictionKey, originalValue, originalRestrictionType))
            .maxFee(maxFee)
            .build();

    // 3) Announce the create restriction transaction
    MosaicGlobalRestrictionTransaction processedCreateTransaction =
        announceAndValidate(type, testAccount, createTransaction);
    // 4) Validate that the received processedCreateTransaction and the create transaction are the
    // same
    assertTransaction(createTransaction, processedCreateTransaction);

    // 5) Validate the data from the endpoints

    waitForIndexing();

    RestrictionMosaicRepository restrictionRepository =
        getRepositoryFactory(type).createRestrictionMosaicRepository();

    assertMosaicGlobalRestriction(
        createTransaction, getMosaicGlobalRestriction(restrictionRepository, mosaicId));

    // 6) Create the restriction by sending a new transaction
    MosaicAddressRestrictionTransaction createAddressTransaction =
        get(restrictionService.createMosaicAddressRestrictionTransactionFactory(
                mosaicAlias, restrictionKey, testAccount.getAddress(), BigInteger.valueOf(40)))
            .maxFee(maxFee)
            .build();

    announceAndValidate(type, testAccount, createAddressTransaction);

    // 7) Announcing the update restriction transaction and checking the processed one.

    waitForIndexing();

    MosaicAddressRestrictionTransaction updateAddressTransaction =
        get(restrictionService.createMosaicAddressRestrictionTransactionFactory(
                mosaicAlias, restrictionKey, testAccount.getAddress(), BigInteger.valueOf(50)))
            .maxFee(maxFee)
            .build();

    MosaicAddressRestrictionTransaction finalTransaction =
        announceAndValidate(type, testAccount, updateAddressTransaction);

    Assertions.assertEquals(BigInteger.valueOf(50), finalTransaction.getNewRestrictionValue());
    Assertions.assertEquals(BigInteger.valueOf(40), finalTransaction.getPreviousRestrictionValue());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getMosaicGlobalRestrictionWhenMosaicDoesNotExist(RepositoryType type) {
    RestrictionMosaicRepository repository =
        getRepositoryFactory(type).createRestrictionMosaicRepository();

    Address address =
        Address.createFromPublicKey(
            "67F69FA4BFCD158F6E1AF1ABC82F725F5C5C4710D6E29217B12BE66397435DFB", getNetworkType());

    Page<MosaicRestriction<?>> page =
        get(
            repository.search(
                new MosaicRestrictionSearchCriteria()
                    .mosaicId(new MosaicId(BigInteger.valueOf(888888)))
                    .targetAddress(address)
                    .entryType(MosaicRestrictionEntryType.GLOBAL)));
    Assertions.assertTrue(page.getData().isEmpty());
  }
}
