# pylint: disable=too-few-public-methods
from abc import ABC, abstractmethod
from enum import Enum
import os
import re
import yaml

SUFFIX = 'Transaction'


class FieldKind(Enum):
    SIMPLE = 1
    BUFFER = 2
    VECTOR = 3
    UNKNOWN = 100


def tokenize(string):
    return re.findall('[A-Z][^A-Z]*', string)


def join_lower(strings):
    return ' '.join([string.lower() for string in strings])


def uncapitalize(string):
    return string[0].lower() + string[1:] if string else string


# note that string.capitalize also lowers [1:]
def capitalize(string):
    return string[0].upper() + string[1:] if string else string


def singularize(string):
    if string.endswith('ies'):
        return string[:-3] + 'y'

    if string.endswith('es'):
        return string[:-2]

    if string.endswith('s'):
        return string[:-1]

    return string


class GeneratorInterface(ABC):
    @abstractmethod
    def _add_includes(self):
        raise NotImplementedError('need to override method')

    @abstractmethod
    def _class_header(self):
        raise NotImplementedError('need to override method')

    @abstractmethod
    def _generate_setter(self, field_kind, field, full_setter_name, param_name):
        raise NotImplementedError('need to override method')

    @abstractmethod
    def _generate_field(self, field_kind, field, builder_field_typename):
        raise NotImplementedError('need to override method')

    @abstractmethod
    def _builds(self):
        raise NotImplementedError('need to override method')

    @abstractmethod
    def _class_footer(self):
        raise NotImplementedError('need to override method')


