import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * Generator utility class.
 */
public final class GeneratorUtils {

    /**
     * Constructor.
     */
    private GeneratorUtils() {
    }

    /**
     * Throws if the object is null.
     *
     * @param object Object to to check.
     * @param message Format string message.
     * @param values Format values.
     * @param <T> Type of object.
     */
    public static <T> void notNull(T object, String message, Object... values) {
        if (object == null) {
            throw new NullPointerException(String.format(message, values));
        }
    }

    /**
     * Throws if the value is not true.
     *
     * @param expression Expression to check.
     * @param message Format string message.
     * @param values Format values.
     */
    public static void isTrue(boolean expression, String message, Object... values) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    /**
     * Throws if the value is not false.
     *
     * @param expression Expression to check.
     * @param message Format string message.
     * @param values Format values.
     */
    public static void isFalse(boolean expression, String message, Object... values) {
        isTrue(!expression, message, values);
    }

    /**
     * Converts to an int by an unsigned conversion.
     *
     * @param value Signed byte.
     * @return Positive integer.
     */
    public static int toUnsignedInt(final byte value) {
        return Byte.toUnsignedInt(value);
    }

    /**
     * Converts to an int by an unsigned conversion.
     *
     * @param value Signed short.
     * @return Positive integer.
     */
    public static int toUnsignedInt(final short value) {
        return Short.toUnsignedInt(value);
    }

    /**
     * Creates a bitwise representation for an Set.
     *
     * @param enumClass Enum type.
     * @param enumSet EnumSet to convert to bit representation.
     * @param <T> Type of enum.
     * @return Long value of the EnumSet.
     */
    public static <T extends Enum<T> & BitMaskable> long toLong(final Class<T> enumClass,
        final Set<T> enumSet) {
        final T[] enumValues = enumClass.getEnumConstants();
        isFalse(enumValues.length > Long.SIZE,
            "The number of enum constants is greater than " + Long.SIZE);
        long result = 0;
        for (final T value : enumValues) {
            if (enumSet.contains(value)) {
                result += value.getValueAsLong();
            }
        }
        return result;
    }

    /**
     * Creates a EnumSet from from a bit representation.
     *
     * @param enumClass Enum class.
     * @param bitMaskValue Bitmask value.
     * @param <T> Enum type.
     * @return EnumSet representing the long value.
     */
    public static <T extends Enum<T> & BitMaskable> EnumSet<T> toSet(final Class<T> enumClass,
        final long bitMaskValue) {
        final EnumSet<T> results = EnumSet.noneOf(enumClass);
        for (final T constant : enumClass.getEnumConstants()) {
            if (0 != (constant.getValueAsLong() & bitMaskValue)) {
                results.add(constant);
            }
        }
        return results;
    }

    /**
     * Gets a runtime exception to propagates from an exception.
     *
     * @param exception Exception to propagate.
     * @param wrap Function that wraps an exception in a runtime exception.
     * @param <E> Specific exception type.
     * @return RuntimeException to throw.
     */
    public static <E extends RuntimeException> RuntimeException getExceptionToPropagate(
        final Exception exception,
        final Function<Exception, E> wrap) {
        if ((exception instanceof ExecutionException) && (RuntimeException.class
            .isAssignableFrom(exception.getCause().getClass()))) {
            return (RuntimeException) exception.getCause();
        }
        if (exception instanceof RuntimeException) {
            return (RuntimeException) exception;
        }
        if (exception instanceof InterruptedException) {
            Thread.currentThread().interrupt();
            return new IllegalStateException(exception);
        }
        return wrap.apply(exception);
    }

    /**
     * Gets a runtime exception to propagates from an exception.
     *
     * @param exception Exception to propagate.
     * @param <E> Specific exception type.
     * @return RuntimeException to throw.
     */
    public static <E extends RuntimeException> RuntimeException getExceptionToPropagate(
        final Exception exception) {
        return getExceptionToPropagate(exception, RuntimeException::new);
    }

    /**
     * Propagates checked exceptions as a specific runtime exception.
     *
     * @param callable Function to call.
     * @param wrap Function that wraps an exception in a runtime exception.
     * @param <T> Return type.
     * @param <E> Specific exception type.
     * @return Function result.
     */
    public static <T, E extends RuntimeException> T propagate(final Callable<T> callable,
        final Function<Exception, E> wrap) {
        try {
            return callable.call();
        } catch (final Exception e) {
            throw getExceptionToPropagate(e, wrap);
        }
    }

    /**
     * Propagates checked exceptions as a runtime exception.
     *
     * @param callable Function to call.
     * @param <T> Function return type.
     * @return Function result.
     */
    public static <T> T propagate(final Callable<T> callable) {
        return propagate(callable, RuntimeException::new);
    }



    /**
     * Throwing consumer interface.
     *
     * @param <T> Input type.
     * @param <E> Exception that is thrown.
     */
    public interface ThrowingConsumer<T, E extends Exception> {

        /**
         * Performs operation on the given argument.
         *
         * @param t Input argument.
         * @throws E Exception that is thrown.
         */
        void accept(T t) throws E;
    }

