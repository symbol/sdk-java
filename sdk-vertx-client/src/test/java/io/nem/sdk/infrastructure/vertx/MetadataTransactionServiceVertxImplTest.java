package io.nem.sdk.infrastructure.vertx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.core.utils.ConvertUtils;
import io.nem.core.utils.MapperUtils;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.metadata.Metadata;
import io.nem.sdk.model.transaction.AccountMetadataTransaction;
import io.nem.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.sdk.openapi.vertx.model.AccountMetadataTransactionDTO;
import io.nem.sdk.openapi.vertx.model.MetadataDTO;
import io.nem.sdk.openapi.vertx.model.MetadataEntryDTO;
import io.nem.sdk.openapi.vertx.model.MetadataTypeEnum;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link MetadataTransactionServiceVertxImpl}
 *
 * @author Ravi Shanker
 */
public class MetadataTransactionServiceVertxImplTest extends MetadataRepositoryVertxImplTest {

    private MetadataTransactionServiceVertxImpl service;
    static Account account;

    @BeforeEach
    public void setUp() {
        super.setUp();
        service = new MetadataTransactionServiceVertxImpl(apiClientMock, networkType);
        account =
            new Account(
                "041e2ce90c31cd65620ed16ab7a5a485e5b335d7e61c75cd9b3a2fed3e091728",
                NetworkType.MIJIN_TEST);
    }

    @Test
    public void shouldGetAccountMetadataByKeyAndSender() throws Exception {
        Address address = MapperUtils
            .toAddressFromRawAddress("SBCPGZ3S2SCC3YHBBTYDCUZV4ZZEPHM2KGCP4QXX");

        String targetPublicKey = account.getPublicAccount().getPublicKey().toHex();
        String scopedMetadataKey = BigInteger.TEN.toString(16);
        String value = "123BAC";
        int valueSizeDelta = 10;
        BigInteger maxFee = BigInteger.ZERO;
        AccountMetadataTransactionDTO expected = createMetadataTransactionDto(targetPublicKey, scopedMetadataKey, value, valueSizeDelta, maxFee);
        mockRemoteCall(expected);

        AccountMetadataTransaction transaction = service
            .createAccountMetadataTransaction(NetworkType.MIJIN_TEST,account.getPublicAccount(),BigInteger.TEN,"123BAC","someSender",BigInteger.ZERO)
            .toFuture().get();
        assertEquals(account.getPublicKey(),
            transaction.getTargetAccount().getPublicKey().toHex());

    }

    private AccountMetadataTransactionDTO createMetadataTransactionDto(String targetPublicKey, String scopedMetadataKey, String value, int valueSizeDelta, BigInteger maxFee) {
        AccountMetadataTransactionDTO dto = new AccountMetadataTransactionDTO();

        dto.setTargetPublicKey(targetPublicKey);
        dto.setScopedMetadataKey(scopedMetadataKey);
        dto.setValue("123BAC");
        dto.setValueSizeDelta(valueSizeDelta);
        dto.setMaxFee(maxFee);
        return dto;
    }
}
