/*
 * Copyright 2020 NEM
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
package io.nem.symbol.sdk.model.mosaic;

import io.nem.symbol.sdk.api.RepositoryFactoryConfiguration;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import java.util.Optional;
import org.apache.commons.lang3.Validate;

/**
 * Builder of {@link Currency}. This builders helps the user creating the {@link Currency} objects
 * when setting up the {@link RepositoryFactoryConfiguration}.
 */
public final class CurrencyBuilder {

  /**
   * The selected unresolved mosaic id the {@link Currency} uses when creating {@link Mosaic}. This
   * could either be the Namespace or the Mosaic id.
   */
  private final UnresolvedMosaicId unresolvedMosaicId;

  /** Divisibility of the currency, required to create Mosaic from relative amounts. */
  private final int divisibility;

  /**
   * Mosaic id of this currency. This value is optional if the user only wants to provide the mosaic
   * id. This value will be set if it's loaded by rest.
   */
  private Optional<MosaicId> mosaicId = Optional.empty();
  /**
   * The Namespace id of this currency. This value is option if the user only wants to provide the
   * namespace id. This value will be set if it's loaded by rest.
   */
  private Optional<NamespaceId> namespaceId = Optional.empty();

  /** Is the currency transferable. */
  private boolean transferable = true;

  /** Is this currency supply mutable. */
  private boolean supplyMutable = false;

  /** Is this currency restrictable. */
  private boolean restrictable = false;

  /** Is this currency revokable. */
  private boolean revokable = false;

  public CurrencyBuilder(UnresolvedMosaicId unresolvedMosaicId, int divisibility) {
    Validate.notNull(unresolvedMosaicId, "unresolvedMosaicId must not be null");
    Validate.isTrue(divisibility > 0, "divisibility must be greater than 0");
    this.unresolvedMosaicId = unresolvedMosaicId;
    this.divisibility = divisibility;
    if (unresolvedMosaicId instanceof NamespaceId) {
      withNamespaceId((NamespaceId) unresolvedMosaicId);
    } else {
      withMosaicId((MosaicId) unresolvedMosaicId);
    }
  }

  /**
   * Helper method to setup the mosiac id.
   *
   * @param mosaicId the mosaic id
   * @return this builder.
   */
  public CurrencyBuilder withMosaicId(MosaicId mosaicId) {
    Validate.notNull(mosaicId, "mosaicId must not be null");
    this.mosaicId = Optional.of(mosaicId);
    return this;
  }

  /**
   * Helper method to setup the namespace id.
   *
   * @param namespaceId the namespace id
   * @return this builder.
   */
  public CurrencyBuilder withNamespaceId(NamespaceId namespaceId) {
    Validate.notNull(namespaceId, "namespaceId must not be null");
    this.namespaceId = Optional.of(namespaceId);
    return this;
  }

  /**
   * Helper method to setup the transferable flag.
   *
   * @param transferable the transferable
   * @return this builder.
   */
  public CurrencyBuilder withTransferable(boolean transferable) {
    this.transferable = transferable;
    return this;
  }

  /**
   * Helper method to setup the supplyMutable flag.
   *
   * @param supplyMutable the supplyMutable
   * @return this builder.
   */
  public CurrencyBuilder withSupplyMutable(boolean supplyMutable) {
    this.supplyMutable = supplyMutable;
    return this;
  }

  /**
   * Helper method to setup the restrictable flag.
   *
   * @param restrictable the restrictable
   * @return this builder.
   */
  public CurrencyBuilder withRestrictable(boolean restrictable) {
    this.restrictable = restrictable;
    return this;
  }

  /**
   * Helper method to setup the revokable flag.
   *
   * @param revokable the revokable
   * @return this builder.
   */
  public CurrencyBuilder withRevokable(boolean revokable) {
    this.revokable = revokable;
    return this;
  }

  /**
   * Once the builder is configured, call this method to create the {@link Currency}
   *
   * @return the network currency.
   */
  public Currency build() {
    return new Currency(this);
  }

  public UnresolvedMosaicId getUnresolvedMosaicId() {
    return unresolvedMosaicId;
  }

  public int getDivisibility() {
    return divisibility;
  }

  public Optional<MosaicId> getMosaicId() {
    return mosaicId;
  }

  public Optional<NamespaceId> getNamespaceId() {
    return namespaceId;
  }

  public boolean isTransferable() {
    return transferable;
  }

  public boolean isSupplyMutable() {
    return supplyMutable;
  }

  public boolean isRestrictable() {
    return restrictable;
  }

  public boolean isRevokable() {
    return revokable;
  }
}
