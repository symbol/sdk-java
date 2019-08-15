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

import io.nem.sdk.openapi.okhttp_gson.invoker.ApiCallback;
import io.nem.sdk.openapi.okhttp_gson.invoker.ApiException;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by fernando on 03/08/19.
 *
 * @author Fernando Boucquez
 */
public class ApiCallbackObservable<T> extends Single<T> {

    private final ApiCall<ApiCallback<T>> call;

    public ApiCallbackObservable(ApiCall<ApiCallback<T>> call) {
        this.call = call;
    }

    @Override
    protected void subscribeActual(SingleObserver<? super T> observer) {

        AtomicBoolean disposed = new AtomicBoolean();
        observer.onSubscribe(new Disposable() {
            @Override
            public void dispose() {
                disposed.set(true);
            }

            @Override
            public boolean isDisposed() {
                return disposed.get();
            }
        });
        if (!disposed.get()) {

            try {
                call.accept(new ApiCallback<T>() {
                    @Override
                    public void onFailure(ApiException e, int statusCode,
                        Map<String, List<String>> responseHeaders) {
                        if (!disposed.getAndSet(true)) {
                            observer.onError(e);
                        }
                    }

                    @Override
                    public void onSuccess(T result, int statusCode,
                        Map<String, List<String>> responseHeaders) {
                        if (!disposed.getAndSet(true)) {
                            observer.onSuccess(result);
                        }
                    }

                    @Override
                    public void onUploadProgress(long bytesWritten, long contentLength,
                        boolean done) {

                    }

                    @Override
                    public void onDownloadProgress(long bytesRead, long contentLength,
                        boolean done) {

                    }
                });
            } catch (ApiException e) {
                observer.onError(e);
            }
        }
    }


}
