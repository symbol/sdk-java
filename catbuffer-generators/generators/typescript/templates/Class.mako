import { Serializer } from './Serializer';
import { GeneratorUtils } from './GeneratorUtils';
% for a in sorted(generator.required_import):
import { ${a} } from './${a}';
% endfor

/**
* ${helper.capitalize_first_character(generator.comments)}
**/
export class ${generator.generated_class_name}${(' extends ' + str(generator.generated_base_class_name)) if generator.generated_base_class_name is not None else ''} implements Serializer {

% for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_inline and not a.kind == helper.AttributeKind.SIZE_FIELD and not a.attribute_is_reserved and a.attribute_name != 'size']:
    /** ${helper.capitalize_first_character(a.attribute_comment)}. **/
    readonly ${a.attribute_name}${('?:' if a.attribute_is_conditional else ':')} ${a.attribute_var_type};

% endfor\

<%def name="renderCondition(a)" filter="trim">
    ${helper.get_condition_operation_text(a.attribute['condition_operation']).format(a.attribute['condition'], helper.get_generated_class_name(a.condition_type_attribute['type'], a.condition_type_attribute, generator.schema) + '.' + helper.create_enum_name(a.attribute['condition_value']))}
</%def>\
    <%
        constructor_params = generator.all_constructor_params
        constructor_params_CSV = ', '.join([ str(a.attribute_name) + ': ' + str(a.attribute_var_type) + (' | undefined' if a.attribute_is_conditional else '')  for a in constructor_params if a.attribute_condition_value == None and not a.attribute_is_aggregate and not a.attribute_is_reserved and not a.attribute_name == 'size'])
        super_arguments_CSV = ', '.join([str(a.attribute_name) for a in constructor_params if a.attribute_is_super and not a.attribute_is_reserved and not a.attribute_is_aggregate  and not a.attribute_name == 'size'])
    %>
    /**
    * Constructor.
    *
% for a in [a for a in constructor_params if a.attribute_condition_value == None and not a.attribute_is_aggregate and not a.attribute_is_reserved and not a.kind == helper.AttributeKind.SIZE_FIELD and not a.attribute_name == 'size']:
    * @param ${a.attribute_name} ${helper.capitalize_first_character(a.attribute_comment)}.
% endfor
    */
    public constructor(${constructor_params_CSV}) {
    % if generator.base_class_name is not None:
        super(${super_arguments_CSV});
    % endif
    % for a in [a for a in constructor_params if a.attribute_condition_value == None and not a.attribute_is_aggregate and not a.attribute_is_reserved and not a.attribute_name == 'size']:
    %if not a.attribute_is_super and not a.attribute_is_aggregate and not a.attribute_is_inline:
    % if a.attribute_is_conditional:
        if (${renderCondition(a) | trim}) {
            GeneratorUtils.notNull(${a.attribute_name}, "${a.attribute_name} is null or undefined");
        }
    %else:
        GeneratorUtils.notNull(${a.attribute_name}, "${a.attribute_name} is null or undefined");
    % endif
    % endif
    % endfor
    % for a in [a for a in constructor_params if not a.attribute_is_inline and not a.attribute_is_super and not a.attribute_is_reserved and not a.attribute_name == 'size']:
        % if a.attribute_is_aggregate:
        this.${a.attribute_name} = new ${a.attribute_var_type}(${', '.join([str(inline.attribute_name) for inline in constructor_params if inline.attribute_aggregate_attribute_name == a.attribute_name and not inline.attribute_is_reserved and not inline.kind == helper.AttributeKind.SIZE_FIELD and inline.attribute_condition_value is None and not inline.attribute_is_aggregate])});
        % else:
        this.${a.attribute_name} = ${a.attribute_name};
        % endif
    % endfor
    }

    ##     STREAM CONSTRUCTORS
<%def name="renderReader(a)" filter="trim">
<%
    this_attribute_definition = 'const ' + a.attribute_name + ': ' + a.attribute_var_type + ' = '
    if a.attribute_is_conditional:
        this_attribute_definition = a.attribute_name + ' = '
    if a.attribute_is_reserved:
        this_attribute_definition = ''
%>\

    % if a.kind == helper.AttributeKind.SIMPLE:
        ${this_attribute_definition}${helper.get_read_method_name(a.attribute_size, 'byteArray')};
        byteArray.splice(0, ${a.attribute_size});

   % elif a.kind == helper.AttributeKind.BUFFER:
        ${this_attribute_definition}GeneratorUtils.getBytes(Uint8Array.from(byteArray), ${a.attribute_size});
        byteArray.splice(0, ${a.attribute_size});

    % elif a.kind == helper.AttributeKind.SIZE_FIELD:
        ${this_attribute_definition}${helper.get_read_method_name(a.attribute_size, 'byteArray')};
        byteArray.splice(0, ${a.attribute_size});

   % elif a.kind == helper.AttributeKind.ARRAY and a.attribute_base_type == 'enum':
