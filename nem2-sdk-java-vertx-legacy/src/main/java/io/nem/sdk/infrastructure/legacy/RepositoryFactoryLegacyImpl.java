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

package io.nem.sdk.infrastructure.legacy;

import io.nem.sdk.api.AccountRepository;
import io.nem.sdk.api.BlockRepository;
import io.nem.sdk.api.ChainRepository;
import io.nem.sdk.api.DiagnosticRepository;
import io.nem.sdk.api.MosaicRepository;
import io.nem.sdk.api.NamespaceRepository;
import io.nem.sdk.api.NetworkRepository;
import io.nem.sdk.api.NodeRepository;
import io.nem.sdk.api.RepositoryFactory;
import io.nem.sdk.api.TransactionRepository;
import io.nem.sdk.infrastructure.Listener;

/**
 * Legacy implementation of a {@link io.nem.sdk.api.RepositoryFactory} that uses manual endpoints
 * and mappers.
 *
 * @author Fernando Boucquez
 */

public class RepositoryFactoryLegacyImpl implements RepositoryFactory {


    private final String host;

    private final NetworkHttp networkHttp;

    public RepositoryFactoryLegacyImpl(String host, NetworkHttp networkHttp) {
        this.host = host;
        this.networkHttp = networkHttp;
    }

    public RepositoryFactoryLegacyImpl(String host) {
        this(host, new NetworkHttp(host));
    }

    @Override
    public AccountRepository createAccountRepository() {
        return new AccountHttp(host, networkHttp);
    }

    @Override
    public BlockRepository createBlockRepository() {
        return new BlockHttp(host, networkHttp);
    }

    @Override
    public ChainRepository createChainRepository() {
        return new ChainHttp(host, networkHttp);
    }

    @Override
    public DiagnosticRepository createDiagnosticRepository() {
        return new DiagnosticHttp(host, networkHttp);
    }

    @Override
    public MosaicRepository createMosaicRepository() {
        return new MosaicHttp(host, networkHttp);
    }

    @Override
    public NamespaceRepository createNamespaceRepository() {
        return new NamespaceHttp(host, networkHttp);
    }

    @Override
    public NetworkRepository createNetworkRepository() {
        return new NetworkHttp(host, networkHttp);
    }

    @Override
    public NodeRepository createNodeRepository() {
        return new NodeHttp(host, networkHttp);
    }

    @Override
    public TransactionRepository createTransactionRepository() {
        return new TransactionHttp(host, networkHttp);
    }

    @Override
    public Listener createListener() {
        return new ListenerLegacy(host);
    }
}
