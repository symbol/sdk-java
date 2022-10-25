import re

from generators.common.Helper import Helper, AttributeKind


class PythonHelper(Helper):

    @staticmethod
    def add_required_import(required_import: set, import_type, class_name, base_class_name):
        for typename in re.split('[\\[\\]]', import_type):
            if typename:
                if typename in ['List']:
                    required_import.add('from typing import ' + typename)
                elif 'TransactionHeaderBuilder' in typename:
                    if typename == base_class_name:
                        required_import.add('from .' + typename + ' import ' + typename)
                elif typename != class_name and str(typename)[0].isupper():
                    required_import.add('from .' + typename + ' import ' + typename)
        if class_name == 'AggregateTransactionBodyBuilder':
            required_import.add('from. EmbeddedTransactionBuilderFactory import EmbeddedTransactionBuilderFactory')
        return required_import

    @staticmethod
    def get_all_constructor_params(attributes):
        return [a for a in attributes if not a.kind == AttributeKind.SIZE_FIELD]

    @staticmethod
    def get_body_class_name(name):
        body_name = name if not name.startswith('Embedded') else name[8:]
        if name.startswith('Aggregate') and name.endswith('Transaction'):
            body_name = 'AggregateTransaction'
        return '{0}Body'.format(body_name)

    def get_builtin_type(self, size):
        return 'int'

    @staticmethod
    def get_condition_operation_text(op):
        if op == 'has':
            return '{1} in {0}'
        return '{0} == {1}'

    def get_generated_type(self, schema, attribute, attribute_kind):
        typename = attribute['type']
        if attribute_kind in (AttributeKind.SIMPLE, AttributeKind.SIZE_FIELD):
            return self.get_builtin_type(self.get_attribute_size(schema, attribute))
        if attribute_kind == AttributeKind.BUFFER:
            return 'bytes'
        if not self.is_byte_type(typename):
            typename = self.get_generated_class_name(typename, attribute, schema)
        if self.is_any_array_kind(attribute_kind):
            return 'List[{0}]'.format(typename)
        if attribute_kind == AttributeKind.FLAGS:
            return 'List[{0}]'.format(typename)
        return typename
