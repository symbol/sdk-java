## NOTE: do *not* touch `buffered` in render definitions, it will completely break output
<%
    python_lib_import_statements = []
    catbuffer_lib_import_statements = []
    for a in sorted(generator.required_import):
        if str(a).startswith('from .'):
            catbuffer_lib_import_statements.append(a)
        else:
            python_lib_import_statements.append(a)
%>\
from __future__ import annotations
% for a in python_lib_import_statements:
${a}
% endfor
from .GeneratorUtils import GeneratorUtils
% for a in catbuffer_lib_import_statements:
${a}
% endfor

class ${generator.generated_class_name}${'(' + str(generator.generated_base_class_name) + ')' if generator.generated_base_class_name is not None else ''}:
    """${helper.capitalize_first_character(generator.comments)}.

    Attributes:
% for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_inline and not a.kind == helper.AttributeKind.SIZE_FIELD and not a.attribute_is_reserved and a.attribute_name != 'size']:
        ${a.attribute_name}: ${helper.capitalize_first_character(a.attribute_comment)}.
% endfor
    """
<%def name="renderCondition(a, useSelf=True)" filter="trim">
    ${helper.get_condition_operation_text(a.attribute['condition_operation']).format(('self.' if useSelf else '') + a.attribute['condition'], helper.get_generated_class_name(a.condition_type_attribute['type'], a.condition_type_attribute, generator.schema) + '.' + helper.create_enum_name(a.attribute['condition_value']))}
</%def>\
##  CONSTRUCTOR:
<%
    constructor_params = generator.all_constructor_params
    constructor_params_CSV = ', '.join([str(a.attribute_name) + ': ' + str(a.attribute_var_type) for a in constructor_params if a.attribute_condition_value is None and not a.attribute_is_aggregate and not a.attribute_is_reserved and not a.attribute_name == 'size'])
    super_arguments_CSV = ', '.join([str(a.attribute_name) for a in constructor_params if a.attribute_is_super and not a.attribute_is_reserved and not a.attribute_is_aggregate and not a.attribute_name == 'size'])
%>
## condition should be the same as condition in ctor
% if 0 == len([a for a in constructor_params if not a.attribute_is_inline and not a.attribute_is_super and not a.attribute_is_reserved and not a.attribute_name == 'size']):
    # pylint: disable=useless-super-delegation
% endif
    def __init__(self, ${constructor_params_CSV}):
        """Constructor.
        Args:
% for a in [a for a in constructor_params if a.attribute_condition_value is None and not a.attribute_is_aggregate and not a.attribute_is_reserved and not a.kind == helper.AttributeKind.SIZE_FIELD and not a.attribute_name == 'size']:
            ${a.attribute_name}: ${helper.capitalize_first_character(a.attribute_comment)}.
% endfor
        """
    % if generator.base_class_name is not None:
        super().__init__(${super_arguments_CSV})
    % endif
    % for a in [a for a in constructor_params if not a.attribute_is_inline and not a.attribute_is_super and not a.attribute_is_reserved and not a.attribute_name == 'size']:
        % if a.attribute_is_aggregate:
        self.${a.attribute_name} = ${a.attribute_var_type}(${', '.join([str(inline.attribute_name) for inline in constructor_params if inline.attribute_aggregate_attribute_name == a.attribute_name and not inline.attribute_is_reserved and not inline.kind == helper.AttributeKind.SIZE_FIELD and inline.attribute_condition_value is None and not inline.attribute_is_aggregate])})
        % else:
        self.${a.attribute_name} = ${a.attribute_name}
        % endif
    % endfor

% if 'AggregateTransactionBody' in generator.generated_class_name:
    @staticmethod
    def _loadEmbeddedTransactions(transactions: List[EmbeddedTransactionBuilder], payload: bytes, payloadSize: int):
        remainingByteSizes = payloadSize
        while remainingByteSizes > 0:
            item = EmbeddedTransactionBuilderFactory.createBuilder(payload)
            transactions.append(item)
            itemSize = item.getSize() + GeneratorUtils.getTransactionPaddingSize(item.getSize(), 8)
            remainingByteSizes -= itemSize
            payload = payload[itemSize:]
        return payload
