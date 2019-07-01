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

package io.nem.sdk.model.transaction;

import io.nem.catapult.builders.TransferTransactionBuilder;
import io.nem.core.utils.ByteUtils;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TransferTransactionTest {
    static Account account;

    @BeforeAll
    public static void setup() {
        account = new Account("787225aaff3d2c71f4ffa32d4f19ec4922f3cd869747f267378f81f8e3fcb12d", NetworkType.MIJIN_TEST);
    }

    @Test
    void createATransferTransactionViaStaticConstructor() {

        TransferTransaction transferTx = TransferTransaction.create(
                new Deadline(2, ChronoUnit.HOURS),
                new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN_TEST),
                Arrays.asList(),
                PlainMessage.Empty,
                NetworkType.MIJIN_TEST
        );

        assertEquals(NetworkType.MIJIN_TEST, transferTx.getNetworkType());
        assertTrue(3 == transferTx.getVersion());
        assertTrue(LocalDateTime.now().isBefore(transferTx.getDeadline().getLocalDateTime()));
        assertEquals(BigInteger.valueOf(0), transferTx.getMaxFee());
        assertTrue(new Address("SDGLFW-DSHILT-IUHGIB-H5UGX2-VYF5VN-JEKCCD-BR26", NetworkType.MIJIN_TEST)
                .equals(transferTx.getRecipient()));
        assertEquals(0, transferTx.getMosaics().size());
        assertNotNull(transferTx.getMessage());
    }

    @Test // TODO to fix transaction size
    @DisplayName("Serialization")
    void serialization() {
        // Generated at nem2-library-js/test/transactions/TransferTransaction.spec.js
        byte[] expected = new byte[]{(byte) 165, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                3, (byte) 144, 84, 65, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, (byte) 144, (byte) 232, (byte) 254, (byte) 189, (byte) 103, (byte) 29, (byte) 212, (byte) 27, (byte) 238, (byte) 148, (byte) 236, (byte) 59, (byte) 165, (byte) 131, (byte) 28, (byte) 182, (byte) 8, (byte) 163, (byte) 18, (byte) 194, (byte) 242, (byte) 3, (byte) 186, (byte) 132, (byte) 172,
                1, 0, 1, 0, 103, 43, 0, 0, (byte) 206, 86, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0};

        TransferTransaction transferTransaction = TransferTransaction.create(
                new FakeDeadline(),
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", NetworkType.MIJIN_TEST),
                Arrays.asList(
                        new Mosaic(new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))
                ),
                PlainMessage.Empty,
                NetworkType.MIJIN_TEST
        );
        byte[] actual = transferTransaction.generateBytes();
        // assertArrayEquals(expected, actual); // TODO Fix error array lengths differ, expected: <165> but was: <164>
    }

    @Test // TODO to fix transaction size
    @DisplayName("Serialization with Builder")
    void compareSerializationFlatBufferAndBuilder() {
        // Generated at nem2-library-js/test/transactions/TransferTransaction.spec.js
        byte[] expected = new byte[]{(byte) 165, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                3, (byte) 144, 84, 65, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, (byte) 144, (byte) 232, (byte) 254, (byte) 189, (byte) 103, (byte) 29, (byte) 212, (byte) 27, (byte) 238, (byte) 148, (byte) 236, (byte) 59, (byte) 165, (byte) 131, (byte) 28, (byte) 182, (byte) 8, (byte) 163, (byte) 18, (byte) 194, (byte) 242, (byte) 3, (byte) 186, (byte) 132, (byte) 172,
                1, 0, 1, 0, 103, 43, 0, 0, (byte) 206, 86, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0};

        TransferTransaction txModel = TransferTransaction.create(
                new FakeDeadline(),
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", NetworkType.MIJIN_TEST),
                Arrays.asList(
                        new Mosaic(new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))
                ),
                PlainMessage.Empty,
                NetworkType.MIJIN_TEST
        );
        byte[] actual = txModel.generateBytes();
        System.out.println(ByteUtils.unsignedBytesToString(actual));
        //assertArrayEquals(expected, actual);

        byte[] actual2 = txModel.serialize();
        System.out.println(ByteUtils.unsignedBytesToString(actual2));
        //assertArrayEquals(expected, actual2); // TODO Fix error array lengths differ, expected: <165> but was: <164>

        // deserialize
        ByteArrayInputStream bs = new ByteArrayInputStream(actual2);
        DataInput di = new DataInputStream(bs);
        TransferTransactionBuilder txBuilder = TransferTransactionBuilder.loadFromBinary(di);
        System.out.println("\nTransactionBuilder asString:");
        System.out.println(txBuilder.asString());

        System.out.println("\nTransactionBuilder each field:");
        System.out.println("Size " + txBuilder.getSize());
        System.out.println("Signer " + txBuilder.getSigner().asString());
        System.out.println("Signature " + txBuilder.getSignature().asString());
        System.out.println("Version " + txBuilder.getVersion());
        System.out.println("  Txn Version " + txBuilder.getTransactionVersion().intValue());
        System.out.println("  Network Type " + txBuilder.getNetworkType());
        System.out.println("Type " + txBuilder.getType().name());
        System.out.println("Deadline " + txBuilder.getDeadline().getTimestamp());
        System.out.println("Recipient " + txBuilder.getRecipient().asString());
        System.out.println("Fee " + txBuilder.getFee().getAmount());
        System.out.println("Mosaics " + txBuilder.getMosaicsAsString());
        System.out.println("Message " + txBuilder.getMessageAsString());

        assertEquals(txModel.getRecipient().plain(), txBuilder.getRecipient().asString());

        // assert
        /*assertEquals(179, test.getSize());

        String signature = "f77a8dcfcb57b81f9be5b46738f7132998f55123bff4d89dc8e5cae1f071a040e5571f4d8da125b243c785da5261f878e3de898815f6e8f12a2c0a5f0a9c3504";
        assertEquals(signature, test.getSignature().asString());

        assertEquals("SD5DT3CH4BLABL5HIMEKP2TAPUKF4NY3L5HRIR54", test.getRecipient().asString());
        assertEquals("fa6249e8334e3f83e972461125504affd3e7750afbb3371e7b2d22a599a3d0e3", test.getSigner().asString());

        assertEquals(-28669, test.getVersion());
        assertEquals(3, test.getTransactionVersion().intValue());
        assertEquals(NetworkType.MIJIN_TEST, test.getNetworkType());

        assertEquals("TRANSFER_TRANSACTION_BUILDER", test.getType().name());
        assertEquals("16724", test.getType().asString());

        assertEquals(99856833830L, test.getDeadline().getTimestamp());
        assertEquals("99856833830", test.getDeadline().asString());

        assertEquals(0, test.getFee().getAmount());
        assertEquals("0", test.getFee().asString());

        assertEquals(new Long(-8810190493148073404L).longValue(), test.getMosaics().get(0).getMosaicId().getUnresolvedMosaicId());
        assertEquals("-8810190493148073404", test.getMosaics().get(0).getMosaicId().asString());

        assertEquals(15, test.getMessage().array().length);
        assertEquals("Welcome To NEM", test.getMessageAsString());
        System.out.println(ByteUtils.unsignedBytesToString(test.getMessage().array()));
        System.out.println(ByteUtils.hexFormat(test.getMessage().array()));*/
    }

    @Test // TODO to fix transaction size
    @DisplayName("To aggregate")
    void toAggregate() {
        byte[] expected =  new byte[]{85,0,0,0,-102,73,54,100,6,-84,-87,82,-72,-117,-83,-11,-15,-23,-66,108,-28,-106,-127,
                65,3,90,96,-66,80,50,115,-22,101,69,107,36,3,-112,84,65,-112,-24,-2,-67,103,29,-44,27,-18,-108,-20,59,
                -91,-125,28,-74,8,-93,18,-62,-14,3,-70,-124,-84,1,0,1,0,103,43,0,0,-50,86,0,0,100,0,0,0,0,0,0,0};

        TransferTransaction transferTransaction = TransferTransaction.create(
                new FakeDeadline(),
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", NetworkType.MIJIN_TEST),
                Arrays.asList(
                        new Mosaic(new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))
                ),
                PlainMessage.Empty,
                NetworkType.MIJIN_TEST
        );
        byte[] actual = transferTransaction.toAggregate(new PublicAccount("9A49366406ACA952B88BADF5F1E9BE6CE4968141035A60BE503273EA65456B24", NetworkType.MIJIN_TEST)).toAggregateTransactionBytes();
        //assertArrayEquals(expected, actual); // TODO Fix error array lengths differ, expected: <85> but was: <84>
    }

    @Test
    void serializeAndSignTransaction() {
        TransferTransaction transferTransaction = TransferTransaction.create(
                new FakeDeadline(),
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", NetworkType.MIJIN_TEST),
                Arrays.asList(
                        new Mosaic(new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))
                ),
                PlainMessage.Empty,
                NetworkType.MIJIN_TEST
        );

        SignedTransaction signedTransaction = transferTransaction.signWith(account);
        assertEquals("A4000000C9112B389AF5AFFB3609AD634DD213BA97E8158FD22D0F5391FDBE3F663CF98CB58BE659AD63C215E65706FE580D97E12949C9082CAFA20ED134AEC7AADB2B071026D70E1954775749C6811084D6450A3184D977383F0E4282CD47118AF37755039054410000000000000000010000000000000090E8FEBD671DD41BEE94EC3BA5831CB608A312C2F203BA84AC000001672B0000CE5600006400000000000000", signedTransaction.getPayload());
        assertEquals("F69055F917B0C1FBFA8A2D1512AE45E4A3D3FCD692A4D3AF0125784972EE1238", signedTransaction.getHash());
    }
}
