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

import io.nem.core.utils.ByteUtils;

/**
 * An abstract message class that serves as the base class of all message types.
 *
 * @since 1.0
 */
public abstract class Message {
    private final int type;
    private final String payload;

    public Message(int type, String payload) {
        this.type = type;
        this.payload = payload;
    }

    /**
     * Returns message type.
     *
     * @return int
     */
    public int getType() {
        return type;
    }

    /**
     * Returns message type as char.
     *
     * @return char
     */
    public char getTypeAsChar() {
        char plain = '\0';
        char encrypted = '\1';

        if (this.type == 0)
            return plain;
        else if (this.type == 1)
            return encrypted;
        else
            return (char)this.type;
    }

    /**
     * Returns message type as byte.
     *
     * @return byte
     */
    public byte getTypeAsByte() {
        byte plain = 0;
        byte encrypted = 1;

        if (this.type == 0)
            return plain;
        else if (this.type == 1)
            return encrypted;
        else
            return (byte) this.type;
    }

    /**
     * Returns message payload.
     *
     * @return String
     */
    public String getPayload() {
        return payload;
    }


    /**
     * Returns message type + payload.
     *
     * @return String
     */
    public String asString() {
        return this.type + this.payload;
    }
}
