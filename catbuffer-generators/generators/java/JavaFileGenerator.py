from generators.common.FileGenerator import FileGenerator
from .JavaHelper import JavaHelper


class JavaFileGenerator(FileGenerator):
    """Java file generator"""

    def init_code(self):
        code = super().init_code()
        code += ['package io.nem.symbol.catapult.builders;'] + ['']
        return code

    def get_template_path(self):
        return '../java/templates/'

    def get_static_templates_file_names(self):
        return ['BitMaskable', 'GeneratorUtils', 'AggregateTransactionBodyBuilder', 'TransactionBuilderHelper',
                'EmbeddedTransactionBuilderHelper',
                'Serializer']

    def get_main_file_extension(self):
        return '.java'

    def create_helper(self):
        return JavaHelper()