% endif
##  LOAD FROM BINARY:
<%def name="renderReader(a)" filter="trim" buffered="True">
    % if a.kind == helper.AttributeKind.SIMPLE:
        ${a.attribute_name} = GeneratorUtils.bufferToUint(GeneratorUtils.getBytes(bytes_, ${a.attribute_size}))  # kind:SIMPLE
        bytes_ = bytes_[${a.attribute_size}:]
    % elif a.kind == helper.AttributeKind.BUFFER:
        ${a.attribute_name} = GeneratorUtils.getBytes(bytes_, ${a.attribute_size})  # kind:BUFFER
        bytes_ = bytes_[${a.attribute_size}:]
    % elif a.kind == helper.AttributeKind.SIZE_FIELD:
        ${a.attribute_name} = GeneratorUtils.bufferToUint(GeneratorUtils.getBytes(bytes_, ${a.attribute_size}))  # kind:SIZE_FIELD
        bytes_ = bytes_[${a.attribute_size}:]
    % elif a.kind == helper.AttributeKind.ARRAY:
        ${a.attribute_name}: ${a.attribute_var_type} = []  # kind:ARRAY
        for _ in range(${a.attribute_size}):
            item = ${a.attribute_class_name}.loadFromBinary(bytes_)
            ${a.attribute_name}.append(item)
            bytes_ = bytes_[item.getSize():]
    % elif a.kind == helper.AttributeKind.CUSTOM and a.conditional_read_before:
        ${a.attribute_name} = ${a.attribute_class_name}.loadFromBinary(${a.attribute['condition']}Condition)  # kind:CUSTOM3
    % elif a.kind == helper.AttributeKind.CUSTOM and a.attribute_base_type == 'enum':
        ${a.attribute_name} = ${a.attribute_class_name}.loadFromBinary(bytes_)  # kind:CUSTOM2
        bytes_ = bytes_[${a.attribute_name}.getSize():]
    % elif a.kind == helper.AttributeKind.CUSTOM:
        ${a.attribute_name} = ${a.attribute_class_name}.loadFromBinary(bytes_)  # kind:CUSTOM1
        bytes_ = bytes_[${a.attribute_name}.getSize():]
    % elif a.kind == helper.AttributeKind.FILL_ARRAY:
        ${a.attribute_name}: List[${a.attribute_class_name}] = []
        bytes_ = GeneratorUtils.loadFromBinary(${a.attribute_class_name}, ${a.attribute_name}, bytes_, len(bytes_))
    % elif a.kind == helper.AttributeKind.FLAGS:
        ${a.attribute_name} = ${a.attribute_class_name}.bytesToFlags(bytes_, ${a.attribute_size})  # kind:FLAGS
        bytes_ = bytes_[${a.attribute_size}:]
    % elif a.kind == helper.AttributeKind.VAR_ARRAY:
        transactions: List[${a.attribute_class_name}] = []
        bytes_ = ${generator.generated_class_name}._loadEmbeddedTransactions(transactions, bytes_, ${a.attribute_size})
    % else:
        FIX ME!
    % endif
</%def>\
<%
    possible_constructor_params = generator.constructor_attributes[0]
    if generator.base_class_name is None:
        constructor_arguments_CSV = ', '.join([str(a.attribute_name)
        for a in possible_constructor_params if not a.attribute_is_aggregate and not a.attribute_is_reserved and not a.attribute_name == 'size'])
    else:
        constructor_arguments_CSV = ', '.join(['{0}{1}'.format('superObject.' if a.attribute_is_super else ('' if a.attribute_aggregate_attribute_name is None else a.attribute_aggregate_attribute_name + '.'), a.attribute_name)
        for a in possible_constructor_params if not a.attribute_is_aggregate and not a.attribute_is_reserved and not a.attribute_name == 'size'])
%>
    @classmethod
    def loadFromBinary(cls, payload: bytes) -> ${generator.generated_class_name}:
        """Creates an instance of ${generator.generated_class_name} from binary payload.
        Args:
            payload: Byte payload to use to serialize the object.
        Returns:
            Instance of ${generator.generated_class_name}.
        """
        bytes_ = bytes(payload)
    % if generator.base_class_name is not None:
        superObject = ${generator.generated_base_class_name}.loadFromBinary(bytes_)
        bytes_ = bytes_[superObject.getSize():]
    % endif
    % for a in set([(a.attribute['condition'], a.attribute_size, a.conditional_read_before) for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_inline and a.conditional_read_before and a.attribute_is_conditional]):
        ${a[0]}Condition = bytes_[0:${a[1]}]
        bytes_ = bytes_[${a[1]}:]
    % endfor

    % for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_inline and not a.conditional_read_before]:
        %if a.attribute_is_conditional:
        ${a.attribute_name} = None
        if ${renderCondition(a, useSelf=False) | trim}:
            ## handle py indents
            % for line in map(lambda a: a.strip(), renderReader(a).splitlines()):
            ${line}
            % endfor
        % else:
        ${renderReader(a) | trim}
        %endif
    % endfor
    % for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_inline and a.conditional_read_before]:
        ${a.attribute_name} = None
        if ${renderCondition(a, useSelf=False) | trim}:
            ## handle py indents
            % for line in map(lambda a: a.strip(), renderReader(a).splitlines()):
            ${line}
            % endfor
    % endfor
        return ${generator.generated_class_name}(${constructor_arguments_CSV})

