package io.nem.sdk.model.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class AccountPropertyTest {

    @Test
    void shouldCreateAccountPropertyViaConstructor() {
        AccountProperty accountProperty =
            new AccountProperty(
                PropertyType.AllowAddress,
                Arrays.asList("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"));
        assertEquals(PropertyType.AllowAddress, accountProperty.getPropertyType());
        assertEquals(1, accountProperty.getValues().size());
    }
}
