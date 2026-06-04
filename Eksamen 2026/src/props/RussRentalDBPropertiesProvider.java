package props;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class RussRentalDBPropertiesProvider {
    public static final Properties PROPERTIES;


    static {
        PROPERTIES = new Properties();
        try {
            PROPERTIES.load(new FileInputStream("src//RussRental.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Error!, could not find the properties!" + e.getMessage());
        }
    }
}