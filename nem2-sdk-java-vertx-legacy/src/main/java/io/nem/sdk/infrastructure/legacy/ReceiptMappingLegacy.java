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
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.AddressAlias;
import io.nem.sdk.model.namespace.MosaicAlias;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.receipt.ArtifactExpiryReceipt;
import io.nem.sdk.model.receipt.BalanceChangeReceipt;
import io.nem.sdk.model.receipt.BalanceTransferReceipt;
import io.nem.sdk.model.receipt.InflationReceipt;
import io.nem.sdk.model.receipt.Receipt;
import io.nem.sdk.model.receipt.ReceiptSource;
import io.nem.sdk.model.receipt.ReceiptType;
import io.nem.sdk.model.receipt.ReceiptVersion;
import io.nem.sdk.model.receipt.ResolutionEntry;
import io.nem.sdk.model.receipt.ResolutionStatement;
import io.nem.sdk.model.receipt.Statement;
import io.nem.sdk.model.receipt.TransactionStatement;
import io.nem.sdk.model.transaction.UInt64;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class ReceiptMappingLegacy {

    public static Statement CreateStatementFromDto(JsonObject input, NetworkType networkType) {
        List<TransactionStatement> transactionStatements =
            input.getJsonArray("transactionStatements").stream()
                .map(o -> (JsonObject) o)
                .map(receiptDto -> CreateTransactionStatement(receiptDto, networkType))
                .collect(Collectors.toList());

        List<ResolutionStatement<Address>> addressResolutionStatements =
            input.getJsonArray("addressResolutionStatements").stream()
                .map(o -> (JsonObject) o)
                .map(
                    receiptDto ->
                        (ResolutionStatement<Address>) CreateResolutionStatementFromDto(receiptDto))
                .collect(Collectors.toList());
        List<ResolutionStatement<MosaicId>> mosaicResolutionStatements =
            input.getJsonArray("mosaicResolutionStatements").stream()
                .map(o -> (JsonObject) o)
                .map(
                    receiptDto ->
                        (ResolutionStatement<MosaicId>) CreateResolutionStatementFromDto(
                            receiptDto))
                .collect(Collectors.toList());
        return new Statement(
            transactionStatements, addressResolutionStatements, mosaicResolutionStatements);
    }

    public static ResolutionStatement CreateResolutionStatementFromDto(JsonObject receiptDto) {
        ReceiptType type = ReceiptType.rawValueOf(receiptDto.getInteger("type").intValue());
        switch (type) {
            case Address_Alias_Resolution:
                return new ResolutionStatement(
                    extractBigInteger(receiptDto.getJsonArray("height")),
                    Address.createFromEncoded(receiptDto.getString("unresolved")),
                    receiptDto.getJsonArray("resolutionEntries").stream()
                        .map(o -> (JsonObject) o)
                        .map(
                            entry ->
                                new ResolutionEntry<AddressAlias>(
                                    new AddressAlias(
                                        Address.createFromEncoded(entry.getString("resolved"))),
                                    new ReceiptSource(
                                        entry.getJsonObject("source").getInteger("primaryId"),
                                        entry.getJsonObject("source").getInteger("secondaryId")),
                                    ReceiptType.Address_Alias_Resolution))
                        .collect(Collectors.toList()));
            case Mosaic_Alias_Resolution:
                return new ResolutionStatement(
                    extractBigInteger(receiptDto.getJsonArray("height")),
                    new MosaicId(receiptDto.getString("unresolved")),
                    receiptDto.getJsonArray("resolutionEntries").stream()
                        .map(o -> (JsonObject) o)
                        .map(
                            entry ->
                                new ResolutionEntry<MosaicAlias>(
                                    new MosaicAlias(new MosaicId(entry.getString("resolved"))),
                                    new ReceiptSource(
                                        entry.getJsonObject("source").getInteger("primaryId"),
                                        entry.getJsonObject("source").getInteger("secondaryId")),
                                    ReceiptType.Mosaic_Alias_Resolution))
                        .collect(Collectors.toList()));
            default:
                throw new IllegalArgumentException("Receipt type: " + type.name() + " not valid");
        }
    }

    public static TransactionStatement CreateTransactionStatement(
        JsonObject input, NetworkType networkType) {
        return new TransactionStatement(
            extractBigInteger(input.getJsonArray("height")),
            new ReceiptSource(
                input.getJsonObject("source").getInteger("primaryId"),
                input.getJsonObject("source").getInteger("secondaryId")),
            input.getJsonArray("receipts").stream()
                .map(o -> (JsonObject) o)
                .map(receipt -> CreateReceiptFromDto(receipt, networkType))
                .collect(Collectors.toList()));
    }

    public static Receipt CreateReceiptFromDto(JsonObject receiptDto, NetworkType networkType) {
        ReceiptType type = ReceiptType.rawValueOf(receiptDto.getInteger("type").intValue());
        switch (type) {
            case Harvest_Fee:
            case LockHash_Created:
            case LockHash_Completed:
            case LockHash_Expired:
            case LockSecret_Created:
            case LockSecret_Completed:
            case LockSecret_Expired:
                return CreateBalanceChangeReceipt(receiptDto, networkType);
            case Mosaic_Levy:
            case Mosaic_Rental_Fee:
            case Namespace_Rental_Fee:
                return CreateBlanaceTransferReceipt(receiptDto, networkType);
            case Mosaic_Expired:
            case Namespace_Expired:
                return CreateArtifactExpiryReceipt(receiptDto);
            case Inflation:
                return CreateInflationReceipt(receiptDto);
            default:
                throw new IllegalArgumentException("Receipt type: " + type.name() + " not valid");
        }
    }

    public static ArtifactExpiryReceipt CreateArtifactExpiryReceipt(JsonObject receipt) {
        ReceiptType type = ReceiptType.rawValueOf(receipt.getInteger("type").intValue());
        if (type == ReceiptType.Mosaic_Expired) {
            return new ArtifactExpiryReceipt(
                new MosaicId(extractBigInteger(receipt.getJsonArray("artifactId"))),
                type,
                ReceiptVersion.ARTIFACT_EXPIRY);
        } else if (type == ReceiptType.Namespace_Expired) {
            return new ArtifactExpiryReceipt(
                new NamespaceId(extractBigInteger(receipt.getJsonArray("artifactId"))),
                type,
                ReceiptVersion.ARTIFACT_EXPIRY);
        } else {
            throw new IllegalArgumentException("Receipt type: " + type.name() + " not valid");
        }
    }

    public static BalanceChangeReceipt CreateBalanceChangeReceipt(
        JsonObject receipt, NetworkType networkType) {
        return new BalanceChangeReceipt(
            PublicAccount.createFromPublicKey(receipt.getString("account"), networkType),
            new MosaicId(extractBigInteger(receipt.getJsonArray("mosaicId"))),
            extractBigInteger(receipt.getJsonArray("amount")),
            ReceiptType.rawValueOf(receipt.getInteger("type")),
            ReceiptVersion.BALANCE_CHANGE);
    }

    public static BalanceTransferReceipt<Address> CreateBlanaceTransferReceipt(
        JsonObject receipt, NetworkType networkType) {
        return new BalanceTransferReceipt(
            PublicAccount.createFromPublicKey(receipt.getString("sender"), networkType),
            Address.createFromEncoded(receipt.getString("recipient")),
            new MosaicId(extractBigInteger(receipt.getJsonArray("mosaicId"))),
            extractBigInteger(receipt.getJsonArray("amount")),
            ReceiptType.rawValueOf(receipt.getInteger("type")),
            ReceiptVersion.BALANCE_TRANSFER);
    }

    public static InflationReceipt CreateInflationReceipt(JsonObject receipt) {
        return new InflationReceipt(
            new MosaicId(extractBigInteger(receipt.getJsonArray("mosaicId"))),
            extractBigInteger(receipt.getJsonArray("amount")),
            ReceiptType.rawValueOf(receipt.getInteger("type")),
            ReceiptVersion.INFLATION_RECEIPT);
    }

    public static BigInteger extractBigInteger(JsonArray input) {
        return UInt64.fromLongArray(
            input.stream().map(Object::toString).map(Long::parseLong).mapToLong(Long::longValue)
                .toArray());
    }

    private static BigInteger extractBigInteger(Long input) {
        return BigInteger.valueOf(input.intValue());
    }

    private static Integer extractTransactionVersion(int version) {
        return (int) Long.parseLong(Integer.toHexString(version).substring(2, 4), 16);
    }
}
