package io.nem.sdk.infrastructure.vertx;

import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.api.MetadataTransactionService;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.AccountMetadataTransaction;
import io.nem.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.sdk.model.transaction.NamespaceMetadataTransaction;
import io.nem.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.nem.sdk.openapi.vertx.invoker.ApiClient;
import io.reactivex.Observable;
import java.math.BigInteger;
import java.util.function.Supplier;

/**
 * Implementation of {@link MetadataTransactionService}
 *
 * @author Ravi Shanker
 */
public class MetadataTransactionServiceVertxImpl extends MetadataRepositoryVertxImpl
    implements MetadataTransactionService {

    public MetadataTransactionServiceVertxImpl(ApiClient apiClient,
        Supplier<NetworkType> networkType) {
        super(apiClient, networkType);
    }

    @Override
    public Observable<AccountMetadataTransaction> createAccountMetadataTransaction(NetworkType networkType,
        PublicAccount targetPublicAccount, BigInteger key, String value, String senderPublicKey,
        BigInteger maxFee) {

        return this.getAccountMetadataByKeyAndSender(Address.createFromPublicKey(targetPublicAccount.getPublicKey().toHex(), networkType), key, senderPublicKey)
            .map(metadata -> {
                byte[] currentValueBytes = ConvertUtils.getBytes(metadata.getMetadataEntry().getValue());
                byte[] newValueBytes = ConvertUtils.getBytes(value);
                String xorValue = ConvertUtils.toHex(ConvertUtils.xor(currentValueBytes, newValueBytes));
                return AccountMetadataTransactionFactory
                    .create(networkType, targetPublicAccount, key, xorValue)
                    .build();
            });
    }

    @Override
    public Observable<MosaicMetadataTransaction> createMosaicMetadataTransaction(NetworkType networkType,
        PublicAccount targetPublicAccount, BigInteger key, String value, String senderPublicKey,
        MosaicId targetId, BigInteger maxFee) {

        return this.getMosaicMetadataByKeyAndSender(targetId, key, senderPublicKey)
            .map(metadata -> {
                byte[] currentValueBytes = ConvertUtils.getBytes(metadata.getMetadataEntry().getValue());
                byte[] newValueBytes = ConvertUtils.getBytes(value);
                String xorValue = ConvertUtils.toHex(ConvertUtils.xor(currentValueBytes, newValueBytes));
                return MosaicMetadataTransactionFactory
                    .create(networkType, targetPublicAccount, targetId, key, xorValue)
                    .build();
            });
    }

    @Override
    public Observable<NamespaceMetadataTransaction> createNamespaceMetadataTransaction(NetworkType networkType,
        PublicAccount targetPublicAccount, BigInteger key, String value, String senderPublicKey,
        NamespaceId targetId, BigInteger maxFee) {

        return this.getNamespaceMetadataByKeyAndSender(targetId, key, senderPublicKey)
            .map(metadata -> {
                byte[] currentValueBytes = ConvertUtils.getBytes(metadata.getMetadataEntry().getValue());
                byte[] newValueBytes = ConvertUtils.getBytes(value);
                String xorValue = ConvertUtils.toHex(ConvertUtils.xor(currentValueBytes, newValueBytes));
                return NamespaceMetadataTransactionFactory
                    .create(networkType, targetPublicAccount, targetId, key, xorValue)
                    .build();
            });
    }
}
