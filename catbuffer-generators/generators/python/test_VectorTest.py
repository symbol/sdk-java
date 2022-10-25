from binascii import hexlify, unhexlify
from collections import defaultdict
import importlib
from pathlib import Path
import pytest
import yaml


def read_test_vectors_file(filepath):
    with open(filepath, 'rt') as inFd:
        return yaml.load(inFd)


def prepare_test_cases():
    cases = []
    for filepath in Path('vector').iterdir():
        cases += read_test_vectors_file(filepath)
    return cases


def to_hex_string(binary: bytes):
    return hexlify(binary).decode('ascii').upper()


g_ids = defaultdict(int)


def generate_pretty_id(val):
    # pylint: disable=global-statement
    global g_ids
    g_ids[val['builder']] += 1
    return '{}_{}'.format(val['builder'], g_ids[val['builder']])


def prepare_payload(payload):
    # some basevalue items in yaml are enclosed in qutoes
    return unhexlify(payload.replace('\'', ''))


@pytest.mark.parametrize('item', prepare_test_cases(), ids=generate_pretty_id)
def test_serialize(item):
    builderName = item['builder']
    comment = item['comment'] if 'comment' in item else ''
    payload = item['payload']

    builderModule = importlib.import_module('symbol_catbuffer.{}'.format(builderName))
    builderClass = getattr(builderModule, builderName)
    builder = builderClass.loadFromBinary(prepare_payload(item['payload']))
    serialized = builder.serialize()
    assert to_hex_string(serialized) == payload.upper(), comment
