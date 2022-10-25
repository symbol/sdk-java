/*
 * Copyright 2020 NEM
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
package io.nem.symbol.sdk.infrastructure;

import io.nem.symbol.catapult.builders.AccountAddressRestrictionTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.AccountKeyLinkTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.AccountMetadataTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.AccountMosaicRestrictionTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.AccountOperationRestrictionTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.AccountRestrictionFlagsDto;
import io.nem.symbol.catapult.builders.AddressAliasTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.AddressDto;
import io.nem.symbol.catapult.builders.AggregateTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.AliasActionDto;
import io.nem.symbol.catapult.builders.AmountDto;
import io.nem.symbol.catapult.builders.BlockDurationDto;
import io.nem.symbol.catapult.builders.CosignatureBuilder;
import io.nem.symbol.catapult.builders.EmbeddedTransactionBuilder;
import io.nem.symbol.catapult.builders.EmbeddedTransactionBuilderHelper;
import io.nem.symbol.catapult.builders.EntityTypeDto;
import io.nem.symbol.catapult.builders.FinalizationEpochDto;
import io.nem.symbol.catapult.builders.Hash256Dto;
import io.nem.symbol.catapult.builders.HashLockTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.KeyDto;
import io.nem.symbol.catapult.builders.LinkActionDto;
import io.nem.symbol.catapult.builders.LockHashAlgorithmDto;
import io.nem.symbol.catapult.builders.MosaicAddressRestrictionTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.MosaicAliasTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.MosaicDefinitionTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.MosaicFlagsDto;
import io.nem.symbol.catapult.builders.MosaicGlobalRestrictionTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.MosaicIdDto;
import io.nem.symbol.catapult.builders.MosaicMetadataTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.MosaicNonceDto;
import io.nem.symbol.catapult.builders.MosaicRestrictionTypeDto;
import io.nem.symbol.catapult.builders.MosaicSupplyChangeActionDto;
import io.nem.symbol.catapult.builders.MosaicSupplyChangeTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.MultisigAccountModificationTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.NamespaceIdDto;
import io.nem.symbol.catapult.builders.NamespaceMetadataTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.NamespaceRegistrationTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.NetworkTypeDto;
import io.nem.symbol.catapult.builders.NodeKeyLinkTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.SecretLockTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.SecretProofTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.Serializer;
import io.nem.symbol.catapult.builders.SignatureDto;
import io.nem.symbol.catapult.builders.TimestampDto;
import io.nem.symbol.catapult.builders.TransactionBuilder;
import io.nem.symbol.catapult.builders.TransactionBuilderHelper;
import io.nem.symbol.catapult.builders.TransferTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.UnresolvedAddressDto;
import io.nem.symbol.catapult.builders.UnresolvedMosaicBuilder;
import io.nem.symbol.catapult.builders.UnresolvedMosaicIdDto;
import io.nem.symbol.catapult.builders.VotingKeyDto;
import io.nem.symbol.catapult.builders.VotingKeyLinkTransactionBodyBuilder;
import io.nem.symbol.catapult.builders.VrfKeyLinkTransactionBodyBuilder;
import io.nem.symbol.core.crypto.PublicKey;
import io.nem.symbol.core.utils.ConvertUtils;
import io.nem.symbol.core.utils.ExceptionUtils;
import io.nem.symbol.core.utils.StringEncoder;
import io.nem.symbol.sdk.api.BinarySerialization;
import io.nem.symbol.sdk.model.account.Address;
import io.nem.symbol.sdk.model.account.PublicAccount;
import io.nem.symbol.sdk.model.account.UnresolvedAddress;
import io.nem.symbol.sdk.model.blockchain.BlockDuration;
import io.nem.symbol.sdk.model.message.Message;
import io.nem.symbol.sdk.model.mosaic.Mosaic;
import io.nem.symbol.sdk.model.mosaic.MosaicFlags;
import io.nem.symbol.sdk.model.mosaic.MosaicId;
import io.nem.symbol.sdk.model.mosaic.MosaicNonce;
import io.nem.symbol.sdk.model.mosaic.MosaicSupplyChangeActionType;
import io.nem.symbol.sdk.model.mosaic.UnresolvedMosaicId;
import io.nem.symbol.sdk.model.namespace.AliasAction;
import io.nem.symbol.sdk.model.namespace.NamespaceId;
import io.nem.symbol.sdk.model.namespace.NamespaceRegistrationType;
import io.nem.symbol.sdk.model.network.NetworkType;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountKeyLinkTransaction;
import io.nem.symbol.sdk.model.transaction.AccountKeyLinkTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.AccountMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountMosaicRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.AccountMosaicRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountMosaicRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountOperationRestrictionFlags;
import io.nem.symbol.sdk.model.transaction.AccountOperationRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.AccountOperationRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AccountRestrictionFlag;
import io.nem.symbol.sdk.model.transaction.AddressAliasTransaction;
import io.nem.symbol.sdk.model.transaction.AddressAliasTransactionFactory;
import io.nem.symbol.sdk.model.transaction.AggregateTransaction;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionCosignature;
import io.nem.symbol.sdk.model.transaction.AggregateTransactionFactory;
import io.nem.symbol.sdk.model.transaction.Deadline;
import io.nem.symbol.sdk.model.transaction.HashLockTransaction;
import io.nem.symbol.sdk.model.transaction.HashLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.LinkAction;
import io.nem.symbol.sdk.model.transaction.LockHashAlgorithm;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicAddressRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicAliasTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicAliasTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicDefinitionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicGlobalRestrictionTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MosaicRestrictionType;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransaction;
import io.nem.symbol.sdk.model.transaction.MosaicSupplyChangeTransactionFactory;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransaction;
import io.nem.symbol.sdk.model.transaction.MultisigAccountModificationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.NamespaceMetadataTransaction;
import io.nem.symbol.sdk.model.transaction.NamespaceMetadataTransactionFactory;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransaction;
import io.nem.symbol.sdk.model.transaction.NamespaceRegistrationTransactionFactory;
import io.nem.symbol.sdk.model.transaction.NodeKeyLinkTransaction;
import io.nem.symbol.sdk.model.transaction.NodeKeyLinkTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SecretLockTransaction;
import io.nem.symbol.sdk.model.transaction.SecretLockTransactionFactory;
import io.nem.symbol.sdk.model.transaction.SecretProofTransaction;
import io.nem.symbol.sdk.model.transaction.SecretProofTransactionFactory;
import io.nem.symbol.sdk.model.transaction.Transaction;
import io.nem.symbol.sdk.model.transaction.TransactionFactory;
import io.nem.symbol.sdk.model.transaction.TransactionType;
import io.nem.symbol.sdk.model.transaction.TransferTransaction;
import io.nem.symbol.sdk.model.transaction.TransferTransactionFactory;
import io.nem.symbol.sdk.model.transaction.VotingKeyLinkTransaction;
import io.nem.symbol.sdk.model.transaction.VotingKeyLinkTransactionFactory;
import io.nem.symbol.sdk.model.transaction.VrfKeyLinkTransaction;
import io.nem.symbol.sdk.model.transaction.VrfKeyLinkTransactionFactory;
import java.io.DataInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Implementation of BinarySerialization. It uses the catbuffer generated builders to deserialize an
 * object.
 */
public class BinarySerializationImpl implements BinarySerialization {

  /** Cached instance. */
  public static final BinarySerialization INSTANCE = new BinarySerializationImpl();

  /** The serializers, one per {@link TransactionType} must be registered. */
  private final Map<Pair<TransactionType, Integer>, TransactionSerializer<?>> serializers =
      new HashMap<>();

