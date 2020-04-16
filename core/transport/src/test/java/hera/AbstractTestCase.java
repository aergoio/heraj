/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.AccountAddress;
import hera.api.model.ChainIdHash;
import hera.api.model.ContractAddress;
import hera.key.AergoKeyGenerator;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({
    "javax.crypto.*",
    "javax.management.*",
    "javax.net.ssl.*",
    "javax.security.*",
    "org.bouncycastle.*"})
public abstract class AbstractTestCase {

  protected final transient Logger logger = getLogger(getClass());

  protected final AccountAddress accountAddress = new AergoKeyGenerator().create().getAddress();

  protected final ContractAddress contractAddress =
      new AergoKeyGenerator().create().getAddress().adapt(ContractAddress.class);

  protected final ChainIdHash chainIdHash = new ChainIdHash(of(randomUUID().toString().getBytes()));

  @Before
  public void setUp() {
  }

}
