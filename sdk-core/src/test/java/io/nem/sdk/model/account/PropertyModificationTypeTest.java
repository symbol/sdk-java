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

package io.nem.sdk.model.account;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PropertyModificationType}
 *
 * @author Fernando Boucquez
 */
public class PropertyModificationTypeTest {


    @Test
    public void rawValueOf() {
        Assertions
            .assertEquals(PropertyModificationType.ADD, PropertyModificationType.rawValueOf("0x01"));
        Assertions
            .assertEquals(PropertyModificationType.ADD, PropertyModificationType.rawValueOf("1"));
        Assertions.assertEquals(PropertyModificationType.REMOVE,
            PropertyModificationType.rawValueOf("0x00"));
        Assertions
            .assertEquals(PropertyModificationType.REMOVE, PropertyModificationType.rawValueOf("0"));

        Arrays.stream(PropertyModificationType.values())
            .forEach(t -> Assertions
                .assertEquals(t, PropertyModificationType.rawValueOf(t.getValue().toString())));

    }

    @Test
    void rawValueOfInvalidValueIncorrectNumber() {
        IllegalArgumentException thrown =
            Assertions.assertThrows(IllegalArgumentException.class,
                () -> PropertyModificationType.rawValueOf("3"));

        Assertions.assertEquals("3 is not a valid value", thrown.getMessage());
    }

    @Test
    void rawValueOfInvalidValueNotANumber() {
        IllegalArgumentException thrown =
            Assertions.assertThrows(IllegalArgumentException.class,
                () -> PropertyModificationType.rawValueOf("IMNotANumber"));

        Assertions.assertEquals("IMNotANumber is not a valid value", thrown.getMessage());
    }
}
