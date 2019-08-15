/*
 *  Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure.vertx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.namespace.NamespaceType;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.LockFundsTransaction;
import io.nem.sdk.model.transaction.ModifyMultisigAccountTransaction;
import io.nem.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.sdk.model.transaction.RegisterNamespaceTransaction;
import io.nem.sdk.model.transaction.SecretLockTransaction;
import io.nem.sdk.model.transaction.SecretProofTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.UInt64;
import io.nem.sdk.openapi.vertx.model.AggregateTransactionBodyDTO;
import io.nem.sdk.openapi.vertx.model.HashLockTransactionDTO;
import io.nem.sdk.openapi.vertx.model.ModifyMultisigAccountTransactionDTO;
import io.nem.sdk.openapi.vertx.model.Mosaic;
import io.nem.sdk.openapi.vertx.model.MosaicDefinitionTransactionDTO;
import io.nem.sdk.openapi.vertx.model.MosaicSupplyChangeTransactionDTO;
import io.nem.sdk.openapi.vertx.model.RegisterNamespaceTransactionDTO;
import io.nem.sdk.openapi.vertx.model.SecretLockTransactionDTO;
import io.nem.sdk.openapi.vertx.model.SecretProofTransactionDTO;
import io.nem.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.nem.sdk.openapi.vertx.model.TransferTransactionDTO;
import io.vertx.core.json.Json;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

public class VertxTransactionMappingTest {


    private final JsonHelper jsonHelper = new JsonHelperJackson2(
        JsonHelperJackson2.configureMapper(Json.mapper));


    @Test
    void shouldCreateStandaloneTransferTransaction() {
        TransactionInfoDTO transferTransactionDTO = createJsonObject(
            "shouldCreateStandaloneTransferTransaction.json");

        Transaction transferTransaction = map(transferTransactionDTO);

        validateStandaloneTransaction(transferTransaction, transferTransactionDTO);
    }

    @Test
    void shouldCreateAggregateTransferTransaction() {
        TransactionInfoDTO aggregateTransferTransactionDTO = createJsonObject(
            "shouldCreateAggregateTransferTransaction.json"
        );

        Transaction aggregateTransferTransaction =
            map(aggregateTransferTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateTransferTransaction, aggregateTransferTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneRootNamespaceCreationTransaction() {
        TransactionInfoDTO namespaceCreationTransactionDTO =
            createJsonObject("shouldCreateStandaloneRootNamespaceCreationTransaction.json"
            );

        Transaction namespaceCreationTransaction =
            map(namespaceCreationTransactionDTO);

        validateStandaloneTransaction(namespaceCreationTransaction,
            namespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateRootNamespaceCreationTransaction() {
        TransactionInfoDTO aggregateNamespaceCreationTransactionDTO =
            createJsonObject("shouldCreateAggregateRootNamespaceCreationTransaction.json"
            );

        Transaction aggregateNamespaceCreationTransaction =
            map(aggregateNamespaceCreationTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateNamespaceCreationTransaction,
            aggregateNamespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneSubNamespaceCreationTransaction() {
        TransactionInfoDTO namespaceCreationTransactionDTO =
            createJsonObject("shouldCreateStandaloneSubNamespaceCreationTransaction.json"
            );

        Transaction namespaceCreationTransaction =
            map(namespaceCreationTransactionDTO);

        validateStandaloneTransaction(namespaceCreationTransaction,
            namespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateSubNamespaceCreationTransaction() {
        TransactionInfoDTO aggregateNamespaceCreationTransactionDTO =
            createJsonObject("shouldCreateAggregateSubNamespaceCreationTransaction.json"
            );

        Transaction aggregateNamespaceCreationTransaction =
            map(aggregateNamespaceCreationTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateNamespaceCreationTransaction,
            aggregateNamespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneMosaicCreationTransaction() {
        TransactionInfoDTO mosaicCreationTransactionDTO = createJsonObject(
            "shouldCreateStandaloneMosaicCreationTransaction.json");

        Transaction mosaicCreationTransaction = map(mosaicCreationTransactionDTO);

        validateStandaloneTransaction(mosaicCreationTransaction, mosaicCreationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMosaicCreationTransaction() {
        TransactionInfoDTO aggregateMosaicCreationTransactionDTO =
            createJsonObject("shouldCreateAggregateMosaicCreationTransaction.json"
            );

        Transaction aggregateMosaicCreationTransaction =
            map(aggregateMosaicCreationTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateMosaicCreationTransaction,
            aggregateMosaicCreationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneMosaicSupplyChangeTransaction() {
        TransactionInfoDTO mosaicSupplyChangeTransactionDTO =
            createJsonObject("shouldCreateStandaloneMosaicSupplyChangeTransaction.json"
            );

        Transaction mosaicSupplyChangeTransaction =
            map(mosaicSupplyChangeTransactionDTO);

        validateStandaloneTransaction(mosaicSupplyChangeTransaction,
            mosaicSupplyChangeTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMosaicSupplyChangeTransaction() {
        TransactionInfoDTO aggregateMosaicSupplyChangeTransactionDTO =
            createJsonObject("shouldCreateAggregateMosaicSupplyChangeTransaction.json"
            );

        Transaction aggregateMosaicSupplyChangeTransaction =
            map(aggregateMosaicSupplyChangeTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateMosaicSupplyChangeTransaction,
            aggregateMosaicSupplyChangeTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneMultisigModificationTransaction() {
        TransactionInfoDTO multisigModificationTransactionDTO =
            createJsonObject("shouldCreateStandaloneMultisigModificationTransaction.json"
            );

        Transaction multisigModificationTransaction =
            map(multisigModificationTransactionDTO);

        validateStandaloneTransaction(
            multisigModificationTransaction, multisigModificationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMultisigModificationTransaction() {
        TransactionInfoDTO aggregateMultisigModificationTransactionDTO =
            createJsonObject("shouldCreateAggregateMultisigModificationTransaction.json"
            );

        Transaction aggregateMultisigModificationTransaction =
            map(aggregateMultisigModificationTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateMultisigModificationTransaction,
            aggregateMultisigModificationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneLockFundsTransaction() {
        TransactionInfoDTO lockFundsTransactionDTO =
            createJsonObject("shouldCreateStandaloneLockFundsTransaction.json");

        Transaction lockFundsTransaction = map(lockFundsTransactionDTO);

        validateStandaloneTransaction(lockFundsTransaction, lockFundsTransactionDTO);
    }

    @Test
    void shouldCreateAggregateLockFundsTransaction() {
        TransactionInfoDTO aggregateLockFundsTransactionDTO =
            createJsonObject("shouldCreateAggregateLockFundsTransaction.json"
            );

        Transaction lockFundsTransaction =
            map(aggregateLockFundsTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) lockFundsTransaction, aggregateLockFundsTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneSecretLockTransaction() {
        TransactionInfoDTO secretLockTransactionDTO =
            createJsonObject("shouldCreateStandaloneSecretLockTransaction.json"
            );

        Transaction secretLockTransaction = map(secretLockTransactionDTO);

        validateStandaloneTransaction(secretLockTransaction, secretLockTransactionDTO);
    }

    @Test
    void shouldCreateAggregateSecretLockTransaction() {
        TransactionInfoDTO aggregateSecretLockTransactionDTO =
            createJsonObject("shouldCreateAggregateSecretLockTransaction.json");

        Transaction aggregateSecretLockTransaction = map(aggregateSecretLockTransactionDTO);

        validateAggregateTransaction((AggregateTransaction) aggregateSecretLockTransaction,
            aggregateSecretLockTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneSecretProofTransaction() {
        TransactionInfoDTO secretProofTransactionDTO =
            createJsonObject("shouldCreateStandaloneSecretProofTransaction.json");

        Transaction secretProofTransaction = map(secretProofTransactionDTO);
        validateStandaloneTransaction(secretProofTransaction, secretProofTransactionDTO);
    }

    @Test
    void shouldCreateAggregateSecretProofTransaction() {
        TransactionInfoDTO aggregateSecretProofTransactionDTO =
            createJsonObject("shouldCreateAggregateSecretProofTransaction.json");

        Transaction aggregateSecretProofTransaction =
            map(aggregateSecretProofTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateSecretProofTransaction,
            aggregateSecretProofTransactionDTO);
    }

    private TransactionInfoDTO createJsonObject(String name) {

        String resourceName = "TransactionMapping-" + name;

        try (InputStream resourceAsStream = getClass().getClassLoader()
            .getResourceAsStream("json/" + resourceName)) {
            return jsonHelper.parse(IOUtils.toString(resourceAsStream), TransactionInfoDTO.class);
        } catch (Exception e) {
            throw new IllegalStateException(
                "Cannot open resource " + resourceName + ". Error: " + ExceptionUtils.getMessage(e),
                e);
        }
    }

    private Transaction map(TransactionInfoDTO jsonObject) {
        return new TransactionMappingVertx(jsonHelper).apply(jsonObject);
    }

    void validateStandaloneTransaction(Transaction transaction, TransactionInfoDTO transactionDTO) {
        validateStandaloneTransaction(transaction, transactionDTO, transactionDTO);
    }

    void validateStandaloneTransaction(Transaction transaction, TransactionInfoDTO transactionDTO,
        TransactionInfoDTO parentTransaction) {
        assertEquals(
            extractBigInteger(transactionDTO.getMeta().getHeight()),
            transaction.getTransactionInfo().get().getHeight());
        if (transaction.getTransactionInfo().get().getHash().isPresent()) {
            assertEquals(
                transactionDTO.getMeta().getHash(),
                transaction.getTransactionInfo().get().getHash().get());
        }
        if (transaction.getTransactionInfo().get().getMerkleComponentHash().isPresent()) {
            assertEquals(
                transactionDTO.getMeta().getMerkleComponentHash(),
                transaction.getTransactionInfo().get().getMerkleComponentHash().get());
        }
        if (transaction.getTransactionInfo().get().getIndex().isPresent()) {
            assertEquals(
                transaction.getTransactionInfo().get().getIndex().get(),
                transactionDTO.getMeta().getIndex());
        }
        if (transaction.getTransactionInfo().get().getId().isPresent()) {
            assertEquals(
                transactionDTO.getMeta().getId(),
                transaction.getTransactionInfo().get().getId().get());
        }
        if (transaction.getTransactionInfo().get().getAggregateHash().isPresent()) {
            assertEquals(
                transactionDTO.getMeta().getAggregateHash(),
                transaction.getTransactionInfo().get().getAggregateHash().get());
        }
        if (transaction.getTransactionInfo().get().getAggregateId().isPresent()) {
            assertEquals(
                transactionDTO.getMeta().getAggregateId(),
                transaction.getTransactionInfo().get().getAggregateId().get());
        }

        assertEquals(
            jsonHelper.getString(parentTransaction.getTransaction(), "signature"),
            transaction.getSignature().get());
        assertEquals(
            jsonHelper.getString(transactionDTO.getTransaction(), "signer"),
            transaction.getSigner().get().getPublicKey().toString());
        assertEquals(transaction.getType().getValue(),
            (int) jsonHelper.getInteger(transactionDTO.getTransaction(), "type"));
        int version =
            (int)
                Long.parseLong(
                    Integer.toHexString(
                        jsonHelper.getInteger(transactionDTO.getTransaction(), "version"))
                        .substring(2, 4),
                    16);
        assertTrue(transaction.getVersion() == version);
        int networkType =
            (int)
                Long.parseLong(
                    Integer.toHexString(
                        jsonHelper.getInteger(transactionDTO.getTransaction(), "version"))
                        .substring(0, 2),
                    16);
        assertTrue(transaction.getNetworkType().getValue() == networkType);
        assertEquals(
            extractBigInteger(
                jsonHelper.getLongList(parentTransaction.getTransaction(), "max_fee")),
            transaction.getFee());
        assertNotNull(transaction.getDeadline());

        if (transaction.getType() == TransactionType.TRANSFER) {
            validateTransferTx((TransferTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.REGISTER_NAMESPACE) {
            validateNamespaceCreationTx((RegisterNamespaceTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.MOSAIC_DEFINITION) {
            validateMosaicCreationTx((MosaicDefinitionTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.MOSAIC_SUPPLY_CHANGE) {
            validateMosaicSupplyChangeTx((MosaicSupplyChangeTransaction) transaction,
                transactionDTO);
        } else if (transaction.getType() == TransactionType.MODIFY_MULTISIG_ACCOUNT) {
            validateMultisigModificationTx((ModifyMultisigAccountTransaction) transaction,
                transactionDTO);
        } else if (transaction.getType() == TransactionType.LOCK) {
            validateLockFundsTx((LockFundsTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.SECRET_LOCK) {
            validateSecretLockTx((SecretLockTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.SECRET_PROOF) {
            validateSecretProofTx((SecretProofTransaction) transaction, transactionDTO);
        }
    }

    void validateAggregateTransaction(
        AggregateTransaction aggregateTransaction, TransactionInfoDTO transactionDto) {

        AggregateTransactionBodyDTO aggregateTransactionBodyDTO = jsonHelper
            .convert(transactionDto.getTransaction(), AggregateTransactionBodyDTO.class);
        assertEquals(
            extractBigInteger(transactionDto.getMeta().getHeight()),
            aggregateTransaction.getTransactionInfo().get().getHeight());
        if (aggregateTransaction.getTransactionInfo().get().getHash().isPresent()) {
            assertEquals(
                transactionDto.getMeta().getHash(),
                aggregateTransaction.getTransactionInfo().get().getHash().get());
        }
        if (aggregateTransaction.getTransactionInfo().get().getMerkleComponentHash().isPresent()) {
            assertEquals(
                transactionDto.getMeta().getMerkleComponentHash(),
                aggregateTransaction.getTransactionInfo().get().getMerkleComponentHash().get());
        }
        if (aggregateTransaction.getTransactionInfo().get().getIndex().isPresent()) {
            assertEquals(
                aggregateTransaction.getTransactionInfo().get().getIndex().get(),
                transactionDto.getMeta().getIndex());
        }
        if (aggregateTransaction.getTransactionInfo().get().getId().isPresent()) {
            assertEquals(
                transactionDto.getMeta().getId(),
                aggregateTransaction.getTransactionInfo().get().getId().get());
        }

        assertEquals(
            jsonHelper.getString(transactionDto.getTransaction(), "signature"),
            aggregateTransaction.getSignature().get());
        assertEquals(
            jsonHelper.getString(transactionDto.getTransaction(), "signer"),
            aggregateTransaction.getSigner().get().getPublicKey().toString());
        int version =
            (int)
                Long.parseLong(
                    Integer.toHexString(
                        jsonHelper.getInteger(transactionDto.getTransaction(), "version"))
                        .substring(2, 4),
                    16);
        assertEquals((int) aggregateTransaction.getVersion(), version);
        int networkType =
            (int)
                Long.parseLong(
                    Integer.toHexString(
                        jsonHelper.getInteger(transactionDto.getTransaction(), "version"))
                        .substring(0, 2),
                    16);
        assertEquals(aggregateTransaction.getNetworkType().getValue(), networkType);
        assertEquals(aggregateTransaction.getType().getValue(),
            (int) jsonHelper.getInteger(transactionDto.getTransaction(), "type"));
        assertEquals(
            extractBigInteger(jsonHelper.getLongList(transactionDto.getTransaction(), "max_fee")),
            aggregateTransaction.getFee());
        assertNotNull(aggregateTransaction.getDeadline());

        assertEquals(
            aggregateTransactionBodyDTO.getCosignatures().get(0).getSignature(),
            aggregateTransaction.getCosignatures().get(0).getSignature());
        assertEquals(
            aggregateTransactionBodyDTO.getCosignatures().get(0).getSigner(),
            aggregateTransaction.getCosignatures().get(0).getSigner().getPublicKey().toString());

        Transaction innerTransaction = aggregateTransaction.getInnerTransactions().get(0);
        validateStandaloneTransaction(
            innerTransaction,
            jsonHelper.convert(aggregateTransactionBodyDTO.getTransactions().get(0),
                TransactionInfoDTO.class), transactionDto);
    }

    void validateTransferTx(TransferTransaction transaction, TransactionInfoDTO transactionDTO) {
        TransferTransactionDTO transferTransaction = jsonHelper
            .convert(transactionDTO.getTransaction(), TransferTransactionDTO.class);

        assertEquals(
            Address.createFromEncoded(
                transferTransaction.getRecipient()),
            transaction.getRecipient().get());

        List<Mosaic> mosaicsDTO = transferTransaction.getMosaics();
        if (mosaicsDTO != null && mosaicsDTO.size() > 0) {
            assertEquals(
                extractBigInteger(mosaicsDTO.get(0).getId()),
                transaction.getMosaics().get(0).getId().getId());
            assertEquals(
                extractBigInteger(mosaicsDTO.get(0).getAmount()),
                transaction.getMosaics().get(0).getAmount());
        }

        assertEquals(
            new String(
                Hex.decode(
                    transferTransaction.getMessage().getPayload()),
                StandardCharsets.UTF_8),
            transaction.getMessage().getPayload());

        assertEquals((int) transferTransaction.getMessage().getType().getValue(),
            transaction.getMessage().getType());
    }

    void validateNamespaceCreationTx(
        RegisterNamespaceTransaction transaction, TransactionInfoDTO transactionDTO) {

        RegisterNamespaceTransactionDTO registerNamespaceTransaction = jsonHelper
            .convert(transactionDTO.getTransaction(), RegisterNamespaceTransactionDTO.class);

        assertEquals((int) registerNamespaceTransaction.getNamespaceType().getValue(),
            transaction.getNamespaceType().getValue());
        assertEquals(
            registerNamespaceTransaction.getName(),
            transaction.getNamespaceName());
        assertEquals(
            extractBigInteger(registerNamespaceTransaction.getNamespaceId()),
            transaction.getNamespaceId().getId());

        if (transaction.getNamespaceType() == NamespaceType.RootNamespace) {
            assertEquals(
                extractBigInteger(
                    registerNamespaceTransaction.getDuration()),
                transaction.getDuration().get());
        } else {
            assertEquals(
                extractBigInteger(
                    registerNamespaceTransaction.getParentId()),
                transaction.getParentId().get().getId());
        }
    }

    void validateMosaicCreationTx(
        MosaicDefinitionTransaction transaction, TransactionInfoDTO transactionDTO) {
        // assertEquals(extractBigInteger(transactionDTO.getJsonObject("transaction").getJsonArray("parentId")),
        //        transaction.getNamespaceId().getId());
        MosaicDefinitionTransactionDTO mosaicDefinitionTransactionDTO = jsonHelper
            .convert(transactionDTO.getTransaction(), MosaicDefinitionTransactionDTO.class);
        assertEquals(
            extractBigInteger(mosaicDefinitionTransactionDTO.getMosaicId()),
            transaction.getMosaicId().getId());
        // assertEquals(transactionDTO.getJsonObject("transaction").getString("name"),
        //        transaction.getMosaicName());
        assertEquals(transaction.getMosaicProperties().getDivisibility(),
            (long) mosaicDefinitionTransactionDTO.getProperties().get(1).getValue().get(0));
        assertEquals(
            extractBigInteger(
                mosaicDefinitionTransactionDTO.getProperties().get(2).getValue()),
            transaction.getMosaicProperties().getDuration().get());
        assertTrue(transaction.getMosaicProperties().isSupplyMutable());
        assertTrue(transaction.getMosaicProperties().isTransferable());
    }

    void validateMosaicSupplyChangeTx(
        MosaicSupplyChangeTransaction transaction, TransactionInfoDTO transactionDTO) {
        MosaicSupplyChangeTransactionDTO mosaicSupplyChangeTransaction = jsonHelper
            .convert(transactionDTO.getTransaction(), MosaicSupplyChangeTransactionDTO.class);
        assertEquals(
            extractBigInteger(mosaicSupplyChangeTransaction.getMosaicId()),
            transaction.getMosaicId().getId());
        assertEquals(
            extractBigInteger(mosaicSupplyChangeTransaction.getDelta()),
            transaction.getDelta());
        assertEquals(transaction.getMosaicSupplyType().getValue(),
            (int) mosaicSupplyChangeTransaction.getDirection().getValue());
    }

    void validateMultisigModificationTx(
        ModifyMultisigAccountTransaction transaction, TransactionInfoDTO transactionDTO) {

        ModifyMultisigAccountTransactionDTO modifyMultisigAccountTransaction = jsonHelper
            .convert(transactionDTO.getTransaction(), ModifyMultisigAccountTransactionDTO.class);
        assertEquals(transaction.getMinApprovalDelta(),
            (int) modifyMultisigAccountTransaction.getMinApprovalDelta());
        assertEquals(transaction.getMinRemovalDelta(),
            (int) modifyMultisigAccountTransaction.getMinRemovalDelta());
        assertEquals(
            modifyMultisigAccountTransaction.getModifications().get(0).getCosignatoryPublicKey(),
            transaction
                .getModifications()
                .get(0)
                .getCosignatoryPublicAccount()
                .getPublicKey()
                .toString());
        assertEquals(
            (int) modifyMultisigAccountTransaction.getModifications().get(0).getModificationType()
                .getValue(), transaction.getModifications().get(0).getType().getValue());
    }

    void validateLockFundsTx(LockFundsTransaction transaction, TransactionInfoDTO transactionDTO) {

        HashLockTransactionDTO hashLockTransactionDTO = jsonHelper
            .convert(transactionDTO.getTransaction(), HashLockTransactionDTO.class);

        assertEquals(
            extractBigInteger(hashLockTransactionDTO.getMosaic().getId()),
            transaction.getMosaic().getId().getId());
        assertEquals(
            extractBigInteger(hashLockTransactionDTO.getMosaic().getAmount()),
            transaction.getMosaic().getAmount());
        assertEquals(
            extractBigInteger(hashLockTransactionDTO.getDuration()),
            transaction.getDuration());
        assertEquals(
            hashLockTransactionDTO.getHash(),
            transaction.getSignedTransaction().getHash());
    }

    void validateSecretLockTx(SecretLockTransaction transaction,
        TransactionInfoDTO transactionDTO) {
        SecretLockTransactionDTO secretLockTransaction = jsonHelper
            .convert(transactionDTO.getTransaction(), SecretLockTransactionDTO.class);
        assertEquals(
            extractBigInteger(secretLockTransaction.getMosaicId()),
            transaction.getMosaic().getId().getId());
        assertEquals(
            extractBigInteger(secretLockTransaction.getAmount()),
            transaction.getMosaic().getAmount());
        assertEquals(
            extractBigInteger(secretLockTransaction.getDuration()),
            transaction.getDuration());
        assertEquals((int) secretLockTransaction.getHashAlgorithm().getValue(),
            transaction.getHashType().getValue());
        assertEquals(
            secretLockTransaction.getSecret(),
            transaction.getSecret());
        assertEquals(
            Address.createFromEncoded(
                secretLockTransaction.getRecipient()),
            transaction.getRecipient());
    }

    void validateSecretProofTx(SecretProofTransaction transaction,
        TransactionInfoDTO transactionDTO) {
        SecretProofTransactionDTO secretProofTransaction = jsonHelper
            .convert(transactionDTO.getTransaction(), SecretProofTransactionDTO.class);
        assertEquals((int) secretProofTransaction.getHashAlgorithm().getValue(),
            transaction.getHashType().getValue());
        assertEquals(
            secretProofTransaction.getSecret(),
            transaction.getSecret());
        assertEquals(
            secretProofTransaction.getProof(), transaction.getProof());
    }

    BigInteger extractBigInteger(List<Long> input) {
        return UInt64.extractBigInteger(input);
    }
}