## GETTERS:
% for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_reserved and not a.attribute_is_aggregate and not a.kind == helper.AttributeKind.SIZE_FIELD and (not a.attribute_is_reserved or not a.attribute_is_inline) and not a.attribute_name == 'size']:
    def get${helper.capitalize_first_character(a.attribute_name) if a.attribute_name != 'size' else 'BytesSize'}(self) -> ${a.attribute_var_type}:
        """Gets ${a.attribute_comment}.
        Returns:
            ${helper.capitalize_first_character(a.attribute_comment)}.
        """
    % if a.attribute_is_conditional and not a.attribute_is_inline:
        if not ${renderCondition(a) | trim}:
            raise Exception('${a.attribute['condition']} is not set to ${helper.create_enum_name(a.attribute['condition_value'])}.')
    % endif
    % if a.attribute_is_inline:
        return self.${a.attribute_aggregate_attribute_name}.get${helper.capitalize_first_character(a.attribute_name)}()
    % else:
        return self.${a.attribute_name}
    % endif

% endfor
% if 'AggregateTransactionBody' in generator.generated_class_name:
    @classmethod
    def _serialize_aligned(cls, transaction: EmbeddedTransactionBuilder) -> bytes:
        """Serializes an embeded transaction with correct padding.
        Returns:
            Serialized embedded transaction.
        """
        bytes_ = transaction.serialize()
        padding = bytes(GeneratorUtils.getTransactionPaddingSize(len(bytes_), 8))
        return GeneratorUtils.concatTypedArrays(bytes_, padding)

    @classmethod
    def _getSize_aligned(cls, transaction: EmbeddedTransactionBuilder) -> int:
        """Serializes an embeded transaction with correct padding.
        Returns:
            Serialized embedded transaction.
        """
        size = transaction.getSize()
        paddingSize = GeneratorUtils.getTransactionPaddingSize(size, 8)
        return size + paddingSize
% endif
## SIZE:
<%def name="renderSize(a)" filter="trim"  buffered="True">\
    % if a.kind == helper.AttributeKind.SIMPLE:
        size += ${a.attribute_size}  # ${a.attribute_name}
    % elif a.kind == helper.AttributeKind.SIZE_FIELD:
        size += ${a.attribute_size}  # ${a.attribute_name}
    % elif a.kind == helper.AttributeKind.BUFFER:
        size += len(self.${a.attribute_name})
    % elif a.kind == helper.AttributeKind.VAR_ARRAY:
        for _ in self.${a.attribute_name}:
            size += self._getSize_aligned(_)
    % elif a.kind == helper.AttributeKind.ARRAY or a.kind == helper.AttributeKind.FILL_ARRAY:
        for _ in self.${a.attribute_name}:
            size += _.getSize()
    % elif a.kind == helper.AttributeKind.FLAGS:
        size += ${a.attribute_size}  # ${a.attribute_name}
    % else:
        size += self.${a.attribute_name}.getSize()
    % endif
</%def>\
    def getSize(self) -> int:
        """Gets the size of the object.
        Returns:
            Size in bytes.
        """
        size = ${'super().getSize()' if generator.base_class_name is not None else '0'}
% for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_inline]:
    % if a.attribute_is_conditional:
        if ${renderCondition(a) | trim}:
            ## handle py indents
            % for line in map(lambda a: a.strip(), renderSize(a).splitlines()):
            ${line}
            % endfor
    % else:
        ${renderSize(a).strip()}
    % endif
% endfor
        return size

