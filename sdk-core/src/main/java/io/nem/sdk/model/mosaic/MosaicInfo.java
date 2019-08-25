/**
 * ** Copyright (c) 2016-present, ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights
 * reserved. ** ** This file is part of Catapult. ** ** Catapult is free software: you can
 * redistribute it and/or modify ** it under the terms of the GNU Lesser General Public License as
 * published by ** the Free Software Foundation, either version 3 of the License, or ** (at your
 * option) any later version. ** ** Catapult is distributed in the hope that it will be useful, **
 * but WITHOUT ANY WARRANTY; without even the implied warranty of ** MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the ** GNU Lesser General Public License for more details. ** ** You
 * should have received a copy of the GNU Lesser General Public License ** along with Catapult. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package io.nem.sdk.model.mosaic;

import io.nem.sdk.model.account.PublicAccount;
import java.math.BigInteger;

/**
 * The mosaic info structure contains its properties, the owner and the namespace to which it
 * belongs to.
 */
public class MosaicInfo {

    private final String metaId;
    private final MosaicId mosaicId;
    private final BigInteger supply;
    private final BigInteger height;
    private final PublicAccount owner;
    private final Integer revision;
    private final MosaicProperties properties;

    private MosaicInfo(final String metaId, final MosaicId mosaicId, final BigInteger supply,
        final BigInteger height,
        final PublicAccount owner, final Integer revision, final MosaicProperties properties) {
        this.metaId = metaId;
        this.mosaicId = mosaicId;
        this.supply = supply;
        this.height = height;
        this.owner = owner;
        this.revision = revision;
        this.properties = properties;
    }

    public static MosaicInfo create(final String metaId, final MosaicId mosaicId,
        final BigInteger supply, final BigInteger height,
        final PublicAccount owner, final Integer revision, final MosaicProperties properties) {
        return new MosaicInfo(metaId, mosaicId, supply, height, owner, revision, properties);
    }

    /**
     * Returns the meta id
     *
     * @return meta id
     */
    public String getMetaId() {
        return metaId;
    }

    /**
     * Returns the mosaic id
     *
     * @return mosaic id
     */
    public MosaicId getMosaicId() {
        return mosaicId;
    }

    /**
     * Returns the total mosaic supply
     *
     * @return total mosaic supply
     */
    public BigInteger getSupply() {
        return supply;
    }

    /**
     * Returns the block height it was created
     *
     * @return height it was created
     */
    public BigInteger getHeight() {
        return height;
    }

    /**
     * Returns the mosaic account owner
     *
     * @return mosaic account owner
     */
    public PublicAccount getOwner() {
        return owner;
    }

    /**
     * Returns the revision number
     *
     * @return revision
     */
    public Integer getRevision() {
        return revision;
    }

    /**
     * Returns true if the supply is mutable
     *
     * @return if supply is mutable
     */
    public boolean isSupplyMutable() {
        return properties.isSupplyMutable();
    }

    /**
     * Returns tue if the mosaic is transferable between non-owner accounts
     *
     * @return if the mosaic is transferable between non-owner accounts
     */
    public boolean isTransferable() {
        return properties.isTransferable();
    }

    /**
     * Return the number of blocks from height it will be active
     *
     * @return the number of blocks from height it will be active
     */
    public BigInteger getDuration() {
        return properties.getDuration();
    }

    /**
     * Returns the mosaic divisibility
     *
     * @return mosaic divisibility
     */
    public int getDivisibility() {
        return properties.getDivisibility();
    }
}
