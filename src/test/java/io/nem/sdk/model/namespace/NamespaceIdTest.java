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

package io.nem.sdk.model.namespace;

import io.nem.sdk.model.transaction.UInt64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class NamespaceIdTest {

    static Stream<Arguments> provider() {
        return Stream.of(
                Arguments.of("84b3552d375ffa4b", "929036875" , "2226345261"), // new NamespaceId('nem')
                Arguments.of("f8495aee892fa108", "2301600008", "4165556974"), // new NamespaceId('nem.owner.test1')
                Arguments.of("abaef4e86505811f", "1694859551", "2880369896"), // new NamespaceId('nem.owner.test2')
                Arguments.of("aeb8c92b0a1c2d55", "169618773" , "2931345707"), // new NamespaceId('nem.owner.test3')
                Arguments.of("90e09ad44014cabf", "1075104447", "2430638804"), // new NamespaceId('nem.owner.test4')
                Arguments.of("ab114281960bf1cc", "2517365196", "2870035073")  // new NamespaceId('nem.owner.test5')
        );
    }

    @ParameterizedTest
    @MethodSource("provider")
    void createNamespaceIdsFromUInt64LowerAndHigher(String expectedIdAsHex, String lower, String higher) {
        NamespaceId namespaceId = new NamespaceId(UInt64.fromLowerAndHigher(new Long(lower), new Long(higher)));
        assertEquals(expectedIdAsHex, namespaceId.getIdAsHex());
    }

    @Test
    void createANamespaceIdFromRootNamespaceNameViaConstructor() {
        NamespaceId namespaceId = new NamespaceId("nem");
        assertEquals(namespaceId.getId(), new BigInteger("-8884663987180930485"));
        assertEquals(namespaceId.getFullName().get(), "nem");
    }

    @Test
    void createANamespaceIdFromSubNamespacePathViaConstructor() {
        NamespaceId namespaceId = new NamespaceId("nem.subnem");
        assertEquals(namespaceId.getId(), new BigInteger("16440672666685223858"));
        assertEquals(namespaceId.getFullName().get(), "nem.subnem");
    }

    @Test
    void createANamespaceIdFromSubNamespaceNameAndParentNamespaceNameViaConstructor() {
        NamespaceId namespaceId = new NamespaceId("subnem", "nem");
        assertEquals(namespaceId.getId(), new BigInteger("16440672666685223858"));
        assertEquals(namespaceId.getFullName().get(), "nem.subnem");
    }

    @Test
    void createANamespaceIdFromSubNamespaceNameAndParentNamespaceName2ViaConstructor() {
        NamespaceId namespaceId = new NamespaceId("subsubnem", "nem.subnem");
        NamespaceId parentId = new NamespaceId("subnem", "nem");
        NamespaceId namespaceId2 = new NamespaceId("subsubnem", parentId.getId());

        assertEquals(new BigInteger("10592058992486201054"), namespaceId.getId());
        assertEquals("nem.subnem.subsubnem", namespaceId.getFullName().get());
        assertEquals(namespaceId2.getId(), namespaceId.getId());
    }

    @Test
    void createASubNamespaceIdFromSubNamespaceNameAndParentIdViaConstructor() {
        NamespaceId namespaceId = new NamespaceId("subnem", new BigInteger("-8884663987180930485"));
        assertEquals(namespaceId.getId(), new BigInteger("16440672666685223858"));
        assertEquals(namespaceId.getFullName().get(), "subnem");
    }

    @Test
    void createNamespacePathArray() {
        List<BigInteger> path = NamespaceId.getNamespacePath("nem.subnem");
        assertEquals(path.get(0), new BigInteger("-8884663987180930485"));
        assertEquals(path.get(1), new BigInteger("16440672666685223858"));
    }

    @Test
    void createANamespaceIdFromIdViaConstructor() {
        NamespaceId namespaceId = new NamespaceId(new BigInteger("-8884663987180930485"));
        assertEquals(namespaceId.getId(), new BigInteger("-8884663987180930485"));
        assertFalse(namespaceId.getFullName().isPresent());
    }

    @Test
    void shouldCompareNamespaceIdsForEquality() {
        NamespaceId namespaceId = new NamespaceId(new BigInteger("-8884663987180930485"));
        NamespaceId namespaceId2 = new NamespaceId(new BigInteger("-8884663987180930485"));
        assertTrue(namespaceId.equals(namespaceId2));
    }
}
