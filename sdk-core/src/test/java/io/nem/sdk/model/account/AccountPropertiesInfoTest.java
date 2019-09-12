package io.nem.sdk.model.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class AccountPropertiesInfoTest {

    @Test
    void shouldCreateAccountPropertiesInfoViaConstructor() {
        String metaId = "12345";
        Address address =
            Address.createFromEncoded("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E142");
        AccountProperty accountProperty = new AccountProperty(PropertyType.ALLOW_ADDRESS,
            Collections.singletonList("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"));
        AccountProperties accountProperties =
            new AccountProperties(address, Arrays.asList(accountProperty));
        AccountPropertiesInfo accountPropertiesInfo =
            new AccountPropertiesInfo(metaId, accountProperties);

        assertEquals(metaId, accountPropertiesInfo.getMetaId());
        assertEquals(address, accountPropertiesInfo.getAccountProperties().getAddress());
        assertEquals(1, accountPropertiesInfo.getAccountProperties().getProperties().size());
        assertEquals(PropertyType.ALLOW_ADDRESS,
            accountPropertiesInfo.getAccountProperties().getProperties().get(0).getPropertyType());
    }
}
