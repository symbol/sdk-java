def indent(instructions, n_indents=1):
    return [' ' * 4 * n_indents + instruction for instruction in instructions]
