/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static org.slf4j.LoggerFactory.getLogger;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

@SuppressWarnings("rawtypes")
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({
    "javax.crypto.*",
    "javax.management.*",
    "javax.net.ssl.*",
    "javax.security.*",
    "org.bouncycastle.*"})
public abstract class AbstractTestCase {
  protected static final int N_TEST = 100;

  protected final transient Logger logger = getLogger(getClass());

}
