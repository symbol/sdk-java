package io.nem.sdk.model.blockchain;

import java.util.List;

public class MerkelProofInfo {

    private final List<MerkelPathItem> payload;
    private final String type;

    /**
     * Constructor
     */
    public MerkelProofInfo(List<MerkelPathItem> payload, String type) {
        this.payload = payload;
        this.type = type;
    }

    /**
     * Return type
     *
     * @return String
     */
    public String getType() {
        return this.type;
    }

    /**
     * Return Payload
     *
     * @return {@link List} of MerkelPathItem
     */
    public List<MerkelPathItem> getPayload() {
        return this.payload;
    }
}
