package io.nem.sdk.infrastructure;

import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.infrastructure.vertx.RepositoryFactoryVertxImpl;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.transaction.AccountMetadataTransactionFactory;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Could not test service at core level - need io.nem.sdk.openapi.XXXX.invoker.ApiClient
// Implemented service test in MetadataTransactionServiceXXXXImplTest
public class MetadataTransactionServiceTest {

    private MetadataTransactionServiceImpl service;
    static Account account;

    @BeforeEach
    public void setup() {
        String baseUrl = "https://nem.com:3000/path";

        account =
            new Account(
                "26b64cb10f005e5988a36744ca19e20d835ccc7c105aaa5f3b212da593180930",
                NetworkType.MIJIN_TEST);

        RepositoryFactory factory = new RepositoryFactoryVertxImpl(baseUrl) {
            @Override
            protected NetworkType loadNetworkType() {
                return NetworkType.MIJIN_TEST;
            }
        };
        service = new MetadataTransactionServiceImpl(factory);
    }

    @Test
    public void shouldCreateAccountMetadataTransactionFactory() throws Exception {

        AccountMetadataTransactionFactory expected =
            AccountMetadataTransactionFactory.create(
                NetworkType.MIJIN_TEST,
                account.getPublicAccount(),
                BigInteger.valueOf(10),
                "123BAC");

        AccountMetadataTransactionFactory result =
            service.createAccountMetadataTransactionFactory(
                NetworkType.MIJIN_TEST,
                account.getPublicAccount(),
                BigInteger.valueOf(10),
                "123BAC",
                account.getPublicAccount()).toFuture().get();

        Assertions.assertEquals(expected.getScopedMetadataKey(), result.getScopedMetadataKey());
    }

}
