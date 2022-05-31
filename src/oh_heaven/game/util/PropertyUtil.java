package oh_heaven.game.util;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * 配置方法的访问类
 */
public class PropertyUtil {

    private static Properties properties;

    /**
     * get the parameter of int class
     *
     * @param parameterName
     * @return
     */
    public static int getIntParameter(String parameterName) {
        return Integer.parseInt(properties.getProperty(parameterName));
    }

    /**
     * get the parameter of string class
     *
     * @param parameterName
     * @return
     */
    public static String getStringParameter(String parameterName) {
        return properties.getProperty(parameterName);
    }

    public static void loadPropertiesFile(String file) {
        properties = new Properties();

        if (file == null) {
            file = "original.properties";
        }

        try {
            // load
            properties.load(new FileInputStream("properties/" + file));

            if (properties.getProperty("current_mode") != null) {
                // if the file contains the current_mode param
                file = properties.getProperty("current_mode");
                properties.load(new FileInputStream("properties/" + file));
            }
        } catch (Exception e) {
            try {
                properties.load(new FileInputStream("properties/original.properties"));
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }
}
