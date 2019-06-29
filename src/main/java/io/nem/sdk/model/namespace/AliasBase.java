/**
 * ** Copyright (c) 2016-present,
 * ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
 * **
 * ** This file is part of Catapult.
 * **
 * ** Catapult is free software: you can redistribute it and/or modify
 * ** it under the terms of the GNU Lesser General Public License as published by
 * ** the Free Software Foundation, either version 3 of the License, or
 * ** (at your option) any later version.
 * **
 * ** Catapult is distributed in the hope that it will be useful,
 * ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 * ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * ** GNU Lesser General Public License for more details.
 * **
 * ** You should have received a copy of the GNU Lesser General Public License
 * ** along with Catapult. If not, see <http://www.gnu.org/licenses/>.
 **/

package io.nem.sdk.model.namespace;

/**
 * Alias base.
 */
abstract public class AliasBase<T> implements Alias<T> {
	/* alias value. */
	private final T aliasValue;

	/**
	 * Creates an alias of type T.
	 *
	 * @param value Value of type T.
	 */
	public AliasBase(T value) {
		this.aliasValue = value;
	}

	/**
	 * Gets the alias type.
	 *
	 * @return Alias type.
	 */
	abstract public AliasType getType();

	/**
	 * Gets the alias value of type T.
	 *
	 * @return Alias value.
	 */
	@Override
	public T getAliasValue() {
		return this.aliasValue;
	}

	/**
	 * Checks if the values are equal.
	 *
	 * @param alias Alias object to compare to.
	 * @return True if the have the same values.
	 */
	@Override
	public boolean equals(Alias<T> alias) {
		return ((alias.getType() == getType()) && (alias.getAliasValue() == getAliasValue()));
	}

	/**
	 * Checks if the Alias is empty.
	 *
	 * @return True if alias type is none.
	 */
	@Override
	public boolean isEmpty() {
		return AliasType.None == getAliasValue();
	}
}