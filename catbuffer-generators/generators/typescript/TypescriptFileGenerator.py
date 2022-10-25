from generators.common.FileGenerator import FileGenerator
from .TypescriptHelper import TypescriptHelper


class TypescriptFileGenerator(FileGenerator):
    """Typescript file generator"""

    def get_template_path(self):
        return '../typescript/templates/'

    def get_static_templates_file_names(self):
        return ['GeneratorUtils', 'TransactionHelper', 'EmbeddedTransactionHelper', 'Serializer', 'index']

    def get_main_file_extension(self):
        return '.ts'

    def create_helper(self):
        return TypescriptHelper()
