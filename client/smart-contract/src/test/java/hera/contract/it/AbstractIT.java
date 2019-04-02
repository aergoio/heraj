/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract.it;

import static org.slf4j.LoggerFactory.getLogger;

import hera.contract.SmartContract;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.junit.Before;
import org.slf4j.Logger;

public abstract class AbstractIT {

  protected final transient Logger logger = getLogger(getClass());

  protected final String propertiesPath = "/it.properties";

  protected String hostname;

  protected String encrypted;

  protected String password;

  @Before
  public void setUp() throws Exception {
    final Properties properties = readProperties();
    hostname = (String) properties.get("hostname");
    encrypted = (String) properties.get("encrypted");
    password = (String) properties.get("password");
  }

  protected Properties readProperties() throws IOException {
    Properties properties = new Properties();
    properties.load(getClass().getResourceAsStream(propertiesPath));
    return properties;
  }

  protected InputStream open(final String ext) {
    final String path = "/" + getClass().getName().replace('.', '/') + "." + ext;
    logger.trace("Path: {}", path);
    return getClass().getResourceAsStream(path);
  }

  protected interface ValidInterface extends SmartContract {

    void setNil(Object nilArg);

    Object getNil();

    void setBoolean(boolean booleanArg);

    boolean getBoolean();

    void setNumber(int numberArg);

    int getNumber();

    void setString(String stringArg);

    String getString();

  }

  protected interface InvalidMethodNameInterface extends SmartContract {

    // invalid
    void setNill(Object nilArg);

    Object getNil();

    void setBoolean(boolean booleanArg);

    boolean getBoolean();

    void setNumber(int numberArg);

    int getNumber();

    void setString(String stringArg);

    String getString();

  }

  protected interface InvalidMethodParameterCountInterface extends SmartContract {

    // invalid
    void setNil();

    Object getNil();

    void setBoolean(boolean booleanArg);

    boolean getBoolean();

    void setNumber(int numberArg);

    int getNumber();

    void setString(String stringArg);

    String getString();

  }

}
