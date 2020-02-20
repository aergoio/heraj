/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;

import hera.keystore.AergoKeyStore;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.JavaKeyStore;
import hera.keystore.KeyStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class AbstractWalletApiIT extends AbstractIT {

  @Parameters
  public static Collection<Object[]> data() {
    final List<Object[]> args = new ArrayList<>();

    // in memory
    final KeyStore inMemoryKeyStore = new InMemoryKeyStore();
    args.add(new Object[] {inMemoryKeyStore});

    // java kesytore
    final KeyStore javaKeyStore = new JavaKeyStore("PKCS12");
    args.add(new Object[] {javaKeyStore});

    // aergo kesytore
    final String tmpDir = System.getProperty("java.io.tmpdir") + "/" + randomUUID().toString();
    final KeyStore aergoKeyStore = new AergoKeyStore(tmpDir);
    args.add(new Object[] {aergoKeyStore});

    return args;
  }

  @Parameter(0)
  public KeyStore keyStore;

}
