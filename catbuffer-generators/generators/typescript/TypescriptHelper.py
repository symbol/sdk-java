import re
from generators.common.Helper import Helper, AttributeKind


class TypescriptHelper(Helper):

    @staticmethod
    def add_required_import(required_import: set, import_type, class_name, base_class_name):
        for typename in re.split('[\\[\\]]', import_type):
            if typename and typename not in ['List', 'Uint8Array']:
                if 'TransactionHeaderBuilder' in typename:
                    if typename == base_class_name:
                        required_import.add(typename)
                if 'EmbeddedTransactionBuilder' in typename:
                    required_import.add('EmbeddedTransactionHelper')
                    required_import.add(typename)
                elif typename != class_name and str(typename)[0].isupper():
                    required_import.add(typename)
        return required_import

    def get_body_class_name(self, name):
        body_name = name if not name.startswith('Embedded') else name[8:]
        if name.startswith('Aggregate') and name.endswith('Transaction'):
            body_name = 'AggregateTransaction'
        return '{0}Body'.format(body_name)

    def get_builtin_type(self, size):
        if size == 8:
            return 'number[]'
        return 'number'

    def get_read_method_name(self, size, var_name):
        if isinstance(size, str) or size > 8:
            return 'GeneratorUtils.getBytes(Uint8Array.from({0}), {1})'.format(var_name, size)
        if size == 8:
            return 'GeneratorUtils.bufferToUint64(Uint8Array.from({0}))'.format(var_name)
        if size == 4:
            return 'GeneratorUtils.bufferToUint32(Uint8Array.from({0}))'.format(var_name)
        if size == 2:
            return 'GeneratorUtils.bufferToUint16(Uint8Array.from({0}))'.format(var_name)
        if size == 1:
            return 'GeneratorUtils.bufferToUint8(Uint8Array.from({0}))'.format(var_name)
        return 'GeneratorUtils.getBytes(Uint8Array.from({0}), {1})'.format(var_name, size)

    def get_serialize_method_name(self, size):
        if isinstance(size, str) or size > 8:
            return ''
        if size == 8:
            return 'GeneratorUtils.uint64ToBuffer'
        if size == 4:
            return 'GeneratorUtils.uint32ToBuffer'
        if size == 2:
            return 'GeneratorUtils.uint16ToBuffer'
        if size == 1:
            return 'GeneratorUtils.uint8ToBuffer'
        return ''

    def get_load_from_binary_factory(self, attribute_class_name):
        if attribute_class_name == 'EmbeddedTransactionBuilder':
            return 'EmbeddedTransactionHelper'
        return attribute_class_name

    def get_condition_operation_text(self, op):
        if op == 'has':
            return '{0}.indexOf({1}) > -1'
        return '{0} === {1}'

    def get_generated_type(self, schema, attribute, attribute_kind):
        typename = attribute['type']
        if attribute_kind in (AttributeKind.SIMPLE, AttributeKind.SIZE_FIELD):
            return self.get_builtin_type(self.get_attribute_size(schema, attribute))
        if attribute_kind == AttributeKind.BUFFER:
            return 'Uint8Array'
        if not self.is_byte_type(typename):
            typename = self.get_generated_class_name(typename, attribute, schema)
        if self.is_any_array_kind(attribute_kind):
            return '{0}[]'.format(typename)
        if attribute_kind == AttributeKind.FLAGS:
            return '{0}[]'.format(typename)
        return typename
