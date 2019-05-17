package io.nem.sdk.model.mosaic;

import io.nem.sdk.model.namespace.NamespaceId;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * NetworkHarvestMosaic mosaic
 *
 * This represents the per-network harvest mosaic. This mosaicId is aliased
 * with namespace name `cat.harvest`.
 *
 * @since 0.10.2
 */
public class NetworkHarvestMosaic extends Mosaic {

    /**
     * Namespace id of `currency` namespace.
     */
    public static final NamespaceId NAMESPACEID = new NamespaceId("cat.harvest");
    /**
     * Divisibility
     */
    public static final int DIVISIBILITY = 3;
    /**
     * Initial supply
     */
    public static final BigInteger INITIALSUPPLY = new BigInteger("15000000");
    /**
     * Is transferable
     */
    public static final boolean TRANSFERABLE = true;
    /**
     * Is supply mutable
     */
    public static final boolean SUPPLYMUTABLE = true;
    /**
     * Is levy mutable
     */
    public static final boolean LEVYMUTABLE = false;

    /**
     *
     * @param amount
     */
    public NetworkHarvestMosaic(BigInteger amount) {

        super(NetworkHarvestMosaic.NAMESPACEID, amount);
    }

    /**
     * Create xem with using xem as unit.
     *
     * @param amount amount to send
     * @return a NetworkCurrencyMosaic instance
     */
    public static NetworkHarvestMosaic createRelative(BigInteger amount) {

        BigInteger relativeAmount = new BigDecimal(Math.pow(10, NetworkHarvestMosaic.DIVISIBILITY)).toBigInteger().multiply(amount);
        return new NetworkHarvestMosaic(relativeAmount);
    }

    /**
     * Create xem with using micro xem as unit, 1 NetworkCurrencyMosaic = 1000000 micro NetworkCurrencyMosaic.
     *
     * @param amount amount to send
     * @return a NetworkCurrencyMosaic instance
     */
    public static NetworkHarvestMosaic createAbsolute(BigInteger amount) {

        return new NetworkHarvestMosaic(amount);
    }
}
