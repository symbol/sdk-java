import os
from abc import ABC, abstractmethod

from generators.Descriptor import Descriptor
from generators.common.MakoClassGenerator import MakoClassGenerator
from generators.common.MakoEnumGenerator import MakoEnumGenerator
from generators.common.MakoStaticClassGenerator import MakoStaticClassGenerator
from generators.common.MakoTypeGenerator import MakoTypeGenerator


class FileGenerator(ABC):
    """
    Generic top level file generator. A language will extend this class defining how to create the different generators.
    """

    def __init__(self, schema, options):
        self.schema = schema
        self.current = None
        self.options = options

    def __iter__(self):
        self.current = self.generate()
        return self

    def __next__(self):
        return next(self.current)

    def generate(self):
        """
           Main entry point for the generator. It collects all the possible file generators and execute
           them producing different files.
        :return:  multiple Descriptors using yield.
        """
        helper = self.create_helper()
        generators = []
        for type_name, class_schema in self.schema.items():
            attribute_type = class_schema['type']
            if helper.is_byte_type(attribute_type):
                generators.extend(self.create_type_generators(helper, type_name, class_schema))
            elif helper.is_enum_type(attribute_type):
                generators.extend(self.create_enum_generators(helper, type_name, class_schema))
            elif helper.is_struct_type(attribute_type) and helper.should_generate_class(type_name):
                generators.extend(self.create_class_generators(helper, type_name, class_schema))
        # write all the  helper files
        for filename in self.get_static_templates_file_names():
            generators.extend(self.create_static_class_generators(filename, helper))
        for generator in generators:
            code = self.init_code()
            code += generator.generate()
            yield Descriptor(generator.get_generated_file_name(), code)

    def init_code(self):
        """
        :return: a brand new memory file with the license if provided.
        """
        copyright_file = self.options['copyright']
        code = []
        if os.path.isfile(copyright_file):
            with open(copyright_file) as header:
                code = [line.strip() for line in header]
        return code

    def create_static_class_generators(self, filename, helper):
        """
        It creates the generators for a static generator. By default creates one generator by file (like .java or .ts)
        Note that other languages may need more than one (like .cpp and .h)
        :param filename: the filename
        :param helper: the language helper
        :return: a list of generator, one by default using mako templates
        """
        return [MakoStaticClassGenerator(self.get_template_path() + filename + '.mako',
                                         filename + self.get_main_file_extension(), helper,
                                         self.schema, None)]

    def create_class_generators(self, helper, type_name, class_schema):
        """
        Creates the generators for given class type. By default creates one generator by file (like .java or .ts)
        Note that other languages may need more than one (like .cpp and .h)

        :param helper: the language helper
        :param type_name: the type name.
        :param class_schema: the schema of the currency class
        :return: a list of generator, one by default using mako templates
        """
        return [MakoClassGenerator(helper, type_name, self.schema, class_schema, self.get_template_path(),
                                   self.get_main_file_extension())]

    def create_enum_generators(self, helper, type_name, class_schema):
        """

        Creates the generators for given enum type. By default creates one generator by file (like .java or .ts)
        Note that other languages may need more than one (like .cpp and .h)

        :param helper: the language helper
        :param type_name: the type name.
        :param class_schema: the schema of the currency class
        :return: a list of generator, one by default using mako templates
        """
        return [MakoEnumGenerator(helper, type_name, self.schema, class_schema, self.get_template_path(),
                                  self.get_main_file_extension())]

    def create_type_generators(self, helper, type_name, class_schema):
        """

        Creates the generators for given atomic type. By default creates one generator by file (like .java or .ts)
        Note that other languages may need more than one (like .cpp and .h)

        :param helper: the language helper
        :param type_name: the type name.
        :param class_schema: the schema of the currency class
        :return: a list of generator, one by default using mako templates
        """
        return [MakoTypeGenerator(helper, type_name, self.schema, class_schema, self.get_template_path(),
                                  self.get_main_file_extension())]

    @abstractmethod
    def get_template_path(self):
        """

        :return: the path where the language templates will be find. It needs to be redefined if Mako genertors are used.
        """
        raise NotImplementedError('get_template_path must be defined in subclass')

    @abstractmethod
    def get_main_file_extension(self):
        """

        :return: the extension of the generated files. Example: '.java'
        """
        raise NotImplementedError('get_main_file_extension must be defined in subclass')

    @abstractmethod
    def create_helper(self):
        """

        :return: the language helper. Subclasses would override this method returning a subclass of Helper.
        """
        raise NotImplementedError('create_helper must be defined in subclass')

    def get_static_templates_file_names(self):
        """

        :return: a list of known static  (GeneratorUtils for example) to be generated. Most languages would override this.
        """
        return []
