package io.nem.sdk.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigProperties {

    private static ConfigProperties ourInstance = new ConfigProperties();
    private final Properties properties = new Properties();

    private ConfigProperties() {
        try (InputStream inputStream =
            BaseTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                throw new IOException("config.properties not found");
            }
            this.properties.load(inputStream);
        } catch (IOException ignored) {
        }
    }

    public static ConfigProperties getInstance() {
        return ourInstance;
    }

    public String getNodeUrl() {
        return this.properties.getProperty("nem2sdk.conf.apiurl");
    }

    public String getNetworkTypeName() {
        return this.properties.getProperty("network.type.name").toUpperCase();
    }

    public String getAccountPublicKey() {
        return this.properties.getProperty("account.publickey");
    }

    public String getAccountRawAddress() {
        return this.properties.getProperty("account.address").toUpperCase();
    }
}
