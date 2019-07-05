/*
 * Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.model.receipt;

import java.math.BigInteger;
import java.util.List;

public class TransactionStatement {

    private final BigInteger height;
    private final ReceiptSource receiptSource;
    private final List<Receipt> receipts;

    /**
     * Constructor
     *
     * @param height Block height
     * @param receiptSource The receipt source.
     * @param receipts Array of receipts.
     */
    public TransactionStatement(
        BigInteger height, ReceiptSource receiptSource, List<Receipt> receipts) {
        this.height = height;
        this.receiptSource = receiptSource;
        this.receipts = receipts;
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
     * Returns block height
     *
     * @return block height
     */
    public BigInteger getHeight() {
        return this.height;
    }

    /**
     * Returns Array of receipts.
     *
     * @return Array of receipts.
     */
    public List<Receipt> getReceipts() {
        return this.receipts;
    }
}
