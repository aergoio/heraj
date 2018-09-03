/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.DefaultConstants.DEFAULT_ENDPOINT;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertTrue;

import hera.api.AccountOperation;
import hera.api.AergoApi;
import hera.api.ContractOperation;
import hera.api.model.Abi;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.HostnameAndPort;
import hera.api.model.Receipt;
import hera.client.AergoClient;
import hera.strategy.NettyConnectStrategy;
import hera.util.DangerousSupplier;
import hera.util.HexUtils;
import hera.util.IoUtils;
import hera.util.ThreadUtils;
import java.io.InputStream;
import org.junit.Before;
import org.junit.Test;

public class DeployContractIT extends AbstractIT {
  protected final HostnameAndPort hostnameAndPort = HostnameAndPort.of(DEFAULT_ENDPOINT);
  protected final AergoApi aergoApi = new AergoClient(new NettyConnectStrategy().connect(hostnameAndPort));
  protected final DangerousSupplier<AergoApi> apiSupplier = () -> aergoApi;
  protected final DeployContract deployContract = new DeployContract();
  protected Account account;

  @Before
  public void setUp() throws Exception {
    deployContract.setApiSupplier(apiSupplier);
    final AergoApi aergo = apiSupplier.get();
    final AccountOperation accountOperation = aergo.getAccountOperation();
    final String password = randomUUID().toString();

    final ContractOperation contractOperation = aergoApi.getContractOperation();

    account = accountOperation.create(password).getResult();
    final Boolean unlockResult = accountOperation
        .unlock(new Authentication(account.getAddress(), password)).getResult();
    assertTrue(unlockResult);
  }

  @Test
  public void testExecute() throws Exception {
    final DangerousSupplier<Account> accountSupplier      = () -> account;
    final DangerousSupplier<InputStream> payloadSupplier  = () -> open("payload");
    deployContract.setAccount(accountSupplier);
    deployContract.setPayload(payloadSupplier);
    deployContract.execute();

    ThreadUtils.trySleep(3000);
    final ContractOperation contractOperation = aergoApi.getContractOperation();
    final Receipt definitionReceipt = contractOperation.getReceipt(deployContract.getContractAddress()).getResult();
    final AccountAddress contractAddress = definitionReceipt.getReceipt();
    final Abi abi = contractOperation.getAbi(contractAddress, "hello").getResult();
    logger.info("ABI: {}", abi);
  }
}