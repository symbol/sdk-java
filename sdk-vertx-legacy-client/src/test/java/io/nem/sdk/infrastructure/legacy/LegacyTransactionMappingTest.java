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

package io.nem.sdk.infrastructure.legacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.namespace.NamespaceType;
import io.nem.sdk.model.transaction.AggregateTransaction;
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
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

public class LegacyTransactionMappingTest {

    @Test
    void shouldCreateStandaloneTransferTransaction() {
        JsonObject transferTransactionDTO =
            createJsonObject("shouldCreateStandaloneTransferTransaction.json");

        Transaction transferTransaction = new TransactionMappingLegacy()
            .apply(transferTransactionDTO);

        validateStandaloneTransaction(transferTransaction, transferTransactionDTO);
    }

    @Test
    void shouldCreateAggregateTransferTransaction() {
        JsonObject aggregateTransferTransactionDTO =
            createJsonObject("shouldCreateAggregateTransferTransaction.json");

        Transaction aggregateTransferTransaction =
            new TransactionMappingLegacy().apply(aggregateTransferTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateTransferTransaction, aggregateTransferTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneRootNamespaceCreationTransaction() {
        JsonObject namespaceCreationTransactionDTO =
            createJsonObject("shouldCreateStandaloneRootNamespaceCreationTransaction.json");

        Transaction namespaceCreationTransaction =
            new TransactionMappingLegacy().apply(namespaceCreationTransactionDTO);

        validateStandaloneTransaction(namespaceCreationTransaction,
            namespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateRootNamespaceCreationTransaction() {
        JsonObject aggregateNamespaceCreationTransactionDTO =
            createJsonObject("shouldCreateAggregateRootNamespaceCreationTransaction.json");

        Transaction aggregateNamespaceCreationTransaction =
            new TransactionMappingLegacy().apply(aggregateNamespaceCreationTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateNamespaceCreationTransaction,
            aggregateNamespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneSubNamespaceCreationTransaction() {
        JsonObject namespaceCreationTransactionDTO =
            createJsonObject("shouldCreateStandaloneSubNamespaceCreationTransaction.json");

        Transaction namespaceCreationTransaction =
            new TransactionMappingLegacy().apply(namespaceCreationTransactionDTO);

        validateStandaloneTransaction(namespaceCreationTransaction,
            namespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateSubNamespaceCreationTransaction() {
        JsonObject aggregateNamespaceCreationTransactionDTO =
            createJsonObject("shouldCreateAggregateSubNamespaceCreationTransaction.json");

        Transaction aggregateNamespaceCreationTransaction =
            new TransactionMappingLegacy().apply(aggregateNamespaceCreationTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateNamespaceCreationTransaction,
            aggregateNamespaceCreationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneMosaicCreationTransaction() {
        JsonObject mosaicCreationTransactionDTO =
            createJsonObject("shouldCreateStandaloneMosaicCreationTransaction.json");

        Transaction mosaicCreationTransaction =
            new TransactionMappingLegacy().apply(mosaicCreationTransactionDTO);

        validateStandaloneTransaction(mosaicCreationTransaction, mosaicCreationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMosaicCreationTransaction() {
        JsonObject aggregateMosaicCreationTransactionDTO =
            createJsonObject("shouldCreateAggregateMosaicCreationTransaction.json");

        Transaction aggregateMosaicCreationTransaction =
            new TransactionMappingLegacy().apply(aggregateMosaicCreationTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateMosaicCreationTransaction,
            aggregateMosaicCreationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneMosaicSupplyChangeTransaction() {
        JsonObject mosaicSupplyChangeTransactionDTO =
            createJsonObject("shouldCreateStandaloneMosaicSupplyChangeTransaction.json");

        Transaction mosaicSupplyChangeTransaction =
            new TransactionMappingLegacy().apply(mosaicSupplyChangeTransactionDTO);

        validateStandaloneTransaction(mosaicSupplyChangeTransaction,
            mosaicSupplyChangeTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMosaicSupplyChangeTransaction() {
        JsonObject aggregateMosaicSupplyChangeTransactionDTO =
            createJsonObject("shouldCreateAggregateMosaicSupplyChangeTransaction.json");

        Transaction aggregateMosaicSupplyChangeTransaction =
            new TransactionMappingLegacy().apply(aggregateMosaicSupplyChangeTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateMosaicSupplyChangeTransaction,
            aggregateMosaicSupplyChangeTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneMultisigModificationTransaction() {
        JsonObject multisigModificationTransactionDTO =
            createJsonObject("shouldCreateStandaloneMultisigModificationTransaction.json");

        Transaction multisigModificationTransaction =
            new TransactionMappingLegacy().apply(multisigModificationTransactionDTO);

        validateStandaloneTransaction(
            multisigModificationTransaction, multisigModificationTransactionDTO);
    }

    @Test
    void shouldCreateAggregateMultisigModificationTransaction() {
        JsonObject aggregateMultisigModificationTransactionDTO =
            createJsonObject("shouldCreateAggregateMultisigModificationTransaction.json");

        Transaction aggregateMultisigModificationTransaction =
            new TransactionMappingLegacy().apply(aggregateMultisigModificationTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateMultisigModificationTransaction,
            aggregateMultisigModificationTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneLockFundsTransaction() {
        JsonObject lockFundsTransactionDTO =
            createJsonObject("shouldCreateStandaloneLockFundsTransaction.json");

        Transaction lockFundsTransaction = new TransactionMappingLegacy()
            .apply(lockFundsTransactionDTO);

        validateStandaloneTransaction(lockFundsTransaction, lockFundsTransactionDTO);
    }

    @Test
    void shouldCreateAggregateLockFundsTransaction() {
        JsonObject aggregateLockFundsTransactionDTO =
            createJsonObject("shouldCreateAggregateLockFundsTransaction.json");

        Transaction lockFundsTransaction =
            new TransactionMappingLegacy().apply(aggregateLockFundsTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) lockFundsTransaction, aggregateLockFundsTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneSecretLockTransaction() {
        JsonObject secretLockTransactionDTO =
            createJsonObject("shouldCreateStandaloneSecretLockTransaction.json");

        Transaction secretLockTransaction = new TransactionMappingLegacy()
            .apply(secretLockTransactionDTO);

        validateStandaloneTransaction(secretLockTransaction, secretLockTransactionDTO);
    }

    @Test
    void shouldCreateAggregateSecretLockTransaction() {
        JsonObject aggregateSecretLockTransactionDTO =
            createJsonObject("shouldCreateAggregateSecretLockTransaction.json");

        Transaction aggregateSecretLockTransaction =
            new TransactionMappingLegacy().apply(aggregateSecretLockTransactionDTO);

        validateAggregateTransaction((AggregateTransaction) aggregateSecretLockTransaction,
            aggregateSecretLockTransactionDTO);
    }

    @Test
    void shouldCreateStandaloneSecretProofTransaction() {
        JsonObject secretProofTransactionDTO =
            createJsonObject("shouldCreateStandaloneSecretProofTransaction.json");

        Transaction secretProofTransaction = new TransactionMappingLegacy()
            .apply(secretProofTransactionDTO);

        validateStandaloneTransaction(secretProofTransaction, secretProofTransactionDTO);
    }

    @Test
    void shouldCreateAggregateSecretProofTransaction() {
        JsonObject aggregateSecretProofTransactionDTO =
            createJsonObject("shouldCreateAggregateSecretProofTransaction.json");

        Transaction aggregateSecretProofTransaction =
            new TransactionMappingLegacy().apply(aggregateSecretProofTransactionDTO);

        validateAggregateTransaction(
            (AggregateTransaction) aggregateSecretProofTransaction,
            aggregateSecretProofTransactionDTO);
    }

    private JsonObject createJsonObject(String name) {

        String resourceName = "TransactionMapping-" + name;

        try (InputStream resourceAsStream = getClass().getClassLoader()
            .getResourceAsStream("json/" + resourceName)) {
            return new JsonObject(IOUtils.toString(resourceAsStream));
        } catch (Exception e) {
            throw new IllegalStateException(
                "Cannot open resource " + resourceName + ". Error: " + ExceptionUtils.getMessage(e),
                e);
        }
    }

    void validateStandaloneTransaction(Transaction transaction, JsonObject transactionDTO) {
        assertEquals(
            extractBigInteger(transactionDTO.getJsonObject("meta").getJsonArray("height")),
            transaction.getTransactionInfo().get().getHeight());
        if (transaction.getTransactionInfo().get().getHash().isPresent()) {
            assertEquals(
                transactionDTO.getJsonObject("meta").getString("hash"),
                transaction.getTransactionInfo().get().getHash().get());
        }
        if (transaction.getTransactionInfo().get().getMerkleComponentHash().isPresent()) {
            assertEquals(
                transactionDTO.getJsonObject("meta").getString("merkleComponentHash"),
                transaction.getTransactionInfo().get().getMerkleComponentHash().get());
        }
        if (transaction.getTransactionInfo().get().getIndex().isPresent()) {
            assertEquals(
                transaction.getTransactionInfo().get().getIndex().get(),
                transactionDTO.getJsonObject("meta").getInteger("index"));
        }
        if (transaction.getTransactionInfo().get().getId().isPresent()) {
            assertEquals(
                transactionDTO.getJsonObject("meta").getString("id"),
                transaction.getTransactionInfo().get().getId().get());
        }
        if (transaction.getTransactionInfo().get().getAggregateHash().isPresent()) {
            assertEquals(
                transactionDTO.getJsonObject("meta").getString("aggregateHash"),
                transaction.getTransactionInfo().get().getAggregateHash().get());
        }
        if (transaction.getTransactionInfo().get().getAggregateId().isPresent()) {
            assertEquals(
                transactionDTO.getJsonObject("meta").getString("aggregateId"),
                transaction.getTransactionInfo().get().getAggregateId().get());
        }

        assertEquals(
            transactionDTO.getJsonObject("transaction").getString("signature"),
            transaction.getSignature().get());
        assertEquals(
            transactionDTO.getJsonObject("transaction").getString("signer"),
            transaction.getSigner().get().getPublicKey().toString());
        assertTrue(
            transaction.getType().getValue()
                == transactionDTO.getJsonObject("transaction").getInteger("type"));
        int version =
            (int)
                Long.parseLong(
                    Integer.toHexString(
                        transactionDTO.getJsonObject("transaction").getInteger("version"))
                        .substring(2, 4),
                    16);
        assertTrue(transaction.getVersion() == version);
        int networkType =
            (int)
                Long.parseLong(
                    Integer.toHexString(
                        transactionDTO.getJsonObject("transaction").getInteger("version"))
                        .substring(0, 2),
                    16);
        assertTrue(transaction.getNetworkType().getValue() == networkType);
        assertEquals(
            extractBigInteger(transactionDTO.getJsonObject("transaction").getJsonArray("maxFee")),
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
            validateMultisigModificationTx(
                (ModifyMultisigAccountTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.LOCK) {
            validateLockFundsTx((LockFundsTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.SECRET_LOCK) {
            validateSecretLockTx((SecretLockTransaction) transaction, transactionDTO);
        } else if (transaction.getType() == TransactionType.SECRET_PROOF) {
            validateSecretProofTx((SecretProofTransaction) transaction, transactionDTO);
        }
    }

    void validateAggregateTransaction(
        AggregateTransaction aggregateTransaction, JsonObject aggregateTransactionDTO) {
        assertEquals(
            extractBigInteger(aggregateTransactionDTO.getJsonObject("meta").getJsonArray("height")),
            aggregateTransaction.getTransactionInfo().get().getHeight());
        if (aggregateTransaction.getTransactionInfo().get().getHash().isPresent()) {
            assertEquals(
                aggregateTransactionDTO.getJsonObject("meta").getString("hash"),
                aggregateTransaction.getTransactionInfo().get().getHash().get());
        }
        if (aggregateTransaction.getTransactionInfo().get().getMerkleComponentHash().isPresent()) {
            assertEquals(
                aggregateTransactionDTO.getJsonObject("meta").getString("merkleComponentHash"),
                aggregateTransaction.getTransactionInfo().get().getMerkleComponentHash().get());
        }
        if (aggregateTransaction.getTransactionInfo().get().getIndex().isPresent()) {
            assertEquals(
                aggregateTransaction.getTransactionInfo().get().getIndex().get(),
                aggregateTransactionDTO.getJsonObject("meta").getInteger("index"));
        }
        if (aggregateTransaction.getTransactionInfo().get().getId().isPresent()) {
            assertEquals(
                aggregateTransactionDTO.getJsonObject("meta").getString("id"),
                aggregateTransaction.getTransactionInfo().get().getId().get());
        }

        assertEquals(
            aggregateTransactionDTO.getJsonObject("transaction").getString("signature"),
            aggregateTransaction.getSignature().get());
        assertEquals(
            aggregateTransactionDTO.getJsonObject("transaction").getString("signer"),
            aggregateTransaction.getSigner().get().getPublicKey().toString());
        int version =
            (int)
                Long.parseLong(
                    Integer.toHexString(
                        aggregateTransactionDTO.getJsonObject("transaction").getInteger("version"))
                        .substring(2, 4),
                    16);
        assertTrue(aggregateTransaction.getVersion() == version);
        int networkType =
            (int)
                Long.parseLong(
                    Integer.toHexString(
                        aggregateTransactionDTO.getJsonObject("transaction").getInteger("version"))
                        .substring(0, 2),
                    16);
        assertTrue(aggregateTransaction.getNetworkType().getValue() == networkType);
        assertTrue(
            aggregateTransaction.getType().getValue()
                == aggregateTransactionDTO.getJsonObject("transaction").getInteger("type"));
        assertEquals(
            extractBigInteger(
                aggregateTransactionDTO.getJsonObject("transaction").getJsonArray("maxFee")),
            aggregateTransaction.getFee());
        assertNotNull(aggregateTransaction.getDeadline());

        assertEquals(
            aggregateTransactionDTO
                .getJsonObject("transaction")
                .getJsonArray("cosignatures")
                .getJsonObject(0)
                .getString("signature"),
            aggregateTransaction.getCosignatures().get(0).getSignature());
        assertEquals(
            aggregateTransactionDTO
                .getJsonObject("transaction")
                .getJsonArray("cosignatures")
                .getJsonObject(0)
                .getString("signer"),
            aggregateTransaction.getCosignatures().get(0).getSigner().getPublicKey().toString());

        Transaction innerTransaction = aggregateTransaction.getInnerTransactions().get(0);
        validateStandaloneTransaction(
            innerTransaction,
            aggregateTransactionDTO
                .getJsonObject("transaction")
                .getJsonArray("transactions")
                .getJsonObject(0));
    }

    void validateTransferTx(TransferTransaction transaction, JsonObject transactionDTO) {
        assertEquals(
            Address.createFromEncoded(
                transactionDTO.getJsonObject("transaction").getString("recipient")),
            transaction.getRecipient().get());

        JsonArray mosaicsDTO = transactionDTO.getJsonObject("transaction").getJsonArray("mosaics");
        if (mosaicsDTO != null && mosaicsDTO.size() > 0) {
            assertEquals(
                extractBigInteger(mosaicsDTO.getJsonObject(0).getJsonArray("id")),
                transaction.getMosaics().get(0).getId().getId());
            assertEquals(
                extractBigInteger(mosaicsDTO.getJsonObject(0).getJsonArray("amount")),
                transaction.getMosaics().get(0).getAmount());
        }

        assertEquals(
            new String(
                Hex.decode(
                    transactionDTO
                        .getJsonObject("transaction")
                        .getJsonObject("message")
                        .getString("payload")),
                StandardCharsets.UTF_8),
            transaction.getMessage().getPayload());

        assertTrue(
            transactionDTO.getJsonObject("transaction").getJsonObject("message").getInteger("type")
                == transaction.getMessage().getType());
    }

    void validateNamespaceCreationTx(
        RegisterNamespaceTransaction transaction, JsonObject transactionDTO) {
        assertTrue(
            transactionDTO.getJsonObject("transaction").getInteger("namespaceType")
                == transaction.getNamespaceType().getValue());
        assertEquals(
            transactionDTO.getJsonObject("transaction").getString("name"),
            transaction.getNamespaceName());
        assertEquals(
            extractBigInteger(
                transactionDTO.getJsonObject("transaction").getJsonArray("namespaceId")),
            transaction.getNamespaceId().getId());

        if (transaction.getNamespaceType() == NamespaceType.RootNamespace) {
            assertEquals(
                extractBigInteger(
                    transactionDTO.getJsonObject("transaction").getJsonArray("duration")),
                transaction.getDuration().get());
        } else {
            assertEquals(
                extractBigInteger(
                    transactionDTO.getJsonObject("transaction").getJsonArray("parentId")),
                transaction.getParentId().get().getId());
        }
    }

    void validateMosaicCreationTx(
        MosaicDefinitionTransaction transaction, JsonObject transactionDTO) {
        // assertEquals(extractBigInteger(transactionDTO.getJsonObject("transaction").getJsonArray("parentId")),
        //        transaction.getNamespaceId().getId());
        assertEquals(
            extractBigInteger(transactionDTO.getJsonObject("transaction").getJsonArray("mosaicId")),
            transaction.getMosaicId().getId());
        // assertEquals(transactionDTO.getJsonObject("transaction").getString("name"),
        //        transaction.getMosaicName());
        assertTrue(
            transaction.getMosaicProperties().getDivisibility()
                == transactionDTO
                .getJsonObject("transaction")
                .getJsonArray("properties")
                .getJsonObject(1)
                .getJsonArray("value")
                .getInteger(0));
        assertEquals(
            extractBigInteger(
                transactionDTO
                    .getJsonObject("transaction")
                    .getJsonArray("properties")
                    .getJsonObject(2)
                    .getJsonArray("value")).longValue(),
            transaction.getMosaicProperties().getDuration().longValue());
        assertTrue(transaction.getMosaicProperties().isSupplyMutable());
        assertTrue(transaction.getMosaicProperties().isTransferable());
    }

    void validateMosaicSupplyChangeTx(
        MosaicSupplyChangeTransaction transaction, JsonObject transactionDTO) {
        assertEquals(
            extractBigInteger(transactionDTO.getJsonObject("transaction").getJsonArray("mosaicId")),
            transaction.getMosaicId().getId());
        assertEquals(
            extractBigInteger(transactionDTO.getJsonObject("transaction").getJsonArray("delta")),
            transaction.getDelta());
        assertTrue(
            transaction.getMosaicSupplyType().getValue()
                == transactionDTO.getJsonObject("transaction").getInteger("direction"));
    }

    void validateMultisigModificationTx(
        ModifyMultisigAccountTransaction transaction, JsonObject transactionDTO) {
        assertTrue(
            transaction.getMinApprovalDelta()
                == transactionDTO.getJsonObject("transaction").getInteger("minApprovalDelta"));
        assertTrue(
            transaction.getMinRemovalDelta()
                == transactionDTO.getJsonObject("transaction").getInteger("minRemovalDelta"));
        assertEquals(
            transactionDTO
                .getJsonObject("transaction")
                .getJsonArray("modifications")
                .getJsonObject(0)
                .getString("cosignatoryPublicKey"),
            transaction
                .getModifications()
                .get(0)
                .getCosignatoryPublicAccount()
                .getPublicKey()
                .toString());
        assertTrue(
            transactionDTO
                .getJsonObject("transaction")
                .getJsonArray("modifications")
                .getJsonObject(0)
                .getInteger("type")
                == transaction.getModifications().get(0).getType().getValue());
    }

    void validateLockFundsTx(LockFundsTransaction transaction, JsonObject transactionDTO) {
        assertEquals(
            extractBigInteger(transactionDTO.getJsonObject("transaction").getJsonArray("mosaicId")),
            transaction.getMosaic().getId().getId());
        assertEquals(
            extractBigInteger(transactionDTO.getJsonObject("transaction").getJsonArray("amount")),
            transaction.getMosaic().getAmount());
        assertEquals(
            extractBigInteger(transactionDTO.getJsonObject("transaction").getJsonArray("duration")),
            transaction.getDuration());
        assertEquals(
            transactionDTO.getJsonObject("transaction").getString("hash"),
            transaction.getSignedTransaction().getHash());
    }

    void validateSecretLockTx(SecretLockTransaction transaction, JsonObject transactionDTO) {
        assertEquals(
            extractBigInteger(transactionDTO.getJsonObject("transaction").getJsonArray("mosaicId")),
            transaction.getMosaic().getId().getId());
        assertEquals(
            extractBigInteger(transactionDTO.getJsonObject("transaction").getJsonArray("amount")),
            transaction.getMosaic().getAmount());
        assertEquals(
            extractBigInteger(transactionDTO.getJsonObject("transaction").getJsonArray("duration")),
            transaction.getDuration());
        assertTrue(
            transactionDTO.getJsonObject("transaction").getInteger("hashAlgorithm")
                == transaction.getHashType().getValue());
        assertEquals(
            transactionDTO.getJsonObject("transaction").getString("secret"),
            transaction.getSecret());
        assertEquals(
            Address.createFromEncoded(
                transactionDTO.getJsonObject("transaction").getString("recipient")),
            transaction.getRecipient());
    }

    void validateSecretProofTx(SecretProofTransaction transaction, JsonObject transactionDTO) {
        assertTrue(
            transactionDTO.getJsonObject("transaction").getInteger("hashAlgorithm")
                == transaction.getHashType().getValue());
        assertEquals(
            transactionDTO.getJsonObject("transaction").getString("secret"),
            transaction.getSecret());
        assertEquals(
            transactionDTO.getJsonObject("transaction").getString("proof"), transaction.getProof());
    }

    BigInteger extractBigInteger(JsonArray input) {
        return UInt64.fromLongArray(
            input.stream().map(Object::toString).map(Long::parseLong).mapToLong(Long::longValue)
                .toArray());
    }
}
