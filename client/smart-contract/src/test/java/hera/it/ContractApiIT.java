/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.api.model.Authentication;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractTxHash;
import hera.api.model.Fee;
import hera.contract.ContractApi;
import hera.contract.ContractApiFactory;
import hera.exception.ContractException;
import hera.key.AergoKey;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.KeyStore;
import hera.model.KeyAlias;
import hera.util.IoUtils;
import hera.wallet.WalletApi;
import hera.wallet.WalletApiFactory;
import java.io.InputStreamReader;
import org.junit.Before;
import org.junit.Test;

public class ContractApiIT extends AbstractIT {

  protected WalletApi walletApi;

  protected ContractAddress contractAddress;

  protected Fee fee = Fee.ZERO;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    final KeyStore keyStore = new InMemoryKeyStore();

    final KeyAlias alias = KeyAlias.of(randomUUID().toString().replaceAll("-", ""));
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(alias, password);
    final AergoKey key = createNewKey();
    keyStore.save(authentication, key);

    this.walletApi = new WalletApiFactory().create(keyStore);
    this.walletApi.bind(aergoClient);
    this.walletApi.unlock(authentication);

    final String payload = IoUtils.from(new InputStreamReader(open("payload")));
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payload)
        .build();
    final ContractTxHash deployTxHash = walletApi.transactionApi().deploy(definition, fee);
    waitForNextBlockToGenerate();
    this.contractAddress = walletApi.queryApi().getReceipt(deployTxHash).getContractAddress();
  }

  @Test
  public void testInvocation() {
    final ContractApi<ValidInterface> contractApi =
        new ContractApiFactory().create(contractAddress, ValidInterface.class);

    // final Object nilArg = null;
    final boolean booleanArg = true;
    final int numberArg = 20000;
    final String stringArg = randomUUID().toString();

    // contractApi.walletApi(walletApi).fee(fee).setNil(nilArg);
    contractApi.walletApi(walletApi).fee(fee).setBoolean(booleanArg);
    contractApi.walletApi(walletApi).fee(fee).setNumber(numberArg);
    contractApi.walletApi(walletApi).fee(fee).setString(stringArg);;

    waitForNextBlockToGenerate();

    // assertNull(contractApi.walletApi(walletApi).noFee().getNil());
    assertEquals(booleanArg, contractApi.walletApi(walletApi).noFee().getBoolean());
    assertEquals(numberArg, contractApi.walletApi(walletApi).noFee().getNumber());
    assertEquals(stringArg, contractApi.walletApi(walletApi).noFee().getString());
  }

  @Test
  public void testInvocationOnInvalidMethodNameInterface() {
    try {
      // when
      final ContractApi<InvalidMethodNameInterface> contractApi = new ContractApiFactory()
          .create(contractAddress, InvalidMethodNameInterface.class);
      contractApi.walletApi(walletApi).fee(fee).getNill();
      fail();
    } catch (ContractException e) {
      // then
    }
  }

  public static interface ValidInterface {

    void setNil(Object nilArg);

    Object getNil();

    void setBoolean(boolean booleanArg);

    boolean getBoolean();

    void setNumber(int numberArg);

    int getNumber();

    void setString(String stringArg);

    String getString();

  }

  public static interface InvalidMethodNameInterface {

    void setNil(Object nilArg);

    // invalid
    Object getNill();

    void setBoolean(boolean booleanArg);

    boolean getBoolean();

    void setNumber(int numberArg);

    int getNumber();

    void setString(String stringArg);

    String getString();

  }

}
