from generators.common.Helper import AttributeKind
from .MakoStaticClassGenerator import MakoStaticClassGenerator


class MakoTypeGenerator(MakoStaticClassGenerator):
    """
        Generic Mako generator for atomic type schemas.
    """

    def __init__(self, helper, name: str, schema, class_schema, template_path: str, file_extension: str):
        super().__init__(template_path + 'Type.mako',
                         helper.get_generated_class_name(name, class_schema, schema) + file_extension, helper, schema,
                         class_schema)
        class_schema['name'] = name[0].lower() + name[1:]
        self.name = name
        self.attribute_name = self.class_schema['name']
        self.size = self.class_schema['size']
        self.generated_class_name = helper.get_generated_class_name(name, class_schema, schema)
        self.attribute_kind = helper.get_attribute_kind(self.class_schema, None)
        self.attribute_type = helper.get_generated_type(self.schema, self.class_schema, self.attribute_kind)
        self.comments = helper.get_comments_from_attribute(self.class_schema)
        self.AttributeKind = AttributeKind
