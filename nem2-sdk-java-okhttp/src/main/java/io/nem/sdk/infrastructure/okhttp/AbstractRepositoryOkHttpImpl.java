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

import io.nem.core.utils.Suppliers;
import io.nem.sdk.api.QueryParams;
import io.nem.sdk.api.RepositoryCallException;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.model.transaction.UInt64;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiCallback;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by fernando on 30/07/19.
 *
 * @author Fernando Boucquez
 */
public abstract class AbstractRepositoryOkHttpImpl {

    private final ApiClient apiClient;

    private final Supplier<Observable<NetworkType>> networkTypeObservable;

    private final JsonHelper jsonHelper;

    public AbstractRepositoryOkHttpImpl(ApiClient apiClient) {

        this.apiClient = apiClient;
        networkTypeObservable = Suppliers
            .memoize(() -> new NetworkRepositoryOkHttpImpl(apiClient).getNetworkType().cache());

        jsonHelper = new JsonHelperGson(apiClient.getJSON().getGson());

    }


    public <T> Observable<T> call(ApiCall<ApiCallback<T>> callback) {
        Function<? super Throwable, ? extends ObservableSource<? extends T>> resumeFunction = this::onError;
        return new ApiCallbackObservable<T>(callback).toObservable()
            .onErrorResumeNext(resumeFunction);
    }

    public RepositoryCallException exceptionHandling(Throwable e) {
        if (e instanceof RepositoryCallException) {
            return (RepositoryCallException) e;
        }
        return new RepositoryCallException(e.getMessage(), e);
    }

    public <T> Observable<T> onError(Throwable e) {
        return Observable.error(exceptionHandling(e));
    }

    /**
     * Static method to convert from integer list [lower,higher] to BigInteger
     *
     * @param input integer list
     * @return BigInteger
     */
    public static BigInteger extractBigInteger(List<Long> input) {
        return UInt64.extractBigInteger(input);
    }


    protected NetworkType getNetworkTypeBlocking() {
        return networkTypeObservable.get().blockingFirst();
    }

    public <T> Observable<T> exceptionHandling(Observable<T> observable) {
        Function<? super Throwable, ? extends ObservableSource<? extends T>> resumeFunction = this::onError;
        return observable.onErrorResumeNext(resumeFunction);
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    protected BigInteger extractIntArray(List<Long> list) {
        return UInt64.extractBigInteger(list);
    }

    protected Integer getPageSize(Optional<QueryParams> queryParams) {
        return queryParams.map(QueryParams::getPageSize).orElse(null);
    }

    protected String getId(Optional<QueryParams> queryParams) {
        return queryParams.map(QueryParams::getId).orElse(null);
    }

    protected <F, T> java.util.function.Function<List<F>, List<T>> listMap(
        java.util.function.Function<F, T> mapper) {
        return list -> list.stream().map(mapper).collect(Collectors.toList());
    }


    public JsonHelper getJsonHelper() {
        return jsonHelper;
    }
}
