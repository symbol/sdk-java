/*
 * Copyright 2020 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.nem.symbol.sdk.infrastructure.vertx;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nem.symbol.core.utils.HttpStatus;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.vertx.invoker.ApiException;
import io.nem.symbol.sdk.openapi.vertx.invoker.Pair;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.impl.headers.VertxHttpHeaders;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * Abstract class for all the unit tests related to OkHttp repositories.
 *
 * @author Fernando Boucquez
 */
public abstract class AbstractVertxRespositoryTest {

  protected ApiClient apiClientMock;

  protected JsonHelper jsonHelper;

  protected final NetworkType networkType = NetworkType.TEST_NET;

  protected final Observable<NetworkType> networkTypeObservable = Observable.just(networkType);

  @BeforeEach
  public void setUp() {
    apiClientMock = Mockito.mock(ApiClient.class);
    ObjectMapper objectMapper = JsonHelperJackson2.configureMapper(new ObjectMapper());
    jsonHelper = new JsonHelperJackson2(objectMapper);
    Mockito.when(apiClientMock.getObjectMapper()).thenReturn(objectMapper);
  }

  protected String encodeAddress(Address address) {
    return address.encoded();
  }

  /**
   * Mocks the api client telling what would it be the next response when any remote call is
   * executed.
   *
   * @param <T> tye type of the remote response.
   * @param value the next mocked remote call response
   */
  protected <T> ArgumentCaptor<Object> mockRemoteCall(T value) {
    ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
    Mockito.doAnswer(
            (Answer<Object>)
                invocationOnMock -> {
                  Handler<AsyncResult<T>> resultHandler =
                      (Handler<AsyncResult<T>>)
                          invocationOnMock
                              .getArguments()[invocationOnMock.getArguments().length - 1];
                  resultHandler.handle(Future.succeededFuture(value));

                  Object params = invocationOnMock.getArguments()[3];
                  return params;
                })
        .when(apiClientMock)
        .invokeAPI(
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyListOf(Pair.class),
            argument.capture(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any());
    return argument;
  }

  /**
   * Mocks the api client telling that the next time there is remote call, an error should be
   * returned. The mocked response body is the expected json from the catapult rest error handler.
   *
   * @param statusCode the status code of the response (404 for example)
   * @param message the error message that will be returned in the body.
   */
  protected void mockErrorCode(final int statusCode, final String message) {
    Map<String, String> errorBody = new HashMap<>();

    String reasonPhrase = HttpStatus.valueOf(statusCode).getReasonPhrase();
    errorBody.put("code", "Code " + reasonPhrase);
    errorBody.put("message", message);
    String errorResponse = jsonHelper.print(errorBody);
    mockErrorCodeRawResponse(statusCode, errorResponse);
  }

  /**
   * Mocks the api client telling that the next time there is remote call, an error should be
   * returned.
   *
   * @param statusCode the status code of the response (404 for example)
   * @param errorResponse the raw response, it may or may not be a json string.
   */
  protected void mockErrorCodeRawResponse(int statusCode, String errorResponse) {
    String reasonPhrase = HttpStatus.valueOf(statusCode).getReasonPhrase();
    VertxHttpHeaders headers = new VertxHttpHeaders();
    ApiException exception = new ApiException(reasonPhrase, statusCode, headers, errorResponse);

    Mockito.doAnswer(
            (Answer<Void>)
                invocationOnMock -> {
                  Handler<AsyncResult<Object>> resultHandler =
                      (Handler<AsyncResult<Object>>)
                          invocationOnMock
                              .getArguments()[invocationOnMock.getArguments().length - 1];
                  resultHandler.handle(Future.failedFuture(exception));

                  return null;
                })
        .when(apiClientMock)
        .invokeAPI(
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyList(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any(),
            Mockito.any());
  }

  protected Mosaic createAbsolute(BigInteger amount) {
    return new Mosaic(NamespaceId.createFromName("xem.currency"), amount);
  }
}
