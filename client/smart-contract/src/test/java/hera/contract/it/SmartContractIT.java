/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import hera.api.model.Authentication;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractTxHash;
import hera.api.model.Fee;
import hera.contract.SmartContractFactory;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.util.IoUtils;
import hera.util.ThreadUtils;
import hera.wallet.Wallet;
import hera.wallet.WalletBuilder;
import hera.wallet.WalletType;
import java.io.InputStreamReader;
import org.junit.Before;
import org.junit.Test;

public class SmartContractIT extends AbstractIT {

  protected Wallet wallet;

  protected ContractAddress contractAddress;

  @Before
  public void setUp() {
    try {
      this.wallet = new WalletBuilder()
          .withEndpoint("localhost:7845")
          .build(WalletType.Naive);
      final String password = randomUUID().toString();
      AergoKey key = new AergoKeyGenerator().create();
      wallet.saveKey(key, password);
      wallet.unlock(Authentication.of(key.getAddress(), password));

      final String payload = IoUtils.from(new InputStreamReader(open("payload")));
      final ContractDefinition definition = ContractDefinition.newBuilder()
          .encodedContract(payload)
          .build();
      final ContractTxHash deployTxHash = wallet.deploy(definition, Fee.getDefaultFee());
      Thread.sleep(1200L);
      this.contractAddress = wallet.getReceipt(deployTxHash).getContractAddress();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Test
  public void testInvocation() {
    final SmartContractSample smartContarct = new SmartContractFactory()
        .create(SmartContractSample.class, contractAddress);
    smartContarct.bind(wallet);
    smartContarct.bind(Fee.getDefaultFee());

    final Object nilArg = null;
    final boolean booleanArg = true;
    final int numberArg = 20000;
    final String stringArg = randomUUID().toString();

    smartContarct.setNil(nilArg);
    smartContarct.setBoolean(booleanArg);
    smartContarct.setNumber(numberArg);
    smartContarct.setString(stringArg);

    ThreadUtils.trySleep(1500L);

    assertNull(smartContarct.getNil());
    assertEquals(booleanArg, smartContarct.getBoolean());
    assertEquals(numberArg, smartContarct.getNumber());
    assertEquals(stringArg, smartContarct.getString());
  }

}
