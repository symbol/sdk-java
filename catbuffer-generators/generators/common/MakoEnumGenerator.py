from generators.common.Helper import TypeDescriptorDisposition
from .MakoStaticClassGenerator import MakoStaticClassGenerator


class MakoEnumGenerator(MakoStaticClassGenerator):
    """
        Generic Mako generator for enum type schemas.
    """

    def __init__(self, helper, name: str, schema, class_schema, template_path: str, file_extension: str):
        super().__init__(template_path + 'Enum.mako',
                         helper.get_generated_class_name(name, class_schema, schema) + file_extension,
                         helper,
                         schema,
                         class_schema)
        self.name = name
        self.enum_values = {}
        self.size = self.class_schema['size']
        self.enum_type = helper.get_builtin_type(self.size)
        self.generated_class_name = helper.get_generated_class_name(name, class_schema, schema)
        self._add_enum_values(self.class_schema)
        self.comments = helper.get_comments_from_attribute(self.class_schema)
        self.is_flag = helper.is_flags_enum(self.name)
        for type_descriptor, entity_schema in self.schema.items():
            if 'layout' in entity_schema:
                for attribute in entity_schema['layout']:
                    if attribute.get('disposition', None) == TypeDescriptorDisposition.Const.value and attribute.get(
                            'type', None) == self.name and not type_descriptor.endswith('TransactionV1'):
                        enum_name = type_descriptor
                        enum_comment = self.helper.get_comment_from_name(enum_name)
                        enum_value = attribute['value']
                        self._add_enum_value(enum_name, enum_value, enum_comment)

    def _add_enum_values(self, enum_attribute):
        enum_attribute_values = enum_attribute['values']
        for current_attribute in enum_attribute_values:
            self._add_enum_value(current_attribute['name'], current_attribute['value'],
                                 self.helper.get_comments_from_attribute(current_attribute))

    def _add_enum_value(self, name, value, comments):
        self.enum_values[self.helper.create_enum_name(name)] = [value, comments]
