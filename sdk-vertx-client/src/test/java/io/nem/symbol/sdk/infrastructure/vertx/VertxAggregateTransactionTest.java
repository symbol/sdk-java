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
package io.nem.symbol.sdk.infrastructure.vertx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.sdk.api.BinarySerialization;
import io.nem.symbol.sdk.infrastructure.BinarySerializationImpl;
import io.nem.symbol.sdk.infrastructure.vertx.mappers.GeneralTransactionMapper;
import io.nem.symbol.sdk.model.account.Account;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.message.PlainMessage;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.JsonHelper;
import io.nem.symbol.sdk.model.transaction.SignedTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import io.nem.symbol.sdk.openapi.vertx.model.TransactionInfoDTO;
import io.vertx.core.json.Json;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class VertxAggregateTransactionTest {

  private final String generationHash =
      "57F7DA205008026C776CB6AED843393F04CD458E0AA2D9F1D5F31A402072B2D6";

  private final JsonHelper jsonHelper =
      new JsonHelperJackson2(JsonHelperJackson2.configureMapper(Json.mapper));

  private NetworkType networkType = NetworkType.TEST_NET;

  @Test
  void createAAggregateTransactionViaStaticConstructor() {

    Duration epochAdjustment = Duration.ofSeconds(100);
    Address recipient = Address.generateRandom(networkType);
    Deadline deadline = Deadline.create(epochAdjustment);
    TransferTransaction transferTx =
        TransferTransactionFactory.create(networkType, deadline, recipient, Collections.emptyList())
            .message(new PlainMessage(""))
            .build();

    AggregateTransaction aggregateTx =
        AggregateTransactionFactory.createComplete(
                networkType,
                deadline,
                Collections.singletonList(
                    transferTx.toAggregate(
                        new PublicAccount(
                            "9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24",
                            networkType))))
            .build();

    assertEquals(networkType, aggregateTx.getNetworkType());
    assertEquals(2, (int) aggregateTx.getVersion());
    assertTrue(
        LocalDateTime.now().isBefore(aggregateTx.getDeadline().getLocalDateTime(epochAdjustment)));
    assertEquals(BigInteger.valueOf(0), aggregateTx.getMaxFee());
    assertEquals(1, aggregateTx.getInnerTransactions().size());
  }

  @Test
  @DisplayName("Serialization")
  void serialization() {
    Address address = Address.generateRandom(networkType);
    TransferTransaction transferTx =
        TransferTransactionFactory.create(
                networkType,
                new Deadline(BigInteger.ONE),
                address,
                Collections.singletonList(createAbsolute(BigInteger.valueOf(10000000))))
            .message(new PlainMessage(""))
            .build();

    PublicAccount signer = Account.generateNewAccount(networkType).getPublicAccount();
    AggregateTransaction aggregateTx =
        AggregateTransactionFactory.createComplete(
                networkType,
                new Deadline(BigInteger.ONE),
                Collections.singletonList(transferTx.toAggregate(signer)))
            .build();

    byte[] actual = aggregateTx.serialize();

    BinarySerialization serialization = BinarySerializationImpl.INSTANCE;
    AggregateTransaction deserialized = (AggregateTransaction) serialization.deserialize(actual);

    assertEquals(signer, deserialized.getInnerTransactions().get(0).getSigner().get());
  }

  protected Mosaic createAbsolute(BigInteger amount) {
    return new Mosaic(NamespaceId.createFromName("cat.currency"), amount);
  }

  @Test
  void shouldCreateAggregateTransactionAndSignWithMultipleCosignatories() {

    Address address = Address.generateRandom(networkType);
    TransferTransaction transferTx =
        TransferTransactionFactory.create(
                networkType, new Deadline(BigInteger.ONE), address, Collections.emptyList())
            .message(new PlainMessage("test-message"))
            .build();

    AggregateTransaction aggregateTx =
        AggregateTransactionFactory.createComplete(
                networkType,
                new Deadline(BigInteger.ONE),
                Collections.singletonList(
                    transferTx.toAggregate(
                        new PublicAccount(
                            "B694186EE4AB0558CA4AFCFDD43B42114AE71094F5A1FC4A913FE9971CACD21D",
                            networkType))))
            .build();

    Account cosignatoryAccount = Account.generateNewAccount(this.networkType);
    Account cosignatoryAccount2 = Account.generateNewAccount(this.networkType);
    Account cosignatoryAccount3 = Account.generateNewAccount(this.networkType);

    SignedTransaction signedTransaction =
        cosignatoryAccount.signTransactionWithCosignatories(
            aggregateTx, Arrays.asList(cosignatoryAccount2, cosignatoryAccount3), generationHash);

    BinarySerialization serialization = BinarySerializationImpl.INSTANCE;
    AggregateTransaction deserialized =
        (AggregateTransaction)
            serialization.deserialize(ConvertUtils.fromHexToBytes(signedTransaction.getPayload()));

    Assertions.assertEquals(2, deserialized.getCosignatures().size());

    Assertions.assertEquals(
        cosignatoryAccount2.getPublicAccount(), deserialized.getCosignatures().get(0).getSigner());
    Assertions.assertEquals(
        cosignatoryAccount3.getPublicAccount(), deserialized.getCosignatures().get(1).getSigner());
  }

  @Test
  void shouldFindAccountInAsASignerOfTheTransaction() {
    TransactionInfoDTO aggregateTransferTransactionDTO =
        TestHelperVertx.loadTransactionInfoDTO("accountInAsASignerOfTheTransaction.json");

    AggregateTransaction aggregateTransferTransaction =
        (AggregateTransaction)
            new GeneralTransactionMapper(jsonHelper).mapFromDto(aggregateTransferTransactionDTO);

    assertTrue(
        aggregateTransferTransaction.signedByAccount(
            PublicAccount.createFromPublicKey(
                "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630", networkType)));
    assertTrue(
        aggregateTransferTransaction.signedByAccount(
            PublicAccount.createFromPublicKey(
                "7681ED5023141D9CDCF184E5A7B60B7D466739918ED5DA30F7E71EA7B86EFF2D", networkType)));
    assertFalse(
        aggregateTransferTransaction.signedByAccount(
            PublicAccount.createFromPublicKey(
                "B4F12E7C9F6946091E2CB8B6D3A12B50D17CCBBF646386EA27CE2946A7423DCF", networkType)));
  }
}
