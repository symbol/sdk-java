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

import io.nem.sdk.api.AccountRepository;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.model.account.AccountInfo;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.MultisigAccountGraphInfo;
import io.nem.sdk.model.account.MultisigAccountInfo;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.openapi.vertx.api.AccountRoutesApi;
import io.nem.sdk.openapi.vertx.api.AccountRoutesApiImpl;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.sdk.openapi.vertx.model.AccountDTO;
import io.nem.sdk.openapi.vertx.model.AccountIds;
import io.nem.sdk.openapi.vertx.model.AccountInfoDTO;
import io.nem.sdk.openapi.vertx.model.MultisigAccountGraphInfoDTO;
import io.nem.sdk.openapi.vertx.model.MultisigAccountInfoDTO;
import io.nem.sdk.openapi.vertx.model.MultisigDTO;
import io.nem.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

/**
 * Created by fernando on 29/07/19.
 *
 * @author Fernando Boucquez
 */
public class AccountRepositoryVertxImpl extends AbstractRepositoryVertxImpl implements
    AccountRepository {


    private final AccountRoutesApi client;

    public AccountRepositoryVertxImpl(ApiClient apiClient,
        Supplier<NetworkType> networkType) {
        super(apiClient, networkType);
        this.client = new AccountRoutesApiImpl(apiClient);
    }

    private String getAddressEncoded(String address) throws DecoderException {
        return new String(new Base32().encode(Hex.decodeHex(address)));
    }

    @Override
    public Observable<AccountInfo> getAccountInfo(Address address) {

        Consumer<Handler<AsyncResult<AccountInfoDTO>>> callback = handler -> getClient()
            .getAccountInfo(address.plain(), handler);
        return exceptionHandling(
            call(callback).map(AccountInfoDTO::getAccount).map(this::toAccountInfo));
    }

    @Override
    public Observable<List<AccountInfo>> getAccountsInfo(List<Address> addresses) {
        AccountIds accountIds = new AccountIds()
            .addresses(addresses.stream().map(Address::plain).collect(Collectors.toList()));
        Consumer<Handler<AsyncResult<List<AccountInfoDTO>>>> callback = handler -> getClient()
            .getAccountsInfo(accountIds, handler);
        return exceptionHandling(
            call(callback).flatMapIterable(item -> item)
                .map(AccountInfoDTO::getAccount)
                .map(this::toAccountInfo).toList().toObservable());
    }

    @Override
    public Observable<MultisigAccountInfo> getMultisigAccountInfo(Address address) {
        return exceptionHandling(call(
            (Handler<AsyncResult<MultisigAccountInfoDTO>> handler) -> getClient()
                .getAccountMultisig(address.plain(), handler))
            .map(MultisigAccountInfoDTO::getMultisig)
            .map(this::toMultisigAccountInfo));

    }


    @Override
    public Observable<MultisigAccountGraphInfo> getMultisigAccountGraphInfo(Address address) {

        return exceptionHandling(call(
            (Handler<AsyncResult<List<MultisigAccountGraphInfoDTO>>> handler) -> getClient()
                .getAccountMultisigGraph(address.plain(), handler))
            .map(
                multisigAccountGraphInfoDTOList -> {
                    Map<Integer, List<MultisigAccountInfo>> multisigAccountInfoMap =
                        new HashMap<>();
                    multisigAccountGraphInfoDTOList.forEach(
                        item ->
                            multisigAccountInfoMap.put(
                                item.getLevel(),
                                toMultisigAccountInfo(item)));
                    return new MultisigAccountGraphInfo(multisigAccountInfoMap);
                }));
    }

    private List<MultisigAccountInfo> toMultisigAccountInfo(MultisigAccountGraphInfoDTO item) {
        return item.getMultisigEntries().stream()
            .map(MultisigAccountInfoDTO::getMultisig)
            .map(this::toMultisigAccountInfo)
            .collect(Collectors.toList());
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

        Consumer<Handler<AsyncResult<List<TransactionInfoDTO>>>> callback = (handler) ->
            client.transactions(publicAccount.getPublicKey().toString(),
                getPageSize(queryParams),
                getId(queryParams),
                null,
                handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction).toList()
                .toObservable());
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

        Consumer<Handler<AsyncResult<List<TransactionInfoDTO>>>> callback = (handler) ->
            client.incomingTransactions(publicAccount.getPublicKey().toString(),
                getPageSize(queryParams),
                getId(queryParams),
                null,
                handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction).toList()
                .toObservable());
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

        Consumer<Handler<AsyncResult<List<TransactionInfoDTO>>>> callback = (handler) ->
            client.outgoingTransactions(publicAccount.getPublicKey().toString(),
                getPageSize(queryParams),
                getId(queryParams),
                null,
                handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction).toList()
                .toObservable());
    }

    private Transaction toTransaction(TransactionInfoDTO input) {
        return new TransactionMappingVertx(getJsonHelper()).apply(input);
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

        Consumer<Handler<AsyncResult<List<TransactionInfoDTO>>>> callback = (handler) ->
            client.partialTransactions(publicAccount.getPublicKey().toString(),
                getPageSize(queryParams),
                getId(queryParams),
                null,
                handler);

        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction)
                .map(o -> (AggregateTransaction) o).toList()
                .toObservable());
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
        Consumer<Handler<AsyncResult<List<TransactionInfoDTO>>>> callback = (handler) ->
            client.unconfirmedTransactions(publicAccount.getPublicKey().toString(),
                getPageSize(queryParams),
                getId(queryParams),
                null,
                handler);
        return exceptionHandling(
            call(callback).flatMapIterable(item -> item).map(this::toTransaction).toList()
                .toObservable());
    }


    private AccountInfo toAccountInfo(AccountDTO accountDTO) throws DecoderException {
        return new AccountInfo(
            Address.createFromRawAddress(getAddressEncoded(accountDTO.getAddress())),
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
                .collect(Collectors.toList()));
    }

    private MultisigAccountInfo toMultisigAccountInfo(MultisigDTO dto) {
        NetworkType networkType = getNetworkTypeBlocking();
        return new MultisigAccountInfo(
            new PublicAccount(
                dto.getAccount(), networkType),
            dto.getMinApproval(),
            dto.getMinRemoval(),
            dto.getCosignatories().stream()
                .map(
                    cosigner ->
                        new PublicAccount(
                            cosigner, networkType))
                .collect(Collectors.toList()),
            dto.getMultisigAccounts().stream()
                .map(
                    multisigAccount ->
                        new PublicAccount(
                            multisigAccount,
                            networkType))
                .collect(Collectors.toList()));
    }


    private AccountRoutesApi getClient() {
        return client;
    }
}
