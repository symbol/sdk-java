import java.io.DataInputStream;

/**
* ${helper.capitalize_first_character(generator.comments)}
**/
public enum ${generator.generated_class_name} implements ${'BitMaskable, ' if generator.is_flag else '' }Serializer {

% for i, (name, (value, comment)) in enumerate(generator.enum_values.items()):
    /** ${comment}. */
    ${name}((${generator.enum_type}) ${value})${';' if i == len(generator.enum_values) -1 else ','}

% endfor

    /** Enum value. */
    private final ${generator.enum_type} value;

    /**
     * Constructor.
     *
     * @param value Enum value.
     */
     ${generator.generated_class_name}(final ${generator.enum_type} value) {
        this.value = value;
    }

    /**
     * Gets enum value.
     *
     * @param value Raw value of the enum.
     * @return Enum value.
     */
    public static ${generator.generated_class_name} rawValueOf(final ${generator.enum_type} value) {
        for (${generator.generated_class_name} current : ${generator.generated_class_name}.values()) {
            if (value == current.value) {
                return current;
            }
        }
        throw new IllegalArgumentException(value + " was not a backing value for ${generator.generated_class_name}.");
    }

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        return ${generator.size};
    }

    /**
     * Gets the value of the enum.
     *
     * @return Value of the enum.
     */
    public ${generator.enum_type} getValue() {
        return this.value;
    }
% if generator.is_flag:
    /**
     * Gets the value of the enum.
     *
     * @return Value of the enum.
     */
    public long getValueAsLong() {
        return ${helper.get_to_unsigned_method_name(generator.size).format('this.value')};
    }

% endif
    /**
     * Creates an instance of ${generator.generated_class_name} from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of ${generator.generated_class_name}.
     */
    public static ${generator.generated_class_name} loadFromBinary(final DataInputStream stream) {
        try {
            final ${generator.enum_type} streamValue = ${helper.get_reverse_method_name(generator.size).format('stream.' + generator.helper.get_read_method_name(generator.size) + '()')};
            return rawValueOf(streamValue);
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
            dataOutputStream.${helper.get_write_method_name(generator.size)}(${helper.get_reverse_method_name(generator.size).format('this.value')});
        });
    }
}
