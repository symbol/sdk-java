from inspect import getframeinfo, currentframe
from os.path import dirname, abspath, realpath, join

from mako.template import Template


class MakoStaticClassGenerator:
    """
        Generic Mako generator.
        Note that the mako context has 2 main objects.
        - "genertor" with this object keeping all the known state
        - "helper" with the language helper methods.
    """

    def __init__(self, template_file_name, generated_file_name, helper, schema, class_schema):
        self.template_file_name = template_file_name
        self.generated_file_name = generated_file_name
        self.class_output = []
        self.schema = schema
        self.class_schema = class_schema
        self.helper = helper

    def _get_full_file_name(self):
        filename = getframeinfo(currentframe()).filename
        path = dirname(realpath(abspath(filename)))
        return join(path, self.template_file_name)

    def _read_file(self):
        full_file_name = self._get_full_file_name()
        fileTemplate = Template(filename=full_file_name)
        self.class_output += [fileTemplate.render(generator=self, helper=self.helper)]

    def generate(self):
        self._read_file()
        return self.class_output

    def log_context(self):
        description = ''
        for key in filter(lambda a: not a.startswith('_'), dir(self)):
            description = description + key + ' = \'' + str(getattr(self, key)) + '\'\n'
        return description

    def get_generated_file_name(self):
        return self.generated_file_name
