from setuptools import setup, find_packages  # noqa: H301

with open('README.md', 'r') as readme_file:
    README = readme_file.read()

NAME = '#artifactName'
VERSION = '#artifactVersion'

REQUIRES = []

setup(
    name=NAME,
    version=VERSION,
    description='Symbol Catbuffer Builders',
    author='nemtech',
    author_email='ravi@nem.foundation',
    url='https://github.com/nemtech/catbuffer-generators',
    keywords=['catbuffer-generators', 'catbuffer', 'builders', 'Symbol Catbuffer Builders'],
    install_requires=REQUIRES,
    package_dir={'': 'src'},
    packages=find_packages('src'),
    include_package_data=True,
    license='Apache 2.0',
    long_description=README,
    long_description_content_type='text/markdown',
    classifiers=[
        'Programming Language :: Python :: 3.7',
    ]
)
