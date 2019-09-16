/*
 *  Copyright 2019 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.sdk.infrastructure;

import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.BlockInfo;
import io.nem.sdk.model.transaction.AggregateTransaction;
import io.nem.sdk.model.transaction.CosignatureSignedTransaction;
import io.nem.sdk.model.transaction.Transaction;
import io.nem.sdk.model.transaction.TransactionStatusError;
import io.nem.sdk.model.transaction.TransferTransaction;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by fernando on 19/08/19.
 *
 * @author Fernando Boucquez
 */
public abstract class ListenerBase implements Listener {

    private final Subject<ListenerMessage> messageSubject = PublishSubject.create();

    @Override
    public Observable<BlockInfo> newBlock() {
        validateOpen();
        this.subscribeTo(ListenerChannel.BLOCK.toString());
        return getMessageSubject()
            .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.BLOCK))
            .map(rawMessage -> (BlockInfo) rawMessage.getMessage());
    }

    /**
     * Returns an observable stream of Transaction for a specific address. Each time a transaction
     * is in confirmed state an it involves the address, it emits a new Transaction in the event
     * stream.
     *
     * @param address address we listen when a transaction is in confirmed state
     * @return an observable stream of Transaction with state confirmed
     */
    @Override
    public Observable<Transaction> confirmed(final Address address) {
        validateOpen();
        this.subscribeTo(ListenerChannel.STATUS.toString() + "/" + address.plain());
        this.subscribeTo(ListenerChannel.CONFIRMED_ADDED.toString() + "/" + address.plain());
        return getMessageSubject()
            .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.CONFIRMED_ADDED))
            .map(rawMessage -> (Transaction) rawMessage.getMessage())
            .filter(transaction -> this.transactionFromAddress(transaction, address));
    }

    /**
     * Returns an observable stream of Transaction for a specific address. Each time a transaction
     * is in unconfirmed state an it involves the address, it emits a new Transaction in the event
     * stream.
     *
     * @param address address we listen when a transaction is in unconfirmed state
     * @return an observable stream of Transaction with state unconfirmed
     */
    @Override
    public Observable<Transaction> unconfirmedAdded(Address address) {
        validateOpen();
        this.subscribeTo(ListenerChannel.UNCONFIRMED_ADDED + "/" + address.plain());
        return getMessageSubject()
            .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.UNCONFIRMED_ADDED))
            .map(rawMessage -> (Transaction) rawMessage.getMessage())
            .filter(transaction -> this.transactionFromAddress(transaction, address));
    }

    /**
     * Returns an observable stream of Transaction Hashes for specific address. Each time a
     * transaction with state unconfirmed changes its state, it emits a new message with the
     * transaction hash in the event stream.
     *
     * @param address address we listen when a transaction is removed from unconfirmed state
     * @return an observable stream of Strings with the transaction hash
     */
    @Override
    public Observable<String> unconfirmedRemoved(Address address) {
        validateOpen();
        this.subscribeTo(ListenerChannel.UNCONFIRMED_REMOVED + "/" + address.plain());
        return getMessageSubject()
            .filter(
                rawMessage -> rawMessage.getChannel().equals(ListenerChannel.UNCONFIRMED_REMOVED))
            .map(rawMessage -> (String) rawMessage.getMessage());
    }

    /**
     * Return an observable of {@link AggregateTransaction} for specific address. Each time an
     * aggregate bonded transaction is announced, it emits a new {@link AggregateTransaction} in the
     * event stream.
     *
     * @param address address we listen when a transaction with missing signatures state
     * @return an observable stream of AggregateTransaction with missing signatures state
     */
    @Override
    public Observable<AggregateTransaction> aggregateBondedAdded(Address address) {
        validateOpen();
        this.subscribeTo(ListenerChannel.AGGREGATE_BONDED_ADDED + "/" + address.plain());
        return getMessageSubject()
            .filter(
                rawMessage -> rawMessage.getChannel()
                    .equals(ListenerChannel.AGGREGATE_BONDED_ADDED))
            .map(rawMessage -> (AggregateTransaction) rawMessage.getMessage())
            .filter(transaction -> this.transactionFromAddress(transaction, address));
    }

    /**
     * Returns an observable stream of Transaction Hashes for specific address. Each time an
     * aggregate bonded transaction is announced, it emits a new message with the transaction hash
     * in the event stream.
     *
     * @param address address we listen when a transaction is confirmed or rejected
     * @return an observable stream of Strings with the transaction hash
     */
    @Override
    public Observable<String> aggregateBondedRemoved(Address address) {
        validateOpen();
        this.subscribeTo(ListenerChannel.AGGREGATE_BONDED_REMOVED + "/" + address.plain());
        return getMessageSubject()
            .filter(
                rawMessage -> rawMessage.getChannel()
                    .equals(ListenerChannel.AGGREGATE_BONDED_REMOVED))
            .map(rawMessage -> (String) rawMessage.getMessage());
    }

    /**
     * Returns an observable stream of {@link TransactionStatusError} for specific address. Each
     * time a transaction contains an error, it emits a new message with the transaction status
     * error in the event stream.
     *
     * @param address address we listen to be notified when some error happened
     * @return an observable stream of {@link TransactionStatusError}
     */
    @Override
    public Observable<TransactionStatusError> status(Address address) {
        validateOpen();
        this.subscribeTo(ListenerChannel.STATUS + "/" + address.plain());
        return getMessageSubject()
            .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.STATUS))
            .map(rawMessage -> (TransactionStatusError) rawMessage.getMessage());
    }

    /**
     * Returns an observable stream of {@link CosignatureSignedTransaction} for specific address.
     * Each time a cosigner signs a transaction the address initialized, it emits a new message with
     * the cosignatory signed transaction in the even stream.
     *
     * @param address address we listen when a cosignatory is added to some transaction address
     * sent
     * @return an observable stream of {@link CosignatureSignedTransaction}
     */
    @Override
    public Observable<CosignatureSignedTransaction> cosignatureAdded(Address address) {
        validateOpen();
        this.subscribeTo(ListenerChannel.CONFIRMED_ADDED + "/" + address.plain());
        return getMessageSubject()
            .filter(rawMessage -> rawMessage.getChannel().equals(ListenerChannel.COSIGNATURE))
            .map(rawMessage -> (CosignatureSignedTransaction) rawMessage.getMessage());
    }

    private void validateOpen() {
        if (getUid() == null) {
            throw new IllegalStateException(
                "Listener has been open yet. Please call open before subscribing. ");
        }
    }


    private boolean transactionFromAddress(final Transaction transaction, final Address address) {
        return transactionHasSignerOrReceptor(transaction, address)
            || isAggregateTransactionSignerOrInnerTransaction(transaction, address);
    }

    private boolean transactionHasSignerOrReceptor(final Transaction transaction,
        final Address address) {
        return transaction.getSigner().filter(s -> s.getAddress().equals(address)).isPresent()
            || isTransferTransactionRecipient(transaction, address);
    }

    private boolean isTransferTransactionRecipient(final Transaction transaction,
        final Address address) {
        return transaction instanceof TransferTransaction && ((TransferTransaction) transaction)
            .getRecipient().filter(r -> r.equals(address)).isPresent();
    }

    private boolean isAggregateTransactionSignerOrInnerTransaction(final Transaction transaction,
        final Address address) {
        if (transaction instanceof AggregateTransaction) {
            final AggregateTransaction aggregateTransaction = (AggregateTransaction) transaction;
            return aggregateTransaction.getCosignatures()
                .stream().anyMatch(c -> c.getSigner().getAddress().equals(address)) ||
                aggregateTransaction.getInnerTransactions()
                    .stream().anyMatch(t -> this.transactionHasSignerOrReceptor(t, address));
        } else {
            return false;
        }
    }

    protected void onNext(ListenerChannel channel, Object messageObject) {
        this.getMessageSubject().onNext(new ListenerMessage(channel, messageObject));
    }

    protected abstract void subscribeTo(String channel);

    public Subject<ListenerMessage> getMessageSubject() {
        return messageSubject;
    }
}
