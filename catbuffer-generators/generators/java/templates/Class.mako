import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.List;

/**
* ${helper.capitalize_first_character(generator.comments)}
**/
public class ${generator.generated_class_name}${(' extends ' + str(generator.generated_base_class_name)) if generator.generated_base_class_name is not None else ''} implements Serializer {

% for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_inline and not a.kind == helper.AttributeKind.SIZE_FIELD]:
    /** ${helper.capitalize_first_character(a.attribute_comment)}. **/
    private ${'final ' if a.attribute_is_final else ''}${a.attribute_var_type} ${a.attribute_name};

% endfor\

<%def name="renderCondition(a)" filter="trim">
    ${helper.get_condition_operation_text(a.attribute['condition_operation']).format(a.attribute['condition'], helper.get_generated_class_name(a.condition_type_attribute['type'], a.condition_type_attribute, generator.schema) + '.' + helper.create_enum_name(a.attribute['condition_value']))}
</%def>\
##     STREAM CONSTRUCTORS
<%def name="renderReader(a)" filter="trim">
    % if a.kind == helper.AttributeKind.SIMPLE:
            this.${a.attribute_name} = ${helper.get_reverse_method_name(a.attribute_size).format('stream.' + helper.get_read_method_name(a.attribute_size) + '()')};
   % elif a.kind == helper.AttributeKind.BUFFER:
            this.${a.attribute_name} = GeneratorUtils.readByteBuffer(stream, ${a.attribute_size});
    % elif a.kind == helper.AttributeKind.SIZE_FIELD:
            final ${a.attribute_var_type} ${a.attribute_name} = ${helper.get_reverse_method_name(a.attribute_size).format('stream.' + helper.get_read_method_name(a.attribute_size) + '()')};
   % elif a.kind == helper.AttributeKind.ARRAY:
            this.${a.attribute_name} = GeneratorUtils.loadFromBinaryArray(${helper.get_load_from_binary_factory(a.attribute_class_name)}::loadFromBinary, stream, ${a.attribute_size});
    % elif a.kind == helper.AttributeKind.CUSTOM and (not a.attribute_is_conditional or not a.conditional_read_before):
            this.${a.attribute_name} = ${helper.get_load_from_binary_factory(a.attribute_class_name)}.loadFromBinary(stream);
    % elif a.kind == helper.AttributeKind.CUSTOM:
            this.${a.attribute_name} = new ${helper.get_load_from_binary_factory(a.attribute_class_name)}(${a.attribute['condition']}Condition);
    % elif a.kind == helper.AttributeKind.FILL_ARRAY:
            this.${a.attribute_name} = GeneratorUtils.loadFromBinaryArray(${helper.get_load_from_binary_factory(a.attribute_class_name)}::loadFromBinary, stream, ${a.attribute_size});
    % elif a.kind == helper.AttributeKind.FLAGS:
            this.${a.attribute_name} = ${'GeneratorUtils.toSet({0}, {1})'.format(a.attribute_class_name + '.class', helper.get_reverse_method_name(a.attribute_size).format('stream.' + helper.get_read_method_name(a.attribute_size) + '()'))};
    % elif a.kind == helper.AttributeKind.VAR_ARRAY:
            this.${a.attribute_name} = GeneratorUtils.loadFromBinaryArrayRemaining(TransactionBuilderFactory::createEmbeddedTransactionBuilder, stream, payloadSize);
    % else:
            FIX ME!
    % endif