% if generator.base_class_name in ['Transaction', 'EmbeddedTransaction']:
    def getBody(self) -> ${generator.body_class_name}Builder:
        """Gets the body builder of the object.
        Returns:
            Body builder.
        """
        return self.${helper.decapitalize_first_character(generator.body_class_name)}

% endif
% if generator.name in ['Transaction', 'EmbeddedTransaction']:
    def getBody(self) -> None:
        """Gets the body builder of the object.
        Returns:
            Body builder.
        """
        return None

% endif
##  SERIALIZE:
<%def name="renderSerialize(a)" filter="trim" buffered="True">\
    % if a.kind == helper.AttributeKind.SIMPLE and a.attribute_is_reserved:
        bytes_ = GeneratorUtils.concatTypedArrays(bytes_, GeneratorUtils.uintToBuffer(0, ${a.attribute_size}))
    % elif a.kind == helper.AttributeKind.SIMPLE and (generator.name != 'Receipt' or a.attribute_name != 'size'):
        % if a.attribute_is_reserved:
        bytes_ = GeneratorUtils.concatTypedArrays(bytes_, GeneratorUtils.uintToBuffer(0, ${a.attribute_size}))  # kind:SIMPLE
        % else:
        bytes_ = GeneratorUtils.concatTypedArrays(bytes_, GeneratorUtils.uintToBuffer(self.get${helper.capitalize_first_character(a.attribute_name)}(), ${a.attribute_size}))  # kind:SIMPLE
        % endif
    % elif a.kind == helper.AttributeKind.BUFFER:
        bytes_ = GeneratorUtils.concatTypedArrays(bytes_, self.${a.attribute_name})  # kind:BUFFER
    % elif a.kind == helper.AttributeKind.SIZE_FIELD:
        ## note: it would be best to access parent 'kind'
        % if 'AggregateTransactionBody' in generator.generated_class_name and a.attribute_name == 'payloadSize':
        # calculate payload size
        size_value = 0
        for _ in self.${a.parent_attribute['name']}:
            size_value += self._getSize_aligned(_)
        bytes_ = GeneratorUtils.concatTypedArrays(bytes_, GeneratorUtils.uintToBuffer(size_value, ${a.attribute_size}))  # kind:SIZE_FIELD
        % else:
        bytes_ = GeneratorUtils.concatTypedArrays(bytes_, GeneratorUtils.uintToBuffer(len(self.get${helper.capitalize_first_character(a.parent_attribute['name'])}()), ${a.attribute_size}))  # kind:SIZE_FIELD
        % endif
    % elif a.kind == helper.AttributeKind.ARRAY or a.kind == helper.AttributeKind.FILL_ARRAY:
        for _ in self.${a.attribute_name}: # kind:ARRAY|FILL_ARRAY
            bytes_ = GeneratorUtils.concatTypedArrays(bytes_, _.serialize())
    % elif a.kind == helper.AttributeKind.VAR_ARRAY:
        for _ in self.${a.attribute_name}: # kind:VAR_ARRAY
            bytes_ = GeneratorUtils.concatTypedArrays(bytes_, self._serialize_aligned(_))
    % elif a.kind == helper.AttributeKind.CUSTOM:
        bytes_ = GeneratorUtils.concatTypedArrays(bytes_, self.${a.attribute_name}.serialize())  # kind:CUSTOM
    % elif a.kind == helper.AttributeKind.FLAGS:
        bytes_ = GeneratorUtils.concatTypedArrays(bytes_, GeneratorUtils.uintToBuffer(${a.attribute_class_name}.flagsToInt(self.get${helper.capitalize_first_character(a.attribute_name)}()), ${a.attribute_size}))  # kind:FLAGS
    % else:
        # Ignored serialization: ${a.attribute_name} ${a.kind}
    % endif
</%def>\
    def serialize(self) -> bytes:
        """Serializes self to bytes.
        Returns:
            Serialized bytes.
        """
        bytes_ = bytes()
 % if generator.base_class_name is not None:
        bytes_ = GeneratorUtils.concatTypedArrays(bytes_, super().serialize())
% endif
% for a in [a for a in generator.attributes if not a.attribute_is_super and not a.attribute_is_inline]:
    % if a.attribute_is_conditional:
        if ${renderCondition(a) | trim}:
            ## handle py indents
            % for line in map(lambda a: a.strip(), renderSerialize(a).splitlines()):
            ${line}
            % endfor
    % else:
        ${renderSerialize(a)}
    % endif
% endfor
        return bytes_