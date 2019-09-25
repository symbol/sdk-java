package io.nem.sdk.model.blockchain;

import java.math.BigInteger;

public class BlockDuration {
    /**
     * The duration in blocks a mosaic will be available. After the duration finishes mosaic is
     * inactive and can be renewed. Duration is required when defining the mosaic
     */
    private final long duration;


    public BlockDuration(long duration) {
        this.duration = duration;
    }

    public BlockDuration(BigInteger duration) {
        this.duration = duration.longValue();
    }

    /**
     * Returns the number of blocks from height it will be active
     *
     * @return the number of blocks from height it will be active
     */
    public long getDuration() {
        return duration;
    }
}
