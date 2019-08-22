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

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.mosaic.MosaicProperties;
import io.nem.sdk.model.mosaic.MosaicSupplyType;
import io.nem.sdk.model.namespace.AliasAction;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceType;
import io.nem.sdk.model.transaction.AccountLinkAction;
import io.nem.sdk.model.transaction.AccountLinkTransaction;
import io.nem.sdk.model.transaction.AddressAliasTransaction;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.HashType;
import io.nem.sdk.model.transaction.LockFundsTransaction;
import io.nem.sdk.model.transaction.Message;
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
import io.nem.sdk.model.transaction.TransactionInfo;
import io.nem.sdk.model.transaction.TransactionType;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.nem.sdk.model.transaction.UInt64;
import io.reactivex.functions.Function;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bouncycastle.util.encoders.Hex;

public class TransactionMappingLegacy implements Function<JsonObject, Transaction> {

    @Override
    public Transaction apply(JsonObject input) {
        JsonObject transaction = input.getJsonObject("transaction");
        int type = transaction.getInteger("type");

        if (type == TransactionType.TRANSFER.getValue()) {
            return new TransferTransactionMapping().apply(input);
        } else if (type == TransactionType.REGISTER_NAMESPACE.getValue()) {
            return new NamespaceCreationTransactionMapping().apply(input);
        } else if (type == TransactionType.MOSAIC_DEFINITION.getValue()) {
            return new MosaicCreationTransactionMapping().apply(input);
        } else if (type == TransactionType.MOSAIC_SUPPLY_CHANGE.getValue()) {
            return new MosaicSupplyChangeTransactionMapping().apply(input);
        } else if (type == TransactionType.ADDRESS_ALIAS.getValue()) {
            return new AddressAliasTransactionMapping().apply(input);
            // } else if (type == TransactionType.MOSAIC_ALIAS.getValue()) {
            //    return new MosaicAliasTransactionMapping().apply(input);
        } else if (type == TransactionType.MODIFY_MULTISIG_ACCOUNT.getValue()) {
            return new MultisigModificationTransactionMapping().apply(input);
        } else if (type == TransactionType.AGGREGATE_COMPLETE.getValue()
            || type == TransactionType.AGGREGATE_BONDED.getValue()) {
            return new AggregateTransactionMapping().apply(input);
        } else if (type == TransactionType.LOCK.getValue()) {
            return new LockFundsTransactionMapping().apply(input);
        } else if (type == TransactionType.SECRET_LOCK.getValue()) {
            return new SecretLockTransactionMapping().apply(input);
        } else if (type == TransactionType.SECRET_PROOF.getValue()) {
            return new SecretProofTransactionMapping().apply(input);
        } else if (type == TransactionType.ACCOUNT_LINK.getValue()) {
            return new AccountLinkTransactionMapping().apply(input);
        }

        throw new UnsupportedOperationException("Unimplemented Transaction type " + type);
    }

    BigInteger extractBigInteger(JsonArray input) {
        return UInt64.fromLongArray(
            input.stream().map(Object::toString).map(Long::parseLong).mapToLong(Long::longValue)
                .toArray());
    }

    BigInteger extractBigInteger(Long input) {
        return BigInteger.valueOf(input.intValue());
    }

    Integer extractTransactionVersion(int version) {
        return (int) Long.parseLong(Integer.toHexString(version).substring(2, 4), 16);
    }

    NetworkType extractNetworkType(int version) {
        int networkType = (int) Long.parseLong(Integer.toHexString(version).substring(0, 2), 16);
        return NetworkType.rawValueOf(networkType);
    }

    public TransactionInfo createTransactionInfo(JsonObject jsonObject) {
        if (jsonObject.containsKey("hash") && jsonObject.containsKey("id")) {
            return TransactionInfo.create(
                extractBigInteger(jsonObject.getJsonArray("height")),
                jsonObject.getInteger("index"),
                jsonObject.getString("id"),
                jsonObject.getString("hash"),
                jsonObject.getString("merkleComponentHash"));
        } else if (jsonObject.containsKey("aggregateHash") && jsonObject.containsKey("id")) {
            return TransactionInfo.createAggregate(
                extractBigInteger(jsonObject.getJsonArray("height")),
                jsonObject.getInteger("index"),
                jsonObject.getString("id"),
                jsonObject.getString("aggregateHash"),
                jsonObject.getString("aggregateId"));
        } else {
            return TransactionInfo.create(
                extractBigInteger(jsonObject.getJsonArray("height")),
                jsonObject.getString("hash"),
                jsonObject.getString("merkleComponentHash"));
        }
    }
}

