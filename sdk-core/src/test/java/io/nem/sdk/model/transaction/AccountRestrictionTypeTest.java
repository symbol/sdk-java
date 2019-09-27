package io.nem.sdk.model.transaction;


import io.nem.catapult.builders.AccountRestrictionTypeDto;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AccountRestrictionType}
 */
public class AccountRestrictionTypeTest {


    @Test
    public void shouldMatchCatbufferValues() {
        Arrays.stream(AccountRestrictionType.values()).forEach(a -> {
            //Testing that all possible values are handled.
            Assert.assertNotNull(AccountRestrictionTypeDto.rawValueOf((byte) a.getValue()));
        });
    }

}
