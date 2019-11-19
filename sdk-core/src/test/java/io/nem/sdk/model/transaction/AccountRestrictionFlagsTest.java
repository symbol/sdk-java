package io.nem.sdk.model.transaction;


import io.nem.catapult.builders.AccountRestrictionFlagsDto;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AccountRestrictionFlags}
 */
public class AccountRestrictionFlagsTest {


    @Test
    public void shouldMatchCatbufferValues() {
        Arrays.stream(AccountRestrictionFlags.values()).forEach(a -> {
            //Testing that all possible values are handled.
            Assertions.assertNotNull(AccountRestrictionFlagsDto.rawValueOf((byte) a.getValue()));
        });
    }

}