# FP from pylint, this is semi-abstract class
# pylint: disable=abstract-method
class CppGenerator(GeneratorInterface):
    def __init__(self, schema, options, name):
        super(CppGenerator, self).__init__()
        self.schema = schema
        self.code = []
        self.transaction_name = name
        self.replacements = {
            'TRANSACTION_NAME': self.transaction_name,
            'BUILDER_NAME': self.builder_name(),
            'COMMENT_NAME': self.written_name(),
            'COMMENT_NAME_A_OR_AN': 'an' if self.written_name().startswith(('a', 'e', 'i', 'o', 'u')) else 'a'
        }

        self.indent = 0
        self.hints = CppGenerator._load_hints(['includes', 'namespaces', 'plugin', 'rewrites', 'setters'])[self.transaction_name]
        self.prepend_copyright(options['copyright'])

    @staticmethod
    def _load_hints(filenames):
        all_hints = {}
        for filename in filenames:
            with open('generators/cpp_builder/hints/{0}.yaml'.format(filename)) as input_file:
                hints = yaml.load(input_file, Loader=yaml.SafeLoader)
                for hint_key in hints:
                    if hint_key not in all_hints:
                        all_hints[hint_key] = {}

                    all_hints[hint_key][filename] = hints.get(hint_key)

        return all_hints

    def transaction_body_name(self):
        return '{}Body'.format(self.transaction_name)

    def builder_name(self):
        return '{}Builder'.format(self.transaction_name[:-len(SUFFIX)])

    def written_name(self):
        return join_lower(tokenize(self.transaction_name[:-len(SUFFIX)]))

    def prepend_copyright(self, copyright_file):
        if os.path.isfile(copyright_file):
            with open(copyright_file) as header:
                self.code = [line.strip() for line in header]

    def generate(self):
        self._add_includes()
        self._namespace_start()
        self.indent = 1
        self._class_header()
        self._setters()
        self._builds()
        self._privates()
        self._class_footer()
        self.indent = 0
        self._namespace_end()

        return self.code

    # region helpers

    def _get_namespace(self, typename):
        namespace = self.hints['namespaces'].get(typename, '') if 'namespaces' in self.hints else ''
        if namespace:
            namespace += '::'

        return namespace

    def append(self, multiline_string, additional_replacements=None):
        for line in re.split(r'\n', multiline_string):
            # indent non-empty lines
            if line:
                replacements = {**self.replacements, **additional_replacements} if additional_replacements else self.replacements
                self.code.append('\t' * self.indent + line.format(**replacements))
            else:
                self.code.append('')

    def qualified_type(self, typename):
        namespace = self._get_namespace(typename)
        return namespace + typename

    @staticmethod
    def _is_builtin_type(typename, size):
        # uint8_t up to uint64_t are passed as 'byte' with size set to proper value
        return 'byte' == typename and size <= 8

    @staticmethod
    def _builtin_type(size, signedness):
        builtin_types = {1: 'int8_t', 2: 'int16_t', 4: 'int32_t', 8: 'int64_t'}
        builtin_type = builtin_types[size]
        return builtin_type if signedness == 'signed' else 'u' + builtin_type

    def param_type(self, typename, size, signedness):
        if not isinstance(size, str) and size > 0 and self._is_builtin_type(typename, size):
            return self._builtin_type(size, signedness)

        # if type is simple pass by value, otherwise pass by reference
        type_descriptor = self.schema[typename]
        qualified_typename = self.qualified_type(typename)

        if 'byte' == type_descriptor['type'] and type_descriptor['size'] <= 8:
            return qualified_typename

        if 'enum' == type_descriptor['type']:
            return qualified_typename

        return 'const {}&'.format(qualified_typename)

    def _get_schema_field(self, field_name):
        return next(field for field in self.schema[self.transaction_body_name()]['layout'] if field['name'] == field_name)

    @staticmethod
    def method_name(prefix, param_name):
        return '{PREFIX}{CAPITALIZED_PARAM_NAME}'.format(PREFIX=prefix, CAPITALIZED_PARAM_NAME=capitalize(param_name))

    @staticmethod
    def full_method_name(prefix, typename, param_name):
        method_name = CppGenerator.method_name(prefix, param_name)
        return '{METHOD_NAME}({TYPE_NAME} {PARAM_NAME})'.format(METHOD_NAME=method_name, TYPE_NAME=typename, PARAM_NAME=param_name)

    # endregion

    # region generate sub-methods

    def _namespace_start(self):
        self.append('namespace catapult {{ namespace builders {{')
        self.append('')

    def _setters(self):
        self._foreach_builder_field(self._generate_setter_proxy)

    def _privates(self):
        self._foreach_builder_field(self._generate_field_proxy)

    def _namespace_end(self):
        self.append('}}}}')

    # endregion

    # region internals

    def _foreach_builder_field(self, callback):
        for field in self.schema[self.transaction_body_name()]['layout']:
            # for builder fields, skip Size or count fields, they are always used for variable data
            name = field['name']
            if name.endswith('Size') or name.endswith('Count') or '_Reserved' in name:
                continue

            callback(field)

    def _get_simple_setter_name_desc(self, field):
        """sample: void setRemoteAccountKey(const Key& remoteAccountKey)"""
        param_type = self.param_type(field['type'], field.get('size', 0), field.get('signedness', ''))
        param_name = field['name']
        return 'set', param_type, param_name

    @staticmethod
    def _get_buffer_setter_name_desc(field):
        """sample: void setMessage(const RawBuffer& message)"""
        assert 'byte' == field['type']
        param_type = 'const RawBuffer&'
        param_name = field['name']
        return 'set', param_type, param_name

    def _get_vector_setter_name_desc(self, field):
        """sample: void addMosaic(const Mosaic& mosaic)"""
        param_type = self.param_type(field['type'], field.get('size', 0), field.get('signedness', ''))
        param_name = singularize(field['name'])
        return 'add', param_type, param_name

    def _get_setter_name_desc(self, field_kind, field):
        getters = {
            FieldKind.SIMPLE: self._get_simple_setter_name_desc,
            FieldKind.BUFFER: self._get_buffer_setter_name_desc,
            FieldKind.VECTOR: self._get_vector_setter_name_desc
        }
        return getters[field_kind](field)

    @staticmethod
    def _get_field_kind(field):
        if 'size' not in field:
            return FieldKind.SIMPLE

        # if raw uint type treat as SIMPLE (uint8_t - uint64_t)
        if not isinstance(field['size'], str) and 'byte' == field['type'] and field['size'] <= 8:
            return FieldKind.SIMPLE

        if field['size'].endswith('Size'):
            return FieldKind.BUFFER

        if field['size'].endswith('Count'):
            return FieldKind.VECTOR

        return FieldKind.UNKNOWN

    def _contains_any_field_kind(self, field_kind):
        for field in self.schema[self.transaction_body_name()]['layout']:
            if field_kind == CppGenerator._get_field_kind(field):
                return True

        return False

    def _contains_any_other_field_kind(self, field_kind):
        for field in self.schema[self.transaction_body_name()]['layout']:
            if field_kind != CppGenerator._get_field_kind(field):
                return True

        return False

    def _generate_setter_proxy(self, field):
        suppress_setter = self.hints['setters'].get(field['name'], '') if 'setters' in self.hints else ''
        if suppress_setter:
            return

        field_kind = CppGenerator._get_field_kind(field)
        prefix, param_type, param_name = self._get_setter_name_desc(field_kind, field)
        full_setter_name = CppGenerator.full_method_name(prefix, param_type, param_name)
        self._generate_setter(field_kind, field, full_setter_name, param_name)

    def _generate_field_proxy(self, field):
        field_kind = CppGenerator._get_field_kind(field)
        field_type = field['type']
        if 'size' in field and not isinstance(field['size'], str) and self._is_builtin_type(field['type'], field['size']):
            field_type = self._builtin_type(field['size'], field['signedness'])

        qualified_typename = self.qualified_type(field_type)
        types = {
            FieldKind.SIMPLE: '{TYPE}',
            FieldKind.BUFFER: 'std::vector<uint8_t>',
            FieldKind.VECTOR: 'std::vector<{TYPE}>'
        }
        builder_field_typename = types[field_kind].format(TYPE=qualified_typename)
        self._generate_field(field_kind, field, builder_field_typename)

    # endregion
