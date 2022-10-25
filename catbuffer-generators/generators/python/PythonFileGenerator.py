import os
from generators.common.FileGenerator import FileGenerator
from .PythonHelper import PythonHelper


class PythonFileGenerator(FileGenerator):
    """Python file generator"""

    def init_code(self):
        code = ['#!/usr/bin/python']
        copyright_file = self.options['copyright']
        code += self.get_copyright(copyright_file)
        code += ['# pylint: disable=W0622,W0612,C0301,R0904', '']
        return code

    @staticmethod
    def get_copyright(copyright_file):
        code = []
        if os.path.isfile(copyright_file):
            with open(copyright_file) as header:
                for line in header:
                    line = line.strip()
                    if line.startswith('/**') or line.startswith('**/'):
                        code += ['"""']
                    elif line.startswith('***'):
                        if len(line) > 3:
                            code += [line.replace('***', '   ')]
                        else:
                            code += [line.replace('***', '')]
                    else:
                        code += [line]
        return code

    def get_template_path(self):
        return '../python/templates/'

    def get_static_templates_file_names(self):
        return ['GeneratorUtils', 'EmbeddedTransactionBuilderFactory', 'TransactionBuilderFactory']

    def get_main_file_extension(self):
        return '.py'

    def create_helper(self):
        return PythonHelper()
