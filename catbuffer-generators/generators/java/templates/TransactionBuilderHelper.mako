import java.io.DataInputStream;
import java.io.SequenceInputStream;
import java.io.ByteArrayInputStream;

/** Factory in charge of creating the right transaction builder from the streamed data. */
public class TransactionBuilderHelper {

    /**
    * It creates the right transaction builder from the stream data.
    *
    * @param stream the stream
    * @return the TransactionBuilder subclass
    */
    public static TransactionBuilder loadFromBinary(final DataInputStream stream) {

        TransactionBuilder headerBuilder = TransactionBuilder.loadFromBinary(stream);
% for name in generator.schema:
<%
        layout = generator.schema[name].get("layout", [{type:""}])
        entityTypeValue = next(iter([x for x in layout if x.get('name','') == 'entityType']),{}).get('value',0)
        entityTypeVersion = next(iter([x for x in layout if x.get('name','') == 'version']),{}).get('value',0)
%>\
    %if (entityTypeValue > 0  and 'Aggregate' not in name and 'Block' not in name and not name.startswith('Embedded')):
        if (headerBuilder.getType().getValue() == ${entityTypeValue} && headerBuilder.getVersion() == ${entityTypeVersion}) {
            ${name}BodyBuilder bodyBuilder = ${name}BodyBuilder.loadFromBinary(stream);
            SequenceInputStream concatenate = new SequenceInputStream(
            new ByteArrayInputStream(headerBuilder.serialize()),
            new ByteArrayInputStream(bodyBuilder.serialize()));
            return ${name}Builder.loadFromBinary(new DataInputStream(concatenate));
        }
    %elif (entityTypeValue > 0 and 'Block' not in name and not name.startswith('Embedded')):
        if (headerBuilder.getType().getValue() == ${entityTypeValue} && headerBuilder.getVersion() == ${entityTypeVersion}) {
            AggregateTransactionBodyBuilder bodyBuilder = AggregateTransactionBodyBuilder.loadFromBinary(stream);
            SequenceInputStream concatenate = new SequenceInputStream(
            new ByteArrayInputStream(headerBuilder.serialize()),
            new ByteArrayInputStream(bodyBuilder.serialize()));
            return ${name}Builder.loadFromBinary(new DataInputStream(concatenate));
        }
    %endif
% endfor
        return headerBuilder;
    }

}
