/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.service;

import static hera.util.HexUtils.dump;
import static hera.util.IoUtils.from;
import static java.util.UUID.randomUUID;

import hera.api.AccountOperation;
import hera.api.ContractOperation;
import hera.api.Decoder;
import hera.api.model.Abi;
import hera.api.model.AbiSet;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.ContractAddress;
import hera.api.model.ContractTxHash;
import hera.api.model.Hash;
import hera.api.model.HostnameAndPort;
import hera.build.web.exception.ResourceNotFoundException;
import hera.build.web.model.BuildDetails;
import hera.build.web.model.DeploymentResult;
import hera.build.web.model.ExecutionResult;
import hera.client.AergoClient;
import hera.strategy.NettyConnectStrategy;
import hera.test.LuaBinary;
import hera.test.LuaCompiler;
import hera.util.HexUtils;
import hera.util.IoUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class ContractService extends AbstractService {
  @Getter
  @Setter
  protected String endpoint;

  protected LuaCompiler luaCompiler = new LuaCompiler();

  protected List<DeploymentResult> deployHistory = new ArrayList<>();

  protected Map<String, Hash> buildUuid2contractAddresses = new HashMap<>();

  /**
   * Deploy {@code buildDetails}'s result.
   *
   * @param buildDetails build result
   *
   * @return deployment result
   */
  public DeploymentResult deploy(final BuildDetails buildDetails) throws Exception {
    final NettyConnectStrategy connectStrategy = new NettyConnectStrategy();
    final HostnameAndPort hostnameAndPort = HostnameAndPort.of(endpoint);
    logger.debug("Hostname and port: {}", hostnameAndPort);
    try (final AergoClient aergoApi = new AergoClient(connectStrategy.connect(hostnameAndPort))) {
      final AccountOperation accountOperation = aergoApi.getAccountOperation();
      final String password = randomUUID().toString();
      logger.trace("Password: {}", password);
      final Account account = accountOperation.create(password).getResult();
      final AccountAddress accountAddress = account.getAddress();
      final Authentication authentication = new Authentication(accountAddress, password);
      accountOperation.unlock(authentication);
      logger.debug("{} unlocked", authentication);
      final byte[] buildResult = buildDetails.getResult().getBytes();
      final LuaBinary luaBinary = luaCompiler.compile(() -> new ByteArrayInputStream(buildResult));
      logger.trace("Successful to compile:\n{}", dump(from(luaBinary.getPayload())));
      final ContractOperation contractOperation = aergoApi.getContractOperation();
      final ContractTxHash contractTransactionHash =
          contractOperation.deploy(accountAddress, () -> from(luaBinary.getPayload())).getResult();
      logger.debug("Contract transaction hash: {}", contractTransactionHash);
      buildUuid2contractAddresses.put(buildDetails.getUuid(), contractTransactionHash);
      final DeploymentResult deploymentResult = new DeploymentResult();
      deploymentResult.setBuildUuid(buildDetails.getUuid());
      deploymentResult.setContractAddress(contractTransactionHash.toString());
      deployHistory.add(deploymentResult);
      return deploymentResult;
    }
  }

  /**
   * Get application blockchain interface for {@code contractAddress} from {@code endpoint}.
   *
   * @return abi set
   */
  public AbiSet getAbi(final String contractAddress) throws IOException {
    final NettyConnectStrategy connectStrategy = new NettyConnectStrategy();
    final HostnameAndPort hostnameAndPort = HostnameAndPort.of(endpoint);
    try (final AergoClient client = new AergoClient(connectStrategy.connect(hostnameAndPort))) {
      final ContractOperation contractOperation = client.getContractOperation();
      final byte[] decoded = from(Decoder.defaultDecoder.decode(new StringReader(contractAddress)));
      return contractOperation.getReceipt(ContractTxHash.of(decoded))
          .flatMap(receipt -> contractOperation.getAbiSet(ContractAddress.of(decoded)))
          .getOrThrows(ResourceNotFoundException::new);
    }
  }

  /**
   * Execute smart contract.
   *
   * @param encodedContractAddress  contract address
   * @param functionName            function's name to execute
   * @param arguments               function's arguments to execute
   *
   * @return execution result
   *
   * @throws IOException Fail to execute
   */
  public ExecutionResult execute(
      final String encodedContractAddress,
      final String functionName,
      final Map<String, String> arguments)
      throws IOException {
    final AbiSet abiSet = getAbi(encodedContractAddress);
    final Abi abi = abiSet.findAbiByName(functionName).orElseThrow(ResourceNotFoundException::new);
    final NettyConnectStrategy connectStrategy = new NettyConnectStrategy();
    final HostnameAndPort hostnameAndPort = HostnameAndPort.of(endpoint);
    try (final AergoClient client = new AergoClient(connectStrategy.connect(hostnameAndPort))) {
      final ContractOperation contractOperation =  client.getContractOperation();
      final byte[] decoded =
          from(Decoder.defaultDecoder.decode(new StringReader(encodedContractAddress)));
      final AccountAddress executor = AccountAddress.of(decoded);
      final ContractAddress contractAddress = ContractAddress.of(decoded);
      final Object[] argumentValues = abi.getArgumentNames().stream().map(arguments::get).toArray();
      final Hash resultHash =
          contractOperation.execute(executor, contractAddress, abi, argumentValues).getResult();
      final ExecutionResult executionResult = new ExecutionResult();
      executionResult.setHash(resultHash.toString());
      return executionResult;
    }
  }

}
