/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import hera.api.model.Authentication;
import hera.api.model.ClientManagedAccount;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Fee;
import hera.api.model.ServerManagedAccount;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.strategy.NettyConnectStrategy;
import hera.util.IoUtils;
import java.io.InputStreamReader;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.Before;
import org.junit.Test;

public class ContractOperationIT extends AbstractIT {

  protected String contractPayload;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    contractPayload = IoUtils.from(new InputStreamReader(open("payload")));
  }

  @Test
  public void testLuaContractConstructor() throws Exception {
    // make aergo client object
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    // create an account
    final AergoKey key = new AergoKeyGenerator().create();
    final ClientManagedAccount account = ClientManagedAccount.of(key);

    // we need a money to deploy/execute smart contract
    aergoClient.getAccountOperation().unlock(Authentication.of(rich.getAddress(), richPassword));
    aergoClient.getTransactionOperation().send(rich, account, 1L);
    aergoClient.getAccountOperation().lock(Authentication.of(rich.getAddress(), richPassword));

    // define contract definition
    final ContractDefinition definition =
        new ContractDefinition(() -> contractPayload, 32, "Someone");

    // deploy contract definition
    final ContractTxHash deployTxHash = aergoClient.getContractOperation().deploy(account,
        definition, Fee.of(Optional.of(0L), Optional.of(0L)));
    logger.info("Deploy hash: {}", deployTxHash);
    account.incrementNonce();

    waitForNextBlockToGenerate();

    // query definition transaction receipt
    final ContractTxReceipt definitionReceipt =
        aergoClient.getContractOperation().getReceipt(deployTxHash);
    logger.info("Deploy receipt: {}", definitionReceipt);

    // get contract address from contract tx receipt
    final ContractAddress contractAddress = definitionReceipt.getContractAddress();
    logger.info("Contract address: {}", contractAddress);

    // get contract interface of contract address
    final ContractInterface contractInterface =
        aergoClient.getContractOperation().getContractInterface(contractAddress);
    logger.info("Contract interface: {}", contractInterface);

    // build query invocation
    final ContractFunction queryFunction = contractInterface.findFunction("getConsVal").get();
    final ContractInvocation query = ContractInvocation.of(contractAddress, queryFunction);
    logger.info("Query invocation : {}", query);

    // request query invocation
    final ContractResult queryResult = aergoClient.getContractOperation().query(query);
    logger.info("Query result: {}", queryResult);

    // find query result with java object
    final Data data = queryResult.bind(ContractOperationIT.Data.class);
    logger.info("Binded result: {}", data);

    // close the client
    aergoClient.close();
  }

  @Test
  public void testLuaContractDeployAndExecuteWithLocalAccount() throws Exception {
    // make aergo client object
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    // create an account
    final AergoKey key = new AergoKeyGenerator().create();
    final ClientManagedAccount account = ClientManagedAccount.of(key);

    // we need a money to deploy/execute smart contract
    aergoClient.getAccountOperation().unlock(Authentication.of(rich.getAddress(), richPassword));
    aergoClient.getTransactionOperation().send(rich, account, 3L);
    aergoClient.getAccountOperation().lock(Authentication.of(rich.getAddress(), richPassword));

    // define contract definition
    final ContractDefinition definition =
        new ContractDefinition(() -> contractPayload, 32, "Someone");

    // deploy contract definition
    final ContractTxHash deployTxHash = aergoClient.getContractOperation().deploy(account,
        definition, Fee.of(Optional.of(0L), Optional.of(0L)));
    logger.info("Deploy hash: {}", deployTxHash);
    account.incrementNonce();

    waitForNextBlockToGenerate();

    // query definition transaction receipt
    final ContractTxReceipt definitionReceipt =
        aergoClient.getContractOperation().getReceipt(deployTxHash);
    logger.info("Deploy receipt: {}", definitionReceipt);

    // get contract address from contract tx receipt
    final ContractAddress contractAddress = definitionReceipt.getContractAddress();
    logger.info("Contract address: {}", contractAddress);

    // get contract interface of contract address
    final ContractInterface contractInterface =
        aergoClient.getContractOperation().getContractInterface(contractAddress);
    logger.info("Contract interface: {}", contractInterface);

    // define contract execution
    final ContractFunction executionFunction = contractInterface.findFunction("set").get();
    final ContractInvocation execution =
        ContractInvocation.of(contractAddress, executionFunction, "key1", "value");
    logger.info("Contract invocation: {}", execution);

    // execute the invocation
    final ContractTxHash executionTxHash = aergoClient.getContractOperation().execute(account,
        execution, Fee.of(Optional.of(0L), Optional.of(0L)));
    logger.info("Execution hash: {}", executionTxHash);
    account.incrementNonce();

    waitForNextBlockToGenerate();

    // query execution transaction receipt
    final ContractTxReceipt executionReceipt =
        aergoClient.getContractOperation().getReceipt(executionTxHash);
    logger.info("Execution receipt: {}", executionReceipt);

    // build query invocation
    final ContractFunction queryFunction = contractInterface.findFunction("get").get();
    final ContractInvocation query = ContractInvocation.of(contractAddress, queryFunction, "key");
    logger.info("Query invocation : {}", query);

    // request query invocation
    final ContractResult queryResult = aergoClient.getContractOperation().query(query);
    logger.info("Query result: {}", queryResult);

    // find query result with java object
    final Data data = queryResult.bind(ContractOperationIT.Data.class);
    logger.info("Binded result: {}", data);

    // close the client
    aergoClient.close();
  }

  @Test
  public void testLuaContractDeployAndExecuteWithRemoteAccount() throws Exception {
    // make aergo client object
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    // create an account
    final String password = "password";
    final ServerManagedAccount account = aergoClient.getAccountOperation().create(password);

    // we need a money to deploy/execute smart contract
    aergoClient.getAccountOperation().unlock(Authentication.of(rich.getAddress(), richPassword));
    aergoClient.getTransactionOperation().send(rich, account, 3L);
    aergoClient.getAccountOperation().lock(Authentication.of(rich.getAddress(), richPassword));

    // unlock smart contract executor
    aergoClient.getAccountOperation().unlock(Authentication.of(account.getAddress(), password));

    // define contract definition
    final ContractDefinition definition =
        new ContractDefinition(() -> contractPayload, 32, "Someone");

    // deploy contract definition
    final ContractTxHash deployTxHash = aergoClient.getContractOperation().deploy(account,
        definition, Fee.of(Optional.of(0L), Optional.of(0L)));
    logger.info("Deploy hash: {}", deployTxHash);
    account.incrementNonce();

    waitForNextBlockToGenerate();

    // query definition transaction receipt
    final ContractTxReceipt definitionReceipt =
        aergoClient.getContractOperation().getReceipt(deployTxHash);
    logger.info("Deploy receipt: {}", definitionReceipt);

    // get contract address from contract tx receipt
    final ContractAddress contractAddress = definitionReceipt.getContractAddress();
    logger.info("Contract address: {}", contractAddress);

    // get contract interface of contract address
    final ContractInterface contractInterface =
        aergoClient.getContractOperation().getContractInterface(contractAddress);
    logger.info("Contract interface: {}", contractInterface);

    // define contract execution
    final ContractFunction executionFunction = contractInterface.findFunction("set").get();
    final ContractInvocation execution =
        ContractInvocation.of(contractAddress, executionFunction, "key1", "value");
    logger.info("Contract invocation: {}", execution);

    // execute the invocation
    final ContractTxHash executionTxHash = aergoClient.getContractOperation().execute(account,
        execution, Fee.of(Optional.of(0L), Optional.of(0L)));
    logger.info("Execution hash: {}", executionTxHash);
    account.incrementNonce();

    waitForNextBlockToGenerate();

    // query execution transaction receipt
    final ContractTxReceipt executionReceipt =
        aergoClient.getContractOperation().getReceipt(executionTxHash);
    logger.info("Execution receipt: {}", executionReceipt);

    // build query invocation
    final ContractFunction queryFunction = contractInterface.findFunction("get").get();
    final ContractInvocation query = ContractInvocation.of(contractAddress, queryFunction, "key");
    logger.info("Query invocation : {}", query);

    // request query invocation
    final ContractResult queryResult = aergoClient.getContractOperation().query(query);
    logger.info("Query result: {}", queryResult);

    // find query result with java object
    final Data data = queryResult.bind(ContractOperationIT.Data.class);
    logger.info("Binded result: {}", data);

    // lock smart contract executor
    aergoClient.getAccountOperation().lock(Authentication.of(account.getAddress(), password));

    // close the client
    aergoClient.close();
  }

  @ToString
  public static class Data {
    @Getter
    @Setter
    protected int intVal;

    @Getter
    @Setter
    protected String stringVal;
  }

}
