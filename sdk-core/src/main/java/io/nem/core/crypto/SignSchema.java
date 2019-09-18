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

/**
 * This enum defines the strategies that can be used when signing and generating public addresses.
 */
public enum SignSchema {

    /**
     * SHA3 hash algorithm without key reversal
     */
    SHA3,
    /**
     * Keccak hash algorithm without key reversal
     */
    KECCAK,

    /**
     * Keccak hash algorithm with key reversal
     *
     * Note: current vector tests are defined using the reversed key version. This sign schema may
     * be removed once vector tests are removed and core confirm that KECCAK is preferred for public
     * and test networks.
     */
    KECCAK_REVERSED_KEY;

    /**
     * It 32 bytes hashes the input according to the schema.
     *
     * @param signSchema the hashed input.
     * @param inputs the inputs
     * @return the hash
     */
    public static byte[] toHashShort(SignSchema signSchema, byte[]... inputs) {
        return getHasher(signSchema, false).hash(inputs);
    }

    /**
     * It 64 bytes hashes the input according to the schema.
     *
     * @param signSchema the hashed input.
     * @param inputs the schema
     * @return the hash
     */
    public static byte[] toHashLong(SignSchema signSchema, byte[]... inputs) {
        return getHasher(signSchema, true).hash(inputs);
    }

    /**
     * It 64 bytes hashes the private key reversing it if necessary.
     *
     * @param privateKey the privateKey
     * @param signSchema the hashed input.
     * @return the hash
     */
    public static byte[] toHash(PrivateKey privateKey, SignSchema signSchema) {
        byte[] secret = privateKey.getBytes();
        if (signSchema == KECCAK_REVERSED_KEY) {
            secret = reverse(secret);
        }
        //Here is where you reverse the secret if necessary.
        return getHasher(signSchema, true).hash(secret);
    }

    /**
     * Reversed convertion an byte array;
     *
     * @@return the reverse of input.
     */
    public static byte[] reverse(byte[] input) {
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i += 1) {
            output[output.length - 1 - i] = input[i];
        }
        return output;
    }

    /**
     * It resolved the righ hasher for the configuration.
     *
     * @param signSchema the {@link SignSchema}
     * @param longSize the size of the hasher.
     * @return the hasher
     */
    public static Hasher getHasher(SignSchema signSchema, boolean longSize) {
        if (signSchema == SignSchema.SHA3 && !longSize) {
            return Hashes::sha3_256;
        }
        if (signSchema == SignSchema.SHA3 && longSize) {
            return Hashes::sha3_512;
        }
        if (signSchema == SignSchema.KECCAK && !longSize) {
            return Hashes::keccak256;
        }
        if (signSchema == SignSchema.KECCAK && longSize) {
            return Hashes::keccak512;
        }
        if (signSchema == SignSchema.KECCAK_REVERSED_KEY && !longSize) {
            return Hashes::keccak256;
        }
        if (signSchema == SignSchema.KECCAK_REVERSED_KEY && longSize) {
            return Hashes::keccak512;
        }
        throw new IllegalStateException("Unknown SignSchema " + signSchema);
    }

    @FunctionalInterface
    public interface Hasher {

        byte[] hash(byte[]... inputs);
    }

}
