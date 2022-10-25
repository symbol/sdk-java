# pylint: disable=R0911,R0912

# Imports for creating transaction builders
from .TransactionBuilder import TransactionBuilder
% for name in sorted(generator.schema):
<%
    layout = generator.schema[name].get("layout", [{type:""}])
    entityTypeValue = next(iter([x for x in layout if x.get('name','') == 'entityType']),{}).get('value',0)
%>\
% if entityTypeValue > 0 and 'Block' not in name and not name.startswith('Embedded'):
from .${name}Builder import ${name}Builder
% endif
% endfor


class TransactionBuilderFactory:
    """Factory in charge of creating the specific transaction builder from the binary payload.
    """

    @classmethod
    def createBuilder(cls, payload) -> TransactionBuilder:
        """
        It creates the specific transaction builder from the payload bytes.
        Args:
            payload: bytes
        Returns:
            the TransactionBuilder subclass
        """
        headerBuilder = TransactionBuilder.loadFromBinary(payload)
        entityType = headerBuilder.getType().value
        entityTypeVersion = headerBuilder.getVersion()
% for name in generator.schema:
<%
    layout = generator.schema[name].get("layout", [{type:""}])
    entityTypeValue = next(iter([x for x in layout if x.get('name','') == 'entityType']),{}).get('value',0)
    entityTypeVersion = next(iter([x for x in layout if x.get('name','') == 'version']),{}).get('value',0)
%>\
    % if entityTypeValue > 0 and 'Block' not in name and not name.startswith('Embedded'):
        if entityType == 0x${'{:x}'.format(entityTypeValue)} and entityTypeVersion == ${entityTypeVersion}:
            return ${name}Builder.loadFromBinary(payload)
    % endif
% endfor
        return headerBuilder