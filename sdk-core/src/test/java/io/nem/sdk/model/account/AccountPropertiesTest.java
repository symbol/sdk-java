package io.nem.sdk.model.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class AccountPropertiesTest {

    @Test
    void shouldCreateAccountPropertiesViaConstructor() {
        Address address =
            Address.createFromEncoded("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E142");
        AccountProperty accountProperty =
            new AccountProperty(
                PropertyType.ALLOW_ADDRESS,
                Arrays.asList("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"));
        AccountProperties accountProperties =
            new AccountProperties(address, Arrays.asList(accountProperty));

        assertEquals(address, accountProperties.getAddress());
        assertEquals(1, accountProperties.getProperties().size());
        assertEquals(
            PropertyType.ALLOW_ADDRESS, accountProperties.getProperties().get(0).getPropertyType());
    }
}