##       TODO. Size 2 is hardcoded here. Improve!
        ${this_attribute_definition}GeneratorUtils.loadFromBinaryEnums(Uint8Array.from(byteArray), ${a.attribute_size}, 2);
        byteArray.splice(0, ${a.attribute_name}.reduce((sum) => sum + 2, 0));

   % elif a.kind == helper.AttributeKind.ARRAY:
        ${this_attribute_definition}GeneratorUtils.loadFromBinary(${helper.get_load_from_binary_factory(a.attribute_class_name)}.loadFromBinary, Uint8Array.from(byteArray), ${a.attribute_size});
        byteArray.splice(0, ${a.attribute_name}.reduce((sum, c) => sum + c.getSize(), 0));

    % elif a.kind == helper.AttributeKind.CUSTOM and a.conditional_read_before:
        ${this_attribute_definition}new ${a.attribute_class_name}(${a.attribute['condition']}Condition);

    % elif a.kind == helper.AttributeKind.CUSTOM and a.attribute_base_type == 'enum':
        ${this_attribute_definition}${helper.get_read_method_name(a.attribute_size, 'byteArray')};
        byteArray.splice(0, ${a.attribute_size});

    % elif a.kind == helper.AttributeKind.CUSTOM:
        ${this_attribute_definition}${helper.get_load_from_binary_factory(a.attribute_class_name)}.loadFromBinary(Uint8Array.from(byteArray));
        byteArray.splice(0, ${a.attribute_name}.getSize());


    % elif a.kind == helper.AttributeKind.FILL_ARRAY:
        ${this_attribute_definition}GeneratorUtils.loadFromBinaryRemaining(${helper.get_load_from_binary_factory(a.attribute_class_name)}.loadFromBinary, Uint8Array.from(byteArray), byteArray.length, ${helper.resolve_alignment(a)});
        byteArray.splice(0, ${a.attribute_name}.reduce((sum, c) => sum + GeneratorUtils.getSizeWithPadding(c.getSize(), ${helper.resolve_alignment(a)}), 0));

    % elif a.kind == helper.AttributeKind.FLAGS:
        ${this_attribute_definition}GeneratorUtils.toFlags(${a.attribute_class_name}, ${helper.get_read_method_name(a.attribute_size, 'byteArray')});
        byteArray.splice(0, ${a.attribute_size});

    % elif a.kind == helper.AttributeKind.VAR_ARRAY:
        ${this_attribute_definition}GeneratorUtils.loadFromBinaryRemaining(${helper.get_load_from_binary_factory(a.attribute_class_name)}.loadFromBinary, Uint8Array.from(byteArray), payloadSize, ${helper.resolve_alignment(a)});
        byteArray.splice(0, ${a.attribute_name}.reduce((sum, c) => sum + GeneratorUtils.getSizeWithPadding(c.getSize(), ${helper.resolve_alignment(a)}), 0));

    % else:
            FIX ME!
    % endif

</%def>\

    /**
     * Load from binary array - Creates an object from payload.
     *
     * @param payload Byte payload to use to serialize the object.
     */
    <%
        constructor_params = generator.all_constructor_params
        constructor_params_CSV = ', '.join([ str(a.attribute_name) + ': ' + str(a.attribute_var_type) + (' | undefined' if a.attribute_is_conditional else '') for a in constructor_params if a.attribute_condition_value == None and not a.attribute_is_aggregate and not a.attribute_is_reserved and not a.attribute_name == 'size'])
        arguments_CSV = ', '.join(['superObject.' + str(a.attribute_name) if a.attribute_is_super else (a.attribute_aggregate_attribute_name + '.' + str(a.attribute_name) if a.attribute_is_inline else str(a.attribute_name)) for a in constructor_params if not a.attribute_is_reserved and not a.attribute_is_aggregate  and not a.attribute_name == 'size'])
    %>
    public static loadFromBinary(payload: Uint8Array): ${generator.generated_class_name} {
        const byteArray = Array.from(payload);
    % if generator.base_class_name is not None:
        const superObject = ${generator.generated_base_class_name}.loadFromBinary(payload);
        byteArray.splice(0, superObject.getSize());
    % endif
    % for a in set([(a.attribute['condition'], a.attribute_size, a.conditional_read_before) for a in generator.attributes if not a.attribute_is_inline and a.conditional_read_before and a.attribute_is_conditional]):
        const ${a[0]}Condition = ${helper.get_read_method_name(a[1], 'byteArray')};
        byteArray.splice(0,  ${a[1]});
    % endfor
    % for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_inline and not a.conditional_read_before]:
        %if a.attribute_is_conditional:
        let ${a.attribute_name} : ${a.attribute_var_type} | undefined = undefined;
        if (${renderCondition(a) | trim}) {
            ${renderReader(a) | trim}
        }
        % else:
        ${renderReader(a) | trim}
        %endif
    % endfor
    % for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_inline and a.conditional_read_before]:
        let ${a.attribute_name} : ${a.attribute_var_type} | undefined = undefined;
        if (${renderCondition(a) | trim}) {
            ${renderReader(a) | trim}
        }
    % endfor
        return new ${generator.generated_class_name}(${arguments_CSV});
    }

