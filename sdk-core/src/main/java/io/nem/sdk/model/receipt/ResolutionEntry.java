package io.nem.sdk.model.receipt;

import io.nem.core.utils.ByteUtils;
import io.nem.core.utils.ConvertUtils;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;
import java.nio.ByteBuffer;

public class ResolutionEntry<T> {

    private final T resolved;
    private final ReceiptSource receiptSource;
    private final ReceiptType type;

    /**
     * Constructor
     *
     * @param resolved A resolved address or resolved mosaicId alias (MosaicId| Address).
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
     * @return resolution (MosaicId| Address)
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
        if (!ReceiptType.RESOLUTION_STATEMENT.contains(type)) {
            throw new IllegalArgumentException("Receipt type: [" + type.name() + "] is not valid.");
        }
    }

    /**
     * Serialize receipt and returns receipt bytes
     *
     * @return receipt bytes
     */
    public byte[] serialize() {
        final byte[] resolvedBytes = getResolvedBytes();
        final ByteBuffer buffer = ByteBuffer.allocate(8 + resolvedBytes.length);
        buffer.put(resolvedBytes);
        buffer.put(getReceiptSource().serialize());
        return buffer.array();
    }

    /**
     * Serialize resolved value depends on type
     *
     * @return resolved bytes
     */
    private byte[] getResolvedBytes() {
        Class resolutionClass = this.resolved.getClass();
        if (Address.class.isAssignableFrom(resolutionClass)) {
            return ConvertUtils.getBytes(((Address)getResolved()).encoded());
        }
        return ByteUtils.reverseCopy(ByteUtils.bigIntToBytes(((MosaicId)getResolved()).getId()));
    }

    /**
     * Validate resolved type (MosaicId | Address)
     *
     * @return void
     */
    private void validateResolvedType() {
        Class resolutionClass = this.resolved.getClass();
        if (!Address.class.isAssignableFrom(resolutionClass)
            && !MosaicId.class.isAssignableFrom(resolutionClass)) {
            throw new IllegalArgumentException(
                "Resolved type: ["
                    + resolutionClass.getName()
                    + "] is not valid for this ResolutionEntry");
        }
    }
}