class TransferTransactionMapping extends TransactionMappingLegacy {

    @Override
    public TransferTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        List<Mosaic> mosaics = new ArrayList<>();

        if (transaction.getJsonArray("mosaics") != null) {
            mosaics =
                transaction.getJsonArray("mosaics").stream()
                    .map(item -> (JsonObject) item)
                    .map(
                        mosaic ->
                            new Mosaic(
                                new MosaicId(extractBigInteger(mosaic.getJsonArray("id"))),
                                extractBigInteger(mosaic.getJsonArray("amount"))))
                    .collect(Collectors.toList());
        }

        Message message = PlainMessage.Empty;
        if (transaction.getJsonObject("message") != null) {
            message =
                new PlainMessage(
                    new String(
                        Hex.decode(transaction.getJsonObject("message").getString("payload")),
                        StandardCharsets.UTF_8));
        }

        return new TransferTransaction(
            extractNetworkType(transaction.getInteger("version")),
            extractTransactionVersion(transaction.getInteger("version")),
            deadline,
            extractBigInteger(transaction.getJsonArray("maxFee")),
            Optional.of(Address.createFromEncoded(transaction.getString("recipient"))),
            Optional.empty(),
            mosaics,
            message,
            transaction.getString("signature"),
            new PublicAccount(
                transaction.getString("signer"),
                extractNetworkType(transaction.getInteger("version"))),
            transactionInfo);
    }
}

class NamespaceCreationTransactionMapping extends TransactionMappingLegacy {

    @Override
    public RegisterNamespaceTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        NamespaceType namespaceType = NamespaceType
            .rawValueOf(transaction.getInteger("namespaceType"));

        return new RegisterNamespaceTransaction(
            extractNetworkType(transaction.getInteger("version")),
            extractTransactionVersion(transaction.getInteger("version")),
            deadline,
            extractBigInteger(transaction.getJsonArray("maxFee")),
            transaction.getString("name"),
            new NamespaceId(extractBigInteger(transaction.getJsonArray("namespaceId"))),
            namespaceType,
            namespaceType == NamespaceType.RootNamespace
                ? Optional.of(extractBigInteger(transaction.getJsonArray("duration")))
                : Optional.empty(),
            namespaceType == NamespaceType.SubNamespace
                ? Optional
                .of(new NamespaceId(extractBigInteger(transaction.getJsonArray("parentId"))))
                : Optional.empty(),
            transaction.getString("signature"),
            new PublicAccount(
                transaction.getString("signer"),
                extractNetworkType(transaction.getInteger("version"))),
            transactionInfo);
    }
}

class MosaicCreationTransactionMapping extends TransactionMappingLegacy {

    @Override
    public MosaicDefinitionTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));

        JsonArray mosaicProperties = transaction.getJsonArray("properties");

        String flags =
            "00"
                + Integer.toBinaryString(
                extractBigInteger(mosaicProperties.getJsonObject(0).getJsonArray("value"))
                    .intValue());
        String bitMapFlags = flags.substring(flags.length() - 2);
        MosaicProperties properties =
            MosaicProperties.create(
                bitMapFlags.charAt(1) == '1',
                bitMapFlags.charAt(0) == '1',
                extractBigInteger(mosaicProperties.getJsonObject(1).getJsonArray("value"))
                    .intValue(),
                mosaicProperties.size() == 3
                    ? extractBigInteger(mosaicProperties.getJsonObject(2).getJsonArray("value"))
                    : BigInteger.valueOf(0));

        return new MosaicDefinitionTransaction(
            extractNetworkType(transaction.getInteger("version")),
            extractTransactionVersion(transaction.getInteger("version")),
            deadline,
            extractBigInteger(transaction.getJsonArray("maxFee")),
            MosaicNonce.createFromBigInteger(extractBigInteger(transaction.getLong("nonce"))),
            new MosaicId(extractBigInteger(transaction.getJsonArray("mosaicId"))),
            properties,
            transaction.getString("signature"),
            new PublicAccount(
                transaction.getString("signer"),
                extractNetworkType(transaction.getInteger("version"))),
            transactionInfo);
    }
}

class MosaicSupplyChangeTransactionMapping extends TransactionMappingLegacy {

