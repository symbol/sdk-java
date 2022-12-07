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

import io.nem.symbol.sdk.api.AccountRepository;
import io.nem.symbol.sdk.api.AccountRestrictionSearchCriteria;
import io.nem.symbol.sdk.api.AccountSearchCriteria;
import io.nem.symbol.sdk.api.HashLockRepository;
import io.nem.symbol.sdk.api.HashLockSearchCriteria;
import io.nem.symbol.sdk.api.MetadataRepository;
import io.nem.symbol.sdk.api.MetadataSearchCriteria;
import io.nem.symbol.sdk.api.MosaicRepository;
import io.nem.symbol.sdk.api.MosaicRestrictionSearchCriteria;
import io.nem.symbol.sdk.api.MosaicSearchCriteria;
import io.nem.symbol.sdk.api.NamespaceRepository;
import io.nem.symbol.sdk.api.NamespaceSearchCriteria;
import io.nem.symbol.sdk.api.OrderBy;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.api.RestrictionAccountRepository;
import io.nem.symbol.sdk.api.RestrictionMosaicRepository;
import io.nem.symbol.sdk.api.SearchCriteria;
import io.nem.symbol.sdk.api.SearcherRepository;
import io.nem.symbol.sdk.api.SecretLockRepository;
import io.nem.symbol.sdk.api.SecretLockSearchCriteria;
import io.nem.symbol.sdk.model.account.AccountInfo;
import io.nem.symbol.sdk.model.account.AccountRestrictions;
import io.nem.symbol.sdk.model.account.MultisigAccountInfo;
import io.nem.symbol.sdk.model.metadata.Metadata;
import io.nem.symbol.sdk.model.mosaic.MosaicInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceInfo;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.model.restriction.MosaicRestriction;
import io.nem.symbol.sdk.model.state.StateMerkleProof;
import io.nem.symbol.sdk.model.transaction.HashLockInfo;
import io.nem.symbol.sdk.model.transaction.LockStatus;
import io.nem.symbol.sdk.model.transaction.SecretLockInfo;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(Lifecycle.PER_CLASS)
public class MerkleLoaderIntegrationTest extends BaseIntegrationTest {

  public static final int TAKE_COUNT = 10;

  public static final OrderBy ORDER_BY = OrderBy.DESC;

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void mosaicRestrictionMerkles(RepositoryType repositoryType) {
    mosaicRestriction()
        .forEach(
            mosaicRestriction -> {
              StateProofServiceImpl service =
                  new StateProofServiceImpl(getRepositoryFactory(repositoryType));
              StateMerkleProof<MosaicRestriction<?>> proof =
                  get(service.mosaicRestriction(mosaicRestriction));
              Assertions.assertTrue(
                  proof.isValid(), "Invalid proof " + proof.getState().getCompositeHash());
            });
  }

  private List<MosaicRestriction<?>> mosaicRestriction() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    RestrictionMosaicRepository repository = repositoryFactory.createRestrictionMosaicRepository();
    return getArguments(repository, new MosaicRestrictionSearchCriteria());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void hashLocksMerkles(RepositoryType repositoryType) {
    hashLocks()
        .forEach(
            hashLockInfo -> {
              if (hashLockInfo.getStatus() == LockStatus.UNUSED) {
                StateProofServiceImpl service =
                    new StateProofServiceImpl(getRepositoryFactory(repositoryType));
                StateMerkleProof<HashLockInfo> proof = get(service.hashLock(hashLockInfo));
                Assertions.assertTrue(
                    proof.isValid(), "Invalid proof " + proof.getState().getHash());
              }
            });
  }

  private List<HashLockInfo> hashLocks() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    HashLockRepository repository = repositoryFactory.createHashLockRepository();
    return getArguments(repository, new HashLockSearchCriteria());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void accountRestrictionsMerkles(RepositoryType repositoryType) {
    accountRestrictions()
        .forEach(
            accountRestrictions -> {
              StateProofServiceImpl service =
                  new StateProofServiceImpl(getRepositoryFactory(repositoryType));
              StateMerkleProof<AccountRestrictions> proof =
                  get(service.accountRestrictions(accountRestrictions));
              Assertions.assertTrue(
                  proof.isValid(), "Invalid proof " + proof.getState().getAddress().plain());
            });
  }

