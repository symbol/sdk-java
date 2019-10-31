package io.nem.sdk.infrastructure;

import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.api.MetadataRepository;
import io.nem.sdk.api.MetadataTransactionService;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.reactivex.Observable;
import java.math.BigInteger;

/**
 * Implementation of {@link MetadataTransactionService}
 *
 * @author Ravi Shanker
 */
public class MetadataTransactionServiceImpl implements MetadataTransactionService {
    RepositoryFactory repositoryFactory;
    MetadataRepository metadataRepository;

    public MetadataTransactionServiceImpl(RepositoryFactory factory) {
        repositoryFactory = factory;
        metadataRepository = factory.createMetadataRepository();
    }

    @Override
    public Observable<AccountMetadataTransactionFactory> createAccountMetadataTransactionFactory(NetworkType networkType,
        PublicAccount targetPublicAccount, BigInteger key, String value, PublicAccount senderPublicAccount) {

        Observable<AccountMetadataTransactionFactory> observable;
        Address address = Address.createFromPublicKey(targetPublicAccount.getPublicKey().toHex(), networkType);
        String senderPublicKey = senderPublicAccount.getPublicKey().toHex();
        observable = metadataRepository
            .getAccountMetadataByKeyAndSender(address, key, senderPublicKey)
            .map(metadata -> {
                byte[] currentValueBytes = ConvertUtils.getBytes(metadata.getMetadataEntry().getValue());
                byte[] newValueBytes = ConvertUtils.getBytes(value);
                String xorValue = ConvertUtils.toHex(ConvertUtils.xor(currentValueBytes, newValueBytes));
                return AccountMetadataTransactionFactory
                    .create(networkType, targetPublicAccount, key, xorValue);
            });
        return observable;
    }

    @Override
    public Observable<MosaicMetadataTransactionFactory> createMosaicMetadataTransactionFactory(NetworkType networkType,
        PublicAccount targetPublicAccount, BigInteger key, String value, PublicAccount senderPublicAccount,
        MosaicId targetId) {

        Observable<MosaicMetadataTransactionFactory> observable;
        String senderPublicKey = senderPublicAccount.getPublicKey().toHex();
        observable = metadataRepository
            .getMosaicMetadataByKeyAndSender(targetId, key, senderPublicKey)
            .map(metadata -> {
                byte[] currentValueBytes = ConvertUtils.getBytes(metadata.getMetadataEntry().getValue());
                byte[] newValueBytes = ConvertUtils.getBytes(value);
                String xorValue = ConvertUtils.toHex(ConvertUtils.xor(currentValueBytes, newValueBytes));
                return MosaicMetadataTransactionFactory
                    .create(networkType, targetPublicAccount, targetId, key, xorValue);
            });
        return observable;
    }

    @Override
    public Observable<NamespaceMetadataTransactionFactory> createNamespaceMetadataTransactionFactory(NetworkType networkType,
        PublicAccount targetPublicAccount, BigInteger key, String value, PublicAccount senderPublicAccount,
        NamespaceId targetId) {

        Observable<NamespaceMetadataTransactionFactory> observable;
        String senderPublicKey = senderPublicAccount.getPublicKey().toHex();
        observable = metadataRepository
            .getNamespaceMetadataByKeyAndSender(targetId, key, senderPublicKey)
            .map(metadata -> {
                byte[] currentValueBytes = ConvertUtils.getBytes(metadata.getMetadataEntry().getValue());
                byte[] newValueBytes = ConvertUtils.getBytes(value);
                String xorValue = ConvertUtils.toHex(ConvertUtils.xor(currentValueBytes, newValueBytes));
                return NamespaceMetadataTransactionFactory
                    .create(networkType, targetPublicAccount, targetId, key, xorValue);
            });
        return observable;
    }
}