  /** Constructor */
  public BinarySerializationImpl() {
    {
		TransactionSerializer<?> serializer = new TransferTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new MosaicSupplyChangeTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new MosaicDefinitionTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new AccountKeyLinkTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new AccountMetadataTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new MosaicMetadataTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new NamespaceMetadataTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new NamespaceRegistrationTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new SecretLockTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new SecretProofTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new AddressAliasTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new MosaicAliasTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new HashLockTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new MultisigAccountModificationTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new MosaicAddressRestrictionTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new MosaicGlobalRestrictionTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new AccountMosaicRestrictionTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new AccountOperationRestrictionTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new AccountAddressRestrictionTransactionSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new NodeKeyLinkTransactionBuilderSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new VotingKeyLinkTransactionBuilderSerializer();
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new VrfKeyLinkTransactionBuilderSerializer();
		register(serializer, serializer.getVersion());
	}

	// beginregion use same objects for OLD version (format has not changed)
	{
		TransactionSerializer<?> serializer = new AggregateTransactionSerializer(TransactionType.AGGREGATE_COMPLETE, this);
		register(serializer, 1);
	}
    {
		TransactionSerializer<?> serializer = new AggregateTransactionSerializer(TransactionType.AGGREGATE_BONDED, this);
		register(serializer, 1);
	}
	// endregion

    {
		TransactionSerializer<?> serializer = new AggregateTransactionSerializer(TransactionType.AGGREGATE_COMPLETE, this);
		register(serializer, serializer.getVersion());
	}
    {
		TransactionSerializer<?> serializer = new AggregateTransactionSerializer(TransactionType.AGGREGATE_BONDED, this);
		register(serializer, serializer.getVersion());
	}
  }

  /** @param serializer the serializer to be registered. */
  private void register(TransactionSerializer<?> serializer, int version) {

    Pair<TransactionType, Integer> pair = Pair.of(serializer.getTransactionType(), version);
    if (serializers.put(pair, serializer) != null) {
      throw new IllegalArgumentException(
          "TransactionSerializer for type "
              + serializer.getTransactionType()
              + " and version "
              + version
              + " was already registered!");
    }
  }

  /**
   * It returns the registered {@link TransactionSerializer} for the given {@link TransactionType}.
   *
   * @param transactionType the transaction type.
   * @param version the transaction version.
   * @param <T> the transaction type
   * @return the {@link TransactionSerializer}
   */
  <T extends Transaction> TransactionSerializer<T> resolveSerializer(
      TransactionType transactionType, int version) {
    @SuppressWarnings("unchecked")
    TransactionSerializer<T> mapper =
        (TransactionSerializer<T>) serializers.get(Pair.of(transactionType, version));
    if (mapper == null) {
      throw new UnsupportedOperationException("Unimplemented Transaction type " + transactionType);
    }
    return mapper;
  }

  /**
   * Serialization basic implementation, it just delegates the work to the transactions.
   *
   * @param transaction the transaction
   * @return the serialized transaction.
   */
  @Override
  public <T extends Transaction> byte[] serialize(T transaction) {
    Validate.notNull(transaction, "Transaction must not be null");
    TransactionBuilder transactionBuilder = getTransactionBuilder(transaction);
    return serializeTransaction(transactionBuilder.serialize(), transaction);
  }

  /**
   * Serialized the transfer transaction to embedded bytes.
   *
   * @param transaction the transaction
   * @param <T> the transaction class
   * @return bytes of the transaction.
   */
  public <T extends Transaction> byte[] serializeEmbedded(T transaction) {
    Validate.notNull(transaction, "Transaction must not be null");
    EmbeddedTransactionBuilder embeddedTransactionBuilder =
        EmbeddedTransactionBuilder.create(
            new KeyDto(getRequiredSignerBytes(transaction.getSigner())),
            transaction.getVersion().byteValue(),
            NetworkTypeDto.rawValueOf((byte) transaction.getNetworkType().getValue()),
            EntityTypeDto.rawValueOf((short) transaction.getType().getValue()));
    return serializeTransaction(embeddedTransactionBuilder.serialize(), transaction);
  }

  /**
   * Creates the right {@link EmbeddedTransactionBuilder} from the transaction
   *
   * @param transaction the transaction
   * @param <T> the transaction class
   * @return the {@link EmbeddedTransactionBuilder}
   */
  private <T extends Transaction> EmbeddedTransactionBuilder toEmbeddedTransactionBuilder(
      T transaction) {
    return EmbeddedTransactionBuilderHelper.loadFromBinary(
        SerializationUtils.toDataInput(serializeEmbedded(transaction)));
  }

  /**
   * Gets the top level {@link TransactionBuilder} for the given transaction
   *
   * @param transaction the transaction
   * @return the top level {@link TransactionBuilder}
   */
  private TransactionBuilder getTransactionBuilder(Transaction transaction) {

    final SignatureDto signatureDto =
        transaction
            .getSignature()
            .map(SerializationUtils::toSignatureDto)
            .orElseGet(() -> new SignatureDto(ByteBuffer.allocate(64)));

    final ByteBuffer signerBuffer =
        transaction
            .getSigner()
            .map(SerializationUtils::toByteBuffer)
            .orElseGet(() -> ByteBuffer.allocate(32));

    int networkTypeValue = transaction.getNetworkType().getValue();
    int typeValue = transaction.getType().getValue();
    byte version = transaction.getVersion().byteValue();
    return TransactionBuilder.create(
        signatureDto,
        new KeyDto(signerBuffer),
        version,
        NetworkTypeDto.rawValueOf((byte) networkTypeValue),
        EntityTypeDto.rawValueOf((short) typeValue),
        SerializationUtils.toAmount(transaction.getMaxFee()),
        new TimestampDto(transaction.getDeadline().getValue()));
  }

  /**
   * This method concats the common bytes to the serialized specific transaction.
   *
   * @param <T> the type of the transaction
   * @param commonBytes the common byte array.
   * @param transaction the transaction.
   * @return the serialized transaction.
   */
  private <T extends Transaction> byte[] serializeTransaction(byte[] commonBytes, T transaction) {
    Validate.isTrue(
        transaction.isTransactionFullyLoaded(),
        "Partially loaded and incomplete transactions cannot be serialized.");
    TransactionSerializer<T> transactionSerializer =
        resolveSerializer(transaction.getType(), transaction.getVersion());
    Validate.isTrue(
        transactionSerializer.getTransactionClass().isAssignableFrom(transaction.getClass()),
        "Invalid TransactionSerializer's transaction class.");
    byte[] transactionBytes = transactionSerializer.toBodyBuilder(transaction).serialize();
    return SerializationUtils.concat(commonBytes, transactionBytes);
  }

  /**
   * It returns the transaction's byte array size useful to calculate its fee.
   *
   * @param <T> the type of the transaction
   * @param transaction the transaction
   * @return the size of the transaction.
   */
  @Override
  public <T extends Transaction> long getSize(T transaction) {
    return getTransactionBuilder(transaction).getSize()
        + resolveSerializer(transaction.getType(), transaction.getVersion())
            .toBodyBuilder(transaction)
            .getSize();
  }

  /**
   * Returns the transaction creator public account.
   *
   * @return the signer public account
   */
  private ByteBuffer getRequiredSignerBytes(Optional<PublicAccount> signer) {
    return signer
        .map(SerializationUtils::toByteBuffer)
        .orElseThrow(() -> new IllegalStateException("SignerBytes is required"));
  }

  /**
   * Deserialization of transactions. All the code related to the deserialization is handled in the
   * class and its helpers. Transaction Model Objects are not polluted with deserialization
   * functionality.
   *
   * @param payload the byte array payload
   * @return the {@link TransactionFactory}.
   */
  @Override
  public TransactionFactory<?> deserializeToFactory(byte[] payload) {
    Validate.notNull(payload, "Payload must not be null");
    DataInputStream stream = SerializationUtils.toDataInput(payload);
    TransactionBuilder builder = TransactionBuilderHelper.loadFromBinary(stream);

    return toTransactionFactory(builder);
  }

  /**
   * Deserialization of transactions. All the code related to the deserialization is handled in the
   * class and its helpers. Transaction Model Objects are not polluted with deserialization
   * functionality.
   *
   * @param payload the byte array payload
   * @return the {@link Transaction}
   */
  @Override
  @SuppressWarnings("squid:S1192")
  public Transaction deserialize(byte[] payload) {
    return deserializeToFactory(payload).build();
  }

