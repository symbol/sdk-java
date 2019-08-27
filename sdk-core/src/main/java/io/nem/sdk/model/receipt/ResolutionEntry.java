package io.nem.sdk.model.receipt;

import io.nem.sdk.model.namespace.AddressAlias;
import io.nem.sdk.model.namespace.MosaicAlias;

public class ResolutionEntry<T> {

    private final T resolved;
    private final ReceiptSource receiptSource;
    private final ReceiptType type;

    /**
     * Constructor
     *
     * @param resolved A resolved address or resolved mosaicId alias (MosaicAlias| AddressAlias).
     * @param receiptSource The receipt source.
     */
    public ResolutionEntry(T resolved, ReceiptSource receiptSource, ReceiptType type) {
        this.receiptSource = receiptSource;
        this.resolved = resolved;
        this.type = type;
        this.validateReceiptType(type);
        this.validateResolvedType();
    }

    /**
     * Returns the resolution
     *
     * @return resolution (MosaicAlias| AddressAlias)
     */
    public T getResolved() {
        return this.resolved;
    }

    /**
     * Returns the receipt type
     *
     * @return receipt type
     */
    public ReceiptType getType() {
        return this.type;
    }

    /**
     * Returns receipt source
     *
     * @return receipt source
     */
    public ReceiptSource getReceiptSource() {
        return this.receiptSource;
    }

    /**
     * Validate receipt type
     *
     * @return void
     */
    private void validateReceiptType(ReceiptType type) {
        if (!ReceiptType.ResolutionStatement.contains(type)) {
            throw new IllegalArgumentException("Receipt type: [" + type.name() + "] is not valid.");
        }
    }

    /**
     * Validate resolved type (MosaicId | NamespaceId)
     *
     * @return void
     */
    private void validateResolvedType() {
        Class resolutionClass = this.resolved.getClass();
        if (!AddressAlias.class.isAssignableFrom(resolutionClass)
            && !MosaicAlias.class.isAssignableFrom(resolutionClass)) {
            throw new IllegalArgumentException(
                "Resolved type: ["
                    + resolutionClass.getName()
                    + "] is not valid for this ResolutionEntry");
        }
    }
}
