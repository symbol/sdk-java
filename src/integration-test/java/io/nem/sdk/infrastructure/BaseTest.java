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

package io.nem.sdk.infrastructure;

import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;

public abstract class BaseTest {

    private static final Config CONFIG = Config.getInstance();
    private NetworkType networkType;
    private Account testAccount;
    private PublicAccount testPublicAccount;
    private Address testAccountAddress;
    private Address testRecipient; // Test Account2 Address
    private String generationHash;

    public Config config() {
        return BaseTest.CONFIG;
    }

    public String getApiUrl() {
        return this.config().getApiUrl();
    }

    public NetworkType getNetworkType() {
        if (this.networkType == null) {
            this.networkType = NetworkType.valueOf(this.config().getNetworkType());
        }
        return this.networkType;
    }

    public Account getTestAccount() {
        if (this.testAccount == null) {
            this.testAccount =
                Account.createFromPrivateKey(
                    this.config().getTestAccountPrivateKey(), this.getNetworkType());
        }
        return this.testAccount;
    }

    public PublicAccount getTestPublicAccount() {
        if (this.testPublicAccount == null) {
            this.testPublicAccount =
                PublicAccount.createFromPublicKey(
                    this.config().getTestAccountPublicKey(), this.getNetworkType());
        }
        return this.testPublicAccount;
    }

    public Address getTestAccountAddress() {
        if (this.testAccountAddress == null) {
            this.testAccountAddress = Address
                .createFromRawAddress(this.config().getTestAccountAddress());
        }
        return this.testAccountAddress;
    }

    public Address getRecipient() {
        if (this.testRecipient == null) {
            this.testRecipient = Address
                .createFromRawAddress(this.config().getTestAccount2Address());
        }
        return this.testRecipient;
    }

    public String getGenerationHash() {
        if (this.generationHash == null) {
            this.generationHash = this.config().getGenerationHash();
        }
        return this.generationHash;
    }
}
