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

package io.nem.sdk.model.transaction;

/**
 * Account link action.
 */
public enum AccountLinkAction {
	/**
	 * Link account.
	 */
	LINK((byte) 0),
	/**
	 * Unlink account.
	 */
	UNLINK((byte) 1);

	private final byte value;

	/**
	 * Constructor.
	 *
	 * @param value Link action value.
	 */
	AccountLinkAction(final byte value) {
		this.value = value;
	}

	/**
	 * Gets enum value from raw.
	 *
	 * @param value Raw value.
	 * @return Enum value.
	 */
	public static AccountLinkAction rawValueOf(final int value) {
		switch (value) {
			case 0:
				return AccountLinkAction.LINK;
			case 1:
				return AccountLinkAction.UNLINK;
			default:
				throw new IllegalArgumentException(value + " is not a valid value");
		}
	}

	/**
	 * Gets the raw value.
	 *
	 * @return Ram value.
	 */
	public byte getValue() {
		return value;
	}
}
