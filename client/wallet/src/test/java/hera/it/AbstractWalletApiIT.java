/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;

import hera.keystore.KeyStore;
import hera.keystore.KeyStores;
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
    try {
      final List<Object[]> args = new ArrayList<>();

      // in memory
      final KeyStore inMemoryKeyStore = KeyStores.newInMemoryKeyStore();
      args.add(new Object[]{inMemoryKeyStore});

      // java keystore
      final java.security.KeyStore delegate = java.security.KeyStore.getInstance("PKCS12");
      delegate.load(null, null);
      final KeyStore javaKeyStore = KeyStores.newJavaKeyStore(delegate);
      args.add(new Object[]{javaKeyStore});

      // aergokeystore
      final String rootDir = System.getProperty("java.io.tmpdir") + "/" + randomUUID().toString();
      final KeyStore aergoKeyStore = KeyStores.newAergoKeyStore(rootDir);
      args.add(new Object[]{aergoKeyStore});

      return args;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Parameter(0)
  public KeyStore keyStore;

}
