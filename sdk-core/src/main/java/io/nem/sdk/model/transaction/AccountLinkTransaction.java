/*
 * Copyright 2019 NEM
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

package io.nem.sdk.model.transaction;

import io.nem.catapult.builders.AccountLinkActionDto;
import io.nem.catapult.builders.AccountLinkTransactionBuilder;
import io.nem.catapult.builders.AmountDto;
import io.nem.catapult.builders.EmbeddedAccountLinkTransactionBuilder;
import io.nem.catapult.builders.KeyDto;
import io.nem.catapult.builders.SignatureDto;
import io.nem.catapult.builders.TimestampDto;
import io.nem.sdk.model.account.PublicAccount;
import java.nio.ByteBuffer;

/**
 *
 */
public class AccountLinkTransaction extends Transaction {

    private final PublicAccount remoteAccount;

    private final AccountLinkAction linkAction;

    public AccountLinkTransaction(AccountLinkTransactionFactory factory) {
        super(factory);
        this.remoteAccount = factory.getRemoteAccount();
        this.linkAction = factory.getLinkAction();
    }

    /**
     * Gets the public key.
     *
     * @return Public key.
     */
    public PublicAccount getRemoteAccount() {
        return remoteAccount;
    }

    /**
     * Gets the link action.
     *
     * @return Link action.
     */
    public AccountLinkAction getLinkAction() {
        return linkAction;
    }

    /**
     * Serialized the transaction.
     *
     * @return bytes of the transaction.
     */
    @Override
    byte[] generateBytes() {
        // Add place holders to the signer and signature until actually signed
        final ByteBuffer signerBuffer = ByteBuffer.allocate(32);
        final ByteBuffer signatureBuffer = ByteBuffer.allocate(64);

        final AccountLinkTransactionBuilder txBuilder =
            AccountLinkTransactionBuilder.create(
                new SignatureDto(signatureBuffer),
                new KeyDto(signerBuffer),
                getNetworkVersion(),
                getEntityTypeDto(),
                new AmountDto(getMaxFee().longValue()),
                new TimestampDto(getDeadline().getInstant()),
                new KeyDto(getRemoteAccount().getPublicKey().getByteBuffer()),
                AccountLinkActionDto.rawValueOf(getLinkAction().getValue()));
        return txBuilder.serialize();
    }

    /**
     * Serialized the transaction to embedded bytes.
     *
     * @return bytes of the transaction.
     */
    @Override
    byte[] generateEmbeddedBytes() {
        final EmbeddedAccountLinkTransactionBuilder txBuilder =
            EmbeddedAccountLinkTransactionBuilder.create(
                new KeyDto(getRequiredSignerBytes()),
                getNetworkVersion(),
                getEntityTypeDto(),
                new KeyDto(getRemoteAccount().getPublicKey().getByteBuffer()),
                AccountLinkActionDto.rawValueOf(getLinkAction().getValue()));
        return txBuilder.serialize();
    }
}
