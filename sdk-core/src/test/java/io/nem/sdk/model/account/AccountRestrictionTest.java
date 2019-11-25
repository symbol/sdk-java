package io.nem.sdk.model.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nem.sdk.model.transaction.AccountRestrictionFlags;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class AccountRestrictionTest {

    @Test
    void shouldCreateAccountRestrictionViaConstructor() {
        AccountRestriction accountRestriction =
            new AccountRestriction(
                AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS,
                Arrays.asList("SDUP5PLHDXKBX3UU5Q52LAY4WYEKGEWC6IB3VBFM"));
        assertEquals(AccountRestrictionFlags.ALLOW_INCOMING_ADDRESS,
            accountRestriction.getRestrictionFlags());
        assertEquals(1, accountRestriction.getValues().size());
    }
}
