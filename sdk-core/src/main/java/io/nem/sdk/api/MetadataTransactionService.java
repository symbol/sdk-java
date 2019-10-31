package io.nem.sdk.api;

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
import io.reactivex.Observable;
import java.math.BigInteger;

/**
 * Metadata transaction service.
 *
 * @author Ravi Shanker
 */
public interface MetadataTransactionService {

    /**
     *
     * @param networkType
     * @param targetPublicAccount
     * @param key
     * @param value
     * @param senderPublicAccount
     * @return
     */
    Observable<AccountMetadataTransactionFactory> createAccountMetadataTransactionFactory(
        NetworkType networkType,
        PublicAccount targetPublicAccount,
        BigInteger key,
        String value,
        PublicAccount senderPublicAccount);

    Observable<MosaicMetadataTransactionFactory> createMosaicMetadataTransactionFactory(
        NetworkType networkType,
        PublicAccount targetPublicAccount,
        BigInteger key,
        String value,
        PublicAccount senderPublicAccount,
        MosaicId targetId);

    Observable<NamespaceMetadataTransactionFactory> createNamespaceMetadataTransactionFactory(
        NetworkType networkType,
        PublicAccount targetPublicAccount,
        BigInteger key,
        String value,
        PublicAccount senderPublicAccount,
        NamespaceId targetId);
}
