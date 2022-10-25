from enum import Enum

from .JavaScriptGeneratorUtils import indent


class BlockType(Enum):
    NONE = 0
    # pylint: disable=invalid-name
    IF = 1
    ELSE = 2
    ELIF = 3
    FOR = 4


class JavaScriptBlockGenerator:
    def __init__(self):
        self.type = BlockType.NONE
        self.rule = ''
        self.instructions = []
        self.iterator = None

    def wrap(self, block_type, rule, iterator=None):
        self.type = block_type
        self.rule = rule
        self.iterator = iterator

    def add_instructions(self, instructions):
        self.instructions += instructions

    def add_block(self, block):
        self.add_instructions(block.get_instructions())

    def get_instructions(self):
        if self.type is not BlockType.NONE:
            if self.type is BlockType.IF:
                return ['if ({0}) {{'.format(self.rule)] + indent(self.instructions) + ['}']
            if self.type is BlockType.ELIF:
                return ['else if ({0}) {{'.format(self.rule)] + indent(self.instructions) + ['}']
            if self.type is BlockType.ELSE:
                return ['else {'] + indent(self.instructions) + ['}']
            if self.type is BlockType.FOR:
                return [
                    'const {0}'.format(self.iterator),
                    'for ({0} = 0; {0} {1}; {0}++) {{'.format(self.iterator, self.rule)
                ] + indent(self.instructions) + ['}']

        return self.instructions
