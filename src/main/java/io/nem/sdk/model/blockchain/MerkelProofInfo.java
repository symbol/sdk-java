package io.nem.sdk.model.blockchain;

import java.util.List;

public class MerkelProofInfo {
    private final List<MerkelPathItem> payload;
    private final String type;

    /**
     * Constructor
     * @param payload
     * @param type
     */
    public MerkelProofInfo(List<MerkelPathItem> payload, String type) {
        this.payload = payload;
        this.type = type;
    }

    /**
     * Return type
     * @return String
     */
    public String getType() {
        return this.type;
    }

    /**
     * Return Payload
     * @return List<MerkelPathItem>
     */
    public List<MerkelPathItem> getPayload() {
        return this.payload;
    }
}
