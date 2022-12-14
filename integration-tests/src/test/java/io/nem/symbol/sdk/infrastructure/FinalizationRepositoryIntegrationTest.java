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

import static java.lang.Math.max;

import io.nem.symbol.sdk.api.FinalizationRepository;
import io.nem.symbol.sdk.api.RepositoryFactory;
import io.nem.symbol.sdk.model.blockchain.FinalizedBlock;
import io.nem.symbol.sdk.model.finalization.FinalizationProof;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FinalizationRepositoryIntegrationTest extends BaseIntegrationTest {

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getFinalizationProofAtEpoch(RepositoryType type) {
    FinalizationRepository repository = getRepositoryFactory(type).createFinalizationRepository();
    FinalizationProof finalizationProof = get(repository.getFinalizationProofAtEpoch(1));
    Assertions.assertEquals(1L, finalizationProof.getFinalizationEpoch());
    Assertions.assertEquals(1L, finalizationProof.getFinalizationPoint());
    Assertions.assertEquals(BigInteger.ONE, finalizationProof.getHeight());
    Assertions.assertEquals(1, finalizationProof.getVersion());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getFinalizationProofAtHeight(RepositoryType type) {
    FinalizationRepository repository = getRepositoryFactory(type).createFinalizationRepository();
    FinalizationProof finalizationProof =
        get(repository.getFinalizationProofAtHeight(BigInteger.ONE));
    Assertions.assertEquals(1L, finalizationProof.getFinalizationEpoch());
    Assertions.assertEquals(1L, finalizationProof.getFinalizationPoint());
    Assertions.assertEquals(BigInteger.ONE, finalizationProof.getHeight());
    Assertions.assertEquals(1, finalizationProof.getVersion());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getFinalizationProofAtPreviousFinalizedEpochHeight(RepositoryType type) {
    RepositoryFactory repositoryFactory = getRepositoryFactory(type);
    FinalizedBlock finalizedBlock =
        get(repositoryFactory.createChainRepository().getChainInfo()).getLatestFinalizedBlock();

    FinalizationRepository repository = repositoryFactory.createFinalizationRepository();
    long previousEpoch = max(finalizedBlock.getFinalizationEpoch() - 1, 1);
    FinalizationProof latestFinalizationProof =
        get(repository.getFinalizationProofAtEpoch(previousEpoch));
    FinalizationProof finalizationProof =
        get(repository.getFinalizationProofAtHeight(latestFinalizationProof.getHeight()));

    Assertions.assertEquals(
        finalizationProof.getFinalizationEpoch(), finalizationProof.getFinalizationEpoch());
    Assertions.assertEquals(
        finalizationProof.getFinalizationPoint(), finalizationProof.getFinalizationPoint());
    Assertions.assertEquals(finalizationProof.getHeight(), finalizationProof.getHeight());
    Assertions.assertEquals(1, finalizationProof.getVersion());
  }

  @ParameterizedTest
  @EnumSource(RepositoryType.class)
  void getFinalizationProofAtEpochLatest(RepositoryType type) {
    RepositoryFactory repositoryFactory = getRepositoryFactory(type);
    FinalizedBlock finalizedBlock =
        get(repositoryFactory.createChainRepository().getChainInfo()).getLatestFinalizedBlock();

    FinalizationRepository repository = repositoryFactory.createFinalizationRepository();
    FinalizationProof finalizationProof =
        get(repository.getFinalizationProofAtEpoch(finalizedBlock.getFinalizationEpoch()));

    Assertions.assertEquals(
        finalizationProof.getFinalizationEpoch(), finalizationProof.getFinalizationEpoch());
    Assertions.assertEquals(
        finalizationProof.getFinalizationPoint(), finalizationProof.getFinalizationPoint());
    Assertions.assertEquals(finalizationProof.getHeight(), finalizationProof.getHeight());
    Assertions.assertEquals(1, finalizationProof.getVersion());
  }
}
