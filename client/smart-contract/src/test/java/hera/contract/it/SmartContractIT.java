/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import hera.api.model.Authentication;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractTxHash;
import hera.contract.SmartContract;
import hera.contract.SmartContractFactory;
import hera.exception.ContractException;
import hera.key.AergoKey;
import hera.util.IoUtils;
import hera.util.ThreadUtils;
import hera.wallet.Wallet;
import hera.wallet.WalletBuilder;
import hera.wallet.WalletType;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;

public class SmartContractIT extends AbstractIT {

  protected Wallet wallet;

  protected ContractAddress contractAddress;

  protected String password = randomUUID().toString();

  /*
  @Before
  public void setUp() throws Exception {
    super.setUp();
    this.wallet = new WalletBuilder()
        .withEndpoint(hostname)
        .withRefresh(3, 500L, TimeUnit.MILLISECONDS)
        .build(WalletType.Naive);
    final AergoKey key = createNewKey();
    wallet.saveKey(key, password);
    wallet.unlock(Authentication.of(key.getAddress(), password));

    final String payload = IoUtils.from(new InputStreamReader(open("payload")));
    final ContractDefinition definition = ContractDefinition.newBuilder()
        .encodedContract(payload)
        .build();
    final ContractTxHash deployTxHash = wallet.deploy(definition);
    Thread.sleep(2200L);
    this.contractAddress = wallet.getReceipt(deployTxHash).getContractAddress();
  }

  @Test
  public void testInvocation() {
    final ValidInterface smartContarct =
        new SmartContractFactory().create(ValidInterface.class, contractAddress);
    smartContarct.bind(wallet);

    final Object nilArg = null;
    final boolean booleanArg = true;
    final int numberArg = 20000;
    final String stringArg = randomUUID().toString();

    smartContarct.setNil(nilArg);
    smartContarct.setBoolean(booleanArg);
    smartContarct.setNumber(numberArg);
    smartContarct.setString(stringArg);

    ThreadUtils.trySleep(2200L);

    assertNull(smartContarct.getNil());
    assertEquals(booleanArg, smartContarct.getBoolean());
    assertEquals(numberArg, smartContarct.getNumber());
    assertEquals(stringArg, smartContarct.getString());
  }

  @Test
  public void testInvocationOnNoBindedWallet() {
    final ValidInterface smartContarct = new SmartContractFactory()
        .create(ValidInterface.class, contractAddress);
    // smartContarct.bind(wallet);

    try {
      smartContarct.setBoolean(true);
      fail();
    } catch (ContractException e) {
      // good we expected this
    }
  }

  @Test
  public void testInvocationOnInvalidMethodNameInterface() {
    final InvalidMethodNameInterface smartContract = new SmartContractFactory()
        .create(InvalidMethodNameInterface.class, contractAddress);
    smartContract.bind(wallet);

    try {
      smartContract.getNil();
      fail();
    } catch (ContractException e) {
      // good we expected this
    }
  }

  @Test
  public void testInvocationOnInvalidMethodParameterCountInterface() {
    final InvalidMethodParameterCountInterface smartContract = new SmartContractFactory()
        .create(InvalidMethodParameterCountInterface.class, contractAddress);
    smartContract.bind(wallet);

    try {
      smartContract.getNil();
      fail();
    } catch (ContractException e) {
      // good we expected this
    }
  }
  */

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
