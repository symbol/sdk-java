package io.nem.sdk.api;

import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.namespace.NamespaceId;
import io.nem.sdk.model.transaction.AccountMetadataTransaction;
import io.nem.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.sdk.model.transaction.NamespaceMetadataTransaction;
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
     * @param senderPublicKey
     * @param maxFee
     * @return
     */
    Observable<AccountMetadataTransaction> createAccountMetadataTransaction(
        NetworkType networkType,
        PublicAccount targetPublicAccount,
        BigInteger key,
        String value,
        String senderPublicKey,
        BigInteger maxFee);

    Observable<MosaicMetadataTransaction> createMosaicMetadataTransaction(
        NetworkType networkType,
        PublicAccount targetPublicAccount,
        BigInteger key,
        String value,
        String senderPublicKey,
        MosaicId targetId,
        BigInteger maxFee);

    Observable<NamespaceMetadataTransaction> createNamespaceMetadataTransaction(
        NetworkType networkType,
        PublicAccount targetPublicAccount,
        BigInteger key,
        String value,
        String senderPublicKey,
        NamespaceId targetId,
        BigInteger maxFee);
}
