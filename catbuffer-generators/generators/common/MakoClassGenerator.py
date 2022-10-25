from collections import namedtuple
from itertools import chain
from typing import List

from generators.common.Helper import TypeDescriptorDisposition
from .MakoStaticClassGenerator import MakoStaticClassGenerator

AttributeData = namedtuple('AttributeData',
                           ['attribute', 'kind', 'attribute_name', 'attribute_comment', 'attribute_base_type',
                            'attribute_var_type', 'attribute_is_final', 'attribute_class_name', 'attribute_is_super',
                            'attribute_size', 'attribute_is_conditional', 'attribute_aggregate_attribute_name',
                            'attribute_is_reserved', 'attribute_aggregate_class', 'attribute_is_inline',
                            'attribute_is_aggregate', 'parent_attribute', 'condition_type_attribute',
                            'attribute_condition_value', 'attribute_condition_provide',
                            'conditional_read_before'])


class MakoClassGenerator(MakoStaticClassGenerator):
    """
    Generic Mako generator for class type schemas.
    """

    def __init__(self, helper, name, schema, class_schema, template_path, file_extension):
        super().__init__(template_path + 'Class.mako',
                         helper.get_generated_class_name(name, class_schema, schema) + file_extension,
                         helper,
                         schema,
                         class_schema)
        class_schema['name'] = name[0].lower() + name[1:]
        self.required_import = set()
        self.name = name
        self.attributes = []
        self.generated_class_name = helper.get_generated_class_name(name, class_schema, schema)
        self.base_class_name = None
        self.generated_base_class_name = None
        if self.helper.should_use_super_class():
            self.foreach_attributes(self.class_schema['layout'], self._find_base_callback)
        self.comments = helper.get_comments_from_attribute(self.class_schema)
        self._recurse_foreach_attribute(self.name, self._add_attribute)
        self.body_class_name = helper.get_body_class_name(self.name)

        condition_types = [(a, schema[a.condition_type_attribute['type']]) for a in self.attributes if
                           a.attribute_is_conditional and a.attribute['condition_operation'] != 'has']
        condition_types_values = self._calculate_constructor_options(condition_types)

        self.all_constructor_params = helper.get_all_constructor_params(self.attributes)
        # not a.attribute_is_aggregate
        self.constructor_attributes = [self.all_constructor_params] if not condition_types else [
            self.constructor_arguments(self.all_constructor_params, condition_type) for condition_type in
            condition_types_values]

    @staticmethod
    def _calculate_constructor_options(condition_types):
        if not condition_types:
            return []
        condition_types_values = [[(a, value['name'])
                                   for value in schema_type['values']] for (a, schema_type) in condition_types]
        condition_types_values = list(chain.from_iterable(condition_types_values))
        condition_types_values = [
            (a.condition_type_attribute['name'], a.attribute['condition_value'], value) for
            (a, value) in condition_types_values]

        with_values = {a_condition_value for (a_condition_name, a_condition_value, conditional_value) in
                       condition_types_values}

        condition_types_values = {(a_condition_name,
                                   conditional_value if conditional_value in with_values else None,
                                   conditional_value) for
                                  (a_condition_name, a_condition_value, conditional_value) in
                                  condition_types_values}
        return condition_types_values

    def _recurse_foreach_attribute(self, class_name: str, callback, aggregate_attribute=None, deep=0):
        print(str('\t' * deep) + '- ' + class_name)
        class_generated = (class_name != self.name and self.helper.should_generate_class(class_name))
        class_attributes = self.schema[class_name]['layout']
        for attribute in class_attributes:
            if class_generated:
                attribute['aggregate_class'] = class_name
            if 'disposition' in attribute:
                if attribute['disposition'] == TypeDescriptorDisposition.Inline.value:
                    attribute['name'] = self.helper.decapitalize_first_character(attribute['type'])
                    aggregate_class_is_generated = self.helper.should_generate_class(attribute['type'])
                    # Is the aggregate class generated?
                    if aggregate_class_is_generated:
                        print(str('\t ' * (deep + 1)) + ' ' + attribute['name'])
                        callback(attribute, class_attributes, aggregate_attribute)
                    new_aggregate_attribute = attribute if aggregate_attribute is None and aggregate_class_is_generated \
                        else aggregate_attribute
                    self._recurse_foreach_attribute(attribute['type'], self._add_attribute,
                                                    new_aggregate_attribute,
                                                    deep + 1)
                elif attribute['disposition'] == TypeDescriptorDisposition.Const.value:
                    continue
                elif self.helper.is_var_array_type(attribute) or self.helper.is_fill_array_type(attribute):
                    print(str('\t ' * (deep + 1)) + ' ' + attribute['name'])
                    callback(attribute, class_attributes, aggregate_attribute)
                    continue
            else:
                print(str('\t ' * (deep + 1)) + ' ' + attribute['name'])
                callback(attribute, class_attributes, aggregate_attribute)

    def _add_attribute(self, attribute, class_attributes, aggregate_attribute):
        aggregate_attribute_name = aggregate_attribute['name'] if aggregate_attribute else None
        aggregate_attribute_type = aggregate_attribute['type'] if aggregate_attribute else None
        kind = self.helper.get_attribute_kind(attribute, class_attributes)
        attribute_is_conditional = self.helper.is_conditional_attribute(attribute)
        attribute_comment = self.helper.get_comments_from_attribute(attribute)
        attribute_name = attribute['name']
        attribute_size = self.helper.get_attribute_size(self.schema, attribute)
        attribute_var_type = self.helper.get_generated_type(self.schema, attribute, kind)
        attribute_is_final = attribute_name != 'size' and not attribute_is_conditional
        attribute_type = attribute.get('type', None)
        attribute_base_type = self.helper.get_base_type(self.schema, attribute_type)
        attribute_class_name = self.helper.get_generated_class_name(attribute_type, attribute, self.schema)
        attribute_aggregate_attribute_name = aggregate_attribute_name
        attribute_is_aggregate = self.helper.is_inline_class(attribute)
        attribute_is_super = self.base_class_name is not None and self.base_class_name == aggregate_attribute_type
        if attribute_is_aggregate and self.base_class_name is not None:
            attribute_is_super = attribute_type == self.base_class_name
        attribute_is_reserved = self.helper.is_reserved_field(attribute)
        attribute_is_inline = not attribute_is_super and aggregate_attribute_name is not None
        attribute_aggregate_class = attribute.get('aggregate_class', None)
        self.required_import = self.helper.add_required_import(self.required_import,
                                                               attribute_var_type,
                                                               self.generated_class_name,
                                                               self.generated_base_class_name)
        if attribute_is_conditional:
            condition_type_attribute = self.helper.get_attribute_property_equal(self.schema,
                                                                                self.class_schema['layout'], 'name',
                                                                                attribute['condition'])
        else:
            condition_type_attribute = None

        parent_attribute = self.helper.get_attribute_property_equal(self.schema, self.class_schema['layout'], 'size',
                                                                    attribute_name)
        conditional_read_before: bool = False
        if 'condition' in attribute:
            conditional_read_before = len(
                [a1 for a1 in self.attributes if a1.attribute_name == attribute['condition']]) == 0

        attribute_tuple = AttributeData(attribute, kind, attribute_name,
                                        attribute_comment, attribute_base_type, attribute_var_type,
                                        attribute_is_final, attribute_class_name,
                                        attribute_is_super, attribute_size, attribute_is_conditional,
                                        attribute_aggregate_attribute_name, attribute_is_reserved,
                                        attribute_aggregate_class,
                                        attribute_is_inline, attribute_is_aggregate, parent_attribute,
                                        condition_type_attribute, None, True, conditional_read_before)
        self.attributes.append(attribute_tuple)

    def _find_base_callback(self, attribute):
        if self.helper.is_inline_class(attribute) and self.helper.should_generate_class(attribute['type']):
            self.base_class_name = attribute['type']
            self.generated_base_class_name = self.helper.get_generated_class_name(self.base_class_name,
                                                                                  self.schema[self.base_class_name],
                                                                                  self.schema)
            return True
        return False

    def foreach_attributes(self, attributes, callback):
        for attribute in attributes:
            if callback(attribute):
                break

    def constructor_arguments(self, constructor_params: List[AttributeData], condition_type_and_value):
        return [self.set_default_argument(a, condition_type_and_value) for a in constructor_params]

    def set_default_argument(self, a: AttributeData, condition_type_and_value) -> AttributeData:

        (a_condition_name, a_condition_value, conditional_value) = condition_type_and_value

        if a.attribute_name == a_condition_name:
            return a._replace(attribute_condition_value=conditional_value)

        if self.should_not_provide_argument(a, a_condition_name, a_condition_value):
            return a._replace(attribute_condition_provide=False)
        return a

    def should_not_provide_argument(self, a: AttributeData, a_condition, a_condition_value):
        if 'condition' not in a.attribute:
            return False
        if a.attribute['condition'] != a_condition:
            return False
        if a.attribute['condition_value'] == a_condition_value:
            return False
        return True
