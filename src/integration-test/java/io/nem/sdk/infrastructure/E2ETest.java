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

import io.nem.core.crypto.Hashes;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.mosaic.MosaicProperties;
import io.nem.sdk.model.mosaic.MosaicSupplyType;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.CosignatureTransaction;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.HashType;
import io.nem.sdk.model.transaction.LockFundsTransaction;
import io.nem.sdk.model.transaction.ModifyMultisigAccountTransaction;
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
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class E2ETest extends BaseTest {

    private TransactionHttp transactionHttp;
    private Account account;
    private Address recipient;
    private Account multisigAccount;
    private Account cosignatoryAccount;
    private Account cosignatoryAccount2;
    private NamespaceId rootNamespaceId;
    private MosaicId mosaicId;
    private Listener listener;
    private String generationHash;
    private long timeoutSeconds;

    @BeforeAll
    void setup() throws ExecutionException, InterruptedException, IOException {
        transactionHttp = new TransactionHttp(this.getApiUrl());
        account = this.getTestAccount();
        recipient = this.getRecipient();
        multisigAccount = this.getTestMultisigAccount();
        cosignatoryAccount = this.getTestCosignatoryAccount();
        cosignatoryAccount2 = this.getTestCosignatoryAccount2();
        generationHash = this.getGenerationHash();
        timeoutSeconds = this.getTimeoutSeconds().longValue();
        listener = new Listener(this.getApiUrl());
        listener.open().get();
    }

    @Test
    void standaloneTransferTransaction()
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
            transactionHttp.announce(signedTransaction).toFuture().get();
        assertEquals(
            "packet 9 was pushed to the network via /transaction",
            transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash());
    }

    @Test
    void aggregateTransferTransaction()
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
                BigInteger.ZERO,
                Collections.singletonList(
                    transferTransaction.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            transactionHttp.announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash());
    }

    @Test
    void standaloneRootRegisterNamespaceTransaction()
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
            transactionHttp.announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash());
    }

    @Test
    void aggregateRootRegisterNamespaceTransaction()
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
                BigInteger.ZERO,
                Collections.singletonList(
                    registerNamespaceTransaction.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            transactionHttp.announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash());
    }

    @Test
    void standaloneSubNamespaceRegisterNamespaceTransaction()
        throws ExecutionException, InterruptedException, TimeoutException {

        this.standaloneRootRegisterNamespaceTransaction();

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
            transactionHttp.announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash());
    }

    @Test
    void aggregateSubNamespaceRegisterNamespaceTransaction()
        throws ExecutionException, InterruptedException, TimeoutException {

        this.aggregateRootRegisterNamespaceTransaction();

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
                BigInteger.ZERO,
                Collections.singletonList(
                    registerNamespaceTransaction.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            transactionHttp.announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash());
    }

    @Test
    void standaloneMosaicDefinitionTransaction()
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
            transactionHttp.announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash());
    }

    @Test
    void aggregateMosaicDefinitionTransaction()
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
                BigInteger.ZERO,
                Collections.singletonList(
                    mosaicDefinitionTransaction.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            transactionHttp.announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash());
    }

    @Test
    void standaloneMosaicSupplyChangeTransaction()
        throws ExecutionException, InterruptedException, TimeoutException {
        this.standaloneMosaicDefinitionTransaction();

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
            transactionHttp.announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash());
    }

    @Test
    void aggregateMosaicSupplyChangeTransaction()
        throws ExecutionException, InterruptedException, TimeoutException {
        this.aggregateMosaicDefinitionTransaction();

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
                BigInteger.ZERO,
                Collections.singletonList(
                    mosaicSupplyChangeTransaction.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction signedTransaction = this.account
            .sign(aggregateTransaction, generationHash);

        TransactionAnnounceResponse transactionAnnounceResponse =
            transactionHttp.announce(signedTransaction).toFuture().get();
        System.out.println(transactionAnnounceResponse.getMessage());

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), signedTransaction.getHash());
    }


    @Test
    void standaloneLockFundsTransaction()
        throws ExecutionException, InterruptedException, TimeoutException {
        AggregateTransaction aggregateTransaction =
            AggregateTransaction.createBonded(
                new Deadline(2, HOURS), BigInteger.ZERO, Collections.emptyList(),
                NetworkType.MIJIN_TEST);
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
        transactionHttp.announce(lockFundsTransactionSigned).toFuture().get();

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), lockFundsTransactionSigned.getHash());
    }

    @Test
    void aggregateLockFundsTransaction()
        throws ExecutionException, InterruptedException, TimeoutException {
        AggregateTransaction aggregateTransaction =
            AggregateTransaction.createBonded(
                new Deadline(2, HOURS), BigInteger.ZERO, Collections.emptyList(),
                NetworkType.MIJIN_TEST);
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
                BigInteger.ZERO,
                Collections.singletonList(lockFundstx.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction lockFundsTransactionSigned =
            this.account.sign(lockFundsAggregatetx, generationHash);

        transactionHttp.announce(lockFundsTransactionSigned).toFuture().get();

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), lockFundsTransactionSigned.getHash());
    }

    @Test
    void standaloneSecretLockTransaction()
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

        transactionHttp.announce(secretLockTransactionSigned).toFuture().get();

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), secretLockTransactionSigned.getHash());
    }

    @Test
    void aggregateSecretLockTransaction()
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
                BigInteger.ZERO,
                Collections
                    .singletonList(secretLocktx.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction secretLockTransactionSigned =
            this.account.sign(secretLockAggregatetx, generationHash);

        transactionHttp.announce(secretLockTransactionSigned).toFuture().get();

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), secretLockTransactionSigned.getHash());
    }

    @Test
    void standaloneSecretProofTransaction()
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

        transactionHttp.announce(lockFundsTransactionSigned).toFuture().get();

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), lockFundsTransactionSigned.getHash());

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

        transactionHttp.announce(secretProofTransactionSigned).toFuture().get();

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), secretProofTransactionSigned.getHash());
    }

    @Test
    void aggregateSecretProofTransaction()
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

        transactionHttp.announce(lockFundsTransactionSigned).toFuture().get();

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), lockFundsTransactionSigned.getHash());

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
                BigInteger.ZERO,
                Collections
                    .singletonList(secretProoftx.toAggregate(this.account.getPublicAccount())),
                NetworkType.MIJIN_TEST);

        SignedTransaction secretProofTransactionSigned =
            this.account.sign(secretProofAggregatetx, generationHash);

        transactionHttp.announce(secretProofTransactionSigned).toFuture().get();

        this.validateTransactionAnnounceCorrectly(
            this.account.getAddress(), secretProofTransactionSigned.getHash());
    }

    @Test
    void shouldSignModifyMultisigAccountTransactionWithCosignatories()
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
                BigInteger.ZERO,
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

        transactionHttp.announce(lockFundsSignedTransaction).toFuture().get();

        listener.confirmed(this.cosignatoryAccount.getAddress()).take(1).toFuture().get();

        transactionHttp.announceAggregateBonded(signedTransaction).toFuture().get();

        this.validateAggregateBondedTransactionAnnounceCorrectly(
            this.cosignatoryAccount.getAddress(), signedTransaction.getHash());
    }

    @Test
    void cosignatureTransaction()
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
                BigInteger.ZERO,
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

        transactionHttp.announce(lockFundsSignedTransaction).toFuture().get();

        listener.confirmed(this.cosignatoryAccount.getAddress()).take(1).toFuture().get();

        transactionHttp.announceAggregateBonded(signedTransaction).toFuture().get();

        this.validateAggregateBondedTransactionAnnounceCorrectly(
            this.cosignatoryAccount.getAddress(), signedTransaction.getHash());

        CosignatureTransaction cosignatureTransaction =
            CosignatureTransaction.create(aggregateTransaction);

        CosignatureSignedTransaction cosignatureSignedTransaction =
            this.cosignatoryAccount2.signCosignatureTransaction(cosignatureTransaction);

        transactionHttp
            .announceAggregateBondedCosignature(cosignatureSignedTransaction)
            .toFuture()
            .get();

        this.validateAggregateBondedCosignatureTransactionAnnounceCorrectly(
            this.cosignatoryAccount.getAddress(), cosignatureSignedTransaction.getParentHash());
    }

    void validateTransactionAnnounceCorrectly(Address address, String transactionHash)
        throws ExecutionException, InterruptedException, TimeoutException {
        Transaction transaction = listener.confirmed(address).take(1).toFuture()
            .get(this.timeoutSeconds, TimeUnit.SECONDS);

        assertEquals(transactionHash, transaction.getTransactionInfo().get().getHash().get());
    }

    void validateAggregateBondedTransactionAnnounceCorrectly(Address address,
        String transactionHash)
        throws ExecutionException, InterruptedException, TimeoutException {
        AggregateTransaction aggregateTransaction =
            listener.aggregateBondedAdded(address).take(1).toFuture()
                .get(this.timeoutSeconds, TimeUnit.SECONDS);
        assertEquals(transactionHash,
            aggregateTransaction.getTransactionInfo().get().getHash().get());
    }

    void validateAggregateBondedCosignatureTransactionAnnounceCorrectly(
        Address address, String transactionHash)
        throws ExecutionException, InterruptedException, TimeoutException {
        String hash = listener.cosignatureAdded(address).take(1).toFuture()
            .get(this.timeoutSeconds, TimeUnit.SECONDS).getParentHash();
        assertEquals(transactionHash, hash);
    }
}
