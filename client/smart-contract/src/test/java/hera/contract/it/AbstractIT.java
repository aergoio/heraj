/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract.it;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.InputStream;
import org.slf4j.Logger;

public abstract class AbstractIT {

  protected final transient Logger logger = getLogger(getClass());

  protected InputStream open(final String ext) {
    final String path = "/" + getClass().getName().replace('.', '/') + "." + ext;
    logger.trace("Path: {}", path);
    return getClass().getResourceAsStream(path);
  }

}