  /**
   * It converts a {@link TransactionBuilder} to a {@link Transaction}
   *
   * @param builder the builder
   * @return the {@link Transaction} model.
   */
  private TransactionFactory<?> toTransactionFactory(TransactionBuilder builder) {
    TransactionType transactionType =
        TransactionType.rawValueOf(
            SerializationUtils.shortToUnsignedInt(builder.getType().getValue()));
    NetworkType networkType =
        NetworkType.rawValueOf(
            SerializationUtils.byteToUnsignedInt(builder.getNetwork().getValue()));

    Deadline deadline =
        new Deadline(SerializationUtils.toUnsignedBigInteger(builder.getDeadline().getTimestamp()));

    TransactionFactory<?> factory =
        resolveSerializer(transactionType, builder.getVersion())
            .fromBodyBuilder(networkType, deadline, builder.getBody());

    factory.version(SerializationUtils.byteToUnsignedInt(builder.getVersion()));
    factory.maxFee(SerializationUtils.toUnsignedBigInteger(builder.getFee()));
    if (!areAllZeros(builder.getSignature().getSignature().array())) {
      factory.signature(SerializationUtils.toHexString(builder.getSignature().getSignature()));
    }
    if (!areAllZeros(builder.getSignerPublicKey().getKey().array())) {
      factory.signer(SerializationUtils.toPublicAccount(builder.getSignerPublicKey(), networkType));
    }
    return factory;
  }

  /**
   * @param array the byte array.
   * @return if all the values in the array are zeros
   */
  private boolean areAllZeros(byte[] array) {
    for (byte b : array) {
      if (b != 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * It deserializes a payload of a embedded transaction into a transact model.
   *
   * @param payload the payload as {@link DataInputStream}
   * @return the {@link Transaction} model.
   */
  public Transaction deserializeEmbedded(DataInputStream payload) {

    return ExceptionUtils.propagate(
        () -> {
          Validate.notNull(payload, "Payload must not be null");
          EmbeddedTransactionBuilder builder =
              EmbeddedTransactionBuilderHelper.loadFromBinary(payload);
          return toTransaction(builder);
        });
  }

  /**
   * It converts a {@link EmbeddedTransactionBuilder} to a {@link Transaction}
   *
   * @param builder the builder
   * @return the {@link Transaction} model.
   */
  private Transaction toTransaction(EmbeddedTransactionBuilder builder) {
    TransactionType transactionType =
        TransactionType.rawValueOf(
            SerializationUtils.shortToUnsignedInt(builder.getType().getValue()));
    NetworkType networkType =
        NetworkType.rawValueOf(
            SerializationUtils.byteToUnsignedInt(builder.getNetwork().getValue()));
    TransactionFactory<?> factory =
        resolveSerializer(transactionType, builder.getVersion())
            .fromBodyBuilder(networkType, new Deadline(BigInteger.ZERO), builder.getBody());
    factory.signer(SerializationUtils.toPublicAccount(builder.getSignerPublicKey(), networkType));
    factory.version(SerializationUtils.byteToUnsignedInt(builder.getVersion()));
    return factory.build();
  }

  /**
   * Interface of the serializer helper classes that know how to serialize/deserialize one type of
   * transaction from a payload.
   */
  interface TransactionSerializer<T extends Transaction> {

    /** @return the {@link TransactionType} of the transaction this helper handles. */
    TransactionType getTransactionType();

    /** @return the version of the transaction this helper handles. */
    default int getVersion() {
      return getTransactionType().getCurrentVersion();
    }

    /** @return the transaction class this serializer handles. */
    Class<T> getTransactionClass();

    /**
     * Subclasses would need to create the {@link TransactionFactory} for the handled {@link
     * TransactionType} with just the specific transaction values. Common values like maxFee and
     * deadline are handled at top level, subclasses won't need to duplicate the deserialization
     * efforts.
     *
     * @param networkType the network type
     * @param deadline the deadline.
     * @param transactionBuilder the stream containing just the specific transaction values in the
     *     right order.
     * @return the TransactionFactory of the transaction type this object handles.
     */
    TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder);

    /**
     * Subclasses would need to know how to serialize the internal components of a transaction, the
     * bytes that are serialized after the common attributes like max fee and duration. Subclasses
     * would use catbuffer's transaction body builders.
     *
     * @param transaction the transaction to be serialized
     * @return the catbuffer {@link Serializer}.
     */
    Serializer toBodyBuilder(T transaction);
  }

  private static class TransferTransactionSerializer
      implements TransactionSerializer<TransferTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.TRANSFER;
    }

    @Override
    public Class getTransactionClass() {
      return TransferTransaction.class;
    }

    @Override
    public TransactionFactory<?> fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {
      TransferTransactionBodyBuilder builder =
          ((TransferTransactionBodyBuilder) transactionBuilder);
      byte[] messageArray = builder.getMessage().array();

      UnresolvedAddress recipient =
          SerializationUtils.toUnresolvedAddress(builder.getRecipientAddress());
      List<Mosaic> mosaics =
          builder.getMosaics().stream()
              .map(SerializationUtils::toMosaic)
              .collect(Collectors.toList());
      TransferTransactionFactory factory =
          TransferTransactionFactory.create(networkType, deadline, recipient, mosaics);
      Message.createFromPayload(messageArray).ifPresent(factory::message);
      return factory;
    }

    @Override
    public Serializer toBodyBuilder(TransferTransaction transaction) {
      return TransferTransactionBodyBuilder.create(
          SerializationUtils.toUnresolvedAddress(
              transaction.getRecipient(), transaction.getNetworkType()),
          getUnresolvedMosaicArray(transaction),
          getMessageBuffer(transaction));
    }

    /**
     * Gets mosaic array.
     *
     * @return Mosaic array.
     */
    private List<UnresolvedMosaicBuilder> getUnresolvedMosaicArray(
        TransferTransaction transaction) {
      // Create Mosaics
      final List<UnresolvedMosaicBuilder> unresolvedMosaicList =
          new ArrayList<>(transaction.getMosaics().size());
      // Sort mosaics first
      final List<Mosaic> sortedMosaics =
          transaction.getMosaics().stream()
              .sorted(Comparator.comparing(m -> m.getId().getId()))
              .collect(Collectors.toList());

      for (final Mosaic mosaic : sortedMosaics) {
        final UnresolvedMosaicBuilder mosaicBuilder =
            UnresolvedMosaicBuilder.create(
                new UnresolvedMosaicIdDto(mosaic.getId().getIdAsLong()),
                SerializationUtils.toAmount(mosaic.getAmount()));
        unresolvedMosaicList.add(mosaicBuilder);
      }
      return unresolvedMosaicList;
    }

    /**
     * Gets message buffer.
     *
     * @return Message buffer.
     */
    private ByteBuffer getMessageBuffer(TransferTransaction transaction) {
      return transaction
          .getMessage()
          .map(Message::getPayloadByteBuffer)
          .orElseGet(() -> ByteBuffer.allocate(0));
    }
  }

