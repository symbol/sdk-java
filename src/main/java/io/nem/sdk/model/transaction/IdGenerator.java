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

import io.nem.core.crypto.Hashes;
import io.nem.core.utils.ByteUtils;
import io.nem.sdk.model.mosaic.IllegalIdentifierException;
import org.apache.commons.lang.ArrayUtils;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class IdGenerator {

	public static BigInteger generateMosaicId(byte[] nonce, byte[] publicKey) {
		byte[] hash = IdGenerator.getHashInLittleEndian(nonce, publicKey);
		// Unset the high bit for mosaic id
		hash = (new BigInteger(hash)).and(new BigInteger("7FFFFFFFFFFFFFFF", 16)).toByteArray();
		return new BigInteger(hash);
	}

	public static BigInteger generateNamespaceId(String namespaceName, BigInteger parentId) {
		if (!namespaceName.matches("^[a-z0-9][a-z0-9-_]*$")) {
			throw new IllegalIdentifierException("invalid namespace name");
		}

		byte[] parentIdBytes = new byte[8];
		byte[] bytes = ByteUtils.bigIntToBytes(parentId);
		ByteBuffer.wrap(parentIdBytes).put(bytes);
		ArrayUtils.reverse(parentIdBytes);

		byte[] hash = IdGenerator.getHashInLittleEndian(parentIdBytes, namespaceName.getBytes());
		// Set the high bit for namespace id
		hash = (new BigInteger(hash)).or(new BigInteger("8000000000000000", 16)).toByteArray();
		return new BigInteger(hash);
	}

	public static BigInteger generateNamespaceId(String namespaceName, String parentNamespaceName) {
		return IdGenerator.generateNamespaceId(parentNamespaceName + "." + namespaceName);
	}

	public static BigInteger generateNamespaceId(String namespacePath) {
		List<BigInteger> namespaceList = generateNamespacePath(namespacePath);
		return namespaceList.get(namespaceList.size() - 1);
	}

	public static List<BigInteger> generateNamespacePath(String namespacePath) {
		String[] parts = namespacePath.split(Pattern.quote("."));
		List<BigInteger> path = new ArrayList<BigInteger>();

		if (parts.length == 0) {
			throw new IllegalIdentifierException("invalid namespace path");
		} else if (parts.length > 3) {
			throw new IllegalIdentifierException("too many parts");
		}

		BigInteger namespaceId = BigInteger.valueOf(0);

		for (int i = 0; i < parts.length; i++) {
			namespaceId = generateNamespaceId(parts[i], namespaceId);
			path.add(namespaceId);
		}
		return path;
	}

	private static byte[] getHashInLittleEndian(final byte[]... inputs) {
		byte[] result = Hashes.sha3_256(inputs);
		result = Arrays.copyOfRange(result, 0, 8);
		ArrayUtils.reverse(result);
		return result;
	}

/*  public static BigInteger generateNamespaceId2(String name, BigInteger parentId) {

        byte[] parentIdBytes = new byte[8];
        ByteBuffer.wrap(parentIdBytes).put(parentId.toByteArray()); // GO
        ArrayUtils.reverse(parentIdBytes);

        byte[] nameBytes = name.getBytes();
        List<byte[]> hashes = IdGenerator.getHashesInLittleEndian(parentIdBytes, nameBytes);

        byte[] low = hashes.get(0);
        byte[] high = hashes.get(1);

        // Set the high bit for namespace id
        high = (new BigInteger(high)).or(new BigInteger("80000000", 16)).toByteArray();

        byte[] last = ArrayUtils.addAll(high, low);

        return new BigInteger(last);
    }

    private static List<byte[]> getHashesInLittleEndian(final byte[]... inputs) {
        byte[] result = Hashes.sha3_256(inputs);

        byte[] low = Arrays.copyOfRange(result, 0, 4);
        byte[] high = Arrays.copyOfRange(result, 4, 8);

        ArrayUtils.reverse(low);
        ArrayUtils.reverse(high);
        List<byte[]> hashes = new ArrayList<>(2);
        hashes.add(low);
        hashes.add(high);
        return hashes;
    }*/
}