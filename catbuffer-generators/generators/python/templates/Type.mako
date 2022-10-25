from __future__ import annotations
from .GeneratorUtils import GeneratorUtils


class ${generator.generated_class_name}:
    """${generator.comments}.

    Attributes:
        ${generator.attribute_name}: ${generator.comments}.
    """

    def __init__(self, ${generator.attribute_name}: ${generator.attribute_type}):
        """Constructor.

        Args:
            ${generator.attribute_name}: ${generator.comments}.
        """
        self.${generator.attribute_name} = ${generator.attribute_name}

    @classmethod
    def loadFromBinary(cls, payload: bytes) -> ${generator.generated_class_name}:
        """Creates an instance of ${generator.generated_class_name} from binary payload.

        Args:
            payload: Byte payload to use to serialize the object.
        Returns:
            Instance of ${generator.generated_class_name}.
        """
        bytes_ = bytes(payload)
% if generator.attribute_kind == helper.AttributeKind.BUFFER:
        ${generator.attribute_name} = GeneratorUtils.getBytes(bytes_, ${generator.size})
% else:
        ${generator.attribute_name} = GeneratorUtils.bufferToUint(GeneratorUtils.getBytes(bytes_, ${generator.size}))
% endif
        return ${generator.generated_class_name}(${generator.attribute_name})

    @classmethod
    def getSize(cls) -> int:
        """Gets the size of the object.
        Returns:
            Size in bytes.
        """
        return ${generator.size}

    def get${generator.name}(self) -> ${generator.attribute_type}:
        """Gets ${generator.comments}.

        Returns:
            ${generator.comments}.
        """
        return self.${generator.attribute_name}

    def serialize(self) -> bytes:
        """Serializes self to bytes.

        Returns:
            Serialized bytes.
        """
        bytes_ = bytes()
% if generator.attribute_kind == helper.AttributeKind.BUFFER:
        bytes_ = GeneratorUtils.concatTypedArrays(bytes_, self.${generator.attribute_name})
% else:
        bytes_ = GeneratorUtils.concatTypedArrays(bytes_, GeneratorUtils.uintToBuffer(self.get${generator.name}(), ${generator.size}))
% endif
        return bytes_