from enum import Enum

from .JavaScriptGeneratorUtils import indent


class FunctionType(Enum):
    FUNCTION = 0
    ARROW_FUNCTION = 1
    CONSTRUCTOR = 2
    STATIC = 3


class JavaScriptFunctionGenerator:
    def __init__(self, function_type=FunctionType.FUNCTION):
        self.type = function_type
        self.name = None
        self.params = []
        self.instructions = []

    def set_name(self, name):
        self.name = name

    def set_params(self, params):
        self.params = params

    def _get_header(self):
        if self.type is FunctionType.ARROW_FUNCTION:
            return ['{} = ({}) => {{'.format(self.name, ', '.join(self.params))]
        if self.type is FunctionType.STATIC:
            return ['static {}({}) {{'.format(self.name, ', '.join(self.params))]
        if self.type is FunctionType.CONSTRUCTOR:
            return ['constructor({}) {{'.format(', '.join(self.params))]
        return ['{}({}) {{'.format(self.name, ', '.join(self.params))]

    def add_instructions(self, instructions):
        self.instructions += instructions

    def add_block(self, block):
        self.instructions += block.get_instructions()

    def get_instructions(self):
        return self._get_header() + indent(self.instructions) + ['}']