</%def>\
    /**
     * Constructor - Creates an object from stream.
     *
     * @param stream Byte stream to use to serialize the object.
     */
    protected ${generator.generated_class_name}(DataInputStream stream) {
 % if generator.base_class_name is not None:
        super(stream);
% endif
        try {
    % for a in set([(a.attribute['condition'], a.attribute_size, a.conditional_read_before) for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_inline and a.conditional_read_before and a.attribute_is_conditional]):
            final ${helper.get_builtin_type(a[1])} ${a[0]}Condition = ${helper.get_reverse_method_name(a[1]).format('stream.' + helper.get_read_method_name(a[1]) + '()')};
    % endfor
    % for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_inline and not a.conditional_read_before]:
        %if a.attribute_is_conditional:
            if (this.${renderCondition(a) | trim}) {
                ${renderReader(a) | trim}
            }
            % else:
            ${renderReader(a) | trim}
        %endif
    % endfor
    % for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_inline and a.conditional_read_before]:
            if (this.${renderCondition(a) | trim}) {
                ${renderReader(a) | trim}
            }
    % endfor
        } catch (Exception e) {
            throw GeneratorUtils.getExceptionToPropagate(e);
        }
    }

    /**
     * Creates an instance of ${generator.generated_class_name} from a stream.
     *
     * @param stream Byte stream to use to serialize the object.
     * @return Instance of ${generator.generated_class_name}.
     */
    public static ${generator.generated_class_name} loadFromBinary(DataInputStream stream) {
        return new ${generator.generated_class_name}(stream);
    }
    <%
        constructor_params = generator.all_constructor_params
        constructor_params_CSV = ', '.join([str(a.attribute_var_type) + ' ' + str(a.attribute_name) for a in constructor_params if a.attribute_condition_value == None and not a.attribute_is_aggregate and not a.attribute_is_reserved and not a.attribute_name == 'size'])
        super_arguments_CSV = ', '.join([str(a.attribute_name) for a in constructor_params if a.attribute_is_super and not a.attribute_is_reserved and not a.attribute_is_aggregate  and not a.attribute_name == 'size'])
    %>
    /**
    * Constructor.
    *
% for a in [a for a in constructor_params if a.attribute_condition_value == None and not a.attribute_is_aggregate and not a.attribute_is_reserved and not a.kind == helper.AttributeKind.SIZE_FIELD and not a.attribute_name == 'size']:
    * @param ${a.attribute_name} ${helper.capitalize_first_character(a.attribute_comment)}.
% endfor
    */
    protected ${generator.generated_class_name}(${constructor_params_CSV}) {
    % if generator.base_class_name is not None:
        super(${super_arguments_CSV});
    % endif
    % for a in [a for a in constructor_params if a.attribute_condition_value == None and not a.attribute_is_aggregate and not a.attribute_is_reserved and not a.attribute_name == 'size']:
    % if a.attribute_is_conditional:
        if (${renderCondition(a) | trim}) {
            GeneratorUtils.notNull(${a.attribute_name}, "${a.attribute_name} is null");
        }
    %else:
        GeneratorUtils.notNull(${a.attribute_name}, "${a.attribute_name} is null");
    % endif
    % endfor
    % for a in [a for a in constructor_params if not a.attribute_is_inline and not a.attribute_is_super and not a.attribute_name == 'size']:
        % if a.attribute_is_aggregate:
        this.${a.attribute_name} = new ${a.attribute_var_type}(${', '.join([str(inline.attribute_name) for inline in constructor_params if inline.attribute_aggregate_attribute_name == a.attribute_name and not inline.attribute_is_reserved and not inline.kind == helper.AttributeKind.SIZE_FIELD and inline.attribute_condition_value is None and not inline.attribute_is_aggregate])});
        % else:
        this.${a.attribute_name} = ${a.attribute_name if not a.attribute_is_reserved else '0'};
        % endif
    % endfor
    }
## CONDITIONAL CONSTRUCTORS
% for possible_constructor_params in generator.constructor_attributes:
    <%
        constructor_params = [a for a in possible_constructor_params if a.attribute_condition_value is None and a.attribute_condition_provide and not a.attribute_is_reserved and not a.attribute_is_aggregate]
        constructor_params_CSV = ', '.join([str(a.attribute_var_type) + ' ' + str(a.attribute_name) for a in constructor_params])
        default_value_attributes = [a for a in possible_constructor_params if a.attribute_condition_value is not None]
        create_name_suffix = ''.join([helper.capitalize_first_character(a.attribute_condition_value) for a in default_value_attributes])
        constructor_arguments_CSV = ', '.join([str(a.attribute_name)
        if a.attribute_condition_value is not None or a.attribute_condition_provide else 'null'
        for a in possible_constructor_params if not a.attribute_is_aggregate and not a.attribute_is_reserved and not a.attribute_name == 'size'])
    %>
    /**
     * Creates an instance of ${generator.generated_class_name}.
     *
% for a in [a for a in constructor_params if a.attribute_condition_value == None and not a.attribute_is_aggregate and not a.attribute_is_reserved and not a.attribute_name == 'size']:
     * @param ${a.attribute_name} ${helper.capitalize_first_character(a.attribute_comment)}.
% endfor
     * @return Instance of ${generator.generated_class_name}.
     */
    public static ${generator.generated_class_name} create${create_name_suffix}(${constructor_params_CSV}) {
    % for a in default_value_attributes:
        ${helper.get_generated_class_name(a.attribute['type'], a.attribute, generator.schema)} ${a.attribute_name} = ${helper.get_generated_class_name(a.attribute['type'], a.attribute, generator.schema)}.${helper.create_enum_name(a.attribute_condition_value)};
    % endfor
        return new ${generator.generated_class_name}(${constructor_arguments_CSV});
    }
% endfor

