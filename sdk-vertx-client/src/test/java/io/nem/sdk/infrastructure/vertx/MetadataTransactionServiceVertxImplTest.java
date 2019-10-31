package io.nem.sdk.infrastructure.vertx;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.infrastructure.MetadataTransactionServiceImpl;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.sdk.model.transaction.JsonHelper;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.nem.sdk.openapi.vertx.invoker.Pair;
import io.nem.sdk.openapi.vertx.model.MetadataDTO;
import io.nem.sdk.openapi.vertx.model.MetadataEntryDTO;
import io.nem.sdk.openapi.vertx.model.MetadataTypeEnum;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

public class MetadataTransactionServiceVertxImplTest {

    private MetadataTransactionServiceImpl service;
    static Account account;
    protected ApiClient apiClientMock;
    protected JsonHelper jsonHelper;
    //protected Supplier<NetworkType> networkType = () -> NetworkType.MIJIN_TEST;
    protected NetworkType networkType = NetworkType.MIJIN_TEST;

    @BeforeEach
    public void setup() {

        apiClientMock = Mockito.mock(ApiClient.class);
        ObjectMapper objectMapper = JsonHelperJackson2.configureMapper(new ObjectMapper());
        jsonHelper = new JsonHelperJackson2(objectMapper);
        Mockito.when(apiClientMock.getObjectMapper()).thenReturn(objectMapper);

        String baseUrl = "https://nem.com:3000/path";

        account =
            new Account(
                "26b64cb10f005e5988a36744ca19e20d835ccc7c105aaa5f3b212da593180930",
                networkType);

        RepositoryFactory factory = new RepositoryFactoryVertxImpl(baseUrl) {
            @Override
            protected NetworkType loadNetworkType() {
                return networkType;
            }
        };

        service = new MetadataTransactionServiceImpl(factory);
    }

    @Test
    public void shouldCreateAccountMetadataTransactionFactory() throws Exception {

        AccountMetadataTransactionFactory factory =
            AccountMetadataTransactionFactory.create(
                networkType,
                account.getPublicAccount(),
                BigInteger.valueOf(10),
                "123BAC");

        Address address = Address.createFromPublicKey(account.getPublicKey(), networkType);
        String senderPublicKey = account.getPublicKey();

        MetadataDTO expected = createMetadataDto(senderPublicKey,
            MetadataTypeEnum.NUMBER_0, "10");

        mockRemoteCall(expected);
        mockRemoteCall(factory);

        AccountMetadataTransactionFactory result =
            service.createAccountMetadataTransactionFactory(
                networkType,
                account.getPublicAccount(),
                BigInteger.valueOf(10),
                "123BAC",
                account.getPublicAccount().getPublicKey()).toFuture().get();

        Assertions.assertEquals(factory.getScopedMetadataKey(), result.getScopedMetadataKey());
    }

    /**
     * Mocks the api client telling what would it be the next response when any remote call is
     * executed.
     *
     * @param value the next mocked remote call response
     * @param <T> tye type of the remote response.
     */
    protected <T> void mockRemoteCall(T value) {
        Mockito.doAnswer((Answer<Void>) invocationOnMock -> {

            Handler<AsyncResult<T>> resultHandler = (Handler<AsyncResult<T>>) invocationOnMock
                .getArguments()[invocationOnMock.getArguments().length - 1];
            resultHandler.handle(Future.succeededFuture(value));

            return null;
        }).when(apiClientMock)
            .invokeAPI(Mockito.anyString(), Mockito.anyString(), Mockito.anyListOf(Pair.class),
                Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any());
    }

    protected MetadataDTO createMetadataDto(String name,
        MetadataTypeEnum type, String targetId) {
        MetadataDTO dto = new MetadataDTO();
        dto.setId(name);
        MetadataEntryDTO metadataEntry = new MetadataEntryDTO();
        metadataEntry.setCompositeHash("compositeHash " + name);
        metadataEntry.setMetadataType(type);
        metadataEntry.setScopedMetadataKey("10");
        metadataEntry.setSenderPublicKey("senderPublicKey " + name);
        metadataEntry.setTargetId(targetId);
        metadataEntry.setTargetPublicKey("targetPublicKey " + name);
        metadataEntry.setValue(ConvertUtils.fromStringToHex(name + " message"));
        dto.setMetadataEntry(metadataEntry);
        return dto;
    }

}
