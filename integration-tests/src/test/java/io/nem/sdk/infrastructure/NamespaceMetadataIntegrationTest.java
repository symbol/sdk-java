/*
 * Copyright 2019 NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.NetworkCurrencyMosaic;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.sdk.model.transaction.NamespaceMetadataTransaction;
import io.nem.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.sdk.model.transaction.NamespaceRegistrationTransactionFactory;
import io.nem.sdk.model.transaction.SignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionAnnounceResponse;
import io.nem.sdk.model.transaction.TransactionType;
import java.math.BigInteger;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Integration tests around account metadata.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NamespaceMetadataIntegrationTest extends BaseIntegrationTest {

    //TODO use test account, not nemesis (getting Failure_Core_Insufficient_Balance errors when creating Namespace)
    private Account testAccount = config().getNemesisAccount();

    @ParameterizedTest
    @EnumSource(RepositoryType.class)
    public void addMetadataToNamespace(RepositoryType type) {

        NamespaceId targetNamespaceId = createNamespace(type);

        System.out.println("Setting metadata to namespace " + targetNamespaceId.getId());

        String message = "This is the message in the Namespace!";
        NamespaceMetadataTransaction transaction =
            new NamespaceMetadataTransactionFactory(
                getNetworkType(), testAccount.getPublicAccount(), targetNamespaceId,
                BigInteger.TEN, message
            ).build();

        AggregateTransaction aggregateTransaction = AggregateTransactionFactory
            .createComplete(getNetworkType(),
                Collections.singletonList(transaction.toAggregate(testAccount.getPublicAccount())))
            .build();

        SignedTransaction signedTransaction = testAccount
            .sign(aggregateTransaction, getGenerationHash());

        TransactionAnnounceResponse transactionAnnounceResponse =
            get(getRepositoryFactory(type).createTransactionRepository()
                .announce(signedTransaction));
        assertEquals(
            "packet 9 was pushed to the network via /transaction",
            transactionAnnounceResponse.getMessage());

        AggregateTransaction announceCorrectly = (AggregateTransaction) this
            .validateTransactionAnnounceCorrectly(
                testAccount.getAddress(), signedTransaction.getHash(), type);

        Assertions.assertEquals(aggregateTransaction.getType(), announceCorrectly.getType());
        Assertions
            .assertEquals(testAccount.getPublicAccount(), announceCorrectly.getSigner().get());
        Assertions.assertEquals(1, announceCorrectly.getInnerTransactions().size());
        Assertions
            .assertEquals(transaction.getType(),
                announceCorrectly.getInnerTransactions().get(0).getType());
        NamespaceMetadataTransaction processedTransaction = (NamespaceMetadataTransaction) announceCorrectly
            .getInnerTransactions()
            .get(0);

//        Assertions.assertEquals(transaction.getTargetNamespaceId(),
//            processedTransaction.getTargetNamespaceId());
        Assertions.assertEquals(transaction.getValueSizeDelta(),
            processedTransaction.getValueSizeDelta());
        Assertions.assertEquals(transaction.getValueSize(), processedTransaction.getValueSize());

        System.out.println("Metadata '" + message + "' stored!");
        Assertions.assertEquals(message, processedTransaction.getValue());
    }

    public NamespaceId createNamespace(RepositoryType type) {

        String namespaceName =
            "namespace-for-metadata-integration-test-" + new Double(
                Math.floor(Math.random() * 10000))
                .intValue();

        System.out.println("Creating namespace " + namespaceName);
        NamespaceRegistrationTransaction namespaceRegistrationTransaction = NamespaceRegistrationTransactionFactory
            .createRootNamespace(
                getNetworkType(), namespaceName, BigInteger.valueOf(10)).build();

        SignedTransaction signedTransaction =
            testAccount.sign(namespaceRegistrationTransaction, getGenerationHash());

        TransactionAnnounceResponse transactionAnnounceResponse =
            get(getRepositoryFactory(type).createTransactionRepository()
                .announce(signedTransaction));

        assertEquals(
            "packet 9 was pushed to the network via /transaction",
            transactionAnnounceResponse.getMessage());

        Transaction transaction = this.validateTransactionAnnounceCorrectly(
            testAccount.getAddress(), signedTransaction.getHash(), type);
        Assertions.assertEquals(TransactionType.REGISTER_NAMESPACE, transaction.getType());
        System.out.println("Namespace created");
        return namespaceRegistrationTransaction.getNamespaceId();
    }
}