    @Override
    public MosaicSupplyChangeTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));

        JsonArray maxFee = transaction.getJsonArray("maxFee");
        return new MosaicSupplyChangeTransaction(
            extractNetworkType(transaction.getInteger("version")),
            extractTransactionVersion(transaction.getInteger("version")),
            deadline,
            extractBigInteger(maxFee),
            new MosaicId(extractBigInteger(transaction.getJsonArray("mosaicId"))),
            MosaicSupplyType.rawValueOf(transaction.getInteger("direction")),
            extractBigInteger(transaction.getJsonArray("delta")),
            transaction.getString("signature"),
            new PublicAccount(
                transaction.getString("signer"),
                extractNetworkType(transaction.getInteger("version"))),
            transactionInfo);
    }
}

class MultisigModificationTransactionMapping extends TransactionMappingLegacy {

    @Override
    public ModifyMultisigAccountTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.getInteger("version"));

        List<MultisigCosignatoryModification> modifications =
            transaction.containsKey("modifications")
                ? transaction.getJsonArray("modifications").stream()
                .map(item -> (JsonObject) item)
                .map(
                    multisigModification ->
                        new MultisigCosignatoryModification(
                            MultisigCosignatoryModificationType.rawValueOf(
                                multisigModification.getInteger("type")),
                            PublicAccount.createFromPublicKey(
                                multisigModification.getString("cosignatoryPublicKey"),
                                networkType)))
                .collect(Collectors.toList())
                : Collections.emptyList();

        return new ModifyMultisigAccountTransaction(
            networkType,
            extractTransactionVersion(transaction.getInteger("version")),
            deadline,
            extractBigInteger(transaction.getJsonArray("maxFee")),
            (byte) transaction.getInteger("minApprovalDelta").intValue(),
            (byte) transaction.getInteger("minRemovalDelta").intValue(),
            modifications,
            transaction.getString("signature"),
            new PublicAccount(transaction.getString("signer"), networkType),
            transactionInfo);
    }
}

class AggregateTransactionMapping extends TransactionMappingLegacy {

    @Override
    public AggregateTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.getInteger("version"));

        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < transaction.getJsonArray("transactions").getList().size(); i++) {
            JsonObject innerTransaction = transaction.getJsonArray("transactions").getJsonObject(i);
            innerTransaction
                .getJsonObject("transaction")
                .put("deadline", transaction.getJsonArray("deadline"));
            innerTransaction
                .getJsonObject("transaction")
                .put("maxFee", transaction.getJsonArray("maxFee"));
            innerTransaction
                .getJsonObject("transaction")
                .put("signature", transaction.getString("signature"));
            if (!innerTransaction.containsKey("meta")) {
                innerTransaction.put("meta", input.getJsonObject("meta"));
            }
            transactions.add(new TransactionMappingLegacy().apply(innerTransaction));
        }

        List<AggregateTransactionCosignature> cosignatures = new ArrayList<>();
        if (transaction.getJsonArray("cosignatures") != null) {
            cosignatures =
                transaction.getJsonArray("cosignatures").stream()
                    .map(item -> (JsonObject) item)
                    .map(
                        aggregateCosignature ->
                            new AggregateTransactionCosignature(
                                aggregateCosignature.getString("signature"),
                                new PublicAccount(aggregateCosignature.getString("signer"),
                                    networkType)))
                    .collect(Collectors.toList());
        }

        return new AggregateTransaction(
            networkType,
            TransactionType.rawValueOf(transaction.getInteger("type")),
            extractTransactionVersion(transaction.getInteger("version")),
            deadline,
            extractBigInteger(transaction.getJsonArray("maxFee")),
            transactions,
            cosignatures,
            transaction.getString("signature"),
            new PublicAccount(transaction.getString("signer"), networkType),
            transactionInfo);
    }
}

class LockFundsTransactionMapping extends TransactionMappingLegacy {

