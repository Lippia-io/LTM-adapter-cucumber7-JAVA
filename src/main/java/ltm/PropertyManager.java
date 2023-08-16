package ltm;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertyManager {
    private static final Logger logger = Logger.getLogger(PropertyManager.class.getName());

    private static final ClassLoader loader;
    private static final String PROPERTY_FILE_NAME = "config.properties";

    private static final ThreadLocal<Properties> properties = new ThreadLocal<>();

    static {
        loader = PropertyManager.class.getClassLoader();
    }

    private PropertyManager() {
    }

    private static Properties getProperties() {
        if (properties.get() == null) {
            try {
                loadProperties();
            } catch (IOException var1) {
                var1.printStackTrace();
            }
        }

        return properties.get();
    }

    public static String getProperty(String propertyKey) {
        return getProperties().getProperty(propertyKey);
    }

    public static boolean isPropertyPresentAndNotEmpty(String propertyKey) {
        return getProperties().containsKey(propertyKey) && !getProperties().getProperty(propertyKey).isEmpty();
    }

    private static void loadProperties() throws IOException {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        Properties clientProperties = new EncryptableProperties(encryptor);

        if (isResourceLoadable()) {
            InputStream inputStream = PropertyManager.class.getClassLoader().getResourceAsStream(PROPERTY_FILE_NAME);
            clientProperties.load(inputStream);

            properties.set(new EncryptableProperties(new StandardPBEStringEncryptor()));
            properties.get().putAll(clientProperties);
            return;
        }

        logger.info(() -> PROPERTY_FILE_NAME + " is not loadable because is not present in the project");
    }

    private static boolean isResourceLoadable() {
        return loader.getResourceAsStream(PROPERTY_FILE_NAME) != null;
    }

}