package io.nem.symbol.sdk.infrastructure.okhttp;

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.core.utils.MapperUtils;
import io.nem.symbol.sdk.api.Page;
import io.nem.symbol.sdk.api.SecretLockRepository;
import io.nem.symbol.sdk.api.SecretLockSearchCriteria;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.transaction.SecretHashAlgorithm;
import io.nem.symbol.sdk.model.transaction.SecretLockInfo;
import io.nem.symbol.sdk.openapi.okhttp_gson.api.SecretLockRoutesApi;
import io.nem.symbol.sdk.openapi.okhttp_gson.invoker.ApiClient;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.Order;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.SecretLockEntryDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.SecretLockInfoDTO;
import io.nem.symbol.sdk.openapi.okhttp_gson.model.SecretLockPage;
import io.reactivex.Observable;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Implements {@link SecretLockRepository}
 *
 * @author Fernando Boucquez
 */
public class SecretLockRepositoryOkHttpImpl extends AbstractRepositoryOkHttpImpl implements SecretLockRepository {

    private final SecretLockRoutesApi client;

    public SecretLockRepositoryOkHttpImpl(ApiClient apiClient) {
        super(apiClient);
        this.client = new SecretLockRoutesApi(apiClient);
    }

    @Override
    public Observable<SecretLockInfo> getSecretLock(String secret) {
        Callable<SecretLockInfoDTO> callback = () -> getClient()
            .getSecretLock(ConvertUtils.padHex(secret, SecretHashAlgorithm.DEFAULT_SECRET_HEX_SIZE));
        return this.call(callback, this::toSecretLockInfo);

    }

    private SecretLockInfo toSecretLockInfo(SecretLockInfoDTO dto) {
        SecretLockEntryDTO lock = dto.getLock();
        MosaicId mosaicId = MapperUtils.toMosaicId(lock.getMosaicId());
        return new SecretLockInfo(Optional.of(dto.getId()), MapperUtils.toAddress(lock.getOwnerAddress()), mosaicId,
            lock.getAmount(), lock.getEndHeight(), lock.getStatus(),
            SecretHashAlgorithm.rawValueOf(lock.getHashAlgorithm().getValue()), lock.getSecret(),
            MapperUtils.toAddress(lock.getRecipientAddress()), lock.getCompositeHash());
    }

    @Override
    public Observable<Page<SecretLockInfo>> search(SecretLockSearchCriteria criteria) {
        String address = toDto(criteria.getAddress());
        Integer pageSize = criteria.getPageSize();
        Integer pageNumber = criteria.getPageNumber();
        String offset = criteria.getOffset();
        Order order = toDto(criteria.getOrder());
        Callable<SecretLockPage> callback = () -> getClient()
            .searchSecretLock(address, pageSize, pageNumber, offset, order);
        return this.call(callback, this::toPage);
    }

    private Page<SecretLockInfo> toPage(SecretLockPage SecretLockPage) {
        return toPage(SecretLockPage.getPagination(),
            SecretLockPage.getData().stream().map(this::toSecretLockInfo).collect(Collectors.toList()));
    }

    public SecretLockRoutesApi getClient() {
        return client;
    }
}