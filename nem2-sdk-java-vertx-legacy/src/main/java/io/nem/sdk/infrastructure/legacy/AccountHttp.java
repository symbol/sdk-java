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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.nem.sdk.api.AccountRepository;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.AccountType;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.MultisigAccountGraphInfo;
import io.nem.sdk.model.account.MultisigAccountInfo;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.openapi.vertx.model.AccountDTO;
import io.nem.sdk.openapi.vertx.model.AccountIds;
import io.nem.sdk.openapi.vertx.model.AccountInfoDTO;
import io.nem.sdk.openapi.vertx.model.MultisigAccountGraphInfoDTO;
import io.nem.sdk.openapi.vertx.model.MultisigAccountInfoDTO;
import io.nem.sdk.openapi.vertx.model.MultisigDTO;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

/**
 * Account http repository.
 *
 * @since 1.0
 */
public class AccountHttp extends Http implements AccountRepository {

    public AccountHttp(String host) {
        this(host, new NetworkHttp(host));
    }

    public AccountHttp(String host, NetworkHttp networkHttp) {
        super(host + "/account/", networkHttp);
    }

    public String getAddressEncoded(String address) throws DecoderException {
        return new String(new Base32().encode(Hex.decodeHex(address)));
    }

    @Override
    public Observable<AccountInfo> getAccountInfo(Address address) {
        return this.client
            .getAbs(this.url + address.plain())
            .as(BodyCodec.jsonObject())
            .rxSend()
            .toObservable()
            .map(Http::mapJsonObjectOrError)
            .map(json -> objectMapper.readValue(json.toString(), AccountInfoDTO.class))
            .map(AccountInfoDTO::getAccount)
            .map(this::toAccountInfo);
    }

    @Override
    public Observable<List<AccountInfo>> getAccountsInfo(List<Address> addresses) {
//        JsonObject requestBody = new JsonObject();
//        requestBody.put(
//            "addresses",
//            addresses.stream().map(Address::plain).collect(Collectors.toList()));

        AccountIds accountIds = new AccountIds()
            .addresses(addresses.stream().map(Address::plain).collect(Collectors.toList()));

        Json.mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
//
//        AccountRoutesApi accountRoutesApi = new AccountRoutesApi();
//
//
//
//        accountRoutesApi.getAccountsInfo(accountIds);

        Observable<NetworkType> networkTypeResolve = getNetworkTypeObservable();
        return networkTypeResolve.flatMap(
            networkType ->
                this.client
                    .postAbs(this.url.toString())
                    .as(BodyCodec.jsonArray())
                    .rxSendJson(accountIds)
                    .toObservable()
                    .map(Http::mapJsonArrayOrError)
                    .map(
                        json ->
                            objectMapper.<List<AccountInfoDTO>>readValue(
                                json.toString(), new TypeReference<List<AccountInfoDTO>>() {
                                }))
                    .flatMapIterable(item -> item)
                    .map(AccountInfoDTO::getAccount)
                    .map(this::toAccountInfo)
                    .toList()
                    .toObservable());
    }

    private AccountInfo toAccountInfo(AccountDTO accountDTO) throws DecoderException {
        return new AccountInfo(
            Address.createFromRawAddress(
                getAddressEncoded(accountDTO.getAddress())),
            extractBigInteger(accountDTO.getAddressHeight()),
            accountDTO.getPublicKey(),
            extractBigInteger(accountDTO.getPublicKeyHeight()),
            extractBigInteger(accountDTO.getImportance()),
            extractBigInteger(accountDTO.getImportanceHeight()),
            accountDTO.getMosaics().stream()
                .map(
                    mosaicDTO ->
                        new Mosaic(
                            new MosaicId(extractBigInteger(mosaicDTO.getId())),
                            extractBigInteger(mosaicDTO.getAmount())))
                .collect(Collectors.toList()),
            AccountType.rawValueOf(accountDTO.getAccountType().getValue()));
    }

    @Override
    public Observable<MultisigAccountInfo> getMultisigAccountInfo(Address address) {
        Observable<NetworkType> networkTypeResolve = getNetworkTypeObservable();
        return networkTypeResolve.flatMap(
            networkType ->
                this.client
                    .getAbs(this.url + address.plain() + "/multisig")
                    .as(BodyCodec.jsonObject())
                    .rxSend()
                    .toObservable()
                    .map(Http::mapJsonObjectOrError)
                    .map(json -> objectMapper
                        .readValue(json.toString(), MultisigAccountInfoDTO.class))
                    .map(MultisigAccountInfoDTO::getMultisig)
                    .map(this.transfromMultisigAccountInfoDTO(networkType)));
    }

    @Override
    public Observable<MultisigAccountGraphInfo> getMultisigAccountGraphInfo(Address address) {
        Observable<NetworkType> networkTypeResolve = getNetworkTypeObservable();
        return networkTypeResolve.flatMap(
            networkType ->
                this.client
                    .getAbs(this.url + address.plain() + "/multisig/graph")
                    .as(BodyCodec.jsonArray())
                    .rxSend()
                    .toObservable()
                    .map(Http::mapJsonArrayOrError)
                    .map(
                        json ->
                            objectMapper.<List<MultisigAccountGraphInfoDTO>>readValue(
                                json.toString(),
                                new TypeReference<List<MultisigAccountGraphInfoDTO>>() {
                                }))
                    .map(
                        multisigAccountGraphInfoDTOList -> {
                            Map<Integer, List<MultisigAccountInfo>> multisigAccountInfoMap =
                                new HashMap<>();
                            multisigAccountGraphInfoDTOList.forEach(
                                item ->
                                    multisigAccountInfoMap.put(
                                        item.getLevel(),
                                        item.getMultisigEntries().stream()
                                            .map(MultisigAccountInfoDTO::getMultisig)
                                            .map(
                                                item2 ->
                                                    new MultisigAccountInfo(
                                                        new PublicAccount(
                                                            item2.getAccount(), networkType),
                                                        item2.getMinApproval(),
                                                        item2.getMinRemoval(),
                                                        item2.getCosignatories().stream()
                                                            .map(
                                                                cosigner ->
                                                                    new PublicAccount(
                                                                        cosigner, networkType))
                                                            .collect(Collectors.toList()),
                                                        item2.getMultisigAccounts().stream()
                                                            .map(
                                                                multisigAccount ->
                                                                    new PublicAccount(
                                                                        multisigAccount,
                                                                        networkType))
                                                            .collect(Collectors.toList())))
                                            .collect(Collectors.toList())));
                            return new MultisigAccountGraphInfo(multisigAccountInfoMap);
                        }));
    }

