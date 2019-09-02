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
package io.nem.sdk.infrastructure.okhttp;

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.mosaic.MosaicNonce;
import io.nem.sdk.model.mosaic.MosaicProperties;
import io.nem.sdk.model.mosaic.MosaicSupplyType;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.namespace.NamespaceType;
import io.nem.sdk.model.transaction.AccountLinkAction;
import io.nem.sdk.model.transaction.AccountLinkTransaction;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.HashType;
import io.nem.sdk.model.transaction.JsonHelper;
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
import io.nem.sdk.openapi.okhttp_gson.model.AccountLinkTransactionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.AggregateBondedTransactionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.HashLockTransactionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.ModifyMultisigAccountTransactionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicDefinitionTransactionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicPropertyDTO;
import io.nem.sdk.openapi.okhttp_gson.model.MosaicSupplyChangeTransactionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.RegisterNamespaceTransactionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.SecretLockTransactionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.SecretProofTransactionDTO;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionInfoDTO;
import io.nem.sdk.openapi.okhttp_gson.model.TransactionMetaDTO;
import io.nem.sdk.openapi.okhttp_gson.model.TransferTransactionDTO;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bouncycastle.util.encoders.Hex;

/*
 *  TODO map generated open api objects like MosaicSupplyChangeTransactionDTO instead of a JsonObject.
 */
public class TransactionMappingOkHttp implements Function<TransactionInfoDTO, Transaction> {

    private final JsonHelper jsonHelper;

    public TransactionMappingOkHttp(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    @Override
    public Transaction apply(TransactionInfoDTO input) {
        int type = jsonHelper.getInteger(input.getTransaction(), "type");
        if (type == TransactionType.TRANSFER.getValue()) {
            return new TransferTransactionMapping(jsonHelper).apply(input);
        } else if (type == TransactionType.REGISTER_NAMESPACE.getValue()) {
            return new NamespaceCreationTransactionMapping(jsonHelper).apply(input);
        } else if (type == TransactionType.MOSAIC_DEFINITION.getValue()) {
            return new MosaicCreationTransactionMapping(jsonHelper).apply(input);
        } else if (type == TransactionType.MOSAIC_SUPPLY_CHANGE.getValue()) {
            return new MosaicSupplyChangeTransactionMapping(jsonHelper).apply(input);
            // } else if (type == TransactionType.MOSAIC_ALIAS.getValue()) {
            //    return new MosaicAliasTransactionMapping().apply(input);
        } else if (type == TransactionType.MODIFY_MULTISIG_ACCOUNT.getValue()) {
            return new MultisigModificationTransactionMapping(jsonHelper).apply(input);
        } else if (type == TransactionType.AGGREGATE_COMPLETE.getValue()
            || type == TransactionType.AGGREGATE_BONDED.getValue()) {
            return new AggregateTransactionMapping(jsonHelper).apply(input);
        } else if (type == TransactionType.LOCK.getValue()) {
            return new LockFundsTransactionMapping(jsonHelper).apply(input);
        } else if (type == TransactionType.SECRET_LOCK.getValue()) {
            return new SecretLockTransactionMapping(jsonHelper).apply(input);
        } else if (type == TransactionType.SECRET_PROOF.getValue()) {
            return new SecretProofTransactionMapping(jsonHelper).apply(input);
        } else if (type == TransactionType.ACCOUNT_LINK.getValue()) {
            return new AccountLinkTransactionMapping(jsonHelper).apply(input);
        }

        throw new UnsupportedOperationException("Unimplemented Transaction type " + type);
    }

    protected BigInteger extractBigInteger(List<Long> input) {
        return UInt64.extractBigInteger(input);
    }

    protected BigInteger extractBigInteger(Long input) {
        return BigInteger.valueOf(input.intValue());
    }

    protected MosaicId toMosaicId(List<Long> id) {
        return UInt64.isUInt64(id) ? new MosaicId(extractBigInteger(id)) : null;
    }

    protected Integer extractTransactionVersion(int version) {
        return (int) Long.parseLong(Integer.toHexString(version).substring(2, 4), 16);
    }

    protected NetworkType extractNetworkType(int version) {
        int networkType = (int) Long.parseLong(Integer.toHexString(version).substring(0, 2), 16);
        return NetworkType.rawValueOf(networkType);
    }

    public TransactionInfo createTransactionInfo(TransactionMetaDTO meta) {
        if (meta.getHash() != null && meta.getId() != null) {
            return TransactionInfo.create(
                extractBigInteger(meta.getHeight()),
                meta.getIndex(), meta.getId(), meta.getHash(), meta.getMerkleComponentHash());
        } else if (meta.getAggregateHash() != null && meta.getId() != null) {
            return TransactionInfo.createAggregate(
                extractBigInteger(meta.getHeight()),
                meta.getIndex(),
                meta.getId(),
                meta.getAggregateHash(),
                meta.getAggregateId());
        } else {
            return TransactionInfo.create(
                extractBigInteger(meta.getHeight()),
                meta.getHash(),
                meta.getMerkleComponentHash());
        }
    }

    public JsonHelper getJsonHelper() {
        return jsonHelper;
    }
}

class TransferTransactionMapping extends TransactionMappingOkHttp {

