/*
 * Copyright 2018 NEM
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

package io.nem.sdk.infrastructure;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.core.crypto.Hashes;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.api.TransactionRepository;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.AccountNames;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNames;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.mosaic.MosaicProperties;
import io.nem.sdk.model.mosaic.MosaicSupplyType;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.AddressAliasTransaction;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.CosignatureTransaction;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.HashType;
import io.nem.sdk.model.transaction.LockFundsTransaction;
import io.nem.sdk.model.transaction.ModifyMultisigAccountTransaction;
import io.nem.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.sdk.model.transaction.MultisigCosignatoryModification;
import io.nem.sdk.model.transaction.MultisigCosignatoryModificationType;
import io.nem.sdk.model.transaction.PlainMessage;
import io.nem.sdk.model.transaction.RegisterNamespaceTransaction;
import io.nem.sdk.model.transaction.SecretLockTransaction;
import io.nem.sdk.model.transaction.SecretProofTransaction;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.sdk.model.transaction.TransferTransaction;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class E2EIntegrationTest extends BaseIntegrationTest {

    private Account account;
    private Address recipient;
    private Account multisigAccount;
    private Account cosignatoryAccount;
    private Account cosignatoryAccount2;
    private NamespaceId rootNamespaceId;
    private MosaicId mosaicId;
    private String generationHash;
    private long timeoutSeconds;
    private Map<RepositoryType, Listener> listenerMap = new HashMap<>();

    @BeforeAll
    void setup() {
        account = this.getTestAccount();
        recipient = this.getRecipient();
        multisigAccount = this.getTestMultisigAccount();
        cosignatoryAccount = this.getTestCosignatoryAccount();
        cosignatoryAccount2 = this.getTestCosignatoryAccount2();
        generationHash = this.getGenerationHash();
        timeoutSeconds = this.getTimeoutSeconds();
    }

    @AfterAll
    void tearDown() {
        listenerMap.values().forEach(Listener::close);
    }

    /**
     * Method that creates a {@link RepositoryFactory} based on the {@link RepositoryType}.
     */
    public Listener getListener(RepositoryType type) {
        return listenerMap.computeIfAbsent(type, this::createListener);
    }

    private Listener createListener(RepositoryType type) {
        Listener listener = getRepositoryFactory(type).createListener();
        try {
            listener.open().get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Listener could not be created or opened. Error " + ExceptionUtils.getMessage(e),
                e);
        }
        return listener;
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void standaloneTransferTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        TransferTransaction transferTransaction =
            TransferTransaction.create(
                new Deadline(2, HOURS),
                BigInteger.ZERO,
                this.recipient,
                Collections
                    .singletonList(NetworkCurrencyMosaic.createAbsolute(BigInteger.valueOf(1))),
                new PlainMessage("E2ETest:standaloneTransferTransaction:message"),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction = this.account
            .sign(transferTransaction, generationHash);
        String payload = signedTransaction.getPayload();
        assertEquals(420, payload.length());

        TransactionAnnounceResponse transactionAnnounceResponse =
            getTransactionRepository(type).announce(signedTransaction).toFuture().get();
        assertEquals(
            "packet 9 was pushed to the network via /transaction",
            transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateTransferTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        TransferTransaction transferTransaction =
            TransferTransaction.create(
                new Deadline(2, HOURS),
                BigInteger.ZERO,
                this.recipient,
                Collections
                    .singletonList(NetworkCurrencyMosaic.createAbsolute(BigInteger.valueOf(1))),
                /*new PlainMessage(
                    "E2ETest:aggregateTransferTransaction:message"), */
// short message for debugging
                new PlainMessage(
                    "E2ETest:aggregateTransferTransaction:messagelooooooooooooooooooooooooooooooooooooooo"
                        +
                        "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                        +
                        "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                        +
                        "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                        +
                        "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                        +
                        "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"
                        +
                        "oooooooong"),
                // Use long message to test if size of inner transaction is calculated correctly
                NetworkType.MIJIN_TEST);

        AggregateTransaction aggregateTransaction =
            AggregateTransaction.createComplete(
                new Deadline(2, HOURS),
                Collections.singletonList(
                    transferTransaction.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            getTransactionRepository(type).announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);
    }

    private TransactionRepository getTransactionRepository(
        RepositoryType type) {
        return getRepositoryFactory(type).createTransactionRepository();
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneRootRegisterNamespaceTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        String namespaceName =
            "test-root-namespace-" + new Double(Math.floor(Math.random() * 10000)).intValue();

        RegisterNamespaceTransaction registerNamespaceTransaction =
            RegisterNamespaceTransaction.createRootNamespace(
                new Deadline(2, HOURS),
                BigInteger.ZERO,
                namespaceName,
                BigInteger.valueOf(100),
                NetworkType.MIJIN_TEST);

        this.rootNamespaceId = registerNamespaceTransaction.getNamespaceId();

        SignedTransaction signedTransaction =
            this.account.sign(registerNamespaceTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            getTransactionRepository(type).announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateRootRegisterNamespaceTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        String namespaceName =
            "test-root-namespace-" + new Double(Math.floor(Math.random() * 10000)).intValue();

        RegisterNamespaceTransaction registerNamespaceTransaction =
            RegisterNamespaceTransaction.createRootNamespace(
                new Deadline(2, HOURS),
                BigInteger.ZERO,
                namespaceName,
                BigInteger.valueOf(100),
                NetworkType.MIJIN_TEST);

        this.rootNamespaceId = registerNamespaceTransaction.getNamespaceId();

        AggregateTransaction aggregateTransaction =
            AggregateTransaction.createComplete(
                new Deadline(2, HOURS),
                Collections.singletonList(
                    registerNamespaceTransaction.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            getTransactionRepository(type).announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void sendAddressAliasTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        String namespaceName =
            "test-root-namespace-for-address-alias-" + new Double(Math.floor(Math.random() * 10000))
                .intValue();

        RegisterNamespaceTransaction registerNamespaceTransaction =
            RegisterNamespaceTransaction.createRootNamespace(
                new Deadline(2, HOURS),
                BigInteger.ZERO,
                namespaceName,
                BigInteger.valueOf(100),
                NetworkType.MIJIN_TEST);

        this.rootNamespaceId = registerNamespaceTransaction.getNamespaceId();

        AggregateTransaction aggregateTransaction =
            AggregateTransaction.createComplete(
                new Deadline(2, HOURS),
                Collections.singletonList(
                    registerNamespaceTransaction.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            getTransactionRepository(type).announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);

        this.rootNamespaceId = registerNamespaceTransaction.getNamespaceId();

        AddressAliasTransaction addressAliasTransaction =
            AddressAliasTransaction.create(
                new Deadline(2, HOURS),
                BigInteger.ZERO,
                AliasAction.Link,
                this.rootNamespaceId,
                this.account.getAddress(),
                NetworkType.MIJIN_TEST);

        AggregateTransaction aggregateTransaction2 =
            AggregateTransaction.createComplete(
                new Deadline(2, HOURS),
                Collections.singletonList(
                    addressAliasTransaction.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction2 = this.account
            .sign(aggregateTransaction2, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse2 =
            getTransactionRepository(type).announce(signedTransaction2).toFuture().get();
        System.out.println(transactionAnnounceResponse2.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction2.getHash(), type);

        List<AccountNames> accountNames = getRepositoryFactory(type).createAccountRepository()
            .getAccountsNames(Collections.singletonList(this.account.getAddress())).toFuture()
            .get();

        Assert.assertEquals(1, accountNames.size());

        assertEquals(1, accountNames.size());
        assertEquals(this.config().getTestAccountAddress(),
            accountNames.get(0).getAddress().plain());
        assertTrue(accountNames.get(0).getNames().contains(namespaceName));
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneSubNamespaceRegisterNamespaceTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {

        this.standaloneRootRegisterNamespaceTransaction(type);

        String namespaceName =
            "test-sub-namespace-" + new Double(Math.floor(Math.random() * 10000)).intValue();

        RegisterNamespaceTransaction registerNamespaceTransaction =
            RegisterNamespaceTransaction.createSubNamespace(
                new Deadline(2, HOURS),
                BigInteger.ZERO,
                namespaceName,
                this.rootNamespaceId,
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction =
            this.account.sign(registerNamespaceTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            getTransactionRepository(type).announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateSubNamespaceRegisterNamespaceTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {

        this.aggregateRootRegisterNamespaceTransaction(type);

        String namespaceName =
            "test-sub-namespace-" + new Double(Math.floor(Math.random() * 10000)).intValue();

        RegisterNamespaceTransaction registerNamespaceTransaction =
            RegisterNamespaceTransaction.createSubNamespace(
                new Deadline(2, HOURS),
                BigInteger.ZERO,
                namespaceName,
                this.rootNamespaceId,
                NetworkType.MIJIN_TEST);

        AggregateTransaction aggregateTransaction =
            AggregateTransaction.createComplete(
                new Deadline(2, HOURS),
                Collections.singletonList(
                    registerNamespaceTransaction.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            getTransactionRepository(type).announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneMosaicDefinitionTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        MosaicNonce nonce = MosaicNonce.createRandom();
        this.mosaicId = MosaicId.createFromNonce(nonce, this.account.getPublicAccount());

        MosaicDefinitionTransaction mosaicDefinitionTransaction =
            MosaicDefinitionTransaction.create(
                new Deadline(2, HOURS),
                BigInteger.ZERO,
                nonce,
                this.mosaicId,
                MosaicProperties.create(true, true, 4, BigInteger.valueOf(100)),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction =
            this.account.sign(mosaicDefinitionTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            getTransactionRepository(type).announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void sendMosaicAliasTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        String namespaceName =
            "test-root-namespace-for-mosaic-alias-" + new Double(Math.floor(Math.random() * 10000))
                .intValue();

        AccountInfo accountInfo = getRepositoryFactory(type).createAccountRepository()
            .getAccountInfo(this.account.getPublicAccount().getAddress()).toFuture()
            .get(timeoutSeconds, TimeUnit.SECONDS);

        Assert.assertFalse(
            accountInfo.getMosaics().isEmpty());

        MosaicId mosaicId = createMosaic(type);

        RegisterNamespaceTransaction registerNamespaceTransaction =
            RegisterNamespaceTransaction.createRootNamespace(
                new Deadline(2, HOURS),
                BigInteger.ZERO,
                namespaceName,
                BigInteger.valueOf(100),
                NetworkType.MIJIN_TEST);

        this.rootNamespaceId = registerNamespaceTransaction.getNamespaceId();

        AggregateTransaction aggregateTransaction =
            AggregateTransaction.createComplete(
                new Deadline(2, HOURS),
                Collections.singletonList(
                    registerNamespaceTransaction.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            getTransactionRepository(type).announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);

        this.rootNamespaceId = registerNamespaceTransaction.getNamespaceId();

        MosaicAliasTransaction addressAliasTransaction =
            MosaicAliasTransaction.create(
                new Deadline(2, HOURS),
                BigInteger.ZERO,
                AliasAction.Link,
                this.rootNamespaceId,
                mosaicId,
                NetworkType.MIJIN_TEST);

        AggregateTransaction aggregateTransaction2 =
            AggregateTransaction.createComplete(
                new Deadline(2, HOURS),
                Collections.singletonList(
                    addressAliasTransaction.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction2 = this.account
            .sign(aggregateTransaction2, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse2 =
            getTransactionRepository(type).announce(signedTransaction2).toFuture().get();
        System.out.println(transactionAnnounceResponse2.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction2.getHash(), type);

        List<MosaicNames> accountNames = getRepositoryFactory(type).createMosaicRepository()
            .getMosaicsNames(Collections.singletonList(mosaicId)).toFuture()
            .get();

        Assert.assertEquals(1, accountNames.size());

        assertEquals(1, accountNames.size());
        assertEquals(mosaicId, accountNames.get(0).getMosaicId());
        assertTrue(accountNames.get(0).getNames().contains(namespaceName));
    }

    private MosaicId createMosaic(RepositoryType type)
        throws InterruptedException, ExecutionException, TimeoutException {
        MosaicNonce nonce = MosaicNonce.createRandom();
        MosaicId mosaicId = MosaicId.createFromNonce(nonce, this.account.getPublicAccount());

        MosaicDefinitionTransaction mosaicDefinitionTransaction =
            MosaicDefinitionTransaction.create(
                new Deadline(2, HOURS),
                BigInteger.ZERO,
                nonce,
                mosaicId,
                MosaicProperties.create(true, true, 4, BigInteger.valueOf(100)),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction =
            this.account.sign(mosaicDefinitionTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            getTransactionRepository(type).announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(this.account.getAddress(),
            signedTransaction.getHash(), type);
        return mosaicId;
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateMosaicDefinitionTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        MosaicNonce nonce = MosaicNonce.createRandom();
        this.mosaicId = MosaicId.createFromNonce(nonce, this.account.getPublicAccount());

        MosaicDefinitionTransaction mosaicDefinitionTransaction =
            MosaicDefinitionTransaction.create(
                new Deadline(2, HOURS),
                BigInteger.ZERO,
                nonce,
                this.mosaicId,
                MosaicProperties.create(true, false, 4, BigInteger.valueOf(100)),
                NetworkType.MIJIN_TEST);

        AggregateTransaction aggregateTransaction =
            AggregateTransaction.createComplete(
                new Deadline(2, HOURS),
                Collections.singletonList(
                    mosaicDefinitionTransaction.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            getTransactionRepository(type).announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneMosaicSupplyChangeTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        this.standaloneMosaicDefinitionTransaction(type);

        MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction =
            MosaicSupplyChangeTransaction.create(
                new Deadline(2, HOURS),
                this.mosaicId,
                MosaicSupplyType.INCREASE,
                BigInteger.valueOf(11),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction =
            this.account.sign(mosaicSupplyChangeTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            getTransactionRepository(type).announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateMosaicSupplyChangeTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        this.aggregateMosaicDefinitionTransaction(type);

        MosaicSupplyChangeTransaction mosaicSupplyChangeTransaction =
            MosaicSupplyChangeTransaction.create(
                new Deadline(2, HOURS),
                this.mosaicId,
                MosaicSupplyType.INCREASE,
                BigInteger.valueOf(12),
                NetworkType.MIJIN_TEST);

        AggregateTransaction aggregateTransaction =
            AggregateTransaction.createComplete(
                new Deadline(2, HOURS),
                Collections.singletonList(
                    mosaicSupplyChangeTransaction.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            getTransactionRepository(type).announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash(), type);
    }


    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneLockFundsTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        AggregateTransaction aggregateTransaction =
            AggregateTransaction.createBonded(
                new Deadline(2, HOURS), Collections.emptyList(), NetworkType.MIJIN_TEST);
        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);
        LockFundsTransaction lockFundstx =
            LockFundsTransaction.create(
                new Deadline(2, HOURS),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                signedTransaction,
                NetworkType.MIJIN_TEST);

        SignedTransaction lockFundsTransactionSigned = this.account
            .sign(lockFundstx, generationHash);
        getTransactionRepository(type).announce(lockFundsTransactionSigned).toFuture().get();

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), lockFundsTransactionSigned.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateLockFundsTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        AggregateTransaction aggregateTransaction =
            AggregateTransaction.createBonded(
                new Deadline(2, HOURS), Collections.emptyList(), NetworkType.MIJIN_TEST);
        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);
        LockFundsTransaction lockFundstx =
            LockFundsTransaction.create(
                new Deadline(2, HOURS),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                signedTransaction,
                NetworkType.MIJIN_TEST);

        AggregateTransaction lockFundsAggregatetx =
            AggregateTransaction.createComplete(
                new Deadline(2, HOURS),
                Collections.singletonList(lockFundstx.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction lockFundsTransactionSigned =
            this.account.sign(lockFundsAggregatetx, generationHash);

        getTransactionRepository(type).announce(lockFundsTransactionSigned).toFuture().get();

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), lockFundsTransactionSigned.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneSecretLockTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        byte[] secretBytes = new byte[20];
        new Random().nextBytes(secretBytes);
        byte[] result = Hashes.sha3_256(secretBytes);
        String secret = Hex.encodeHexString(result);
        SecretLockTransaction secretLocktx =
            SecretLockTransaction.create(
                new Deadline(2, HOURS),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                HashType.SHA3_256,
                secret,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"),
                NetworkType.MIJIN_TEST);

        SignedTransaction secretLockTransactionSigned = this.account
            .sign(secretLocktx, generationHash);

        getTransactionRepository(type).announce(secretLockTransactionSigned).toFuture().get();

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), secretLockTransactionSigned.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateSecretLockTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        byte[] secretBytes = new byte[20];
        new Random().nextBytes(secretBytes);
        byte[] result = Hashes.sha3_256(secretBytes);
        String secret = Hex.encodeHexString(result);
        SecretLockTransaction secretLocktx =
            SecretLockTransaction.create(
                new Deadline(2, HOURS),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                HashType.SHA3_256,
                secret,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"),
                NetworkType.MIJIN_TEST);

        AggregateTransaction secretLockAggregatetx =
            AggregateTransaction.createComplete(
                new Deadline(2, HOURS),
                Collections
                    .singletonList(secretLocktx.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction secretLockTransactionSigned =
            this.account.sign(secretLockAggregatetx, generationHash);

        getTransactionRepository(type).announce(secretLockTransactionSigned).toFuture().get();

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), secretLockTransactionSigned.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void standaloneSecretProofTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        byte[] secretBytes = new byte[20];
        new Random().nextBytes(secretBytes);
        byte[] result = Hashes.sha3_256(secretBytes);
        String secret = Hex.encodeHexString(result);
        String proof = Hex.encodeHexString(secretBytes);
        SecretLockTransaction secretLocktx =
            SecretLockTransaction.create(
                new Deadline(2, HOURS),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                HashType.SHA3_256,
                secret,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"),
                NetworkType.MIJIN_TEST);

        SignedTransaction lockFundsTransactionSigned = this.account
            .sign(secretLocktx, generationHash);

        getTransactionRepository(type).announce(lockFundsTransactionSigned).toFuture().get();

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), lockFundsTransactionSigned.getHash(), type);

        SecretProofTransaction secretProoftx =
            SecretProofTransaction.create(
                new Deadline(2, HOURS),
                BigInteger.ZERO,
                HashType.SHA3_256,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"),
                secret,
                proof,
                NetworkType.MIJIN_TEST);

        SignedTransaction secretProofTransactionSigned =
            this.account.sign(secretProoftx, generationHash);

        getTransactionRepository(type).announce(secretProofTransactionSigned).toFuture().get();

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), secretProofTransactionSigned.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void aggregateSecretProofTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        byte[] secretBytes = new byte[20];
        new Random().nextBytes(secretBytes);
        byte[] result = Hashes.sha3_256(secretBytes);
        String secret = Hex.encodeHexString(result);
        String proof = Hex.encodeHexString(secretBytes);
        SecretLockTransaction secretLocktx =
            SecretLockTransaction.create(
                new Deadline(2, HOURS),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                HashType.SHA3_256,
                secret,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"),
                NetworkType.MIJIN_TEST);

        SignedTransaction lockFundsTransactionSigned = this.account
            .sign(secretLocktx, generationHash);

        getTransactionRepository(type).announce(lockFundsTransactionSigned).toFuture().get();

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), lockFundsTransactionSigned.getHash(), type);

        SecretProofTransaction secretProoftx =
            SecretProofTransaction.create(
                new Deadline(2, HOURS),
                BigInteger.ZERO,
                HashType.SHA3_256,
                Address.createFromRawAddress("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"),
                secret,
                proof,
                NetworkType.MIJIN_TEST);

        AggregateTransaction secretProofAggregatetx =
            AggregateTransaction.createComplete(
                new Deadline(2, HOURS),
                Collections
                    .singletonList(secretProoftx.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction secretProofTransactionSigned =
            this.account.sign(secretProofAggregatetx, generationHash);

        getTransactionRepository(type).announce(secretProofTransactionSigned).toFuture().get();

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), secretProofTransactionSigned.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void shouldSignModifyMultisigAccountTransactionWithCosignatories(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        ModifyMultisigAccountTransaction modifyMultisigAccountTransaction =
            ModifyMultisigAccountTransaction.create(
                new Deadline(2, HOURS),
                (byte) 0,
                (byte) 0,
                Collections.singletonList(
                    new MultisigCosignatoryModification(
                        MultisigCosignatoryModificationType.ADD,
                        this.cosignatoryAccount2.getPublicAccount())),
                NetworkType.MIJIN_TEST);
        AggregateTransaction aggregateTransaction =
            AggregateTransaction.createComplete(
                new Deadline(2, HOURS),
                Collections.singletonList(
                    modifyMultisigAccountTransaction.toAggregate(
                        this.multisigAccount.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction =
            this.cosignatoryAccount.signTransactionWithCosignatories(
                aggregateTransaction,
                Collections.singletonList(this.cosignatoryAccount2),
                generationHash);

        LockFundsTransaction lockFundsTransaction =
            LockFundsTransaction.create(
                new Deadline(2, HOURS),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                signedTransaction,
                NetworkType.MIJIN_TEST);

        SignedTransaction lockFundsSignedTransaction =
            this.cosignatoryAccount.sign(lockFundsTransaction, generationHash);

        getTransactionRepository(type).announce(lockFundsSignedTransaction).toFuture().get();

        getListener(type).confirmed(this.cosignatoryAccount.getAddress()).take(1).toFuture().get();

        getTransactionRepository(type).announceAggregateBonded(signedTransaction).toFuture().get();

        this.validateAggregateBondedTransactionAnnounceCorrectly(
            this.cosignatoryAccount.getAddress(), signedTransaction.getHash(), type);
    }

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    void cosignatureTransaction(RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        TransferTransaction transferTransaction =
            TransferTransaction.create(
                new Deadline(2, HOURS),
                BigInteger.ZERO,
                new Address("SDRDGFTDLLCB67D4HPGIMIHPNSRYRJRT7DOBGWZY", NetworkType.MIJIN_TEST),
                Collections
                    .singletonList(NetworkCurrencyMosaic.createAbsolute(BigInteger.valueOf(1))),
                PlainMessage.create("test-message"),
                NetworkType.MIJIN_TEST);

        AggregateTransaction aggregateTransaction =
            AggregateTransaction.createComplete(
                new Deadline(2, HOURS),
                Collections.singletonList(
                    transferTransaction.toAggregate(this.multisigAccount.getPublicAccount())),
                NetworkType.MIJIN_TEST);
        SignedTransaction signedTransaction =
            this.cosignatoryAccount.sign(aggregateTransaction, generationHash);

        LockFundsTransaction lockFundsTransaction =
            LockFundsTransaction.create(
                new Deadline(2, HOURS),
                NetworkCurrencyMosaic.createRelative(BigInteger.valueOf(10)),
                BigInteger.valueOf(100),
                signedTransaction,
                NetworkType.MIJIN_TEST);

        SignedTransaction lockFundsSignedTransaction =
            this.cosignatoryAccount.sign(lockFundsTransaction, generationHash);

        getTransactionRepository(type).announce(lockFundsSignedTransaction).toFuture().get();

        getListener(type).confirmed(this.cosignatoryAccount.getAddress()).take(1).toFuture().get();

        getTransactionRepository(type).announceAggregateBonded(signedTransaction).toFuture().get();

        this.validateAggregateBondedTransactionAnnounceCorrectly(
            this.cosignatoryAccount.getAddress(), signedTransaction.getHash(), type);

        CosignatureTransaction cosignatureTransaction =
            CosignatureTransaction.create(aggregateTransaction);

        CosignatureSignedTransaction cosignatureSignedTransaction =
            this.cosignatoryAccount2.signCosignatureTransaction(cosignatureTransaction);

        getTransactionRepository(type)
            .announceAggregateBondedCosignature(cosignatureSignedTransaction)
            .toFuture()
            .get();

        this.validateAggregateBondedCosignatureTransactionAnnounceCorrectly(
            this.cosignatoryAccount.getAddress(), cosignatureSignedTransaction.getParentHash(),
            type);
    }

    void validateTransactionAnnounceCorrectly(Address address, String transactionHash,
        RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        Transaction transaction = getListener(type).confirmed(address).take(1).toFuture()
            .get(this.timeoutSeconds, TimeUnit.SECONDS);

        assertEquals(transactionHash, transaction.getTransactionInfo().get().getHash().get());
    }

    void validateAggregateBondedTransactionAnnounceCorrectly(Address address,
        String transactionHash, RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        AggregateTransaction aggregateTransaction =
            getListener(type).aggregateBondedAdded(address).take(1).toFuture()
                .get(this.timeoutSeconds, TimeUnit.SECONDS);
        assertEquals(transactionHash,
            aggregateTransaction.getTransactionInfo().get().getHash().get());
    }

    void validateAggregateBondedCosignatureTransactionAnnounceCorrectly(
        Address address, String transactionHash,
        RepositoryType type)
        throws ExecutionException, InterruptedException, TimeoutException {
        String hash = getListener(type).cosignatureAdded(address).take(1).toFuture()
            .get(this.timeoutSeconds, TimeUnit.SECONDS).getParentHash();
        assertEquals(transactionHash, hash);
    }
}