    /**
     * Serializes data using a helper function to write to the stream.
     *
     * @param consumer Helper function that writes data to DataOutputStream.
     * @return Byte array of data written.
     */
    public static byte[] serialize(ThrowingConsumer<DataOutputStream, Exception> consumer) {
        return propagate(() -> {
            try (final ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
                final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayStream)) {
                consumer.accept(dataOutputStream);
                return byteArrayStream.toByteArray();
            }
        });
    }

    /**
     * It moves the output stream pointer the padding size calculated from the payload size
     *
     * @param size the payload size used to calcualted the padding
     * @param dataInputStream the input stream that will be moved the calcauted padding size
     */
    public static void skipPadding(int size,
        final DataInputStream dataInputStream) {
        GeneratorUtils.propagate(() -> {
            int padding = getPadding(size);
            dataInputStream.skipBytes(padding);
            return null;
        });
    }

    /**
     * This method writes 0 into the dataOutputStream. The amount of 0s is the calculated padding size from provided
     * payload size.
     *
     * @param size the payload size used to calcualted the padding
     * @param dataOutputStream used to write the 0s.
     */
    public static void addPadding(int size, final DataOutputStream dataOutputStream) {
        GeneratorUtils.propagate(() -> {
            int padding = getPadding(size);
            while (padding > 0) {
                dataOutputStream.write(0);
                padding--;
            }
            return null;
        });
    }

    /**
     * It calcualtes the padding that needs to be added/skipped when processing inner transactions.
     *
     * @param size the size of the payload using to calculate the padding
     * @return the padding to be added/skipped.
     */
    public static int getPadding(int size) {
        int alignment = 8;
        return 0 == size % alignment ? 0 : alignment - (size % alignment);
    }

    /**
     * It reads count elements from the stream and creates a list using the builder
     *
     * @param builder the builder
     * @param stream the stream
     * @param count the elements to be read
     * @param <T> the the type to be returned
     * @return a list of T.
     */
    public static <T> List<T> loadFromBinaryArray(final Function<DataInputStream, T> builder,
        final DataInputStream stream, final long count) {
        List<T> list = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(builder.apply(stream));
        }
        return list;
    }


    /**
     * It reads all the remaining entities using the total payload size.
     *
     * @param builder the entity builder
     * @param stream the stream to read from
     * @param payloadSize the payload size
     * @param <T> the type of the entity
     * @return a list of entities
     * @throws IOException when data cannot be loaded.
     */
    public static <T extends Serializer> List<T> loadFromBinaryArrayRemaining(
        final Function<DataInputStream, T> builder, DataInputStream stream, int payloadSize)
        throws IOException {
        final ByteBuffer byteCount = ByteBuffer.allocate(payloadSize);
        stream.read(byteCount.array());
        final DataInputStream dataInputStream = new DataInputStream(
            new ByteArrayInputStream(byteCount.array()));
        List<T> entities = new java.util.ArrayList<>();
        while (dataInputStream.available() > 0) {
            T entity = builder.apply(dataInputStream);
            entities.add(entity);
            GeneratorUtils.skipPadding(entity.getSize(), dataInputStream);
        }
        return entities;
    }

    /**
     * Write a list of catbuffer entities into the writer.
     *
     * @param dataOutputStream the stream to serialize into
     * @param entities the entities to be serialized
     * @throws IOException when data cannot be written.
     */
    public static void writeList(final DataOutputStream dataOutputStream,
        final List<? extends Serializer> entities) throws IOException {
        for (Serializer entity : entities) {
            final byte[] entityBytes = entity.serialize();
            dataOutputStream.write(entityBytes, 0, entityBytes.length);
        }
    }

    /**
     * Write a serializer into the writer.
     *
     * @param dataOutputStream the stream to serialize into
     * @param entity the entities to be serialized
     * @throws IOException when data cannot be written.
     */
    public static void writeEntity(final DataOutputStream dataOutputStream, final Serializer entity)
        throws IOException {
        final byte[] entityBytes = entity.serialize();
        dataOutputStream.write(entityBytes, 0, entityBytes.length);
    }

    /**
     * Read a {@link ByteBuffer} of the given size form the strem
     *
     * @param stream the stream
     * @param size the size of the buffer to read
     * @return the buffer
     * @throws IOException when data cannot be read
     */
    public static ByteBuffer readByteBuffer(final DataInputStream stream, final int size) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        stream.readFully(buffer.array());
        return buffer;
    }

    /**
     * Returns the size of the buffer.
     *
     * @param buffer the buffer
     * @return its size
     */
    public static int getSize(final ByteBuffer buffer) {
        return buffer.array().length;
    }

    /**
     * Returns the size of the collection
     * @param collection the collecion
     * @return the size.
     */
    public static int getSize(final Collection<?> collection) {
        return collection.size();
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * Basic to hex function that converts a byte array to an hex
     * @param bytes the bytes
     * @return the hex representation.
     */
    public static String toHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Basic from hex to byte array function.
     * @param hex the hex string
     * @return the byte array.
     */
    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * It writes the builder into a file for future unit testing.
     * @param <T> the type of the builder.
     * @param builder the builder.
     * @param file the file to append.
     * @return the builder
     */
    public static <T extends Serializer> T writeBuilderToFile(T builder, String file) {
        try (FileWriter writer = new FileWriter(new File(file), true)) {
            String payload = toHex(builder.serialize());
            String builderName = builder.getClass().getSimpleName();
            writer.write("- builder: " + builderName + "\n");
            writer.write("  payload: " + payload + "\n");
            return builder;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}




