/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.service;

import static hera.api.Decoder.defaultDecoder;
import static hera.api.Encoder.defaultEncoder;
import static hera.util.HexUtils.dump;
import static hera.util.IoUtils.from;
import static java.util.UUID.randomUUID;

import hera.api.AccountOperation;
import hera.api.ContractOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInferface;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.HostnameAndPort;
import hera.build.web.exception.AergoNodeException;
import hera.build.web.exception.ResourceNotFoundException;
import hera.build.web.model.BuildDetails;
import hera.build.web.model.DeploymentResult;
import hera.build.web.model.ExecutionResult;
import hera.client.AergoClient;
import hera.exception.RpcConnectionException;
import hera.exception.RpcException;
import hera.strategy.NettyConnectStrategy;
import hera.test.LuaBinary;
import hera.test.LuaCompiler;
import hera.util.HexUtils;
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

  protected String password = randomUUID().toString();

  protected Account account;

  protected LuaCompiler luaCompiler = new LuaCompiler();

  protected List<DeploymentResult> deployHistory = new ArrayList<>();

  protected Map<String, DeploymentResult> encodedContractTxHash2contractAddresses = new HashMap<>();

  protected synchronized void ensureAccount() {
    if (null != account) {
      return;
    }
    final NettyConnectStrategy connectStrategy = new NettyConnectStrategy();
    final HostnameAndPort hostnameAndPort = HostnameAndPort.of(endpoint);

    try (final AergoClient aergoApi = new AergoClient(connectStrategy.connect(hostnameAndPort))) {
      final AccountOperation accountOperation = aergoApi.getAccountOperation();
      logger.trace("Password: {}", password);
      account = accountOperation.create(password).getResult();
      final AccountAddress accountAddress = account.getAddress();
      final Authentication authentication = new Authentication(accountAddress, password);
      accountOperation.unlock(authentication);
      logger.debug("{} unlocked", authentication);
    } catch (final RpcConnectionException ex) {
      throw new AergoNodeException(
          "Fail to connect aergo[" + endpoint + "]. Check your aergo node.", ex);
    } catch (final RpcException ex) {
      throw new AergoNodeException("Fail to deploy contract", ex);
    }

  }

  /**
   * Deploy {@code buildDetails}'s result.
   *
   * @param buildDetails build result
   *
   * @return deployment result
   */
  public DeploymentResult deploy(final BuildDetails buildDetails) throws Exception {
    ensureAccount();
    final NettyConnectStrategy connectStrategy = new NettyConnectStrategy();
    final HostnameAndPort hostnameAndPort = HostnameAndPort.of(endpoint);
    logger.debug("Hostname and port: {}", hostnameAndPort);
    try (final AergoClient aergoApi = new AergoClient(connectStrategy.connect(hostnameAndPort))) {
      final byte[] buildResult = buildDetails.getResult().getBytes();
      final LuaBinary luaBinary = luaCompiler.compile(() -> new ByteArrayInputStream(buildResult));
      logger.trace("Successful to compile:\n{}", dump(from(luaBinary.getPayload())));
      final ContractOperation contractOperation = aergoApi.getContractOperation();
      final ContractTxHash contractTransactionHash =
          contractOperation.deploy(account.getAddress(), () -> from(luaBinary.getPayload()))
              .getResult();
      logger.debug("Contract transaction hash: {}", contractTransactionHash);
      final String encodedContractTxHash = contractTransactionHash.getEncodedValue(defaultEncoder);
      final DeploymentResult deploymentResult = new DeploymentResult();
      deploymentResult.setBuildUuid(buildDetails.getUuid());
      deploymentResult.setEncodedContractTransactionHash(encodedContractTxHash);
      encodedContractTxHash2contractAddresses.put(encodedContractTxHash, deploymentResult);
      deployHistory.add(deploymentResult);
      return deploymentResult;
    } catch (final RpcConnectionException ex) {
      throw new AergoNodeException(
          "Fail to connect aergo[" + endpoint + "]. Check your aergo node.", ex);
    } catch (final RpcException ex) {
      throw new AergoNodeException("Fail to deploy contract", ex);
    }
  }

  /**
   * Execute smart contract.
   *
   * @param encodedContractTxHash contract transaction hash
   * @param functionName          function's name to execute
   * @param args                  function's arguments to execute
   *
   * @return execution result
   *
   * @throws IOException Fail to execute
   */
  public ExecutionResult execute(final String encodedContractTxHash, final String functionName,
      final Object... args) throws IOException {
    logger.trace("Encoded tx hash: {}", encodedContractTxHash);
    final byte[] decoded = from(defaultDecoder.decode(new StringReader(encodedContractTxHash)));
    logger.debug("Decoded contract hash:\n{}", HexUtils.dump(decoded));
    final ContractTxHash contractTxHash = ContractTxHash.of(decoded);
    ensureAccount();
    final NettyConnectStrategy connectStrategy = new NettyConnectStrategy();
    final HostnameAndPort hostnameAndPort = HostnameAndPort.of(endpoint);
    try (final AergoClient client = new AergoClient(connectStrategy.connect(hostnameAndPort))) {
      final ContractOperation contractOperation = client.getContractOperation();
      final ContractTxReceipt contractTxReceipt =
          contractOperation.getReceipt(contractTxHash).getResult();
      logger.debug("Receipt: {}", contractTxReceipt);
      final ContractAddress contractAddress = contractTxReceipt.getContractAddress();
      final DeploymentResult deploymentResult =
          encodedContractTxHash2contractAddresses.get(encodedContractTxHash);
      final ContractFunction contractFunction = deploymentResult.getContractInterface()
          .findFunctionByName(functionName).orElseThrow(ResourceNotFoundException::new);

      logger.trace("Executing...");
      final ContractTxHash executionContractHash = contractOperation.execute(
          account.getAddress(),
          contractAddress,
          contractFunction,
          args
      ).getResult();

      final ExecutionResult executionResult = new ExecutionResult();
      executionResult.setContractTransactionHash(executionContractHash.getEncodedValue());
      return executionResult;
    }
  }

  /**
   * Get latest contract.
   *
   * @return latest deployed contract
   */
  public DeploymentResult getLatestContractInformation() {
    if (deployHistory.isEmpty()) {
      throw new ResourceNotFoundException("No deployment!! Deploy your contract first.");
    }
    final DeploymentResult latest = deployHistory.get(deployHistory.size() - 1);
    logger.debug("Latest deployment: {}", latest);
    if (null == latest.getContractInterface()) {
      final String encodedContractTxHash = latest.getEncodedContractTransactionHash();
      logger.trace("Encoded tx hash: {}", encodedContractTxHash);
      try {
        final byte[] decoded = from(defaultDecoder.decode(new StringReader(encodedContractTxHash)));
        logger.debug("Decoded contract hash:\n{}", HexUtils.dump(decoded));
        final ContractTxHash contractTxHash = ContractTxHash.of(decoded);
        final ContractInferface contractInferface = getInterface(contractTxHash);
        latest.setContractInterface(contractInferface);
      } catch (IOException ex) {
        throw new ResourceNotFoundException(latest + " not found.");
      }
    }
    return latest;
  }

  /**
   * Get application blockchain interface for {@code encodedContractTransactionHash}
   * from {@code endpoint}.
   *
   * @return abi set
   */
  public ContractInferface getInterface(final ContractTxHash contractTxHash) {
    final NettyConnectStrategy connectStrategy = new NettyConnectStrategy();
    final HostnameAndPort hostnameAndPort = HostnameAndPort.of(endpoint);
    try (final AergoClient client = new AergoClient(connectStrategy.connect(hostnameAndPort))) {
      final ContractOperation contractOperation = client.getContractOperation();
      return contractOperation.getReceipt(contractTxHash)
          .map(ContractTxReceipt::getContractAddress)
          .flatMap(contractOperation::getContractInterface)
          .getOrThrows(ResourceNotFoundException::new);
    }
  }
}