    public TransferTransactionMapping(JsonHelper jsonHelper) {
        super(jsonHelper);
    }

    @Override
    public TransferTransaction apply(TransactionInfoDTO input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getMeta());
        TransferTransactionDTO transaction = getJsonHelper()
            .convert(input.getTransaction(), TransferTransactionDTO.class);

        Deadline deadline = new Deadline(extractBigInteger(transaction.getDeadline()));
        List<Mosaic> mosaics = new ArrayList<>();

        if (transaction.getMosaics() != null) {
            mosaics =
                transaction.getMosaics().stream()
                    .map(
                        mosaic ->
                            new Mosaic(
                                toMosaicId(mosaic.getId()),
                                extractBigInteger(mosaic.getAmount())))
                    .collect(Collectors.toList());
        }

        Message message = PlainMessage.Empty;
        if (transaction.getMessage() != null) {
            message =
                new PlainMessage(
                    new String(
                        Hex.decode(transaction.getMessage().getPayload()),
                        StandardCharsets.UTF_8));
        }

        return new TransferTransaction(
            extractNetworkType(transaction.getVersion()),
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            extractBigInteger(transaction.getMaxFee()),
            Optional.of(Address.createFromEncoded(transaction.getRecipient())),
            Optional.empty(),
            mosaics,
            message,
            transaction.getSignature(),
            new PublicAccount(
                transaction.getSigner(),
                extractNetworkType(transaction.getVersion())),
            transactionInfo);
    }
}

class NamespaceCreationTransactionMapping extends TransactionMappingOkHttp {

    public NamespaceCreationTransactionMapping(JsonHelper jsonHelper) {
        super(jsonHelper);
    }

    @Override
    public RegisterNamespaceTransaction apply(TransactionInfoDTO input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getMeta());
        RegisterNamespaceTransactionDTO transaction = getJsonHelper()
            .convert(input.getTransaction(), RegisterNamespaceTransactionDTO.class);

        Deadline deadline = new Deadline(extractBigInteger(transaction.getDeadline()));
        NamespaceType namespaceType = NamespaceType
            .rawValueOf(transaction.getNamespaceType().getValue());

        return new RegisterNamespaceTransaction(
            extractNetworkType(transaction.getVersion()),
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            extractBigInteger(transaction.getMaxFee()),
            transaction.getName(),
            new NamespaceId(extractBigInteger(transaction.getNamespaceId())),
            namespaceType,
            namespaceType == NamespaceType.RootNamespace
                ? Optional.of(extractBigInteger(transaction.getDuration()))
                : Optional.empty(),
            namespaceType == NamespaceType.SubNamespace
                ? Optional
                .of(new NamespaceId(extractBigInteger(transaction.getParentId())))
                : Optional.empty(),
            transaction.getSignature(),
            new PublicAccount(
                transaction.getSigner(),
                extractNetworkType(transaction.getVersion())),
            transactionInfo);
    }
}

