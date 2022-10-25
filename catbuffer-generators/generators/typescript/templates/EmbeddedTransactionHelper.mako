import { EmbeddedTransactionBuilder } from './EmbeddedTransactionBuilder';
% for name in generator.schema:
<%
    layout = generator.schema[name].get("layout", [{type:""}])
    entityTypeValue = next(iter([x for x in layout if x.get('name','') == 'entityType']),{}).get('value',0)
%>\
%if (entityTypeValue > 0 and 'Aggregate' not in name and 'Block' not in name and name.startswith('Embedded')):
import { ${name}Builder } from './${name}Builder';
%endif
% endfor

/** Helper class for embedded transaction serialization */
export class EmbeddedTransactionHelper {

     /** Deserialize an embedded transaction builder from binary */
    public static loadFromBinary(payload: Uint8Array): EmbeddedTransactionBuilder {

        const header = EmbeddedTransactionBuilder.loadFromBinary(payload);
% for name in generator.schema:
    <%
        layout = generator.schema[name].get("layout", [{type:""}])
        entityTypeValue = next(iter([x for x in layout if x.get('name','') == 'entityType']),{}).get('value',0)
        entityTypeVersion = next(iter([x for x in layout if x.get('name','') == 'version']),{}).get('value',0)
    %>\
    %if (entityTypeValue > 0 and 'Aggregate' not in name and 'Block' not in name and name.startswith('Embedded')):

        if (header.type === ${entityTypeValue} && header.version == ${entityTypeVersion}) {
            return ${name}Builder.loadFromBinary(payload);
        }
    %endif
% endfor

        return header;
    }

}
