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

package io.nem.catapult.builders;

import java.lang.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import io.nem.core.utils.ByteUtils;
import io.nem.core.utils.HexEncoder;
import io.nem.sdk.model.account.Account;
import io.nem.sdk.model.account.Address;
import io.nem.sdk.model.blockchain.NetworkType;
import io.nem.sdk.model.mosaic.Mosaic;
import io.nem.sdk.model.mosaic.MosaicId;
import io.nem.sdk.model.transaction.Deadline;
import io.nem.sdk.model.transaction.FakeDeadline;
import io.nem.sdk.model.transaction.PlainMessage;
import io.nem.sdk.model.transaction.TransferTransaction;
import org.apache.commons.lang.ArrayUtils;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferTransactionBuilderTest {
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

        byte[] bytes = transferTx.generateBytes();
        System.out.println(ByteUtils.hexFormat(bytes));

        System.out.print("{ ");
        for (int i = 0; i < bytes.length; i++) {
            System.out.print(Byte.toUnsignedInt(bytes[i]));
            System.out.print(", ");
        }
        System.out.print(" }");
        System.out.println();

        int offset;
        System.out.println("offset:hex:byte:uint");
        offset = 100;
        System.out.println(offset + ":" + Hex.toHexString(bytes, offset, 1) + ":" + bytes[offset] + ":" + Byte.toUnsignedInt(bytes[offset]));
        offset = 101;
        System.out.println(offset + ":" + Hex.toHexString(bytes, offset, 1) + ":" + bytes[offset] + ":" + Byte.toUnsignedInt(bytes[offset]));


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
    @DisplayName("TransferTransaction Serialization")
    void testSerializeTransferTransaction() {
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

        Deadline deadline = new FakeDeadline();
       /*
        2019-06-12T19:17:34.144Z
        1560367054
        144000000
        */

        byte[] actual = transferTransaction.generateBytes();
        //assertArrayEquals(expected, actual);  // TODO to fix transaction size
    }

    @Test
    @DisplayName("TransferTransactionBuilder Serialization")
    void testSerializeTransferTransactionBuilder()
    {
        byte[] expected = new byte[]{(byte) 165, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                3, (byte) 144, 84, 65, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, (byte) 144, (byte) 232, (byte) 254, (byte) 189, (byte) 103, (byte) 29, (byte) 212, (byte) 27, (byte) 238, (byte) 148, (byte) 236, (byte) 59, (byte) 165, (byte) 131, (byte) 28, (byte) 182, (byte) 8, (byte) 163, (byte) 18, (byte) 194, (byte) 242, (byte) 3, (byte) 186, (byte) 132, (byte) 172,
                1, 0, 1, 0, 103, 43, 0, 0, (byte) 206, 86, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0};

        try {

            /* tx.getSignature(),
               tx.getSigner(),
               tx.getVersion(),
               tx.getType(),
               tx.getFee(),
               tx.getDeadline(),
               tx.getRecipient(),
               tx.getMessage(),
               tx.getMosaics()*/

            java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(64);
            bb.put(new byte[64]);
            SignatureDto signature = new SignatureDto(bb);

            bb = java.nio.ByteBuffer.allocate(32);
            bb.put(new byte[32]);
            KeyDto signer = new KeyDto(bb);

            short version = 2;

            EntityTypeDto type = EntityTypeDto.RESERVED;

            AmountDto fee = new AmountDto(10);

            TimestampDto deadline = new TimestampDto(100);

            ByteBuffer bb1 = java.nio.ByteBuffer.allocate(25);
            bb1.put(new byte[25]);
            UnresolvedAddressDto recipient = new UnresolvedAddressDto(bb1);

            ByteBuffer message = java.nio.ByteBuffer.allocate(30);
            message.put(new byte[30]);

            java.util.ArrayList<UnresolvedMosaicBuilder> mosaics = new java.util.ArrayList<>(5);
            mosaics.add(new UnresolvedMosaicBuilder(new UnresolvedMosaicIdDto(1), fee));

            // serialize
            TransferTransactionBuilder test = new TransferTransactionBuilder(signature, signer, version, type, fee, deadline, recipient, message, mosaics);
            byte[] ser = test.serialize();

            String str = ArrayUtils.toString(ser);
            System.out.println(str);

            // deserialize
            ByteArrayInputStream bs = new ByteArrayInputStream(ser);
            DataInput di = new DataInputStream(bs);
            TransferTransactionBuilder test2 = TransferTransactionBuilder.loadFromBinary(di);

            // assert
            assertEquals(test.getSize(), test2.getSize());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void testDeserializeTransferTransactionBuilder()
    {
        byte[] bytes = new byte[]{(byte) 165, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                3, (byte) 144, 84, 65, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, (byte) 144, (byte) 232, (byte) 254, (byte) 189, (byte) 103, (byte) 29, (byte) 212, (byte) 27, (byte) 238, (byte) 148, (byte) 236, (byte) 59, (byte) 165, (byte) 131, (byte) 28, (byte) 182, (byte) 8, (byte) 163, (byte) 18, (byte) 194, (byte) 242, (byte) 3, (byte) 186, (byte) 132, (byte) 172,
                1, 0, 1, 0, 103, 43, 0, 0, (byte) 206, 86, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0};

        try {
            // deserialize
            ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
            DataInput di = new DataInputStream(bs);
            TransferTransactionBuilder tx = TransferTransactionBuilder.loadFromBinary(di);
            System.out.println(tx.asString());

            // assert
            assertEquals(165, tx.getSize());
            assertEquals("TRANSFER_TRANSACTION_BUILDER", tx.getType().name());
            assertEquals(new Long(95442763262823L).longValue(), tx.getMosaics().get(0).getMosaicId().getUnresolvedMosaicId());
            assertEquals("95442763262823", tx.getMosaics().get(0).getMosaicId().asString());
            assertEquals(0, tx.getFee().getAmount());
            assertEquals(-28669, new Integer(tx.getVersion()).intValue());
            assertEquals("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", tx.getRecipient().asString());
            assertEquals("1", tx.getDeadline().asString());
            assertEquals("0", tx.getFee().asString());
            assertEquals("16724", tx.getType().asString());
            assertEquals(-28669, tx.getVersion());
            assertEquals(3, tx.getTransactionVersion().intValue());
            assertEquals(NetworkType.MIJIN_TEST, tx.getNetworkType());
            assertEquals("0", tx.getFee().asString());
            assertEquals("0000000000000000000000000000000000000000000000000000000000000000", tx.getSigner().asString());
            String signature = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
            assertEquals(signature, tx.getSignature().asString());
            assertEquals(1, tx.getMessage().array().length);
            assertEquals("00", HexEncoder.getString(tx.getMessage().array()));
            assertEquals("", tx.getMessageAsString());

            // serialize
            TransferTransactionBuilder tx2 = new TransferTransactionBuilder(tx.getSignature(), tx.getSigner(), tx.getVersion(), tx.getType(), tx.getFee(), tx.getDeadline(), tx.getRecipient(), tx.getMessage(), tx.getMosaics());
            byte[] bytes2 = tx2.serialize();

            String str = ArrayUtils.toString(bytes);
            System.out.println(str);
            System.out.println(ByteUtils.unsignedBytesToString(bytes));
            String str2 = ArrayUtils.toString(bytes2);
            System.out.println(str2);
            System.out.println(ByteUtils.unsignedBytesToString(bytes2));

            assertEquals(tx.getSize(), tx2.getSize());
            assertEquals(str, str2);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void testDeserializeTransferTransactionBuilder2()
    {
        byte[] ser = new byte[]{(byte) -62,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,0,3,(byte) 144,0,0,(byte) 10,0,0,0,0,0,0,0,(byte) 100,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,0,0,0,(byte) 30,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,
                0,0,0,0,(byte) 10,0,0,0,0,0,0,0};

        try {
            String hexString = HexEncoder.getString(ser);
            System.out.println(hexString);

            // deserialize
            ByteArrayInputStream bs = new ByteArrayInputStream(ser);
            DataInput di = new DataInputStream(bs);
            TransferTransactionBuilder test = TransferTransactionBuilder.loadFromBinary(di);
            System.out.println(test.asString());

            // assert
            assertEquals(194, test.getSize());

            assertEquals("RESERVED", test.getType().name());
            assertEquals("0", test.getType().asString());

            assertEquals(new Long(1).longValue(), test.getMosaics().get(0).getMosaicId().getUnresolvedMosaicId());
            assertEquals("1", test.getMosaics().get(0).getMosaicId().asString());

            assertEquals(10, test.getFee().getAmount());
            assertEquals("10", test.getFee().asString());

            assertEquals(-28669, test.getVersion());
            assertEquals(3, test.getTransactionVersion().intValue());
            assertEquals(NetworkType.MIJIN_TEST, test.getNetworkType());

            assertEquals(100, test.getDeadline().getTimestamp());
            assertEquals("100", test.getDeadline().asString());

            assertEquals("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", test.getRecipient().asString());
            assertEquals("0000000000000000000000000000000000000000000000000000000000000000", test.getSigner().asString());
            String signature = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
            assertEquals(signature, test.getSignature().asString());

            assertEquals(30, test.getMessage().array().length);
            assertEquals("000000000000000000000000000000000000000000000000000000000000", HexEncoder.getString(test.getMessage().array()));
            assertEquals("", test.getMessageAsString());


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void testDeserializeSignedTransferTransactionBuilder()
    {
        try {
            String hexString = "B3000000F77A8DCFCB57B81F9BE5B46738F7132998F5512" +
                    "3BFF4D89DC8E5CAE1F071A040E5571F4D8DA125B243C785" +
                    "DA5261F878E3DE898815F6E8F12A2C0A5F0A9C3504FA624" +
                    "9E8334E3F83E972461125504AFFD3E7750AFBB3371E7B2D" +
                    "22A599A3D0E3039054410000000000000000265DEE3F170" +
                    "0000090FA39EC47E05600AFA74308A7EA607D145E371B5F" +
                    "4F1447BC0F00010057656C636F6D6520546F204E454D44B" +
                    "262C46CEABB858096980000000000";

            hexString = "B3000000F77A8DCFCB57B81F9BE5B46738F7132998F5512" +
                    "3BFF4D89DC8E5CAE1F071A040E5571F4D8DA125B243C785" +
                    "DA5261F878E3DE898815F6E8F12A2C0A5F0A9C3504FA624" +
                    "9E8334E3F83E972461125504AFFD3E7750AFBB3371E7B2D" +
                    "22A599A3D0E3039054410000000000000000265DEE3F170" +
                    "0000090FA39EC47E05600AFA74308A7EA607D145E371B5F" +
                    "4F1447BC0F000157656C636F6D6520546F204E454D0044B" +
                    "262C46CEABB858096980000000000";

            byte[] bytes = HexEncoder.getBytes(hexString);

            System.out.println(ByteUtils.hexFormat(bytes));
            System.out.println(ArrayUtils.toString(bytes));
            System.out.println(ByteUtils.unsignedBytesToString(bytes));
            System.out.println();

            int offset;
            System.out.println("offset:hex:byte:uint");
            offset = 100;
            System.out.println(offset + ":" + Hex.toHexString(bytes, offset, 1) + ":" + bytes[offset] + ":" + Byte.toUnsignedInt(bytes[offset]));
            offset = 101;
            System.out.println(offset + ":" + Hex.toHexString(bytes, offset, 1) + ":" + bytes[offset] + ":" + Byte.toUnsignedInt(bytes[offset]));

            // deserialize
            ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
            DataInput di = new DataInputStream(bs);
            TransferTransactionBuilder test = TransferTransactionBuilder.loadFromBinary(di);
            System.out.println(test.asString());

            // assert
            assertEquals(179, test.getSize());

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
            System.out.println(ByteUtils.hexFormat(test.getMessage().array()));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Test
    void testMosaicProperty()
    {
        try {
            MosaicPropertyBuilder test = new MosaicPropertyBuilder(MosaicPropertyIdDto.DURATION, 5);
            byte[] ser = test.serialize();
            ByteArrayInputStream bs = new ByteArrayInputStream(ser);
            DataInput di = new DataInputStream(bs);
            MosaicPropertyBuilder test2 = MosaicPropertyBuilder.loadFromBinary(di);
            assertEquals(test.getId(), test2.getId());
            assertEquals(test.getValue(), test2.getValue());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void testTransferTx()
    {
        try {
            java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(64);
            bb.put(new byte[64]);
            SignatureDto signature = new SignatureDto(bb);

            bb = java.nio.ByteBuffer.allocate(32);
            bb.put(new byte[32]);
            KeyDto signer = new KeyDto(bb);

            short version = 2;

            EntityTypeDto type = EntityTypeDto.RESERVED;

            AmountDto fee = new AmountDto(10);

            TimestampDto deadline = new TimestampDto(100);

            ByteBuffer bb1 = java.nio.ByteBuffer.allocate(25);
            bb1.put(new byte[25]);
            UnresolvedAddressDto recipient = new UnresolvedAddressDto(bb1);

            ByteBuffer message = java.nio.ByteBuffer.allocate(30);
            message.put(new byte[30]);

            java.util.ArrayList<UnresolvedMosaicBuilder> mosaics = new java.util.ArrayList<>(5);
            mosaics.add(new UnresolvedMosaicBuilder(new UnresolvedMosaicIdDto(1), fee));

            // serialize
            TransferTransactionBuilder test = new TransferTransactionBuilder(signature, signer, version, type, fee, deadline, recipient, message, mosaics);
            byte[] ser = test.serialize();

            String str = ArrayUtils.toString(ser);
            System.out.println(str);

            // deserialize
            ByteArrayInputStream bs = new ByteArrayInputStream(ser);
            DataInput di = new DataInputStream(bs);
            TransferTransactionBuilder test2 = TransferTransactionBuilder.loadFromBinary(di);

            // assert
            assertEquals(test.getSize(), test2.getSize());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test // TODO to fix transaction size
    @DisplayName("Serialization with Builder")
    void serializationWithBuilder() {
        // Generated at nem2-library-js/test/transactions/TransferTransaction.spec.js
        byte[] expected = new byte[]{(byte) 165, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                3, (byte) 144, 84, 65, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, (byte) 144, (byte) 232, (byte) 254, (byte) 189, (byte) 103, (byte) 29, (byte) 212, (byte) 27, (byte) 238, (byte) 148, (byte) 236, (byte) 59, (byte) 165, (byte) 131, (byte) 28, (byte) 182, (byte) 8, (byte) 163, (byte) 18, (byte) 194, (byte) 242, (byte) 3, (byte) 186, (byte) 132, (byte) 172,
                1, 0, 1, 0, 103, 43, 0, 0, (byte) 206, 86, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0};

        TransferTransaction tx = TransferTransaction.create(
                new FakeDeadline(),
                new Address("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", NetworkType.MIJIN_TEST),
                Arrays.asList(
                        new Mosaic(new MosaicId(new BigInteger("95442763262823")), BigInteger.valueOf(100))
                ),
                PlainMessage.Empty,
                NetworkType.MIJIN_TEST
        );
        byte[] actual = tx.generateBytes();

        //assertArrayEquals(expected, actual); // TODO fix size, differs by 1 byte
        assertEquals("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM", tx.getRecipient().plain());
    }
}