class MosaicCreationTransactionMapping extends TransactionMappingOkHttp {

    public MosaicCreationTransactionMapping(JsonHelper jsonHelper) {
        super(jsonHelper);
    }

    @Override
    public MosaicDefinitionTransaction apply(TransactionInfoDTO input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getMeta());
        MosaicDefinitionTransactionDTO transaction = getJsonHelper()
            .convert(input.getTransaction(), MosaicDefinitionTransactionDTO.class);

        Deadline deadline = new Deadline(extractBigInteger(transaction.getDeadline()));

        List<MosaicPropertyDTO> mosaicProperties = transaction.getProperties();

        String flags =
            "00"
                + Integer.toBinaryString(
                extractBigInteger(mosaicProperties.get(0).getValue())
                    .intValue());
        String bitMapFlags = flags.substring(flags.length() - 2);
        MosaicProperties properties =
            MosaicProperties.create(
                bitMapFlags.charAt(1) == '1',
                bitMapFlags.charAt(0) == '1',
                extractBigInteger(mosaicProperties.get(1).getValue())
                    .intValue(),
                mosaicProperties.size() == 3
                    ? extractBigInteger(mosaicProperties.get(2).getValue())
                    : BigInteger.valueOf(0));

        return new MosaicDefinitionTransaction(
            extractNetworkType(transaction.getVersion()),
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            extractBigInteger(transaction.getMaxFee()),
            MosaicNonce
                .createFromBigInteger(extractBigInteger(transaction.getNonce())),
            toMosaicId(transaction.getMosaicId()),
            properties,
            transaction.getSignature(),
            new PublicAccount(
                transaction.getSigner(),
                extractNetworkType(transaction.getVersion())),
            transactionInfo);
    }
}

class MosaicSupplyChangeTransactionMapping extends TransactionMappingOkHttp {

    public MosaicSupplyChangeTransactionMapping(JsonHelper jsonHelper) {
        super(jsonHelper);
    }

    @Override
    public MosaicSupplyChangeTransaction apply(TransactionInfoDTO input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getMeta());
        MosaicSupplyChangeTransactionDTO transaction = getJsonHelper()
            .convert(input.getTransaction(), MosaicSupplyChangeTransactionDTO.class);

        Deadline deadline = new Deadline(extractBigInteger(transaction.getDeadline()));

        return new MosaicSupplyChangeTransaction(
            extractNetworkType(transaction.getVersion()),
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            extractBigInteger(transaction.getMaxFee()),
            toMosaicId(transaction.getMosaicId()),
            MosaicSupplyType.rawValueOf(transaction.getDirection().getValue()),
            extractBigInteger(transaction.getDelta()),
            transaction.getSignature(),
            new PublicAccount(
                transaction.getSigner(),
                extractNetworkType(transaction.getVersion())),
            transactionInfo);
    }
}

class MultisigModificationTransactionMapping extends TransactionMappingOkHttp {

    public MultisigModificationTransactionMapping(JsonHelper jsonHelper) {
        super(jsonHelper);
    }

    @Override
    public ModifyMultisigAccountTransaction apply(TransactionInfoDTO input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getMeta());
        ModifyMultisigAccountTransactionDTO transaction = getJsonHelper()
            .convert(input.getTransaction(), ModifyMultisigAccountTransactionDTO.class);

        Deadline deadline = new Deadline(extractBigInteger(transaction.getDeadline()));
        NetworkType networkType = extractNetworkType(transaction.getVersion());

        List<MultisigCosignatoryModification> modifications =
            transaction.getModifications() == null ? Collections.emptyList()
                : transaction.getModifications().stream()
                    .map(
                        multisigModification ->
                            new MultisigCosignatoryModification(
                                MultisigCosignatoryModificationType.rawValueOf(
                                    //TODO it was get "type"!!
                                    multisigModification.getModificationType().getValue()),
                                PublicAccount.createFromPublicKey(
                                    multisigModification.getCosignatoryPublicKey(),
                                    networkType)))
                    .collect(Collectors.toList());