## GETTERS:
% for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_aggregate and not a.kind == helper.AttributeKind.SIZE_FIELD and (not a.attribute_is_reserved or not a.attribute_is_inline)]:
    /**
     * Gets ${a.attribute_comment}.
     *
     * @return ${helper.capitalize_first_character(a.attribute_comment)}.
     */
    ${'private' if a.attribute_is_reserved else 'public'} ${a.attribute_var_type} get${helper.capitalize_first_character(a.attribute_name) if a.attribute_name != 'size' else 'StreamSize'}() {
    % if a.attribute_is_conditional and not a.attribute_is_inline:
        if (!(this.${renderCondition(a) | trim})) {
            throw new java.lang.IllegalStateException("${a.attribute['condition']} is not set to ${helper.create_enum_name(a.attribute['condition_value'])}.");
        }
    % endif
    % if a.attribute_is_inline:
        return this.${a.attribute_aggregate_attribute_name}.get${helper.capitalize_first_character(a.attribute_name)}();
    % else:
        return this.${a.attribute_name};
    % endif
    }

% endfor
## SIZE:
<%def name="renderSize(a)" filter="trim">\
    % if a.kind == helper.AttributeKind.SIMPLE:
        size += ${a.attribute_size}; // ${a.attribute_name}
    % elif a.kind == helper.AttributeKind.SIZE_FIELD:
        size += ${a.attribute_size}; // ${a.attribute_name}
    % elif a.kind == helper.AttributeKind.BUFFER:
        size += this.${a.attribute_name}.array().length;
   % elif a.kind == helper.AttributeKind.ARRAY or a.kind == helper.AttributeKind.VAR_ARRAY or a.kind == helper.AttributeKind.FILL_ARRAY:
        size += this.${a.attribute_name}.stream().mapToInt(o -> o.getSize()).sum();
    % elif a.kind == helper.AttributeKind.FLAGS:
        size += ${a.attribute_class_name}.values()[0].getSize();
    % else:
        size += this.${a.attribute_name}.getSize();
    % endif
</%def>\

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public int getSize() {
        int size = ${'super.getSize()' if generator.base_class_name is not None else '0'};
% for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_inline]:
    % if a.attribute_is_conditional:
        if (this.${renderCondition(a) | trim}) {
            ${renderSize(a).strip()}
        }
    % else:
        ${renderSize(a).strip()}
    % endif
% endfor
        return size;
    }

% if generator.base_class_name in ['Transaction', 'EmbeddedTransaction']:
    /**
     * Gets the body builder of the object.
     *
     * @return Body builder.
     */
    @Override
    public ${generator.body_class_name}Builder getBody() {
        return this.${helper.decapitalize_first_character(generator.body_class_name)};
    }
% endif

% if generator.name in ['Transaction', 'EmbeddedTransaction']:
    /**
     * Gets the body builder of the object.
     *
     * @return Body builder.
     */
    public Serializer getBody() {
        return null;
    }
% endif

<%def name="renderSerialize(a)" filter="trim">\
    % if a.kind == helper.AttributeKind.SIMPLE and (generator.name != 'Receipt' or a.attribute_name != 'size'):
            dataOutputStream.${helper.get_write_method_name(a.attribute_size)}(${helper.get_reverse_method_name(a.attribute_size).format('('+ a.attribute_var_type +') this.get' + helper.capitalize_first_character(a.attribute_name) + '()')});
   % elif a.kind == helper.AttributeKind.BUFFER:
            dataOutputStream.write(this.${a.attribute_name}.array(), 0, this.${a.attribute_name}.array().length);
    % elif a.kind == helper.AttributeKind.SIZE_FIELD:
            dataOutputStream.${helper.get_write_method_name(a.attribute_size)}(${helper.get_reverse_method_name(a.attribute_size).format('('+ a.attribute_var_type +') GeneratorUtils.getSize(this.get' + helper.capitalize_first_character(a.parent_attribute['name']) + '())')});
   % elif a.kind == helper.AttributeKind.ARRAY or a.kind == helper.AttributeKind.VAR_ARRAY or a.kind == helper.AttributeKind.FILL_ARRAY:
            GeneratorUtils.writeList(dataOutputStream, this.${a.attribute_name});
    % elif a.kind == helper.AttributeKind.CUSTOM:
            GeneratorUtils.writeEntity(dataOutputStream, this.${a.attribute_name});
    % elif a.kind == helper.AttributeKind.FLAGS:
            dataOutputStream.${helper.get_write_method_name(a.attribute_size)}(${helper.get_reverse_method_name(a.attribute_size).format('(' +  helper.get_builtin_type(a.attribute_size) + ') GeneratorUtils.toLong(' + a.attribute_class_name + '.class, this.' + a.attribute_name + ')')});
    % else:
            // Ignored serialization: ${a.attribute_name} ${a.kind}
    % endif
</%def>\
    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public byte[] serialize() {
        return GeneratorUtils.serialize((dataOutputStream) -> {
 % if generator.base_class_name is not None:
            final byte[] superBytes = super.serialize();
            dataOutputStream.write(superBytes, 0, superBytes.length);
% endif
    % for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_inline]:
        % if a.attribute_is_conditional:
            if (this.${renderCondition(a) | trim}) {
                ${renderSerialize(a)}
            }
        % else:
            ${renderSerialize(a)}
        % endif
    % endfor
        });
    }
}
