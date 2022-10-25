from generators.common.Helper import Helper, AttributeKind


class JavaHelper(Helper):

    def get_body_class_name(self, name):
        body_name = name if not name.startswith('Embedded') else name[8:]
        if name.startswith('Aggregate') and any(name.endswith(postfix) for postfix in ('Transaction', 'TransactionV1')):
            body_name = 'AggregateTransaction'

        return '{0}Body'.format(body_name)

    def get_builtin_type(self, size):
        builtin_types = {1: 'byte', 2: 'short', 4: 'int', 8: 'long'}
        builtin_type = builtin_types[size]
        return builtin_type

    def get_read_method_name(self, size):
        if isinstance(size, str) or size > 8:
            method_name = 'readFully'
        else:
            type_size_method_name = {1: 'readByte', 2: 'readShort', 4: 'readInt', 8: 'readLong'}
            method_name = type_size_method_name[size]
        return method_name

    def get_load_from_binary_factory(self, attribute_class_name):
        if attribute_class_name == 'EmbeddedTransactionBuilder':
            return 'TransactionBuilderFactory'
        return attribute_class_name

    def get_condition_operation_text(self, op):
        if op == 'has':
            return '{0}.contains({1})'
        return '{0} == {1}'

    def get_reverse_method_name(self, size):
        if isinstance(size, str) or size > 8 or size == 1:
            method_name = '{0}'
        else:
            typesize_methodname = {2: 'Short.reverseBytes({0})',
                                   4: 'Integer.reverseBytes({0})',
                                   8: 'Long.reverseBytes({0})'}
            method_name = typesize_methodname[size]
        return method_name

    def get_to_unsigned_method_name(self, size):
        unsigned_methodname = {1: 'GeneratorUtils.toUnsignedInt({0})',
                               2: 'GeneratorUtils.toUnsignedInt({0})'}
        return unsigned_methodname[size]

    def get_write_method_name(self, size):
        if isinstance(size, str) or size > 8 or size == 0:
            method_name = 'write'
        else:
            typesize_methodname = {1: 'writeByte',
                                   2: 'writeShort',
                                   4: 'writeInt',
                                   8: 'writeLong'}
            method_name = typesize_methodname[size]
        return method_name

    def get_generated_type(self, schema, attribute, attribute_kind):
        typename = attribute['type']
        if attribute_kind in (AttributeKind.SIMPLE, AttributeKind.SIZE_FIELD):
            return self.get_builtin_type(self.get_attribute_size(schema, attribute))
        if attribute_kind == AttributeKind.BUFFER:
            return 'ByteBuffer'
        if not self.is_byte_type(typename):
            typename = self.get_generated_class_name(typename, attribute, schema)
        if self.is_any_array_kind(attribute_kind):
            return 'List<{0}>'.format(typename)
        if attribute_kind == AttributeKind.FLAGS:
            return 'EnumSet<{0}>'.format(typename)
        return typename
