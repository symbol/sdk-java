/*
 * Copyright 2019. NEM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.nem.sdk.infrastructure;

import io.nem.sdk.model.account.Account;
import java.util.concurrent.ExecutionException;

/**
 * Main class that listen to catapult in order to troubleshooting integration tests.
 */
public class ListenerForTests extends BaseIntegrationTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        new ListenerForTests().run();
    }

    private void run() throws ExecutionException, InterruptedException {
        Account account = getTestAccount();
        Listener listener = getRepositoryFactory(DEFAULT_REPOSITORY_TYPE).createListener();
        listener.open().get();
        listener.newBlock()
            .subscribe(c -> System.out.println("New Block: " + c.getType() + " " + c.getHash()));
        listenToAccount(account, listener);
    }

    private void listenToAccount(Account account, Listener listener) {
        System.out.println("Listening for transaction of account " + account.getAddress().plain());

        listener.unconfirmedAdded(account.getAddress())
            .subscribe(c -> System.out.println("Received unconfirmedAdded transaction " + c));

        listener.confirmed(account.getAddress())
            .subscribe(c -> System.out.println("Received confirmed transaction " + c));

        listener.aggregateBondedAdded(account.getAddress())
            .subscribe(c -> System.out.println("Received aggregateBondedAdded transaction " + c));

        listener.cosignatureAdded(account.getAddress())
            .subscribe(c -> System.out.println("Received cosignatureAdded transaction " + c));

        listener.aggregateBondedAdded(account.getAddress())
            .subscribe(c -> System.out.println("Received aggregateBondedAdded transaction " + c));

        listener.status(account.getAddress())
            .subscribe(c -> System.out.println("Error: " + c.getStatus()));
    }

}
