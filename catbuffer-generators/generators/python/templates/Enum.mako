<%
    base_class_name = 'Enum'
    if generator.is_flag:
        base_class_name = 'Flag'
%>\
from __future__ import annotations
from enum import ${base_class_name}
% if generator.is_flag:
from typing import List
% endif
from .GeneratorUtils import GeneratorUtils


class ${generator.generated_class_name}(${base_class_name}):
    """${helper.capitalize_first_character(generator.comments)}

    Attributes:
% for i, (name, (value, comment)) in enumerate(generator.enum_values.items()):
        ${name}: ${comment}.
% endfor
    """

% for i, (name, (value, comment)) in enumerate(generator.enum_values.items()):
    ${name} = ${value}
% endfor

    @classmethod
    def loadFromBinary(cls, payload: bytes) -> ${generator.generated_class_name}:
        """Creates an instance of ${generator.generated_class_name} from binary payload.
        Args:
            payload: Byte payload to use to serialize the object.
        Returns:
            Instance of ${generator.generated_class_name}.
        """
        value: int = GeneratorUtils.bufferToUint(GeneratorUtils.getBytes(bytes(payload), ${generator.size}))
        return ${generator.generated_class_name}(value)

    @classmethod
    def getSize(cls) -> int:
        """Gets the size of the object.
        Returns:
            Size in bytes.
        """
        return ${generator.size}

% if generator.is_flag:
    @classmethod
    def bytesToFlags(cls, bitMaskValue: bytes, size: int) -> List[${generator.generated_class_name}]:
        """Converts a bit representation to a list of ${generator.generated_class_name}.
        Args:
            bitMaskValue Bitmask bytes value.
        Returns:
            List of ${generator.generated_class_name} flags representing the int value.
        """
        return cls.intToFlags(GeneratorUtils.bufferToUint(GeneratorUtils.getBytes(bitMaskValue, size)))

    @classmethod
    def intToFlags(cls, bitMaskValue: int) -> List[${generator.generated_class_name}]:
        """Converts a bit representation to a list of ${generator.generated_class_name}.
        Args:
            bitMaskValue Bitmask int value.
        Returns:
            List of ${generator.generated_class_name} flags representing the int value.
        """
        results = []
        for flag in ${generator.generated_class_name}:
            if 0 != flag.value & bitMaskValue:
                results.append(flag)
        return results

    @classmethod
    def flagsToInt(cls, flags: List[${generator.generated_class_name}]) -> int:
        """Converts a list of ${generator.generated_class_name} to a bit representation.
        Args:
            List of ${generator.generated_class_name} flags representing the int value.
        Returns:
            int value of the list of flags
        """
        result = 0
        for flag in ${generator.generated_class_name}:
            if flag in flags:
                result += flag.value
        return result

% endif
    def serialize(self) -> bytes:
        """Serializes self to bytes.
        Returns:
            Serialized bytes.
        """
        bytes_ = bytes()
        bytes_ = GeneratorUtils.concatTypedArrays(bytes_, GeneratorUtils.uintToBuffer(self.value, ${generator.size}))
        return bytes_