# Contributing to catbuffer-generators

As explained in the [README](README.md) file, this project consists of a set of code generators that serialize and deserialize Catapult entities (Transactions, Types, ...). You can use this project to obtain classes that will help you deal with Catapult entities in their binary form from your language of choice.

If the language you are interested in is not covered by the project, you can add a new generator by following this guide.

## The Generators

The [Catapult Server](https://github.com/nemtech/catapult-server) manages a number of entities (e.g. transactions) which need to be stored in binary form when communicated over a network. The binary layout of these entities is described using **catbuffer** files (Catapult Buffer files with ``.cat`` extension), stored in the [catbuffer](https://github.com/nemtech/catbuffer) repository, inside the ``schemas`` folder.

The generators in this project read catbuffer schema files and produce the necessary files in the target language to serialize and deserialize them.

Each generator is a Python class residing in the ``generators`` folder and listed in ``generators/All.py``. Most of them use [Mako templates](https://www.makotemplates.org/) so the boilerplate code is abstracted to common classes.

> **NOTE:**
> Some generators like ``javascript`` and ``cpp_builder`` are still manually built and do not use templates. They are in the process of being adapted to the new mechanism. **Do not use them as examples to build your own generators!**

Generators are invoked by name from the ``catbuffer/main.py``, after pointing the ``PYTHONPATH`` environment variable to the ``catbuffer-generators`` folder.

For instance, if you're already in the ``catbuffer-generators`` folder, you can see the list of available generators by running:

```bash
PYTHONPATH=. python3 catbuffer/main.py
```

So, to use the java generator:

```bash
PYTHONPATH=. python3 catbuffer/main.py -g java
```

You can take a look at the ``scripts`` folder to see more invocation details of ``main.py``.

## Adding a New Generator

Unfortunately, the process to add a new generator is not automated yet:

1. Copy the ``java`` folder inside ``generators`` and rename the folder and the files inside to the desired language.

2. Edit ``JavaFileGenerator.py`` and ``JavaHelper.py`` (now renamed) to use the proper language name and file extension.

3. Edit all files inside the ``templates`` folder to adapt to the selected language. Use the Java version for inspiration and make sure you know how [Mako templates](https://www.makotemplates.org/) work.

4. Add your new generator to the global register in ``generators/All.py``.

Once the generator is ready you should be able to invoke it using ``catbuffer/main.py`` as shown above. Create a helper script like the ones in the ``scripts`` folder to automate building and deploying the new serializer classes!

> **NOTE:**
> The ``VectorTest`` file contains unit tests for the generator. Add your own tests to the new generator and run them from a script in the ``scripts`` folder. See how ``generate_typescrpt.sh`` executes ``npm run test``, for example.
