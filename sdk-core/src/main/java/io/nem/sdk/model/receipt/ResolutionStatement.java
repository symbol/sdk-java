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

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.mosaic.MosaicId;
import java.math.BigInteger;
import java.util.List;

public class ResolutionStatement<T> {

    private final BigInteger height;
    private final T unresolved;
    private final List<ResolutionEntry> resolutionEntries;

    /**
     * Constructor
     *
     * @param height Height
     * @param unresolved An unresolved address or unresolved mosaicId (Address | MosaicId).
     * @param resolutionEntries Array of resolution entries.
     */
    public ResolutionStatement(
        BigInteger height, T unresolved, List<ResolutionEntry> resolutionEntries) {
        this.height = height;
        this.unresolved = unresolved;
        this.resolutionEntries = resolutionEntries;
        this.validateType();
    }

    /**
     * Returns An unresolved address or unresolved mosaicId (Address | MosaicId).
     *
     * @return An unresolved address or unresolved mosaicId (Address | MosaicId).
     */
    public T getUnresolved() {
        return this.unresolved;
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
     * Returns Array of resolution entries.
     *
     * @return Array of resolution entries.
     */
    public List<ResolutionEntry> getResolutionEntries() {
        return this.resolutionEntries;
    }

    /**
     * Validate unresolved type against resolutionEntry
     *
     * @return void
     */
    private void validateType() {
        Class unresolvedClass = this.unresolved.getClass();
        if (!Address.class.isAssignableFrom(unresolvedClass)
            && !MosaicId.class.isAssignableFrom(unresolvedClass)) {
            throw new IllegalArgumentException(
                "Unresolved type: ["
                    + unresolvedClass.getName()
                    + "] is not valid for this ResolutionStatement");
        }
        this.resolutionEntries.forEach(
            entry -> {
                if ((Address.class.isAssignableFrom(unresolvedClass)
                    && entry.getType() != ReceiptType.ADDRESS_ALIAS_RESOLUTION)
                    || (MosaicId.class.isAssignableFrom(unresolvedClass)
                    && entry.getType() != ReceiptType.MOSAIC_ALIAS_RESOLUTION)) {
                    throw new IllegalArgumentException(
                        "Unresolved type: ["
                            + unresolvedClass.getName()
                            + "] does not match ResolutionEntry's type: ["
                            + entry.getType().name()
                            + "]for this ResolutionStatement");
                }
            });
    }
}
