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

package io.nem.core.model;

import io.nem.sdk.model.account.PublicAccount;
import io.nem.sdk.model.blockchain.NetworkType;
import java.util.HashSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PublicAccountTest {


    @Test
    void shouldConstruct() {
        PublicAccount account1 = PublicAccount.createFromPublicKey(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            NetworkType.MIJIN_TEST);

        Assertions.assertEquals("SDWGJE7XOYRX5RQMMLWF4TE7U5Y2HUYBRDVX2OJE",
            account1.getAddress().plain());
        Assertions.assertEquals("SDWGJE-7XOYRX-5RQMML-WF4TE7-U5Y2HU-YBRDVX-2OJE",
            account1.getAddress().pretty());
        Assertions.assertEquals(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            account1.getPublicKey().toString());
    }

    @Test
    void shouldBeEquals() {
        PublicAccount account1 = PublicAccount.createFromPublicKey(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            NetworkType.MIJIN_TEST);

        PublicAccount account2 = PublicAccount.createFromPublicKey(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            NetworkType.MIJIN_TEST);

        PublicAccount account3 = PublicAccount.createFromPublicKey(
            "A5F82EC8EBB341427B6785C8111906CD0DF18838FB11B51CE0E18B5E79DFF630",
            NetworkType.MAIN_NET);

        Assertions.assertEquals(account1, account2);
        Assertions.assertEquals(account1.hashCode(), account2.hashCode());
        Assertions.assertNotEquals(account1, account3);

        Assertions.assertNotEquals(account1, new HashSet<>());
    }
}
