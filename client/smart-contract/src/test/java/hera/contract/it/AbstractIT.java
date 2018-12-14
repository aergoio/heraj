/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract.it;

import static org.slf4j.LoggerFactory.getLogger;

import hera.contract.SmartContract;
import java.io.InputStream;
import org.slf4j.Logger;

public abstract class AbstractIT {

  protected final transient Logger logger = getLogger(getClass());

  protected final String endpoint = "localhost:7845";

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