        return new ModifyMultisigAccountTransaction(
            networkType,
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            extractBigInteger(transaction.getMaxFee()),
            transaction.getMinApprovalDelta().byteValue(),
            transaction.getMinRemovalDelta().byteValue(),
            modifications,
            transaction.getSignature(),
            new PublicAccount(transaction.getSigner(), networkType),
            transactionInfo);
    }
}

class AggregateTransactionMapping extends TransactionMappingOkHttp {


    public AggregateTransactionMapping(JsonHelper jsonHelper) {
        super(jsonHelper);
    }

    @Override
    public AggregateTransaction apply(TransactionInfoDTO input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getMeta());
        AggregateBondedTransactionDTO transaction = getJsonHelper()
            .convert(input.getTransaction(), AggregateBondedTransactionDTO.class);

        Deadline deadline = new Deadline(extractBigInteger(transaction.getDeadline()));
        NetworkType networkType = extractNetworkType(transaction.getVersion());

        List<Transaction> transactions = transaction.getTransactions().stream()
            .map(embeddedTransactionInfoDTO -> {

//                innerTransaction
//                    .getJsonObject("transaction")
//                    .put("deadline", transaction.getJsonArray("deadline"));
//                innerTransaction
//                    .getJsonObject("transaction")
//                    .put("maxFee", transaction.getJsonArray("maxFee"));
//                innerTransaction
//                    .getJsonObject("transaction")
//                    .put("signature", transaction.getString("signature"));
//
                TransactionInfoDTO transactionInfoDTO = new TransactionInfoDTO();
                transactionInfoDTO.setMeta(getJsonHelper()
                    .convert(embeddedTransactionInfoDTO.getMeta(), TransactionMetaDTO.class));
                transactionInfoDTO.setTransaction(embeddedTransactionInfoDTO.getTransaction());

                Map<String, Object> innerTransaction = (Map<String, Object>) transactionInfoDTO
                    .getTransaction();

                innerTransaction
                    .put("deadline",
                        ((Map<String, Object>) input.getTransaction()).get("deadline"));
                innerTransaction
                    .put("maxFee", ((Map<String, Object>) input.getTransaction()).get("maxFee"));

                innerTransaction.put("signature",
                    getJsonHelper().getString(input.getTransaction(), "signature"));

                if (transactionInfoDTO.getMeta() == null) {
                    transactionInfoDTO.setMeta(input.getMeta());
                }
                return new TransactionMappingOkHttp(getJsonHelper()).apply(transactionInfoDTO);

            }).collect(Collectors.toList());

        List<AggregateTransactionCosignature> cosignatures = new ArrayList<>();
        if (transaction.getCosignatures() != null) {
            cosignatures =
                transaction.getCosignatures().stream()
                    .map(
                        aggregateCosignature ->
                            new AggregateTransactionCosignature(
                                aggregateCosignature.getSignature(),
                                new PublicAccount(aggregateCosignature.getSigner(),
                                    networkType)))
                    .collect(Collectors.toList());
        }

        return new AggregateTransaction(
            networkType,
            TransactionType.rawValueOf(transaction.getType().getValue()),
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            extractBigInteger(transaction.getMaxFee()),
            transactions,
            cosignatures,
            transaction.getSignature(),
            new PublicAccount(transaction.getSigner(), networkType),
            transactionInfo);
    }
}

class LockFundsTransactionMapping extends TransactionMappingOkHttp {

    public LockFundsTransactionMapping(JsonHelper jsonHelper) {
        super(jsonHelper);
    }

    @Override
    public LockFundsTransaction apply(TransactionInfoDTO input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getMeta());
        HashLockTransactionDTO transaction = getJsonHelper()
            .convert(input.getTransaction(), HashLockTransactionDTO.class);

