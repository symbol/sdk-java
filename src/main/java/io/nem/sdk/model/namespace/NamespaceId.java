/*
 * Copyright 2018 NEM
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

package io.nem.sdk.model.namespace;

import io.nem.sdk.model.transaction.IdGenerator;
import io.nem.sdk.model.transaction.UInt64;
import io.nem.sdk.model.transaction.UInt64Id;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.List;

/**
 * The namespace id structure describes namespace id
 *
 * @since 1.0
 */
public class NamespaceId implements UInt64Id {
    private final BigInteger id;
    private final Optional<String> fullName;

    /**
     * Returns a list of BigInteger ids for a namespace path (ex: nem or domain.subdom.subdome)
     *
     * @param namespaceName
     * @return
     */
    public static List<BigInteger> getNamespacePath(String namespaceName) {

        return IdGenerator.generateNamespacePath(namespaceName);
    }

    /**
     * Create NamespaceId from namespace string name (ex: nem or domain.subdom.subdome)
     *
     * @param namespaceName
     */
    public NamespaceId(String namespaceName) {
        this.id = IdGenerator.generateNamespaceId(namespaceName);
        this.fullName = Optional.of(namespaceName);
    }

    /**
     * Create NamespaceId from namespace string name (ex: nem or domain.subdom.subdome) and parent id
     *
     * @param namespaceName
     * @param parentId
     */
    public NamespaceId(String namespaceName, BigInteger parentId) {
        this.id = IdGenerator.generateNamespaceId(namespaceName, parentId);
        this.fullName = Optional.of(namespaceName);
    }

    /**
     * Create NamespaceId from namespace string name (ex: nem or domain.subdom.subdome) and parent namespace name
     *
     * @param namespaceName
     * @param parentNamespaceName
     */
    public NamespaceId(String namespaceName, String parentNamespaceName) {
        this.id = IdGenerator.generateNamespaceId(namespaceName, parentNamespaceName);
        this.fullName = Optional.of(parentNamespaceName + "." + namespaceName);
    }

    /**
     * Create NamespaceId from BigInteger id
     *
     * @param id
     */
    public NamespaceId(BigInteger id) {
        this.id = id;
        this.fullName = Optional.empty();
    }

    /**
     * Returns namespace BigInteger id
     *
     * @return namespace BigInteger id
     */
    public BigInteger getId() {

        return id;
    }

    /**
     * Returns namespace id as a long
     *
     * @return id long
     */
    public long getIdAsLong() {

        return this.id.longValue();
    }

    /**
     * Returns namespace id as a hexadecimal string
     *
     * @return id Hex String
     */
    public String getIdAsHex() {

        return UInt64.bigIntegerToHex(this.id);
    }

    /**
     * Returns optional namespace full name, with subnamespaces if it's the case.
     *
     * @return namespace full name
     */
    public Optional<String> getFullName() {

        return fullName;
    }

    /**
     * Compares namespaceIds for equality.
     *
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamespaceId)) return false;
        NamespaceId namespaceId1 = (NamespaceId) o;

        return Objects.equals(id, namespaceId1.id);
    }
}