  private static class MosaicSupplyChangeTransactionSerializer
      implements TransactionSerializer<MosaicSupplyChangeTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.MOSAIC_SUPPLY_CHANGE;
    }

    @Override
    public Class<MosaicSupplyChangeTransaction> getTransactionClass() {
      return MosaicSupplyChangeTransaction.class;
    }

    @Override
    public TransactionFactory<?> fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {
      MosaicSupplyChangeTransactionBodyBuilder builder =
          ((MosaicSupplyChangeTransactionBodyBuilder) transactionBuilder);
      UnresolvedMosaicId mosaicId = SerializationUtils.toUnresolvedMosaicId(builder.getMosaicId());
      MosaicSupplyChangeActionType action =
          MosaicSupplyChangeActionType.rawValueOf(builder.getAction().getValue());
      BigInteger delta = SerializationUtils.toUnsignedBigInteger(builder.getDelta());
      return MosaicSupplyChangeTransactionFactory.create(
          networkType, deadline, mosaicId, action, delta);
    }

    @Override
    public Serializer toBodyBuilder(MosaicSupplyChangeTransaction transaction) {
      return MosaicSupplyChangeTransactionBodyBuilder.create(
          new UnresolvedMosaicIdDto(
              SerializationUtils.toUnsignedLong(transaction.getMosaicId().getId())),
          SerializationUtils.toAmount(transaction.getDelta()),
          MosaicSupplyChangeActionDto.rawValueOf((byte) transaction.getAction().getValue()));
    }
  }

  private static class MosaicDefinitionTransactionSerializer
      implements TransactionSerializer<MosaicDefinitionTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.MOSAIC_DEFINITION;
    }

    @Override
    public Class<MosaicDefinitionTransaction> getTransactionClass() {
      return MosaicDefinitionTransaction.class;
    }

    @Override
    public TransactionFactory<?> fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {
      MosaicDefinitionTransactionBodyBuilder builder =
          (MosaicDefinitionTransactionBodyBuilder) transactionBuilder;
      MosaicNonce mosaicNonce = MosaicNonce.createFromInteger(builder.getNonce().getMosaicNonce());
      MosaicId mosaicId = SerializationUtils.toMosaicId(builder.getId());
      MosaicFlags mosaicFlags =
          MosaicFlags.create(
              builder.getFlags().contains(MosaicFlagsDto.SUPPLY_MUTABLE),
              builder.getFlags().contains(MosaicFlagsDto.TRANSFERABLE),
              builder.getFlags().contains(MosaicFlagsDto.RESTRICTABLE));
      int divisibility = SerializationUtils.byteToUnsignedInt(builder.getDivisibility());
      BlockDuration blockDuration = new BlockDuration(builder.getDuration().getBlockDuration());
      return MosaicDefinitionTransactionFactory.create(
          networkType, deadline, mosaicNonce, mosaicId, mosaicFlags, divisibility, blockDuration);
    }

    @Override
    public Serializer toBodyBuilder(MosaicDefinitionTransaction transaction) {
      return MosaicDefinitionTransactionBodyBuilder.create(
          new MosaicIdDto(SerializationUtils.toUnsignedLong(transaction.getMosaicId().getId())),
          new BlockDurationDto(transaction.getBlockDuration().getDuration()),
          new MosaicNonceDto((int) transaction.getMosaicNonce().getNonceAsLong()),
          SerializationUtils.getMosaicFlagsEnumSet(transaction.getMosaicFlags()),
          (byte) transaction.getDivisibility());
    }
  }

  private static class AccountKeyLinkTransactionSerializer
      implements TransactionSerializer<AccountKeyLinkTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.ACCOUNT_KEY_LINK;
    }

    @Override
    public Class<AccountKeyLinkTransaction> getTransactionClass() {
      return AccountKeyLinkTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {
      AccountKeyLinkTransactionBodyBuilder builder =
          (AccountKeyLinkTransactionBodyBuilder) transactionBuilder;
      PublicKey linkedPublicKey = SerializationUtils.toPublicKey(builder.getLinkedPublicKey());
      LinkAction linkAction = LinkAction.rawValueOf(builder.getLinkAction().getValue());
      return AccountKeyLinkTransactionFactory.create(
          networkType, deadline, linkedPublicKey, linkAction);
    }

    @Override
    public Serializer toBodyBuilder(AccountKeyLinkTransaction transaction) {
      return AccountKeyLinkTransactionBodyBuilder.create(
          SerializationUtils.toKeyDto(transaction.getLinkedPublicKey()),
          LinkActionDto.rawValueOf(transaction.getLinkAction().getValue()));
    }
  }

  private static class AccountMetadataTransactionSerializer
      implements TransactionSerializer<AccountMetadataTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.ACCOUNT_METADATA;
    }

    @Override
    public Class<AccountMetadataTransaction> getTransactionClass() {
      return AccountMetadataTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {
      AccountMetadataTransactionBodyBuilder builder =
          (AccountMetadataTransactionBodyBuilder) transactionBuilder;
      UnresolvedAddress targetAccount =
          SerializationUtils.toUnresolvedAddress(builder.getTargetAddress());
      BigInteger scopedMetadataKey =
          SerializationUtils.toUnsignedBigInteger(builder.getScopedMetadataKey());
      byte[] value = builder.getValue().array();
      return AccountMetadataTransactionFactory.create(
              networkType, deadline, targetAccount, scopedMetadataKey, value)
          .valueSizeDelta(builder.getValueSizeDelta());
    }

    @Override
    public Serializer toBodyBuilder(AccountMetadataTransaction transaction) {
      return AccountMetadataTransactionBodyBuilder.create(
          SerializationUtils.toUnresolvedAddress(
              transaction.getTargetAddress(), transaction.getNetworkType()),
          SerializationUtils.toUnsignedLong(transaction.getScopedMetadataKey()),
          (short) transaction.getValueSizeDelta(),
          ByteBuffer.wrap(transaction.getValue()));
    }
  }

  private static class MosaicMetadataTransactionSerializer
      implements TransactionSerializer<MosaicMetadataTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.MOSAIC_METADATA;
    }

    @Override
    public Class<MosaicMetadataTransaction> getTransactionClass() {
      return MosaicMetadataTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {
      MosaicMetadataTransactionBodyBuilder builder =
          (MosaicMetadataTransactionBodyBuilder) transactionBuilder;
      UnresolvedAddress targetAccount =
          SerializationUtils.toUnresolvedAddress(builder.getTargetAddress());
      BigInteger scopedMetadataKey =
          SerializationUtils.toUnsignedBigInteger(builder.getScopedMetadataKey());
      byte[] value = builder.getValue().array();
      UnresolvedMosaicId targetMosaicId =
          SerializationUtils.toUnresolvedMosaicId(builder.getTargetMosaicId());
      return MosaicMetadataTransactionFactory.create(
              networkType, deadline, targetAccount, targetMosaicId, scopedMetadataKey, value)
          .valueSizeDelta(builder.getValueSizeDelta());
    }

    @Override
    public Serializer toBodyBuilder(MosaicMetadataTransaction transaction) {
      return MosaicMetadataTransactionBodyBuilder.create(
          SerializationUtils.toUnresolvedAddress(
              transaction.getTargetAddress(), transaction.getNetworkType()),
          SerializationUtils.toUnsignedLong(transaction.getScopedMetadataKey()),
          SerializationUtils.toUnresolvedMosaicIdDto(transaction.getTargetMosaicId()),
          (short) transaction.getValueSizeDelta(),
          ByteBuffer.wrap(transaction.getValue()));
    }
  }

  private static class NamespaceMetadataTransactionSerializer
      implements TransactionSerializer<NamespaceMetadataTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.NAMESPACE_METADATA;
    }

    @Override
    public Class<NamespaceMetadataTransaction> getTransactionClass() {
      return NamespaceMetadataTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {
      NamespaceMetadataTransactionBodyBuilder builder =
          (NamespaceMetadataTransactionBodyBuilder) transactionBuilder;
      UnresolvedAddress targetAddress =
          SerializationUtils.toUnresolvedAddress(builder.getTargetAddress());
      BigInteger scopedMetadataKey =
          SerializationUtils.toUnsignedBigInteger(builder.getScopedMetadataKey());
      byte[] value = builder.getValue().array();
      NamespaceId targetNamespaceId =
          SerializationUtils.toNamespaceId(builder.getTargetNamespaceId());
      return NamespaceMetadataTransactionFactory.create(
              networkType, deadline, targetAddress, targetNamespaceId, scopedMetadataKey, value)
          .valueSizeDelta(builder.getValueSizeDelta());
    }

    @Override
    public Serializer toBodyBuilder(NamespaceMetadataTransaction transaction) {
      return NamespaceMetadataTransactionBodyBuilder.create(
          SerializationUtils.toUnresolvedAddress(
              transaction.getTargetAddress(), transaction.getNetworkType()),
          SerializationUtils.toUnsignedLong(transaction.getScopedMetadataKey()),
          new NamespaceIdDto(
              SerializationUtils.toUnsignedLong(transaction.getTargetNamespaceId().getId())),
          (short) transaction.getValueSizeDelta(),
          ByteBuffer.wrap(transaction.getValue()));
    }
  }

  private static class NamespaceRegistrationTransactionSerializer
      implements TransactionSerializer<NamespaceRegistrationTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.NAMESPACE_REGISTRATION;
    }

    @Override
    public Class<NamespaceRegistrationTransaction> getTransactionClass() {
      return NamespaceRegistrationTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {

      NamespaceRegistrationTransactionBodyBuilder builder =
          (NamespaceRegistrationTransactionBodyBuilder) transactionBuilder;

      NamespaceRegistrationType namespaceRegistrationType =
          NamespaceRegistrationType.rawValueOf(builder.getRegistrationType().getValue());
      String namespaceName = StringEncoder.getString(builder.getName().array());
      NamespaceId namespaceId = SerializationUtils.toNamespaceId(builder.getId());

      Optional<BigInteger> duration =
          namespaceRegistrationType == NamespaceRegistrationType.ROOT_NAMESPACE
              ? Optional.ofNullable(builder.getDuration())
                  .map(BlockDurationDto::getBlockDuration)
                  .map(SerializationUtils::toUnsignedBigInteger)
              : Optional.empty();

      Optional<NamespaceId> parentId =
          namespaceRegistrationType == NamespaceRegistrationType.SUB_NAMESPACE
              ? Optional.of(builder.getParentId()).map(SerializationUtils::toNamespaceId)
              : Optional.empty();

      return NamespaceRegistrationTransactionFactory.create(
          networkType,
          deadline,
          namespaceName,
          namespaceId,
          namespaceRegistrationType,
          duration,
          parentId);
    }

    @Override
    public Serializer toBodyBuilder(NamespaceRegistrationTransaction transaction) {
      NamespaceRegistrationTransactionBodyBuilder txBuilder;
      ByteBuffer namespaceNameByteBuffer =
          ByteBuffer.wrap(StringEncoder.getBytes(transaction.getNamespaceName()));
      NamespaceIdDto namespaceIdDto =
          new NamespaceIdDto(
              SerializationUtils.toUnsignedLong(transaction.getNamespaceId().getId()));

      if (transaction.getNamespaceRegistrationType() == NamespaceRegistrationType.ROOT_NAMESPACE) {
        txBuilder =
            NamespaceRegistrationTransactionBodyBuilder.createRoot(
                new BlockDurationDto(
                    SerializationUtils.toUnsignedLong(
                        transaction
                            .getDuration()
                            .orElseThrow(() -> new IllegalStateException("Duration is required")))),
                namespaceIdDto,
                namespaceNameByteBuffer);

      } else {
        txBuilder =
            NamespaceRegistrationTransactionBodyBuilder.createChild(
                new NamespaceIdDto(
                    SerializationUtils.toUnsignedLong(
                        transaction
                            .getParentId()
                            .orElseThrow(() -> new IllegalStateException("ParentId is required"))
                            .getId())),
                namespaceIdDto,
                namespaceNameByteBuffer);
      }
      return txBuilder;
    }
  }

  private static class SecretLockTransactionSerializer
      implements TransactionSerializer<SecretLockTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.SECRET_LOCK;
    }

    @Override
    public Class<SecretLockTransaction> getTransactionClass() {
      return SecretLockTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {

      SecretLockTransactionBodyBuilder builder =
          (SecretLockTransactionBodyBuilder) transactionBuilder;

      Mosaic mosaic = SerializationUtils.toMosaic(builder.getMosaic());
      BigInteger duration =
          SerializationUtils.toUnsignedBigInteger(builder.getDuration().getBlockDuration());
      LockHashAlgorithm hashAlgorithm =
          LockHashAlgorithm.rawValueOf(
              SerializationUtils.byteToUnsignedInt(builder.getHashAlgorithm().getValue()));
      String secret = SerializationUtils.toHexString(builder.getSecret());
      UnresolvedAddress recipient =
          SerializationUtils.toUnresolvedAddress(builder.getRecipientAddress());
      return SecretLockTransactionFactory.create(
          networkType, deadline, mosaic, duration, hashAlgorithm, secret, recipient);
    }

    @Override
    public Serializer toBodyBuilder(SecretLockTransaction transaction) {
      UnresolvedMosaicIdDto mosaicId =
          new UnresolvedMosaicIdDto(transaction.getMosaic().getId().getIdAsLong());
      AmountDto amount = SerializationUtils.toAmount(transaction.getMosaic().getAmount());
      UnresolvedMosaicBuilder unresolvedMosaicBuilder =
          UnresolvedMosaicBuilder.create(mosaicId, amount);
      return SecretLockTransactionBodyBuilder.create(
          SerializationUtils.toUnresolvedAddress(
              transaction.getRecipient(), transaction.getNetworkType()),
          new Hash256Dto(getSecretBuffer(transaction)),
          unresolvedMosaicBuilder,
          new BlockDurationDto(SerializationUtils.toUnsignedLong(transaction.getDuration())),
          LockHashAlgorithmDto.rawValueOf((byte) transaction.getHashAlgorithm().getValue()));
    }

    /**
     * Gets secret buffer.
     *
     * @param transaction the transaction.
     * @return Secret buffer.
     */
    private ByteBuffer getSecretBuffer(SecretLockTransaction transaction) {
      final ByteBuffer secretBuffer = ByteBuffer.allocate(32);
      secretBuffer.put(ConvertUtils.fromHexToBytes(transaction.getSecret()));
      return secretBuffer;
    }
  }

  private static class SecretProofTransactionSerializer
      implements TransactionSerializer<SecretProofTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.SECRET_PROOF;
    }

    @Override
    public Class<SecretProofTransaction> getTransactionClass() {
      return SecretProofTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {

      SecretProofTransactionBodyBuilder builder =
          (SecretProofTransactionBodyBuilder) transactionBuilder;

      LockHashAlgorithm hashType =
          LockHashAlgorithm.rawValueOf(
              SerializationUtils.byteToUnsignedInt(builder.getHashAlgorithm().getValue()));
      UnresolvedAddress recipient =
          SerializationUtils.toUnresolvedAddress(builder.getRecipientAddress());
      String secret = SerializationUtils.toHexString(builder.getSecret());
      String proof = SerializationUtils.toHexString(builder.getProof());
      return SecretProofTransactionFactory.create(
          networkType, deadline, hashType, recipient, secret, proof);
    }

    @Override
    public Serializer toBodyBuilder(SecretProofTransaction transaction) {
      return SecretProofTransactionBodyBuilder.create(
          SerializationUtils.toUnresolvedAddress(
              transaction.getRecipient(), transaction.getNetworkType()),
          new Hash256Dto(getSecretBuffer(transaction)),
          LockHashAlgorithmDto.rawValueOf((byte) transaction.getHashType().getValue()),
          getProofBuffer(transaction));
    }

    /**
     * Gets proof buffer
     *
     * @param transaction the transaction.
     * @return the secret buffer.
     */
    private ByteBuffer getSecretBuffer(SecretProofTransaction transaction) {
      final ByteBuffer secretBuffer = ByteBuffer.allocate(32);
      secretBuffer.put(ConvertUtils.fromHexToBytes(transaction.getSecret()));
      return secretBuffer;
    }

    /**
     * Gets proof buffer
     *
     * @param transaction the transaction.
     * @return Proof buffer.
     */
    private ByteBuffer getProofBuffer(SecretProofTransaction transaction) {
      final byte[] proofBytes = ConvertUtils.fromHexToBytes(transaction.getProof());
      return ByteBuffer.wrap(proofBytes);
    }
  }

  private static class AddressAliasTransactionSerializer
      implements TransactionSerializer<AddressAliasTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.ADDRESS_ALIAS;
    }

    @Override
    public Class<AddressAliasTransaction> getTransactionClass() {
      return AddressAliasTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {

      AddressAliasTransactionBodyBuilder builder =
          (AddressAliasTransactionBodyBuilder) transactionBuilder;

      AliasAction aliasAction = AliasAction.rawValueOf(builder.getAliasAction().getValue());
      NamespaceId namespaceId = SerializationUtils.toNamespaceId(builder.getNamespaceId());
      Address address = SerializationUtils.toAddress(builder.getAddress());

      return AddressAliasTransactionFactory.create(
          networkType, deadline, aliasAction, namespaceId, address);
    }

    @Override
    public Serializer toBodyBuilder(AddressAliasTransaction transaction) {
      NamespaceIdDto namespaceIdDto =
          new NamespaceIdDto(transaction.getNamespaceId().getIdAsLong());
      AliasActionDto aliasActionDto =
          AliasActionDto.rawValueOf(transaction.getAliasAction().getValue());
      Address address = transaction.getAddress();
      AddressDto addressDto = SerializationUtils.toAddressDto(address);
      return AddressAliasTransactionBodyBuilder.create(namespaceIdDto, addressDto, aliasActionDto);
    }
  }

  private static class MosaicAliasTransactionSerializer
      implements TransactionSerializer<MosaicAliasTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.MOSAIC_ALIAS;
    }

    @Override
    public Class<MosaicAliasTransaction> getTransactionClass() {
      return MosaicAliasTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {

      MosaicAliasTransactionBodyBuilder builder =
          (MosaicAliasTransactionBodyBuilder) transactionBuilder;

      AliasAction aliasAction = AliasAction.rawValueOf(builder.getAliasAction().getValue());
      NamespaceId namespaceId = SerializationUtils.toNamespaceId(builder.getNamespaceId());
      MosaicId mosaicId = SerializationUtils.toMosaicId(builder.getMosaicId());

      return MosaicAliasTransactionFactory.create(
          networkType, deadline, aliasAction, namespaceId, mosaicId);
    }

    @Override
    public Serializer toBodyBuilder(MosaicAliasTransaction transaction) {
      return MosaicAliasTransactionBodyBuilder.create(
          new NamespaceIdDto(transaction.getNamespaceId().getIdAsLong()),
          new MosaicIdDto(transaction.getMosaicId().getIdAsLong()),
          AliasActionDto.rawValueOf(transaction.getAliasAction().getValue()));
    }
  }

  private static class HashLockTransactionSerializer
      implements TransactionSerializer<HashLockTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.HASH_LOCK;
    }

    @Override
    public Class<HashLockTransaction> getTransactionClass() {
      return HashLockTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {

      HashLockTransactionBodyBuilder builder = (HashLockTransactionBodyBuilder) transactionBuilder;

      Mosaic mosaic = SerializationUtils.toMosaic(builder.getMosaic());
      BigInteger duration =
          SerializationUtils.toUnsignedBigInteger(builder.getDuration().getBlockDuration());
      return HashLockTransactionFactory.create(
          networkType,
          deadline,
          mosaic,
          duration,
          SerializationUtils.toHexString(builder.getHash()));
    }

    @Override
    public Serializer toBodyBuilder(HashLockTransaction transaction) {
      return HashLockTransactionBodyBuilder.create(
          UnresolvedMosaicBuilder.create(
              new UnresolvedMosaicIdDto(transaction.getMosaic().getId().getIdAsLong()),
              new AmountDto(
                  SerializationUtils.toUnsignedLong(transaction.getMosaic().getAmount()))),
          new BlockDurationDto(SerializationUtils.toUnsignedLong(transaction.getDuration())),
          new Hash256Dto(getHashBuffer(transaction)));
    }

    /**
     * Gets hash buffer.
     *
     * @param transaction the transaction.
     * @return Hash buffer.
     */
    private ByteBuffer getHashBuffer(HashLockTransaction transaction) {
      return ByteBuffer.wrap(ConvertUtils.fromHexToBytes(transaction.getHash()));
    }
  }

  private static class AccountAddressRestrictionTransactionSerializer
      implements TransactionSerializer<AccountAddressRestrictionTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.ACCOUNT_ADDRESS_RESTRICTION;
    }

    @Override
    public Class<AccountAddressRestrictionTransaction> getTransactionClass() {
      return AccountAddressRestrictionTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {

      AccountAddressRestrictionTransactionBodyBuilder builder =
          (AccountAddressRestrictionTransactionBodyBuilder) transactionBuilder;

      long restrictionFlagsValue =
          builder.getRestrictionFlags().stream()
              .mapToLong(AccountRestrictionFlagsDto::getValue)
              .sum();

      AccountAddressRestrictionFlags restrictionFlags =
          AccountAddressRestrictionFlags.rawValueOf((int) restrictionFlagsValue);

      List<UnresolvedAddress> restrictionAdditions =
          builder.getRestrictionAdditions().stream()
              .map(SerializationUtils::toUnresolvedAddress)
              .collect(Collectors.toList());

      List<UnresolvedAddress> restrictionDeletions =
          builder.getRestrictionDeletions().stream()
              .map(SerializationUtils::toUnresolvedAddress)
              .collect(Collectors.toList());

      return AccountAddressRestrictionTransactionFactory.create(
          networkType, deadline, restrictionFlags, restrictionAdditions, restrictionDeletions);
    }

    @Override
    public Serializer toBodyBuilder(AccountAddressRestrictionTransaction transaction) {

      EnumSet<AccountRestrictionFlagsDto> flags =
          toAccountRestrictionsFlagsDto(transaction.getRestrictionFlags().getFlags());

      List<UnresolvedAddressDto> restrictionAdditions =
          transaction.getRestrictionAdditions().stream()
              .map(a -> SerializationUtils.toUnresolvedAddress(a, transaction.getNetworkType()))
              .collect(Collectors.toList());

      List<UnresolvedAddressDto> restrictionDeletions =
          transaction.getRestrictionDeletions().stream()
              .map(a -> SerializationUtils.toUnresolvedAddress(a, transaction.getNetworkType()))
              .collect(Collectors.toList());

      return AccountAddressRestrictionTransactionBodyBuilder.create(
          flags, restrictionAdditions, restrictionDeletions);
    }
  }

  private static class AccountMosaicRestrictionTransactionSerializer
      implements TransactionSerializer<AccountMosaicRestrictionTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.ACCOUNT_MOSAIC_RESTRICTION;
    }

    @Override
    public Class<AccountMosaicRestrictionTransaction> getTransactionClass() {
      return AccountMosaicRestrictionTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {

      AccountMosaicRestrictionTransactionBodyBuilder builder =
          (AccountMosaicRestrictionTransactionBodyBuilder) transactionBuilder;
      long restrictionFlagsValues =
          builder.getRestrictionFlags().stream()
              .mapToLong(AccountRestrictionFlagsDto::getValue)
              .sum();

      AccountMosaicRestrictionFlags restrictionFlags =
          AccountMosaicRestrictionFlags.rawValueOf((int) restrictionFlagsValues);

      List<UnresolvedMosaicId> restrictionAdditions =
          builder.getRestrictionAdditions().stream()
              .map(SerializationUtils::toUnresolvedMosaicId)
              .collect(Collectors.toList());

      List<UnresolvedMosaicId> restrictionDeletions =
          builder.getRestrictionDeletions().stream()
              .map(SerializationUtils::toUnresolvedMosaicId)
              .collect(Collectors.toList());

      return AccountMosaicRestrictionTransactionFactory.create(
          networkType, deadline, restrictionFlags, restrictionAdditions, restrictionDeletions);
    }

    @Override
    public Serializer toBodyBuilder(AccountMosaicRestrictionTransaction transaction) {

      EnumSet<AccountRestrictionFlagsDto> flags =
          toAccountRestrictionsFlagsDto(transaction.getRestrictionFlags().getFlags());

      List<UnresolvedMosaicIdDto> restrictionAdditions =
          transaction.getRestrictionAdditions().stream()
              .map(a -> new UnresolvedMosaicIdDto(a.getIdAsLong()))
              .collect(Collectors.toList());

      List<UnresolvedMosaicIdDto> restrictionDeletions =
          transaction.getRestrictionDeletions().stream()
              .map(a -> new UnresolvedMosaicIdDto(a.getIdAsLong()))
              .collect(Collectors.toList());

      return AccountMosaicRestrictionTransactionBodyBuilder.create(
          flags, restrictionAdditions, restrictionDeletions);
    }
  }

  private static class AccountOperationRestrictionTransactionSerializer
      implements TransactionSerializer<AccountOperationRestrictionTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.ACCOUNT_OPERATION_RESTRICTION;
    }

    @Override
    public Class<AccountOperationRestrictionTransaction> getTransactionClass() {
      return AccountOperationRestrictionTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {

      AccountOperationRestrictionTransactionBodyBuilder builder =
          (AccountOperationRestrictionTransactionBodyBuilder) transactionBuilder;

      long restrictionFlagsValue =
          builder.getRestrictionFlags().stream()
              .mapToLong(AccountRestrictionFlagsDto::getValue)
              .sum();

      AccountOperationRestrictionFlags restrictionFlags =
          AccountOperationRestrictionFlags.rawValueOf((int) restrictionFlagsValue);

      List<TransactionType> restrictionAdditions =
          builder.getRestrictionAdditions().stream()
              .map(op -> TransactionType.rawValueOf(op.getValue()))
              .collect(Collectors.toList());

      List<TransactionType> restrictionDeletions =
          builder.getRestrictionDeletions().stream()
              .map(op -> TransactionType.rawValueOf(op.getValue()))
              .collect(Collectors.toList());

      return AccountOperationRestrictionTransactionFactory.create(
          networkType, deadline, restrictionFlags, restrictionAdditions, restrictionDeletions);
    }

    @Override
    public Serializer toBodyBuilder(AccountOperationRestrictionTransaction transaction) {

      List<AccountRestrictionFlag> accountRestrictionFlags =
          transaction.getRestrictionFlags().getFlags();
      EnumSet<AccountRestrictionFlagsDto> flags =
          toAccountRestrictionsFlagsDto(accountRestrictionFlags);

      List<EntityTypeDto> restrictionAdditions =
          transaction.getRestrictionAdditions().stream()
              .map(a -> EntityTypeDto.rawValueOf((short) a.getValue()))
              .collect(Collectors.toList());

      List<EntityTypeDto> restrictionDeletions =
          transaction.getRestrictionDeletions().stream()
              .map(a -> EntityTypeDto.rawValueOf((short) a.getValue()))
              .collect(Collectors.toList());

      return AccountOperationRestrictionTransactionBodyBuilder.create(
          flags, restrictionAdditions, restrictionDeletions);
    }
  }

  public static EnumSet<AccountRestrictionFlagsDto> toAccountRestrictionsFlagsDto(
      List<AccountRestrictionFlag> accountRestrictionFlags) {
    return accountRestrictionFlags.stream()
        .map(f -> AccountRestrictionFlagsDto.rawValueOf((short) f.getValue()))
        .collect(Collectors.toCollection(() -> EnumSet.noneOf(AccountRestrictionFlagsDto.class)));
  }

  private static class MosaicAddressRestrictionTransactionSerializer
      implements TransactionSerializer<MosaicAddressRestrictionTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.MOSAIC_ADDRESS_RESTRICTION;
    }

    @Override
    public Class<MosaicAddressRestrictionTransaction> getTransactionClass() {
      return MosaicAddressRestrictionTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {

      MosaicAddressRestrictionTransactionBodyBuilder builder =
          (MosaicAddressRestrictionTransactionBodyBuilder) transactionBuilder;

      UnresolvedMosaicId mosaicId = SerializationUtils.toUnresolvedMosaicId(builder.getMosaicId());
      BigInteger restrictionKey =
          SerializationUtils.toUnsignedBigInteger(builder.getRestrictionKey());
      UnresolvedAddress targetAddress =
          SerializationUtils.toUnresolvedAddress(builder.getTargetAddress());
      BigInteger newRestrictionValue =
          SerializationUtils.toUnsignedBigInteger(builder.getNewRestrictionValue());
      BigInteger previousRestrictionValue =
          SerializationUtils.toUnsignedBigInteger(builder.getPreviousRestrictionValue());
      return MosaicAddressRestrictionTransactionFactory.create(
              networkType, deadline, mosaicId, restrictionKey, targetAddress, newRestrictionValue)
          .previousRestrictionValue(previousRestrictionValue);
    }

    @Override
    public Serializer toBodyBuilder(MosaicAddressRestrictionTransaction transaction) {
      UnresolvedAddressDto unresolvedAddressDto =
          SerializationUtils.toUnresolvedAddress(
              transaction.getTargetAddress(), transaction.getNetworkType());
      return MosaicAddressRestrictionTransactionBodyBuilder.create(
          new UnresolvedMosaicIdDto(transaction.getMosaicId().getIdAsLong()),
          SerializationUtils.toUnsignedLong(transaction.getRestrictionKey()),
          SerializationUtils.toUnsignedLong(transaction.getPreviousRestrictionValue()),
          SerializationUtils.toUnsignedLong(transaction.getNewRestrictionValue()),
          unresolvedAddressDto);
    }
  }

  private static class MosaicGlobalRestrictionTransactionSerializer
      implements TransactionSerializer<MosaicGlobalRestrictionTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.MOSAIC_GLOBAL_RESTRICTION;
    }

    @Override
    public Class<MosaicGlobalRestrictionTransaction> getTransactionClass() {
      return MosaicGlobalRestrictionTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {
      MosaicGlobalRestrictionTransactionBodyBuilder builder =
          (MosaicGlobalRestrictionTransactionBodyBuilder) transactionBuilder;
      UnresolvedMosaicId mosaicId = SerializationUtils.toUnresolvedMosaicId(builder.getMosaicId());
      BigInteger restrictionKey =
          SerializationUtils.toUnsignedBigInteger(builder.getRestrictionKey());
      BigInteger newRestrictionValue =
          SerializationUtils.toUnsignedBigInteger(builder.getNewRestrictionValue());
      BigInteger previousRestrictionValue =
          SerializationUtils.toUnsignedBigInteger(builder.getPreviousRestrictionValue());
      MosaicRestrictionType newRestrictionType =
          MosaicRestrictionType.rawValueOf(builder.getNewRestrictionType().getValue());
      MosaicRestrictionType previousRestrictionType =
          MosaicRestrictionType.rawValueOf(builder.getPreviousRestrictionType().getValue());
      return MosaicGlobalRestrictionTransactionFactory.create(
              networkType,
              deadline,
              mosaicId,
              restrictionKey,
              newRestrictionValue,
              newRestrictionType)
          .referenceMosaicId(
              SerializationUtils.toUnresolvedMosaicId(builder.getReferenceMosaicId()))
          .previousRestrictionValue(previousRestrictionValue)
          .previousRestrictionType(previousRestrictionType);
    }

    @Override
    public Serializer toBodyBuilder(MosaicGlobalRestrictionTransaction transaction) {
      return MosaicGlobalRestrictionTransactionBodyBuilder.create(
          new UnresolvedMosaicIdDto(transaction.getMosaicId().getIdAsLong()),
          new UnresolvedMosaicIdDto(transaction.getReferenceMosaicId().getIdAsLong()),
          SerializationUtils.toUnsignedLong(transaction.getRestrictionKey()),
          SerializationUtils.toUnsignedLong(transaction.getPreviousRestrictionValue()),
          SerializationUtils.toUnsignedLong(transaction.getNewRestrictionValue()),
          MosaicRestrictionTypeDto.rawValueOf(transaction.getPreviousRestrictionType().getValue()),
          MosaicRestrictionTypeDto.rawValueOf(transaction.getNewRestrictionType().getValue()));
    }
  }

  private static class MultisigAccountModificationTransactionSerializer
      implements TransactionSerializer<MultisigAccountModificationTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.MULTISIG_ACCOUNT_MODIFICATION;
    }

    @Override
    public Class<MultisigAccountModificationTransaction> getTransactionClass() {
      return MultisigAccountModificationTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {

      MultisigAccountModificationTransactionBodyBuilder builder =
          (MultisigAccountModificationTransactionBodyBuilder) transactionBuilder;
      byte minApprovalDelta = builder.getMinApprovalDelta();
      byte minRemovalDelta = builder.getMinRemovalDelta();

      List<UnresolvedAddress> addressAdditions =
          builder.getAddressAdditions().stream()
              .map(op -> SerializationUtils.toUnresolvedAddress(op))
              .collect(Collectors.toList());

      List<UnresolvedAddress> addressDeletions =
          builder.getAddressDeletions().stream()
              .map(op -> SerializationUtils.toUnresolvedAddress(op))
              .collect(Collectors.toList());

      return MultisigAccountModificationTransactionFactory.create(
          networkType,
          deadline,
          minApprovalDelta,
          minRemovalDelta,
          addressAdditions,
          addressDeletions);
    }

    @Override
    public Serializer toBodyBuilder(MultisigAccountModificationTransaction transaction) {

      List<UnresolvedAddressDto> addressAdditions =
          transaction.getAddressAdditions().stream()
              .map(a -> SerializationUtils.toUnresolvedAddress(a, transaction.getNetworkType()))
              .collect(Collectors.toList());

      List<UnresolvedAddressDto> addressDeletions =
          transaction.getAddressDeletions().stream()
              .map(a -> SerializationUtils.toUnresolvedAddress(a, transaction.getNetworkType()))
              .collect(Collectors.toList());

      return MultisigAccountModificationTransactionBodyBuilder.create(
          transaction.getMinRemovalDelta(),
          transaction.getMinApprovalDelta(),
          addressAdditions,
          addressDeletions);
    }
  }

  private static class AggregateTransactionSerializer
      implements TransactionSerializer<AggregateTransaction> {

    private final TransactionType transactionType;

    private final BinarySerializationImpl transactionSerialization;

    public AggregateTransactionSerializer(
        TransactionType transactionType, BinarySerializationImpl transactionSerialization) {
      this.transactionType = transactionType;
      this.transactionSerialization = transactionSerialization;
    }

    @Override
    public TransactionType getTransactionType() {
      return transactionType;
    }

    @Override
    public Class<AggregateTransaction> getTransactionClass() {
      return AggregateTransaction.class;
    }

    @Override
    public TransactionFactory<?> fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {
      AggregateTransactionBodyBuilder builder =
          (AggregateTransactionBodyBuilder) transactionBuilder;
      List<Transaction> transactions =
          builder.getTransactions().stream()
              .map(transactionSerialization::toTransaction)
              .collect(Collectors.toList());

      List<AggregateTransactionCosignature> cosignatures =
          builder.getCosignatures().stream()
              .map(
                  cosignatureBuilder ->
                      getAggregateTransactionCosignature(networkType, cosignatureBuilder))
              .collect(Collectors.toList());
      return AggregateTransactionFactory.create(
          getTransactionType(),
          networkType,
          deadline,
          SerializationUtils.toHexString(builder.getTransactionsHash()),
          transactions,
          cosignatures);
    }

    private AggregateTransactionCosignature getAggregateTransactionCosignature(
        NetworkType networkType, CosignatureBuilder cosignatureBuilder) {
      PublicAccount signer =
          SerializationUtils.toPublicAccount(cosignatureBuilder.getSignerPublicKey(), networkType);
      String cosignature =
          SerializationUtils.toHexString(cosignatureBuilder.getSignature().getSignature());
      BigInteger version = SerializationUtils.toUnsignedBigInteger(cosignatureBuilder.getVersion());
      return new AggregateTransactionCosignature(version, cosignature, signer);
    }

    @Override
    public Serializer toBodyBuilder(AggregateTransaction transaction) {

      List<EmbeddedTransactionBuilder> transactions =
          transaction.getInnerTransactions().stream()
              .map(transactionSerialization::toEmbeddedTransactionBuilder)
              .collect(Collectors.toList());

      List<CosignatureBuilder> cosignatures =
          transaction.getCosignatures().stream()
              .map(this::getCosignatureBuilder)
              .collect(Collectors.toList());

      return AggregateTransactionBodyBuilder.create(
          SerializationUtils.toHash256Dto(transaction.getTransactionsHash()),
          transactions,
          cosignatures);
    }

    private CosignatureBuilder getCosignatureBuilder(AggregateTransactionCosignature c) {
      return CosignatureBuilder.create(
          SerializationUtils.toUnsignedLong(c.getVersion()),
          SerializationUtils.toKeyDto(c.getSigner().getPublicKey()),
          SerializationUtils.toSignatureDto(c.getSignature()));
    }
  }

  private static class NodeKeyLinkTransactionBuilderSerializer
      implements TransactionSerializer<NodeKeyLinkTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.NODE_KEY_LINK;
    }

    @Override
    public Class<NodeKeyLinkTransaction> getTransactionClass() {
      return NodeKeyLinkTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {
      NodeKeyLinkTransactionBodyBuilder builder =
          (NodeKeyLinkTransactionBodyBuilder) transactionBuilder;
      PublicKey linkedPublicKey = SerializationUtils.toPublicKey(builder.getLinkedPublicKey());
      LinkAction linkAction = LinkAction.rawValueOf(builder.getLinkAction().getValue());
      return NodeKeyLinkTransactionFactory.create(
          networkType, deadline, linkedPublicKey, linkAction);
    }

    @Override
    public Serializer toBodyBuilder(NodeKeyLinkTransaction transaction) {
      KeyDto linkedPublicKey = SerializationUtils.toKeyDto(transaction.getLinkedPublicKey());
      LinkActionDto linkAction = LinkActionDto.rawValueOf(transaction.getLinkAction().getValue());
      return NodeKeyLinkTransactionBodyBuilder.create(linkedPublicKey, linkAction);
    }
  }

  private static class VrfKeyLinkTransactionBuilderSerializer
      implements TransactionSerializer<VrfKeyLinkTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.VRF_KEY_LINK;
    }

    @Override
    public Class<VrfKeyLinkTransaction> getTransactionClass() {
      return VrfKeyLinkTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {
      VrfKeyLinkTransactionBodyBuilder builder =
          (VrfKeyLinkTransactionBodyBuilder) transactionBuilder;
      PublicKey linkedPublicKey = SerializationUtils.toPublicKey(builder.getLinkedPublicKey());
      LinkAction linkAction = LinkAction.rawValueOf(builder.getLinkAction().getValue());
      return VrfKeyLinkTransactionFactory.create(
          networkType, deadline, linkedPublicKey, linkAction);
    }

    @Override
    public Serializer toBodyBuilder(VrfKeyLinkTransaction transaction) {
      KeyDto linkedPublicKey = SerializationUtils.toKeyDto(transaction.getLinkedPublicKey());
      LinkActionDto linkAction = LinkActionDto.rawValueOf(transaction.getLinkAction().getValue());
      return VrfKeyLinkTransactionBodyBuilder.create(linkedPublicKey, linkAction);
    }
  }

  private static class VotingKeyLinkTransactionBuilderSerializer
      implements TransactionSerializer<VotingKeyLinkTransaction> {

    @Override
    public TransactionType getTransactionType() {
      return TransactionType.VOTING_KEY_LINK;
    }

    @Override
    public Class<VotingKeyLinkTransaction> getTransactionClass() {
      return VotingKeyLinkTransaction.class;
    }

    @Override
    public TransactionFactory fromBodyBuilder(
        NetworkType networkType, Deadline deadline, Serializer transactionBuilder) {
      VotingKeyLinkTransactionBodyBuilder builder =
          (VotingKeyLinkTransactionBodyBuilder) transactionBuilder;
      PublicKey linkedPublicKey =
          new PublicKey(builder.getLinkedPublicKey().getVotingKey().array());
      long startEpoch =
          SerializationUtils.intToUnsignedLong(builder.getStartEpoch().getFinalizationEpoch());
      long endEpoch =
          SerializationUtils.intToUnsignedLong(builder.getEndEpoch().getFinalizationEpoch());
      LinkAction linkAction = LinkAction.rawValueOf(builder.getLinkAction().getValue());
      return VotingKeyLinkTransactionFactory.create(
          networkType, deadline, linkedPublicKey, startEpoch, endEpoch, linkAction);
    }

    @Override
    public Serializer toBodyBuilder(VotingKeyLinkTransaction transaction) {
      VotingKeyDto linkedPublicKey =
          new VotingKeyDto(ByteBuffer.wrap(transaction.getLinkedPublicKey().getBytes()));
      FinalizationEpochDto startEpoch =
          SerializationUtils.toFinalizationEpochDto(transaction.getStartEpoch());
      FinalizationEpochDto endEpoch =
          SerializationUtils.toFinalizationEpochDto(transaction.getEndEpoch());
      LinkActionDto linkAction = LinkActionDto.rawValueOf(transaction.getLinkAction().getValue());
      return VotingKeyLinkTransactionBodyBuilder.create(
          linkedPublicKey, startEpoch, endEpoch, linkAction);
    }
  }
}