        Deadline deadline = new Deadline(extractBigInteger(transaction.getDeadline()));
        NetworkType networkType = extractNetworkType(transaction.getVersion());
        //TODO getter transaction mosaic attribute.
        Mosaic mosaic = new Mosaic(toMosaicId(transaction.getMosaic().getId()),
            extractBigInteger(transaction.getMosaic().getAmount()));
        return new LockFundsTransaction(
            networkType,
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            extractBigInteger(transaction.getMaxFee()),
            mosaic,
            extractBigInteger(transaction.getDuration()),
            new SignedTransaction("", transaction.getHash(),
                TransactionType.AGGREGATE_BONDED),
            transaction.getSignature(),
            new PublicAccount(transaction.getSigner(), networkType),
            transactionInfo);
    }
}

class SecretLockTransactionMapping extends TransactionMappingOkHttp {

    public SecretLockTransactionMapping(JsonHelper jsonHelper) {
        super(jsonHelper);
    }

    @Override
    public SecretLockTransaction apply(TransactionInfoDTO input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getMeta());
        SecretLockTransactionDTO transaction = getJsonHelper()
            .convert(input.getTransaction(), SecretLockTransactionDTO.class);

        Deadline deadline = new Deadline(extractBigInteger(transaction.getDeadline()));
        NetworkType networkType = extractNetworkType(transaction.getVersion());
        Mosaic mosaic =
            new Mosaic(
                toMosaicId(transaction.getMosaicId()),
                extractBigInteger(transaction.getAmount()));
        return new SecretLockTransaction(
            networkType,
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            extractBigInteger(transaction.getMaxFee()),
            mosaic,
            extractBigInteger(transaction.getDuration()),
            HashType.rawValueOf(transaction.getHashAlgorithm().getValue()),
            transaction.getSecret(),
            Address.createFromEncoded(transaction.getRecipient()),
            transaction.getSignature(),
            new PublicAccount(transaction.getSigner(), networkType),
            transactionInfo);
    }
}

class SecretProofTransactionMapping extends TransactionMappingOkHttp {

    public SecretProofTransactionMapping(JsonHelper jsonHelper) {
        super(jsonHelper);
    }

    @Override
    public SecretProofTransaction apply(TransactionInfoDTO input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getMeta());
        SecretProofTransactionDTO transaction = getJsonHelper()
            .convert(input.getTransaction(), SecretProofTransactionDTO.class);

        Deadline deadline = new Deadline(extractBigInteger(transaction.getDeadline()));
        NetworkType networkType = extractNetworkType(transaction.getVersion());

        return new SecretProofTransaction(
            networkType,
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            extractBigInteger(transaction.getMaxFee()),
            HashType.rawValueOf(transaction.getHashAlgorithm().getValue()),
            Address.createFromEncoded(transaction.getRecipient()),
            transaction.getSecret(),
            transaction.getProof(),
            transaction.getSignature(),
            new PublicAccount(transaction.getSigner(), networkType),
            transactionInfo);
    }
}

class AccountLinkTransactionMapping extends TransactionMappingOkHttp {

    public AccountLinkTransactionMapping(JsonHelper jsonHelper) {
        super(jsonHelper);
    }

    @Override
    public AccountLinkTransaction apply(TransactionInfoDTO input) {
        TransactionInfo transactionInfo = this.createTransactionInfo(input.getMeta());
        AccountLinkTransactionDTO transaction = getJsonHelper().convert(input.getTransaction(),
            AccountLinkTransactionDTO.class);
        Deadline deadline = new Deadline(extractBigInteger(transaction.getDeadline()));
        NetworkType networkType = extractNetworkType(transaction.getVersion());

        return new AccountLinkTransaction(
            networkType,
            extractTransactionVersion(transaction.getVersion()),
            deadline,
            extractBigInteger(transaction.getMaxFee()),
            PublicAccount
                .createFromPublicKey(transaction.getRemoteAccountKey(), networkType),
            AccountLinkAction.rawValueOf(transaction.getLinkAction().getValue()),
            transaction.getSignature(),
            new PublicAccount(transaction.getSigner(), networkType),
            transactionInfo);
    }
}