  private List<AccountRestrictions> accountRestrictions() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    RestrictionAccountRepository repository =
        repositoryFactory.createRestrictionAccountRepository();
    return getArguments(repository, new AccountRestrictionSearchCriteria());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void multisigMerkles(RepositoryType repositoryType) {
    RepositoryFactory repositoryFactory = getRepositoryFactory(repositoryType);
    MultisigAccountInfo state =
        get(
            repositoryFactory
                .createMultisigRepository()
                .getMultisigAccountInfo(
                    helper().getMultisigAccount(repositoryType).getLeft().getAddress()));
    StateProofServiceImpl service = new StateProofServiceImpl(repositoryFactory);
    StateMerkleProof<MultisigAccountInfo> proof = get(service.multisig(state));
    Assertions.assertTrue(
        proof.isValid(), "Invalid proof " + proof.getState().getAccountAddress().plain());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void secretLocksMerkles(RepositoryType repositoryType) {
    secretLocks()
        .forEach(
            secretLockInfo -> {
              if (secretLockInfo.getStatus() == LockStatus.UNUSED) {
                StateProofServiceImpl service =
                    new StateProofServiceImpl(getRepositoryFactory(repositoryType));
                StateMerkleProof<SecretLockInfo> proof = get(service.secretLock(secretLockInfo));
                Assertions.assertTrue(
                    proof.isValid(), "Invalid proof " + proof.getState().getCompositeHash());
              }
            });
  }

  private List<SecretLockInfo> secretLocks() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    SecretLockRepository repository = repositoryFactory.createSecretLockRepository();
    return getArguments(repository, new SecretLockSearchCriteria().order(ORDER_BY));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void accountsMerkles(RepositoryType repositoryType) {
    accounts()
        .forEach(
            account -> {
              StateProofServiceImpl service =
                  new StateProofServiceImpl(getRepositoryFactory(repositoryType));
              StateMerkleProof<AccountInfo> proof = get(service.account(account));
              Assertions.assertTrue(
                  proof.isValid(), "Invalid proof " + proof.getState().getAddress().plain());
            });
  }

  private List<AccountInfo> accounts() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    AccountRepository repository = repositoryFactory.createAccountRepository();
    return getArguments(repository, new AccountSearchCriteria().order(ORDER_BY));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void mosaicsMerkles(RepositoryType repositoryType) {
    mosaics()
        .forEach(
            mosaicInfo -> {
              StateProofServiceImpl service =
                  new StateProofServiceImpl(getRepositoryFactory(repositoryType));
              StateMerkleProof<MosaicInfo> proof = get(service.mosaic(mosaicInfo));
              Assertions.assertTrue(
                  proof.isValid(), "Invalid proof " + proof.getState().getMosaicId().getIdAsHex());
            });
  }

  private List<MosaicInfo> mosaics() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    MosaicRepository repository = repositoryFactory.createMosaicRepository();
    return getArguments(repository, new MosaicSearchCriteria().order(ORDER_BY));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void namespacesMerkles(RepositoryType repositoryType) {
    namespaces()
        .forEach(
            namespaceInfo -> {
              RepositoryFactory repositoryFactory = getRepositoryFactory(repositoryType);
              StateProofServiceImpl service = new StateProofServiceImpl(repositoryFactory);
              StateMerkleProof<NamespaceInfo> proof = get(service.namespace(namespaceInfo));
              Assertions.assertTrue(
                  proof.isValid(), "Invalid proof " + proof.getState().getId().getIdAsHex());
            });
  }

  private List<NamespaceInfo> namespaces() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    NamespaceRepository repository = repositoryFactory.createNamespaceRepository();
    return getArguments(
        repository,
        new NamespaceSearchCriteria()
            .order(ORDER_BY)
            .registrationType(NamespaceRegistrationType.ROOT_NAMESPACE));
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void metadatasMerkles(RepositoryType repositoryType) {
    metadatas()
        .forEach(
            metadata -> {
              StateProofServiceImpl service =
                  new StateProofServiceImpl(getRepositoryFactory(repositoryType));
              StateMerkleProof<Metadata> proof = get(service.metadata(metadata));
              Assertions.assertTrue(
                  proof.isValid(), "Invalid proof " + proof.getState().getCompositeHash());
            });
  }

  private List<Metadata> metadatas() {
    RepositoryFactory repositoryFactory = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE);
    MetadataRepository repository = repositoryFactory.createMetadataRepository();
    return getArguments(repository, new MetadataSearchCriteria().order(ORDER_BY));
  }

  private <E, C extends SearchCriteria<C>> List<E> getArguments(
      SearcherRepository<E, C> repository, C criteria) {
    return get(
        repository
            .streamer()
            .search(criteria.order(OrderBy.DESC))
            .take(TAKE_COUNT)
            .toList()
            .toObservable());
  }
}
