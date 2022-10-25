import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.io.ByteArrayInputStream;

/** Binary layout for an aggregate transaction. */
public final class AggregateTransactionBodyBuilder implements Serializer {
    /** Aggregate hash of an aggregate's transactions. */
    private final Hash256Dto transactionsHash;
    /** Reserved padding to align end of AggregateTransactionHeader on 8-byte boundary. */
    private final int aggregateTransactionHeader_Reserved1;
    /** Sub-transaction data (transactions are variable sized and payload size is in bytes). */
    private final List<EmbeddedTransactionBuilder> transactions;
    /** Cosignatures data (fills remaining body space after transactions). */
    private final List<CosignatureBuilder> cosignatures;

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected AggregateTransactionBodyBuilder(final DataInputStream stream) {
        try {
            this.transactionsHash = Hash256Dto.loadFromBinary(stream);
            final int payloadSize = Integer.reverseBytes(stream.readInt());
            this.aggregateTransactionHeader_Reserved1 = Integer.reverseBytes(stream.readInt());
            final ByteBuffer transactionBytes = ByteBuffer.allocate(payloadSize);
            stream.read(transactionBytes.array());
            final DataInputStream dataInputStream =  new DataInputStream(new ByteArrayInputStream(transactionBytes.array()));
            this.transactions = new java.util.ArrayList<>();
            while (dataInputStream.available() > 0) {
                EmbeddedTransactionBuilder embeddedTransactionBuilder = EmbeddedTransactionBuilderHelper.loadFromBinary(dataInputStream);
                transactions.add(embeddedTransactionBuilder);
                GeneratorUtils.skipPadding(embeddedTransactionBuilder.getSize(), dataInputStream);
            }
            this.cosignatures = new java.util.ArrayList<>();
            while (stream.available() > 0) {
                cosignatures.add(CosignatureBuilder.loadFromBinary(stream));
            }
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Constructor.
     *
     * @param transactionsHash Aggregate hash of an aggregate's transactions.
     * @param transactions Sub-transaction data (transactions are variable sized and payload size is in bytes).
     * @param cosignatures Cosignatures data (fills remaining body space after transactions).
     */
    protected AggregateTransactionBodyBuilder(final Hash256Dto transactionsHash, final List<EmbeddedTransactionBuilder> transactions, final List<CosignatureBuilder> cosignatures) {
        GeneratorUtils.notNull(transactionsHash, "transactionsHash is null");
        GeneratorUtils.notNull(transactions, "transactions is null");
        GeneratorUtils.notNull(cosignatures, "cosignatures is null");
        this.transactionsHash = transactionsHash;
        this.aggregateTransactionHeader_Reserved1 = 0;
        this.transactions = transactions;
        this.cosignatures = cosignatures;
    }

    /**
     * Creates an instance of AggregateTransactionBodyBuilder.
     *
     * @param transactionsHash Aggregate hash of an aggregate's transactions.
     * @param transactions Sub-transaction data (transactions are variable sized and payload size is in bytes).
     * @param cosignatures Cosignatures data (fills remaining body space after transactions).
     * @return Instance of AggregateTransactionBodyBuilder.
     */
    public static AggregateTransactionBodyBuilder create(final Hash256Dto transactionsHash, final List<EmbeddedTransactionBuilder> transactions, final List<CosignatureBuilder> cosignatures) {
        return new AggregateTransactionBodyBuilder(transactionsHash, transactions, cosignatures);
    }

    /**
     * Gets aggregate hash of an aggregate's transactions.
     *
     * @return Aggregate hash of an aggregate's transactions.
     */
    public Hash256Dto getTransactionsHash() {
        return this.transactionsHash;
    }

    /**
     * Gets reserved padding to align end of AggregateTransactionHeader on 8-byte boundary.
     *
     * @return Reserved padding to align end of AggregateTransactionHeader on 8-byte boundary.
     */
    private int getAggregateTransactionHeader_Reserved1() {
        return this.aggregateTransactionHeader_Reserved1;
    }

    /**
     * Gets sub-transaction data (transactions are variable sized and payload size is in bytes).
     *
     * @return Sub-transaction data (transactions are variable sized and payload size is in bytes).
     */
    public List<EmbeddedTransactionBuilder> getTransactions() {
        return this.transactions;
    }

    /**
     * Gets cosignatures data (fills remaining body space after transactions).
     *
     * @return Cosignatures data (fills remaining body space after transactions).
     */
    public List<CosignatureBuilder> getCosignatures() {
        return this.cosignatures;
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = 0;
        size += this.transactionsHash.getSize();
        size += 4; // payloadSize
        size += 4; // aggregateTransactionHeader_Reserved1
        size += this.transactions.stream().mapToInt(o -> o.getSize() + GeneratorUtils.getPadding(o.getSize())).sum();
        size += this.cosignatures.stream().mapToInt(o -> o.getSize()).sum();
        return size;
    }

    /**
     * Creates an instance of AggregateTransactionBodyBuilder from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of AggregateTransactionBodyBuilder.
     */
    public static AggregateTransactionBodyBuilder loadFromBinary(final DataInputStream stream) {
        return new AggregateTransactionBodyBuilder(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            final byte[] transactionsHashBytes = this.transactionsHash.serialize();
            dataOutputStream.write(transactionsHashBytes, 0, transactionsHashBytes.length);
            int transactionsSize = (int) this.transactions.stream().mapToInt(o -> o.getSize() + GeneratorUtils.getPadding(o.getSize())).sum();
            dataOutputStream.writeInt(Integer.reverseBytes(transactionsSize));
            dataOutputStream.writeInt(Integer.reverseBytes(this.getAggregateTransactionHeader_Reserved1()));
            for (int i = 0; i < this.transactions.size(); i++) {
                final byte[] transactionsBytes = this.transactions.get(i).serialize();
                dataOutputStream.write(transactionsBytes, 0, transactionsBytes.length);
                GeneratorUtils.addPadding(transactionsBytes.length, dataOutputStream);
            }
            for (int i = 0; i < this.cosignatures.size(); i++) {
                final byte[] cosignaturesBytes = this.cosignatures.get(i).serialize();
                dataOutputStream.write(cosignaturesBytes, 0, cosignaturesBytes.length);
            }
        });
    }
}
