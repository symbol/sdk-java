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

package io.nem.core.crypto;

import java.util.stream.Stream;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test of SignSchema.
 */
public class SignSchemaTest {

    private static Stream<Arguments> params() {
        return Stream.of(
            Arguments.of("227F", SignSchema.SHA3, true,
                "dc229a6d2bb1ee8ce10e5b283254c68b4ee8ab8a28fa078f6c47ddd3d2bb25ee1cdb45f58b6fb2bb164cd5652ba482e6b44beeca293a2b24b70cdf9fe8d4051c"),
            Arguments.of("227F", SignSchema.KECCAK, true,
                "764011e5b78404847b0a0a55f3a19c3db5401889ff438fc950537797baf42d7724ed681857bfe632cf5a132fa43dd881dbf15e4d11f518acb7fd03cacb81177a"),
            Arguments.of("AAAA", SignSchema.SHA3, true,
                "e192911d630f8fcad20b896b9d42f7a79c9fe2146bc8543ab4dcf7263e119215a741a9e774d97c3ccd5a63c484787903f9cb694e22c3f865f866a4f93537eb23"),
            Arguments.of("AAAA", SignSchema.KECCAK, true,
                "e9cc94aeeab674586e0d62ad5f7dab2678dbdf43b73f13debdd014ed5a0c68ca18a35c6a68c9b8c7a6bd3d62a2d94f492b9f61837e985d80217f4b12ce0bd4c9"),
            Arguments.of("BBADABA123", SignSchema.SHA3, true,
                "800df91a0217b997945f94dbd62c2b278925a56f040ebbc677671e396e7e38996992ee527087800b2bb5cdb1cc29658afd8ba49f734e2e17c11b6dffacdd2de6"),
            Arguments.of("BBADABA123", SignSchema.KECCAK, true,
                "72f35d7c791981554bae85677606e61e1f29e70e0d8d7f288af795933f03a6c2b5fccdae53b437238df35cd531cfaac5fb9d4a5590d764adb8c5dec315ab80bd"),

            Arguments.of("227F", SignSchema.SHA3, false,
                "7f735e6b0665ceb120bff1bc1478ef2684bace93e82d5ff6d6e5066381bb365e"),
            Arguments.of("227F", SignSchema.KECCAK, false,
                "8b768bd38b5ff80edb8a9aeb460606a682580616d512ff566d0176b1c8fc1034"),
            Arguments.of("AAAA", SignSchema.SHA3, false,
                "4ee18b807b7dfa443a9d87dd51bc03d868b1cde26581c092ca57a366b8b408ca"),
            Arguments.of("AAAA", SignSchema.KECCAK, false,
                "6330b989705733cc5c1f7285b8a5b892e08be86ed6fbe9d254713a4277bc5bd2"),
            Arguments.of("BBADABA123", SignSchema.SHA3, false,
                "faff241e629dfc621077481a4fec760a86675f74fba39a3c90587f0cefe177f4"),
            Arguments.of("BBADABA123", SignSchema.KECCAK, false,
                "9aad08fdd5ee6599b94c0440b81d5fddc8d03882f1856d72b38d72f743123304")
        );
    }


    private static Stream<Arguments> reverse() {
        return Stream.of(
            Arguments.of("575dbb3062267eff57c970a336ebbc8fbcfe12c5bd3ed7bc11eb0481d7704ced",
                "ED4C70D78104EB11BCD73EBDC512FEBC8FBCEB36A370C957FF7E266230BB5D57"),

            Arguments.of("5b0e3fa5d3b49a79022d7c1e121ba1cbbf4db5821f47ab8c708ef88defc29bfe",
                "FE9BC2EF8DF88E708CAB471F82B54DBFCBA11B121E7C2D02799AB4D3A53F0E5B"),

            Arguments.of("738ba9bb9110aea8f15caa353aca5653b4bdfca1db9f34d0efed2ce1325aeeda",
                "DAEE5A32E12CEDEFD0349FDBA1FCBDB45356CA3A35AA5CF1A8AE1091BBA98B73"),

            Arguments.of("e8bf9bc0f35c12d8c8bf94dd3a8b5b4034f1063948e3cc5304e55e31aa4b95a6",
                "A6954BAA315EE50453CCE3483906F134405B8B3ADD94BFC8D8125CF3C09BBFE8"),

            Arguments.of("c325ea529674396db5675939e7988883d59a5fc17a28ca977e3ba85370232a83",
                "832A237053A83B7E97CA287AC15F9AD5838898E7395967B56D39749652EA25C3"),

            Arguments.of("abf4cf55a2b3f742d7543d9cc17f50447b969e6e06f5ea9195d428ab12b7318d",
                "8D31B712AB28D49591EAF5066E9E967B44507FC19C3D54D742F7B3A255CFF4AB"),

            Arguments.of("ABF4CF55A2B3F742D7543D9CC17F50447B969E6E06F5EA9195D428AB12B7318D",
                "8D31B712AB28D49591EAF5066E9E967B44507FC19C3D54D742F7B3A255CFF4AB"),

            Arguments.of("ABF4CF55A2B3F742D7543D9CC17F50447B969E6E06F5EA9195D428AB12B7318D",
                "8D31B712AB28D49591EAF5066E9E967B44507FC19C3D54D742F7B3A255CFF4AB"),

            Arguments.of("c325ea529674396db5675939e7988883d59a5fc17a28ca977e3ba85370232a83",
                "832A237053A83B7E97CA287AC15F9AD5838898E7395967B56D39749652EA25C3"),

            Arguments.of("6aa6dad25d3acb3385d5643293133936cdddd7f7e11818771db1ff2f9d3f9215",
                "15923F9D2FFFB11D771818E1F7D7DDCD363913933264D58533CB3A5DD2DAA66A")
        );
    }

    @ParameterizedTest
    @MethodSource("reverse")
    public void shouldReverse(String input, String expected) {
        Assertions.assertEquals(expected,
            Hex.toHexString(SignSchema.reverse(Hex.decode(input))).toUpperCase());
    }

    @ParameterizedTest
    @MethodSource("params")
    public void shouldGetHasherHash(String input, SignSchema signSchema,
        boolean longSize, String expected) {
        Assertions.assertEquals(expected,
            Hex.toHexString(SignSchema.getHasher(signSchema, longSize).hash(Hex.decode(input))));
    }


    @ParameterizedTest
    @MethodSource("params")
    public void shouldHashAccordingToSize(String input, SignSchema signSchema,
        boolean longSize, String expected) {
        byte[] hexInput = Hex.decode(input);
        byte[] hexOutput = longSize ? SignSchema.toHashLong(signSchema, hexInput)
            : SignSchema.toHashShort(signSchema, hexInput);
        Assertions.assertEquals(expected, Hex.toHexString(hexOutput));
    }

}
