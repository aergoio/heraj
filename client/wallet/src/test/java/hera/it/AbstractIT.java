/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;
import static org.slf4j.LoggerFactory.getLogger;

import hera.util.ThreadUtils;
import java.io.InputStream;
import org.slf4j.Logger;

public abstract class AbstractIT {

  protected final transient Logger logger = getLogger(getClass());

  protected InputStream open(final String ext) {
    final String path = "/" + getClass().getName().replace('.', '/') + "." + ext;
    logger.trace("Path: {}", path);
    return getClass().getResourceAsStream(path);
  }

  protected void waitForNextBlockToGenerate() {
    ThreadUtils.trySleep(2200L);
  }

  protected String randomName() {
    return randomUUID().toString().substring(0, 12).replace('-', 'a');
  }

}
