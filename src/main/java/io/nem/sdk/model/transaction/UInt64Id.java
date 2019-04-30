package io.nem.sdk.model.transaction;

import java.math.BigInteger;
import java.util.Optional;

public interface UInt64Id {
    /**
     * Gets the UInt64Id id
     *
     * @return BigInteger id
     */
    BigInteger getId();

    /**
     * Gets the UInt64Id id as a long number
     *
     * @return long id
     */
    long getIdAsLong();

    /**
     * Gets the UInt64Id id as a hexadecimal string
     *
     * @return BigInteger id
     */
    String getIdAsHex();

    /**
     * Get the optional UInt64Id full name
     *
     * @return Optional<String> full name
     */
    Optional<String> getFullName();

    /**
     * Compares UInt64Ids for equality.
     *
     * @return boolean
     */
    @Override
    boolean equals(Object o);
}
