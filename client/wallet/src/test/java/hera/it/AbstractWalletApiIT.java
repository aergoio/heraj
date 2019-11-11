/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;

import hera.api.model.Authentication;
import hera.key.AergoKey;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.JavaKeyStore;
import hera.keystore.KeyStore;
import hera.model.KeyAlias;
import hera.wallet.WalletApi;
import hera.wallet.WalletFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
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

    // in memory
    final KeyStore javaKeyStore = new JavaKeyStore("PKCS12");
    args.add(new Object[] {javaKeyStore});

    return args;
  }

  @Parameter(0)
  public KeyStore keyStore;

  protected WalletApi walletApi;

  protected Authentication authentication;

  @Before
  public void setUp() throws Exception {
    super.setUp();

    final KeyAlias alias = new KeyAlias(randomUUID().toString().replace("-", ""));
    authentication = Authentication.of(alias, randomName().toString());

    final AergoKey newKey = createNewKey();
    keyStore.save(authentication, newKey);

    walletApi = new WalletFactory().create(keyStore);
    walletApi.bind(aergoClient);
  }

}
