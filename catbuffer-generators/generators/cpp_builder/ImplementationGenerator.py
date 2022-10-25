from .CppGenerator import CppGenerator, FieldKind, capitalize

SUFFIX = 'Transaction'


class ImplementationGenerator(CppGenerator):
    def _add_includes(self):
        self.append('#include "{BUILDER_NAME}.h"')

        if 'includes' in self.hints:
            for include in self.hints['includes']:
                self.append('#include "{0}"'.format(include))

        self.append('')

    def _class_header(self):
        self.append('{BUILDER_NAME}::{BUILDER_NAME}(model::NetworkIdentifier networkIdentifier, const Key& signer)')
        self.indent += 2
        self.append(': TransactionBuilder(networkIdentifier, signer)')
        self._foreach_builder_field(self._generate_field_initializer_list_entry)
        self.indent -= 2
        self.append('{{}}')
        self.append('')

    def _generate_call_to_setter_for_bound_field(self, condition_field_name, condition_value):
        field = self._get_schema_field(condition_field_name)
        field_kind = CppGenerator._get_field_kind(field)
        _, param_type, param_name = self._get_setter_name_desc(field_kind, field)
        return 'm_{NAME} = {TYPE_NAME}::{VALUE};'.format(NAME=param_name, TYPE_NAME=param_type, VALUE=condition_value)

    def _generate_setter(self, field_kind, field, full_setter_name, param_name):
        self.append('void {BUILDER_NAME}::' + full_setter_name + ' {{')
        self.indent += 1
        if field_kind == FieldKind.SIMPLE:
            self.append('m_{NAME} = {NAME};'.format(NAME=param_name))
            if 'condition' in field:
                call_line = self._generate_call_to_setter_for_bound_field(field['condition'], capitalize(field['condition_value']))
                self.append(call_line)
        elif field_kind == FieldKind.BUFFER:
            self.append('''if (0 == {NAME}.Size)
\tCATAPULT_THROW_INVALID_ARGUMENT("argument `{NAME}` cannot be empty");

if (!m_{NAME}.empty())
\tCATAPULT_THROW_RUNTIME_ERROR("`{NAME}` field already set");

m_{NAME}.resize({NAME}.Size);
m_{NAME}.assign({NAME}.pData, {NAME}.pData + {NAME}.Size);'''.format(NAME=param_name))
        else:
            if 'sort_key' in field:
                format_string = 'InsertSorted(m_{FIELD}, {PARAM}, [](const auto& lhs, const auto& rhs) {{{{'
                self.append(format_string.format(FIELD=field['name'], PARAM=param_name))
                self.indent += 1
                self.append('return lhs.{SORT_KEY} < rhs.{SORT_KEY};'.format(SORT_KEY=capitalize(field['sort_key'])))
                self.indent -= 1
                self.append('}});')
            else:
                self.append('m_{FIELD}.push_back({PARAM});'.format(FIELD=field['name'], PARAM=param_name))
        self.indent -= 1
        self.append('}}\n')

    def _generate_field(self, field_kind, field, builder_field_typename):
        pass

    def _generate_field_initializer_list_entry(self, field):
        self.append(', m_{NAME}()'.format(NAME=field['name']))

    def _generate_build_variable_fields_size(self, variable_sizes, field):
        field_kind = CppGenerator._get_field_kind(field)
        formatted_vector_size = 'm_{NAME}.size()'.format(NAME=field['name'])
        if field_kind == FieldKind.BUFFER:
            self.append('size += {};'.format(formatted_vector_size))
        elif field_kind == FieldKind.VECTOR:
            qualified_typename = self.qualified_type(field['type'])
            formatted_size = '{ARRAY_SIZE} * sizeof({TYPE})'.format(ARRAY_SIZE=formatted_vector_size, TYPE=qualified_typename)
            self.append('size += {};'.format(formatted_size))

        if field_kind != FieldKind.SIMPLE:
            variable_sizes[field['size']] = formatted_vector_size

    def _generate_transaction_field_name(self, name):
        field_name = capitalize(name)
        rewritten = self.hints['rewrites'].get(field_name, '') if 'rewrites' in self.hints else ''
        return rewritten or field_name

    def _generate_build_variable_fields(self, field):
        field_kind = CppGenerator._get_field_kind(field)
        if field_kind == FieldKind.SIMPLE:
            return

        template = {'NAME': field['name'], 'TX_FIELD_NAME': self._generate_transaction_field_name(field['name'])}
        if field_kind in (FieldKind.BUFFER, FieldKind.VECTOR):
            self.append('std::copy(m_{NAME}.cbegin(), m_{NAME}.cend(), pTransaction->{TX_FIELD_NAME}Ptr());'.format(**template))

    @staticmethod
    def byte_size_to_type_name(size):
        return {1: 'uint8_t', 2: 'uint16_t', 4: 'uint32_t', '8': 'uint64_t'}[size]

    def _generate_condition(self, condition_field_name, condition_value):
        field = self._get_schema_field(condition_field_name)
        field_kind = CppGenerator._get_field_kind(field)
        _, param_type, _ = self._get_setter_name_desc(field_kind, field)
        return 'if ({TYPE_NAME}::{VALUE} == m_{NAME})'.format(TYPE_NAME=param_type, VALUE=capitalize(condition_value), NAME=field['name'])

    def _generate_build(self, variable_sizes):
        self.append('template<typename TransactionType>')
        self.append('std::unique_ptr<TransactionType> {BUILDER_NAME}::buildImpl() const {{')
        self.indent += 1

        self.append('// 1. allocate, zero (header), set model::Transaction fields')
        self.append('auto pTransaction = createTransaction<TransactionType>(sizeImpl<TransactionType>());')
        self.append('')

        self.append('// 2. set fixed transaction fields')

        # set non-variadic fields
        for field in self.schema[self.transaction_body_name()]['layout']:
            template = {'NAME': field['name'], 'TX_FIELD_NAME': self._generate_transaction_field_name(field['name'])}
            if field['name'].endswith('Size') or field['name'].endswith('Count'):
                size = variable_sizes[field['name']]
                size_type = ImplementationGenerator.byte_size_to_type_name(field['size'])
                format_string = 'pTransaction->{TX_FIELD_NAME} = utils::checked_cast<size_t, {SIZE_TYPE}>({SIZE});'
                self.append(format_string.format(**template, SIZE_TYPE=size_type, SIZE=size))
            else:
                field_kind = CppGenerator._get_field_kind(field)
                if field_kind == FieldKind.SIMPLE:
                    if 'condition' in field:
                        condition = self._generate_condition(field['condition'], field['condition_value'])
                        self.append(condition)
                        self.indent += 1

                    # if setter has been suppressed, fill in with what is defined in setters.yaml hint file
                    setter = self.hints['setters'].get(field['name'], '') if 'setters' in self.hints else ''
                    if setter:
                        self.append('pTransaction->{TX_FIELD_NAME} = {SETTER};'.format(**template, SETTER=setter))
                    elif '_Reserved' in field['name']:
                        self.append('pTransaction->{TX_FIELD_NAME} = 0;'.format(**template))
                    else:
                        self.append('pTransaction->{TX_FIELD_NAME} = m_{NAME};'.format(**template))

                    if 'condition' in field:
                        self.indent -= 1
                        self.append('')

                # variadic fields are defined at the end of schema,
                # so break if loop reached any of them
                else:
                    break

        self.append('')

        if self._contains_any_other_field_kind(FieldKind.SIMPLE):
            self.append('// 3. set transaction attachments')
            self._foreach_builder_field(self._generate_build_variable_fields)

            # variable fields that expand to conditional statement will append a blank line, so, if one is present, don't add another
            if '' != self.code[-1]:
                self.append('')

        self.append('return pTransaction;')
        self.indent -= 1
        self.append('}}')

    def _generate_size(self):
        self.append('template<typename TransactionType>')
        self.append('size_t {BUILDER_NAME}::sizeImpl() const {{')
        self.indent += 1
        self.append('// calculate transaction size')
        self.append('auto size = sizeof(TransactionType);')

        # go through variable data and add it to size, collect sizes
        variable_sizes = {}
        self._foreach_builder_field(lambda field: self._generate_build_variable_fields_size(variable_sizes, field))

        self.append('return size;')
        self.indent -= 1
        self.append('}}\n')
        return variable_sizes

    def _builds(self):
        self.append('''size_t {BUILDER_NAME}::size() const {{
\treturn sizeImpl<Transaction>();
}}

std::unique_ptr<{BUILDER_NAME}::Transaction> {BUILDER_NAME}::build() const {{
\treturn buildImpl<Transaction>();
}}

std::unique_ptr<{BUILDER_NAME}::EmbeddedTransaction> {BUILDER_NAME}::buildEmbedded() const {{
\treturn buildImpl<EmbeddedTransaction>();
}}
''')
        variable_sizes = self._generate_size()
        self._generate_build(variable_sizes)

    def _class_footer(self):
        pass
