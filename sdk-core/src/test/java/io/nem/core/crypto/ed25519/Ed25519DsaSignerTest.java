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

package io.nem.core.crypto.ed25519;

import io.nem.core.crypto.CryptoEngine;
import io.nem.core.crypto.CryptoEngines;
import io.nem.core.crypto.CryptoException;
import io.nem.core.crypto.DsaSigner;
import io.nem.core.crypto.DsaSignerTest;
import io.nem.core.crypto.KeyPair;
import io.nem.core.crypto.PublicKey;
import io.nem.core.crypto.SignSchema;
import io.nem.core.crypto.Signature;
import io.nem.core.crypto.ed25519.arithmetic.MathUtils;
import io.nem.sdk.infrastructure.RandomUtils;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;

public class Ed25519DsaSignerTest extends DsaSignerTest {

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void isCanonicalReturnsFalseForNonCanonicalSignature(SignSchema signSchema) {
        // Arrange:
        final CryptoEngine engine = this.getCryptoEngine();
        final KeyPair kp = KeyPair.random(engine, signSchema);
        final DsaSigner dsaSigner = this.getDsaSigner(kp, signSchema);
        final byte[] input = RandomUtils.generateRandomBytes();

        // Act:
        final Signature signature = dsaSigner.sign(input);
        final BigInteger nonCanonicalS = engine.getCurve().getGroupOrder().add(signature.getS());
        final Signature nonCanonicalSignature = new Signature(signature.getR(), nonCanonicalS);

        // Assert:
        Assertions.assertFalse(dsaSigner.isCanonicalSignature(nonCanonicalSignature));
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void makeCanonicalMakesNonCanonicalSignatureCanonical(SignSchema signSchema) {
        // Arrange:
        final CryptoEngine engine = this.getCryptoEngine();
        final KeyPair kp = KeyPair.random(engine, signSchema);
        final DsaSigner dsaSigner = this.getDsaSigner(kp, signSchema);
        final byte[] input = RandomUtils.generateRandomBytes();

        // Act:
        final Signature signature = dsaSigner.sign(input);
        final BigInteger nonCanonicalS = engine.getCurve().getGroupOrder().add(signature.getS());
        final Signature nonCanonicalSignature = new Signature(signature.getR(), nonCanonicalS);
        Assertions.assertFalse(dsaSigner.isCanonicalSignature(nonCanonicalSignature));
        final Signature canonicalSignature = dsaSigner
            .makeSignatureCanonical(nonCanonicalSignature);

        // Assert:
        Assertions.assertTrue(dsaSigner.isCanonicalSignature(canonicalSignature));
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void replacingRWithGroupOrderPlusRInSignatureRuinsSignature(SignSchema signSchema) {
        // Arrange:
        final CryptoEngine engine = this.getCryptoEngine();
        final BigInteger groupOrder = engine.getCurve().getGroupOrder();
        final KeyPair kp = KeyPair.random(engine, signSchema);
        final DsaSigner dsaSigner = this.getDsaSigner(kp, signSchema);
        Signature signature;
        byte[] input;
        while (true) {
            input = RandomUtils.generateRandomBytes();
            signature = dsaSigner.sign(input);
            if (signature.getR().add(groupOrder).compareTo(BigInteger.ONE.shiftLeft(256)) < 0) {
                break;
            }
        }

        // Act:
        final Signature signature2 = new Signature(groupOrder.add(signature.getR()),
            signature.getS());

        // Assert:
        Assertions.assertFalse(dsaSigner.verify(input, signature2));
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void signReturnsExpectedSignature(SignSchema signSchema) {
        // Arrange:
        final CryptoEngine engine = this.getCryptoEngine();
        final KeyPair keyPair = KeyPair.random(engine, signSchema);
        for (int i = 0; i < 20; i++) {
            final DsaSigner dsaSigner = this.getDsaSigner(keyPair, signSchema);
            final byte[] input = RandomUtils.generateRandomBytes();

            // Act:
            final Signature signature1 = dsaSigner.sign(input);
            final Signature signature2 = MathUtils.sign(keyPair, input, signSchema);

            // Assert:
            Assertions.assertEquals(signature1, signature2);
        }
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void signReturnsVerifiableSignature(SignSchema signSchema) {
        // Arrange:
        final CryptoEngine engine = this.getCryptoEngine();
        final KeyPair keyPair = KeyPair.random(engine, signSchema);
        for (int i = 0; i < 20; i++) {
            final DsaSigner dsaSigner = this.getDsaSigner(keyPair, signSchema);
            final byte[] input = RandomUtils.generateRandomBytes();

            // Act:
            final Signature signature1 = dsaSigner.sign(input);

            // Assert:
            Assertions.assertTrue(dsaSigner.verify(input, signature1));
        }
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void signThrowsIfGeneratedSignatureIsNotCanonical(SignSchema signSchema) {
        // Arrange:
        final CryptoEngine engine = this.getCryptoEngine();
        final KeyPair keyPair = KeyPair.random(engine, signSchema);
        final Ed25519DsaSigner dsaSigner = new Ed25519DsaSigner(keyPair, signSchema) {
            @Override
            public boolean isCanonicalSignature(Signature signature) {
                return false;
            }
        };
        final byte[] input = RandomUtils.generateRandomBytes();

        // Act:
        Assertions.assertEquals("Generated signature is not canonical",
            Assertions.assertThrows(CryptoException.class, () -> dsaSigner.sign(input))
                .getMessage());
    }

    @ParameterizedTest
    @EnumSource(SignSchema.class)
    public void verifyReturnsFalseIfPublicKeyIsZeroArray(SignSchema signSchema) {
        // Arrange:
        final CryptoEngine engine = this.getCryptoEngine();
        final KeyPair kp = KeyPair.random(engine, signSchema);
        final DsaSigner dsaSigner = this.getDsaSigner(kp, signSchema);
        final byte[] input = RandomUtils.generateRandomBytes();
        final Signature signature = dsaSigner.sign(input);
        final Ed25519DsaSigner dsaSignerWithZeroArrayPublicKey = Mockito
            .mock(Ed25519DsaSigner.class);
        final KeyPair keyPairWithZeroArrayPublicKey = Mockito.mock(KeyPair.class);
        Mockito.when(dsaSignerWithZeroArrayPublicKey.getKeyPair())
            .thenReturn(keyPairWithZeroArrayPublicKey);
        Mockito.when(keyPairWithZeroArrayPublicKey.getPublicKey())
            .thenReturn(new PublicKey(new byte[32]));
        Mockito.when(dsaSignerWithZeroArrayPublicKey.verify(input, signature)).thenCallRealMethod();
        Mockito.when(dsaSignerWithZeroArrayPublicKey.isCanonicalSignature(signature))
            .thenReturn(true);

        // Act:
        final boolean result = dsaSignerWithZeroArrayPublicKey.verify(input, signature);

        // Assert (getKeyPair() would be called more than once if it got beyond the second check):
        Assertions.assertFalse(result);
        Mockito.verify(dsaSignerWithZeroArrayPublicKey, Mockito.times(1))
            .isCanonicalSignature(signature);
        Mockito.verify(dsaSignerWithZeroArrayPublicKey, Mockito.times(1)).getKeyPair();
    }


    @Override
    protected CryptoEngine getCryptoEngine() {
        return CryptoEngines.ed25519Engine();
    }
}
