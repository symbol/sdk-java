import java.io.DataInputStream;
import java.io.SequenceInputStream;
import java.io.ByteArrayInputStream;

/** Factory in charge of creating the right transaction builder from the streamed data. */
public class EmbeddedTransactionBuilderHelper {

    /**
    * It creates the rigth embbeded transaction builder from the stream data.
    *
    * @param stream the stream
    * @return the EmbeddedTransactionBuilder subclass
    */
    public static EmbeddedTransactionBuilder loadFromBinary(final DataInputStream stream) {

        EmbeddedTransactionBuilder headerBuilder = EmbeddedTransactionBuilder.loadFromBinary(stream);
% for name in generator.schema:
<%
        layout = generator.schema[name].get("layout", [{type:""}])
        entityTypeValue = next(iter([x for x in layout if x.get('name','') == 'entityType']),{}).get('value',0)
        entityTypeVersion = next(iter([x for x in layout if x.get('name','') == 'version']),{}).get('value',0)
%>\
    %if (entityTypeValue > 0 and 'Aggregate' not in name and 'Block' not in name and name.startswith('Embedded')):
        if (headerBuilder.getType() == EntityTypeDto.${helper.create_enum_name(name[8:])} && headerBuilder.getVersion() == ${entityTypeVersion}) {
            ${name[8:]}BodyBuilder bodyBuilder = ${name[8:]}BodyBuilder.loadFromBinary(stream);
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
