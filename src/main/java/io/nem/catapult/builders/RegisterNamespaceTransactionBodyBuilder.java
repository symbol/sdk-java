/**
*** Copyright (c) 2016-present,
*** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
***
*** This file is part of Catapult.
***
*** Catapult is free software: you can redistribute it and/or modify
*** it under the terms of the GNU Lesser General Public License as published by
*** the Free Software Foundation, either version 3 of the License, or
*** (at your option) any later version.
***
*** Catapult is distributed in the hope that it will be useful,
*** but WITHOUT ANY WARRANTY; without even the implied warranty of
*** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
*** GNU Lesser General Public License for more details.
***
*** You should have received a copy of the GNU Lesser General Public License
*** along with Catapult. If not, see <http://www.gnu.org/licenses/>.
**/

package io.nem.catapult.builders;

import java.io.DataInput;
import java.nio.ByteBuffer;

/** Binary layout for a register namespace transaction. */
final class RegisterNamespaceTransactionBodyBuilder {
    /** Type of the registered namespace. */
    private final NamespaceTypeDto namespaceType;
    /** Namespace duration. */
    private BlockDurationDto duration;
    /** Id of the parent namespace. */
    private NamespaceIdDto parentId;
    /** Id of the namespace. */
    private final NamespaceIdDto namespaceId;
    /** Namespace name. */
    private final ByteBuffer name;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected RegisterNamespaceTransactionBodyBuilder(final DataInput stream) {
        try {
            this.namespaceType = NamespaceTypeDto.loadFromBinary(stream);
            if (this.namespaceType == NamespaceTypeDto.ROOT) {
                this.duration = BlockDurationDto.loadFromBinary(stream);
                this.parentId = null;
            }
            if (this.namespaceType == NamespaceTypeDto.CHILD) {
                this.parentId = NamespaceIdDto.loadFromBinary(stream);
                this.duration = null;
            }
            this.namespaceId = NamespaceIdDto.loadFromBinary(stream);
            final byte namespaceNameSize = stream.readByte();
            this.name = ByteBuffer.allocate(namespaceNameSize);
            stream.readFully(this.name.array());
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Constructor.
     *
     * @param duration Namespace duration.
     * @param namespaceId Id of the namespace.
     * @param name Namespace name.
     */
    protected RegisterNamespaceTransactionBodyBuilder(final BlockDurationDto duration, final NamespaceIdDto namespaceId, final ByteBuffer name) {
        GeneratorUtils.notNull(duration, "duration is null");
        GeneratorUtils.notNull(namespaceId, "namespaceId is null");
        GeneratorUtils.notNull(name, "name is null");
        this.duration = duration;
        this.namespaceId = namespaceId;
        this.name = name;
        this.namespaceType = NamespaceTypeDto.ROOT;
        this.parentId = null;
    }

    /**
     * Constructor.
     *
     * @param parentId Id of the parent namespace.
     * @param namespaceId Id of the namespace.
     * @param name Namespace name.
     */
    protected RegisterNamespaceTransactionBodyBuilder(final NamespaceIdDto parentId, final NamespaceIdDto namespaceId, final ByteBuffer name) {
        GeneratorUtils.notNull(parentId, "parentId is null");
        GeneratorUtils.notNull(namespaceId, "namespaceId is null");
        GeneratorUtils.notNull(name, "name is null");
        this.parentId = parentId;
        this.namespaceId = namespaceId;
        this.name = name;
        this.namespaceType = NamespaceTypeDto.CHILD;
        this.duration = null;
    }

    /**
     * Creates an instance of RegisterNamespaceTransactionBodyBuilder.
     *
     * @param duration Namespace duration.
     * @param namespaceId Id of the namespace.
     * @param name Namespace name.
     * @return Instance of RegisterNamespaceTransactionBodyBuilder.
     */
    public static RegisterNamespaceTransactionBodyBuilder create(final BlockDurationDto duration, final NamespaceIdDto namespaceId, final ByteBuffer name) {
        return new RegisterNamespaceTransactionBodyBuilder(duration, namespaceId, name);
    }

    /**
     * Creates an instance of RegisterNamespaceTransactionBodyBuilder.
     *
     * @param parentId Id of the parent namespace.
     * @param namespaceId Id of the namespace.
     * @param name Namespace name.
     * @return Instance of RegisterNamespaceTransactionBodyBuilder.
     */
    public static RegisterNamespaceTransactionBodyBuilder create(final NamespaceIdDto parentId, final NamespaceIdDto namespaceId, final ByteBuffer name) {
        return new RegisterNamespaceTransactionBodyBuilder(parentId, namespaceId, name);
    }

    /**
     * Gets type of the registered namespace.
     *
     * @return Type of the registered namespace.
     */
    public NamespaceTypeDto getNamespaceType() {
        return this.namespaceType;
    }

    /**
     * Gets namespace duration.
     *
     * @return Namespace duration.
     */
    public BlockDurationDto getDuration() {
        if (this.namespaceType != NamespaceTypeDto.ROOT) {
            throw new java.lang.IllegalStateException("namespaceType is not set to ROOT.");
        }
        return this.duration;
    }

    /**
     * Gets id of the parent namespace.
     *
     * @return Id of the parent namespace.
     */
    public NamespaceIdDto getParentId() {
        if (this.namespaceType != NamespaceTypeDto.CHILD) {
            throw new java.lang.IllegalStateException("namespaceType is not set to CHILD.");
        }
        return this.parentId;
    }

    /**
     * Gets id of the namespace.
     *
     * @return Id of the namespace.
     */
    public NamespaceIdDto getNamespaceId() {
        return this.namespaceId;
    }

    /**
     * Gets namespace name.
     *
     * @return Namespace name.
     */
    public ByteBuffer getName() {
        return this.name;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.namespaceType.getSize();
        if (this.namespaceType == NamespaceTypeDto.ROOT) {
            size += this.duration.getSize();
        }
        if (this.namespaceType == NamespaceTypeDto.CHILD) {
            size += this.parentId.getSize();
        }
        size += this.namespaceId.getSize();
        size += 1; // namespaceNameSize
        size += this.name.array().length;
        return size;
    }

    /**
     * Creates an instance of RegisterNamespaceTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of RegisterNamespaceTransactionBodyBuilder.
     */
    public static RegisterNamespaceTransactionBodyBuilder loadFromBinary(final DataInput stream) {
        return new RegisterNamespaceTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] namespaceTypeBytes = this.namespaceType.serialize();
            dataOutputStream.write(namespaceTypeBytes, 0, namespaceTypeBytes.length);
            if (this.namespaceType == NamespaceTypeDto.ROOT) {
                final byte[] durationBytes = this.duration.serialize();
                dataOutputStream.write(durationBytes, 0, durationBytes.length);
            }
            if (this.namespaceType == NamespaceTypeDto.CHILD) {
                final byte[] parentIdBytes = this.parentId.serialize();
                dataOutputStream.write(parentIdBytes, 0, parentIdBytes.length);
            }
            final byte[] namespaceIdBytes = this.namespaceId.serialize();
            dataOutputStream.write(namespaceIdBytes, 0, namespaceIdBytes.length);
            dataOutputStream.writeByte((byte) this.name.array().length);
            dataOutputStream.write(this.name.array(), 0, this.name.array().length);
        });
    }
}
