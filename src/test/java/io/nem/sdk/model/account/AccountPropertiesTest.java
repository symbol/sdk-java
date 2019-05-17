package io.nem.sdk.model.account;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountPropertiesTest {

    @Test
    void shouldCreateAccountPropertiesViaConstructor() {
        Address address = Address.createFromEncoded("9050B9837EFAB4BBE8A4B9BB32D812F9885C00D8FC1650E142");
        AccountProperty accountProperty = new AccountProperty(PropertyType.AllowAddress, Arrays.asList("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"));
        AccountProperties accountProperties = new AccountProperties(address, Arrays.asList(accountProperty));

        assertEquals(address, accountProperties.getAddress());
        assertEquals(1, accountProperties.getProperties().size());
        assertEquals(PropertyType.AllowAddress, accountProperties.getProperties().get(0).getPropertyType());
    }
}
