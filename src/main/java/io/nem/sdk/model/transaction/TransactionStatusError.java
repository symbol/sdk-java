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
 * The transaction status error model returned by listeners.
 */
public class TransactionStatusError {
	private final String hash;
	private final String status;
	private final Deadline deadline;

	public TransactionStatusError(String hash, String status, Deadline deadline) {
		this.hash = hash;
		this.status = status;
		this.deadline = deadline;
	}

	/**
	 * Returns transaction hash.
	 *
	 * @return transaction hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * Returns transaction status error when transaction fails.
	 *
	 * @return transaction status error
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Returns transaction deadline.
	 *
	 * @return transaction deadline
	 */
	public Deadline getDeadline() {
		return deadline;
	}
}
