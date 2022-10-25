from enum import Enum

from abc import ABC, abstractmethod


# pylint: disable=too-many-public-methods


class TypeDescriptorType(Enum):
    """Type descriptor enum"""
    Byte = 'byte'
    Struct = 'struct'
    Enum = 'enum'


class TypeDescriptorDisposition(Enum):
    Inline = 'inline'
    Const = 'const'
    Fill = 'fill'
    Var = 'var'


class AttributeKind(Enum):
    """Attribute type enum"""
    SIMPLE = 1
    BUFFER = 2
    ARRAY = 3
    CUSTOM = 4
    FLAGS = 5
    SIZE_FIELD = 6
    FILL_ARRAY = 7
    VAR_ARRAY = 8
    UNKNOWN = 100


class Helper(ABC):
    """
    Helper stateless methods used when generating templates. Most languages would extend this object.
    """

    def __init__(self):
        # a shortcut for the templates to access the AttributeKind type.
        self.AttributeKind = AttributeKind

    @staticmethod
    def is_struct_type(typename):
        return typename == TypeDescriptorType.Struct.value

    @staticmethod
    def is_enum_type(typename):
        return typename == TypeDescriptorType.Enum.value

    @staticmethod
    def is_byte_type(typename):
        return typename == TypeDescriptorType.Byte.value

    @staticmethod
    def resolve_alignment(a):
        embedded = a.attribute is not None and 'type' in a.attribute and a.attribute[
            'type'] == 'EmbeddedTransaction'
        parent_embedded = a.parent_attribute is not None and 'type' in a.parent_attribute and a.parent_attribute[
            'type'] == 'EmbeddedTransaction'
        if embedded or parent_embedded:
            return 8
        return 0

    @staticmethod
    def is_inline_type(attribute):
        return 'disposition' in attribute and attribute['disposition'] == TypeDescriptorDisposition.Inline.value

    @staticmethod
    def is_const_type(attribute):
        return 'disposition' in attribute and attribute['disposition'] == TypeDescriptorDisposition.Const.value

    @staticmethod
    def is_fill_array_type(attribute):
        return 'disposition' in attribute and attribute['disposition'] == TypeDescriptorDisposition.Fill.value

    @staticmethod
    def is_var_array_type(attribute):
        return 'disposition' in attribute and attribute['disposition'] == TypeDescriptorDisposition.Var.value

    @staticmethod
    def is_any_array_kind(attribute_kind):
        return attribute_kind in (AttributeKind.ARRAY, AttributeKind.VAR_ARRAY, AttributeKind.FILL_ARRAY)

    @staticmethod
    def is_sorted_array(attribute):
        return 'sort_key' in attribute

    @staticmethod
    def is_reserved_field(attribute):
        return 'name' in attribute and '_Reserved' in attribute['name'] and 'size' in attribute

    @staticmethod
    def is_conditional_attribute(attribute):
        return 'condition' in attribute

    @staticmethod
    def is_attribute_count_size_field(attribute, class_attributes):
        if class_attributes is None:
            return False
        attribute_name = attribute['name']
        is_size_of_class_attributes = list(
            filter(lambda a: 'size' in a and a['size'] == attribute_name, class_attributes))
        return len(is_size_of_class_attributes) == 1

    @staticmethod
    def should_generate_class(name):
        # subclassees may override this method if the language is not ready to generate all the classes
        # I need to exclude due to the ReceiptBuilder hack of not serializing the size
        # Also, SizePrefixedEntity needs to go first, not VerifiableEntity or EntityBody the way we handle super classes.
        return name not in ('SizePrefixedEntity', 'VerifiableEntity', 'EntityBody', 'EmbeddedTransactionHeader',
                            'TransactionHeader')
        # return True

    @staticmethod
    def should_use_super_class():
        # if true, first inline is super class, the rest are inline builders
        # if false, there is no super class, all inline attributes use inline builders.
        return True

    @staticmethod
    def add_required_import(required_import: set,
                            import_type,
                            class_name,
                            base_class_name  # pylint: disable=unused-argument
                            ):
        if not import_type == class_name:
            required_import.add(import_type)
        return required_import

    @staticmethod
    def get_all_constructor_params(attributes):
        return [a for a in attributes if not a.kind == AttributeKind.SIZE_FIELD and a.attribute_name != 'size']

    def get_generated_class_name(self, typename, class_schema, schema):
        class_type = class_schema['type']
        default_name = typename + 'Dto'
        if self.is_byte_type(class_type) or self.is_enum_type(class_type) or typename not in schema:
            return default_name
        return typename + 'Builder' if self.is_struct_type(schema[typename]['type']) else default_name

    def is_builtin_type(self, typename, size):
        # byte up to long are passed as 'byte' with size set to proper value
        return not isinstance(size, str) and self.is_byte_type(typename) and size <= 8

    def get_attribute_size(self, schema, attribute):
        if 'size' not in attribute and not self.is_byte_type(attribute['type']) and not self.is_enum_type(
                attribute['type']):
            attr = schema[attribute['type']]
            if 'size' in attr:
                return attr['size']
            return 1
        return attribute['size']

    @staticmethod
    def get_base_type(schema: dict, attribute_type):
        attribute: dict = schema.get(attribute_type)
        if attribute is not None:
            return attribute.get('type')
        return None

    @staticmethod
    def is_flags_enum(attribute_type):
        return attribute_type.endswith('Flags')

    @staticmethod
    def is_inline_class(attribute):
        return 'disposition' in attribute and attribute['disposition'] == TypeDescriptorDisposition.Inline.value

    @staticmethod
    def capitalize_first_character(string):
        return string if not string else string[0].upper() + string[1:]

    @staticmethod
    def decapitalize_first_character(string):
        return string if not string else string[0].lower() + string[1:]

    @staticmethod
    def snake_case(string: str):
        return string if not string else string[0] + ''.join('_' + x if x.isupper() else x for x in string[1:])

    # pylint: disable=R0911
    def get_attribute_kind(self, attribute, class_attributes):
        if self.is_var_array_type(attribute):
            return AttributeKind.VAR_ARRAY
        if self.is_fill_array_type(attribute):
            return AttributeKind.FILL_ARRAY
        if self.is_inline_class(attribute):
            return AttributeKind.CUSTOM
        if self.is_attribute_count_size_field(attribute, class_attributes):
            return AttributeKind.SIZE_FIELD

        attribute_type = attribute['type']

        if self.is_flags_enum(attribute_type):
            return AttributeKind.FLAGS

        if self.is_struct_type(attribute_type) or self.is_enum_type(attribute_type) or 'size' not in attribute:
            return AttributeKind.CUSTOM

        attribute_size = attribute['size']

        if isinstance(attribute_size, str):
            if attribute_type == 'byte':
                return AttributeKind.BUFFER
            return AttributeKind.ARRAY

        if isinstance(attribute_size, int) and not attribute_type == 'byte':
            return AttributeKind.ARRAY

        if self.is_builtin_type(attribute_type, attribute_size):
            return AttributeKind.SIMPLE

        return AttributeKind.BUFFER

    def get_attribute_property_equal(self, schema, attributes, attribute_name, attribute_value, recurse=True):
        for attribute in attributes:
            if attribute_name in attribute and attribute[attribute_name] == attribute_value:
                return attribute
            if (recurse and 'disposition' in attribute and
                    attribute['disposition'] == TypeDescriptorDisposition.Inline.value):
                value = self.get_attribute_property_equal(schema, schema[attribute['type']]['layout'], attribute_name,
                                                          attribute_value)
                if value is not None:
                    return value
        return None

    def get_name_from_type(self, type_name: str):
        return self.decapitalize_first_character(type_name)

    @staticmethod
    def get_comment_from_name(name):
        return name[0].upper() + ''.join(' ' + x.lower() if x.isupper() else x for x in name[1:])

    def get_comments_from_attribute(self, attribute):
        comment = attribute['comments'].strip() if 'comments' in attribute else ''
        if not comment and 'name' in attribute:
            comment = self.get_comment_from_name(attribute['name'])
        return comment

    def create_enum_name(self, name: str):
        return self.snake_case(name).upper()

    @abstractmethod
    def get_builtin_type(self, size):
        raise NotImplementedError('get_builtin_type must be overridden')

    @abstractmethod
    def get_generated_type(self, schema, attribute, attribute_kind):
        raise NotImplementedError('get_generated_type must be overridden')