    @Override
    public Observable<List<Transaction>> transactions(PublicAccount publicAccount) {
        return this.transactions(publicAccount, Optional.empty());
    }

    @Override
    public Observable<List<Transaction>> transactions(
        PublicAccount publicAccount, QueryParams queryParams) {
        return this.transactions(publicAccount, Optional.of(queryParams));
    }

    private Observable<List<Transaction>> transactions(
        PublicAccount publicAccount, Optional<QueryParams> queryParams) {
        return this.findTransactions(publicAccount, queryParams, "/transactions");
    }

    @Override
    public Observable<List<Transaction>> incomingTransactions(PublicAccount publicAccount) {
        return this.incomingTransactions(publicAccount, Optional.empty());
    }

    @Override
    public Observable<List<Transaction>> incomingTransactions(
        PublicAccount publicAccount, QueryParams queryParams) {
        return this.incomingTransactions(publicAccount, Optional.of(queryParams));
    }

    private Observable<List<Transaction>> incomingTransactions(
        PublicAccount publicAccount, Optional<QueryParams> queryParams) {
        return this.findTransactions(publicAccount, queryParams, "/transactions/incoming");
    }

    @Override
    public Observable<List<Transaction>> outgoingTransactions(PublicAccount publicAccount) {
        return this.outgoingTransactions(publicAccount, Optional.empty());
    }

    @Override
    public Observable<List<Transaction>> outgoingTransactions(
        PublicAccount publicAccount, QueryParams queryParams) {
        return this.outgoingTransactions(publicAccount, Optional.of(queryParams));
    }

    private Observable<List<Transaction>> outgoingTransactions(
        PublicAccount publicAccount, Optional<QueryParams> queryParams) {
        return this.findTransactions(publicAccount, queryParams, "/transactions/outgoing");
    }

    @Override
    public Observable<List<AggregateTransaction>> aggregateBondedTransactions(
        PublicAccount publicAccount) {
        return this.aggregateBondedTransactions(publicAccount, Optional.empty());
    }

    @Override
    public Observable<List<AggregateTransaction>> aggregateBondedTransactions(
        PublicAccount publicAccount, QueryParams queryParams) {
        return this.aggregateBondedTransactions(publicAccount, Optional.of(queryParams));
    }

    private Observable<List<AggregateTransaction>> aggregateBondedTransactions(
        PublicAccount publicAccount, Optional<QueryParams> queryParams) {
        return this.findTransactions(publicAccount, queryParams, "/transactions/partial")
            .flatMapIterable(item -> item)
            .map(item -> (AggregateTransaction) item)
            .toList()
            .toObservable();
    }

    @Override
    public Observable<List<Transaction>> unconfirmedTransactions(PublicAccount publicAccount) {
        return this.unconfirmedTransactions(publicAccount, Optional.empty());
    }

    @Override
    public Observable<List<Transaction>> unconfirmedTransactions(
        PublicAccount publicAccount, QueryParams queryParams) {
        return this.unconfirmedTransactions(publicAccount, Optional.of(queryParams));
    }

    private Observable<List<Transaction>> unconfirmedTransactions(
        PublicAccount publicAccount, Optional<QueryParams> queryParams) {
        return this.findTransactions(publicAccount, queryParams, "/transactions/unconfirmed");
    }

    private Observable<List<Transaction>> findTransactions(
        PublicAccount publicAccount, Optional<QueryParams> queryParams, String path) {
        return this.client
            .getAbs(
                this.url
                    + publicAccount.getPublicKey().toString()
                    + path
                    + (queryParams.isPresent() ? queryParams.get().toUrl() : ""))
            .as(BodyCodec.jsonArray())
            .rxSend()
            .toObservable()
            .map(Http::mapJsonArrayOrError)
            .map(
                json ->
                    new JsonArray(json.toString())
                        .stream().map(s -> (JsonObject) s).collect(Collectors.toList()))
            .flatMapIterable(item -> item)
            .map(new TransactionMappingLegacy())
            .toList()
            .toObservable();
    }

    private Function<MultisigDTO, MultisigAccountInfo> transfromMultisigAccountInfoDTO(
        NetworkType networkType) {
        return multisig ->
            new MultisigAccountInfo(
                new PublicAccount(multisig.getAccount(), networkType),
                multisig.getMinApproval(),
                multisig.getMinRemoval(),
                multisig.getCosignatories().stream()
                    .map(cosigner -> new PublicAccount(cosigner, networkType))
                    .collect(Collectors.toList()),
                multisig.getMultisigAccounts().stream()
                    .map(multisigAccount -> new PublicAccount(multisigAccount, networkType))
                    .collect(Collectors.toList()));
    }
}