    @Override
    public LockFundsTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.getInteger("version"));
        Mosaic mosaic;
        if (transaction.containsKey("mosaicId")) {
            mosaic =
                new Mosaic(
                    new MosaicId(extractBigInteger(transaction.getJsonArray("mosaicId"))),
                    extractBigInteger(transaction.getJsonArray("amount")));
        } else {
            mosaic =
                new Mosaic(
                    new MosaicId(
                        extractBigInteger(transaction.getJsonObject("mosaic").getJsonArray("id"))),
                    extractBigInteger(transaction.getJsonObject("mosaic").getJsonArray("amount")));
        }
        return new LockFundsTransaction(
            networkType,
            extractTransactionVersion(transaction.getInteger("version")),
            deadline,
            extractBigInteger(transaction.getJsonArray("maxFee")),
            mosaic,
            extractBigInteger(transaction.getJsonArray("duration")),
            new SignedTransaction("", transaction.getString("hash"),
                TransactionType.AGGREGATE_BONDED),
            transaction.getString("signature"),
            new PublicAccount(transaction.getString("signer"), networkType),
            transactionInfo);
    }
}

class SecretLockTransactionMapping extends TransactionMappingLegacy {

    @Override
    public SecretLockTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.getInteger("version"));
        Mosaic mosaic;
        if (transaction.containsKey("mosaicId")) {
            mosaic =
                new Mosaic(
                    new MosaicId(extractBigInteger(transaction.getJsonArray("mosaicId"))),
                    extractBigInteger(transaction.getJsonArray("amount")));
        } else {
            mosaic =
                new Mosaic(
                    new MosaicId(
                        extractBigInteger(transaction.getJsonObject("mosaic").getJsonArray("id"))),
                    extractBigInteger(transaction.getJsonObject("mosaic").getJsonArray("amount")));
        }
        return new SecretLockTransaction(
            networkType,
            extractTransactionVersion(transaction.getInteger("version")),
            deadline,
            extractBigInteger(transaction.getJsonArray("maxFee")),
            mosaic,
            extractBigInteger(transaction.getJsonArray("duration")),
            HashType.rawValueOf(transaction.getInteger("hashAlgorithm")),
            transaction.getString("secret"),
            Address.createFromEncoded(transaction.getString("recipient")),
            transaction.getString("signature"),
            new PublicAccount(transaction.getString("signer"), networkType),
            transactionInfo);
    }
}

class AddressAliasTransactionMapping extends TransactionMappingLegacy {


    @Override
    public AddressAliasTransaction apply(JsonObject input) {

        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.getInteger("version"));
        AliasAction aliasAction = AliasAction
            .rawValueOf(transaction.getInteger("aliasAction").byteValue());
        NamespaceId namespaceId = new NamespaceId(
            extractBigInteger(transaction.getJsonArray("namespaceId")));
        return new AddressAliasTransaction(
            networkType,
            extractTransactionVersion(transaction.getInteger("version")),
            deadline,
            extractBigInteger(transaction.getJsonArray("maxFee")),
            aliasAction,
            namespaceId,
            Address.createFromEncoded(transaction.getString("address")),
            Optional.ofNullable(transaction.getString("signature")),
            Optional.of(new PublicAccount(transaction.getString("signer"), networkType)),
            Optional.of(transactionInfo));
    }

}

class SecretProofTransactionMapping extends TransactionMappingLegacy {

    @Override
    public SecretProofTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.getInteger("version"));

        return new SecretProofTransaction(
            networkType,
            extractTransactionVersion(transaction.getInteger("version")),
            deadline,
            extractBigInteger(transaction.getJsonArray("maxFee")),
            HashType.rawValueOf(transaction.getInteger("hashAlgorithm")),
            Address.createFromEncoded(transaction.getString("recipient")),
            transaction.getString("secret"),
            transaction.getString("proof"),
            transaction.getString("signature"),
            new PublicAccount(transaction.getString("signer"), networkType),
            transactionInfo);
    }
}

class AccountLinkTransactionMapping extends TransactionMappingLegacy {

    @Override
    public AccountLinkTransaction apply(JsonObject input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getJsonObject("meta"));

        JsonObject transaction = input.getJsonObject("transaction");
        Deadline deadline = new Deadline(extractBigInteger(transaction.getJsonArray("deadline")));
        NetworkType networkType = extractNetworkType(transaction.getInteger("version"));

        return new AccountLinkTransaction(
            networkType,
            extractTransactionVersion(transaction.getInteger("version")),
            deadline,
            extractBigInteger(transaction.getJsonArray("maxFee")),
            PublicAccount
                .createFromPublicKey(transaction.getString("remoteAccountKey"), networkType),
            AccountLinkAction.rawValueOf(transaction.getInteger("action")),
            transaction.getString("signature"),
            new PublicAccount(transaction.getString("signer"), networkType),
            transactionInfo);
    }
}
