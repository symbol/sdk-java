import java.io.DataInputStream;
import java.nio.ByteBuffer;

/** ${generator.comments}. */
public final class ${generator.generated_class_name} implements Serializer {
    /** ${generator.comments}. */
    private final ${generator.attribute_type} ${generator.attribute_name};

    /**
     * Constructor.
     *
     * @param ${generator.attribute_name} ${generator.comments}.
     */
    public ${generator.generated_class_name}(final ${generator.attribute_type} ${generator.attribute_name}) {
        this.${generator.attribute_name} = ${generator.attribute_name};
    }

    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize.
     */
    public ${generator.generated_class_name}(final DataInputStream stream) {
        try {
% if generator.attribute_kind == helper.AttributeKind.BUFFER:
            this.${generator.attribute_name} = GeneratorUtils.readByteBuffer(stream, ${generator.size});
% else:
            this.${generator.attribute_name} = ${helper.get_reverse_method_name(generator.size).format('stream.' + generator.helper.get_read_method_name(generator.size) + '()')};
% endif
        } catch(Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Gets ${generator.comments}.
     *
     * @return ${generator.comments}.
     */
    public ${generator.attribute_type} get${generator.name}() {
        return this.${generator.attribute_name};
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
     * Creates an instance of ${generator.generated_class_name} from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of ${generator.generated_class_name}.
     */
    public static ${generator.generated_class_name} loadFromBinary(final DataInputStream stream) {
        return new ${generator.generated_class_name}(stream);
    }

    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize(dataOutputStream -> {
% if generator.attribute_kind == helper.AttributeKind.BUFFER:
            dataOutputStream.write(this.${generator.attribute_name}.array(), 0, this.${generator.attribute_name}.array().length);
% else:
            dataOutputStream.${helper.get_write_method_name(generator.size)}(${helper.get_reverse_method_name(generator.size).format('this.get' + generator.name + '()')});
% endif
        });
    }
}
