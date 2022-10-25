# catbuffer-generators

[![Build Status](https://api.travis-ci.com/nemtech/catbuffer-generators.svg?branch=main)](https://travis-ci.com/nemtech/catbuffer-generators)

Set of code generators to serialize and deserialize Catapult entities in different programming languages.

In combination with the [catbuffer](https://github.com/nemtech/catbuffer) project, developers can generate builder classes for a given set of programming languages. For example, the [Symbol SDKs](https://nemtech.github.io/sdk) use the generated code to operate with the entities in binary form before announcing them to the network.

## Supported programming languages

- C++
- Java
- TypeScript/JavaScript
- Python

## Requirements

- Python >= 3.4

## Installation

1. Clone the ``catbuffer-generators`` repository:

```bash
git clone --recurse-submodules https://github.com/nemtech/catbuffer-generators
```

This will also clone the ``catbuffer`` repository as a submodule.

2. Install the package requirements:

```bash
cd catbuffer-generators
pip install -r requirements.txt
```

## Usage

Use the ``scripts/generate_all.sh`` script to create code for the different languages. For example:

```bash
scripts/generate_all.sh cpp_builder
```

This processes every schema and writes the output files in the ``catbuffer/_generated/<generator>`` folder.

Alternatively, you can use any of the language-specific scripts like ``scripts/generate_typescript.sh``. Most of these scripts, after producing the code will compile it into an output artifact in the ``build`` folder.

> **NOTE:**
> These scripts require Bash 4 or higher.

### Run the linter

```bash
pylint --load-plugins pylint_quotes generators test/python
pycodestyle --config=.pycodestyle .
```

> **NOTE:**
> This requires Python 3.7 or higher.

Copyright (c) 2016-2019, Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp.
Copyright (c) 2020-present, Jaguar0625, gimre, BloodyRookie.
