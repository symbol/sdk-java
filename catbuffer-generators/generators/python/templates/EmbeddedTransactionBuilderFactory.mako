# pylint: disable=R0911,R0912

# Imports for creating embedded transaction builders
from .EmbeddedTransactionBuilder import EmbeddedTransactionBuilder
% for name in sorted(generator.schema):
<%
    layout = generator.schema[name].get("layout", [{type:""}])
    entityTypeValue = next(iter([x for x in layout if x.get('name','') == 'entityType']),{}).get('value',0)
%>\
% if entityTypeValue > 0 and 'Aggregate' not in name and 'Block' not in name and name.startswith('Embedded'):
from .${name}Builder import ${name}Builder
% endif
% endfor

class EmbeddedTransactionBuilderFactory:
    """Factory in charge of creating the specific embedded transaction builder from the binary payload.
    """

    @classmethod
    def createBuilder(cls, payload) -> EmbeddedTransactionBuilder:
        """
        It creates the specific embedded transaction builder from the payload bytes.
        Args:
            payload: bytes
        Returns:
            the EmbeddedTransactionBuilder subclass
        """
        headerBuilder = EmbeddedTransactionBuilder.loadFromBinary(payload)
        entityType = headerBuilder.getType().value
        entityTypeVersion = headerBuilder.getVersion()
% for name in generator.schema:
<%
    layout = generator.schema[name].get("layout", [{type:""}])
    entityTypeValue = next(iter([x for x in layout if x.get('name','') == 'entityType']),{}).get('value',0)
    entityTypeVersion = next(iter([x for x in layout if x.get('name','') == 'version']),{}).get('value',0)
%>\
% if entityTypeValue > 0 and 'Aggregate' not in name and 'Block' not in name and name.startswith('Embedded'):
        if entityType == 0x${'{:x}'.format(entityTypeValue)} and entityTypeVersion == ${entityTypeVersion}:
            return ${name}Builder.loadFromBinary(payload)
% endif
% endfor
        return headerBuilder