## CONDITIONAL CONSTRUCTORS
% for possible_constructor_params in generator.constructor_attributes:
    <%
        constructor_params = [a for a in possible_constructor_params if a.attribute_condition_value is None and a.attribute_condition_provide and not a.attribute_is_reserved and not a.attribute_is_aggregate]
        constructor_params_CSV = ', '.join([ str(a.attribute_name) + ': ' + str(a.attribute_var_type) for a in constructor_params])
        default_value_attributes = [a for a in possible_constructor_params if a.attribute_condition_value is not None]
        create_name_suffix = ''.join([helper.capitalize_first_character(a.attribute_condition_value) for a in default_value_attributes])
        constructor_arguments_CSV = ', '.join([str(a.attribute_name)
        if a.attribute_condition_value is not None or a.attribute_condition_provide else 'undefined'
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
    public static create${generator.generated_class_name}${create_name_suffix}(${constructor_params_CSV}): ${generator.generated_class_name} {
    % for a in default_value_attributes:
        const ${a.attribute_name} = ${helper.get_generated_class_name(a.attribute['type'], a.attribute, generator.schema)}.${helper.create_enum_name(a.attribute_condition_value)};
    % endfor
        return new ${generator.generated_class_name}(${constructor_arguments_CSV});
    }
% endfor

## GETTERS:
% for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_reserved  and not a.attribute_is_aggregate and not a.kind == helper.AttributeKind.SIZE_FIELD and (not a.attribute_is_reserved or not a.attribute_is_inline) and a.attribute_name != 'size']:
    /**
     * Gets ${a.attribute_comment}.
     *
     * @return ${helper.capitalize_first_character(a.attribute_comment)}.
     */
    public get${helper.capitalize_first_character(a.attribute_name) if a.attribute_name != 'size' else 'StreamSize'}(): ${a.attribute_var_type}  {
    % if a.attribute_is_conditional and not a.attribute_is_inline:
        if (!(this.${renderCondition(a) | trim} && this.${a.attribute_name})) {
            throw new Error("${a.attribute['condition']} is not set to ${helper.create_enum_name(a.attribute['condition_value'])}.");
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
<%
this_attribute_name = 'this.' + a.attribute_name + ('!' if a.attribute_is_conditional else '')
%>\
    % if a.kind == helper.AttributeKind.SIMPLE:
        size += ${a.attribute_size}; // ${a.attribute_name}
    % elif a.kind == helper.AttributeKind.SIZE_FIELD:
        size += ${a.attribute_size}; // ${a.attribute_name}
    % elif a.kind == helper.AttributeKind.BUFFER:
        size += ${this_attribute_name}.length;  // ${a.attribute_name}
    % elif a.kind == helper.AttributeKind.ARRAY and a.attribute_base_type == 'enum':
        size += ${this_attribute_name}.reduce((sum) => sum + 2, 0);
   % elif a.kind == helper.AttributeKind.ARRAY or a.kind == helper.AttributeKind.VAR_ARRAY or a.kind == helper.AttributeKind.FILL_ARRAY:
        size += ${this_attribute_name}.reduce((sum, c) => sum + GeneratorUtils.getSizeWithPadding(c.getSize(), ${helper.resolve_alignment(a)}), 0);  // ${a.attribute_name}
    % elif a.kind == helper.AttributeKind.FLAGS:
        size += ${a.attribute_size};  // ${a.attribute_name}
    % elif a.kind == helper.AttributeKind.CUSTOM and a.attribute_base_type == 'enum':
        size += ${a.attribute_size}; // ${a.attribute_name}
    % else:
        size += ${this_attribute_name}.getSize();  // ${a.attribute_name}
    % endif
</%def>\

    /**
     * Gets the size of the object.
     *
     * @return Size in bytes.
     */
    public getSize(): number {
        let size = ${'super.getSize()' if generator.base_class_name is not None else '0'};
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
    public getBody(): ${generator.body_class_name}Builder  {
        return this.${helper.decapitalize_first_character(generator.body_class_name)};
    }
% endif

% if generator.name in ['Transaction', 'EmbeddedTransaction']:
    /**
     * Gets the body builder of the object.
     *
     * @return Body builder.
     */
    public getBody(): undefined | Serializer {
        return undefined;
    }
% endif

<%def name="renderSerialize(a)" filter="trim">\
<%
this_attribute_name = 'this.' + a.attribute_name + ('!' if a.attribute_is_conditional else '')
this_attribute_getter = 'this.get' + helper.capitalize_first_character(a.attribute_name) + '()'
%>\
    % if a.kind == helper.AttributeKind.SIMPLE and a.attribute_is_reserved:
            const ${a.attribute_name}Bytes = ${helper.get_serialize_method_name(a.attribute_size)}(0);
    % elif a.kind == helper.AttributeKind.SIMPLE and (generator.name != 'Receipt' or a.attribute_name != 'size'):
            const ${a.attribute_name}Bytes = ${helper.get_serialize_method_name(a.attribute_size)}(${this_attribute_getter});
    % elif a.kind == helper.AttributeKind.BUFFER:
            const ${a.attribute_name}Bytes = ${this_attribute_name};
    % elif a.kind == helper.AttributeKind.SIZE_FIELD and 'disposition' in a.parent_attribute and a.parent_attribute['disposition'] == 'var':
            const ${a.attribute_name} = this.${a.parent_attribute['name']}.reduce((sum, c) => sum + GeneratorUtils.getSizeWithPadding(c.getSize(), ${helper.resolve_alignment(a)}), 0);
            const ${a.attribute_name}Bytes = ${helper.get_serialize_method_name(a.attribute_size)}(${a.attribute_name});
    % elif a.kind == helper.AttributeKind.SIZE_FIELD:
            const ${a.attribute_name}Bytes = ${helper.get_serialize_method_name(a.attribute_size)}(this.${a.parent_attribute['name']}.length);
    % elif a.kind == helper.AttributeKind.ARRAY and a.attribute_base_type == 'enum':
            const ${a.attribute_name}Bytes = GeneratorUtils.writeListEnum(${this_attribute_name}, ${helper.resolve_alignment(a)});
   % elif a.kind == helper.AttributeKind.ARRAY or a.kind == helper.AttributeKind.VAR_ARRAY or a.kind == helper.AttributeKind.FILL_ARRAY:
            const ${a.attribute_name}Bytes = GeneratorUtils.writeList(${this_attribute_name}, ${helper.resolve_alignment(a)});
    % elif a.kind == helper.AttributeKind.CUSTOM and a.attribute_base_type == 'enum':
            const ${a.attribute_name}Bytes = ${helper.get_serialize_method_name(a.attribute_size)}(${this_attribute_name});
    % elif a.kind == helper.AttributeKind.CUSTOM:
            const ${a.attribute_name}Bytes = ${this_attribute_name}.serialize();
    % elif a.kind == helper.AttributeKind.FLAGS:
            const ${a.attribute_name}Bytes = ${helper.get_serialize_method_name(a.attribute_size)}(GeneratorUtils.fromFlags(${a.attribute_class_name}, ${this_attribute_name}));
    % else:
            const ${a.attribute_name}Bytes = Uint8Array.from([]); // Ignored serialization: ${a.attribute_name} ${a.kind}
    % endif
</%def>\
    /**
     * Serializes an object to bytes.
     *
     * @return Serialized bytes.
     */
    public serialize(): Uint8Array {
        let newArray = Uint8Array.from([]);
 % if generator.base_class_name is not None:
        const superBytes = super.serialize();
        newArray = GeneratorUtils.concatTypedArrays(newArray, superBytes);
% endif
    % for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_inline]:
        % if a.attribute_is_conditional:
        if (this.${renderCondition(a) | trim}) {
            ${renderSerialize(a)}
            newArray = GeneratorUtils.concatTypedArrays(newArray, ${a.attribute_name}Bytes);
        }
        % else:
        ${renderSerialize(a)}
        newArray = GeneratorUtils.concatTypedArrays(newArray, ${a.attribute_name}Bytes);
        % endif
    % endfor
        return newArray;
    }
}
