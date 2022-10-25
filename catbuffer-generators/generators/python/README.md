# catbuffer

This is the Python version of the catbuffer library. It is generated using [catbuffer-generators](https://github.com/nemtech/catbuffer-generators) from the [catbuffer](https://github.com/nemtech/catbuffer) specification. 

The generated code is in Python version 3.7. 

This library helps serialize and deserialize NEM's Catapult entities in Python applications. 

The library's main client may be a community-driven NEM Python SDK (nem2-sdk-python) but it can also be used alone.

## Installation & Usage
### pip install

The python catbuffer package is hosted on [PyPI](https://pypi.org/project/catbuffer).

To install the latest release:

```sh
pip install catbuffer
```
(you may need to run `pip` with root permission: `sudo pip install catbuffer`)

To install a specific version or a snapshot:

```sh
pip install catbuffer=={version}
```

Example:

```sh
pip3 install catbuffer==0.0.2.20200329.111953a1
```

## Python generator developer notes

As catbuffer schema uses upper and lower Camel Case naming convention, the generated code also uses this convention for easier cross-referencing between the code and the schemas. You may want to disable PEP 8 naming convention violation inspection in your IDE.
