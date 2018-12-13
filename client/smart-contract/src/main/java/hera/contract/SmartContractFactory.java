/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.ContractAddress;
import java.lang.reflect.Proxy;
import org.slf4j.Logger;

public class SmartContractFactory {

  protected final Logger logger = getLogger(getClass());

  /**
   * Create a proxy instance to call smart contract corresponding to {@code type}. Class loader is
   * set by {@code getClass().getClassLoader()}.
   *
   * @param <ContractT> a smart contract interface type which extends {@link SmartContract}
   * @param type a proxy type
   * @param contractAddress a contract address
   * @return a proxy instance
   */
  public <ContractT extends SmartContract> ContractT create(final Class<ContractT> type,
      final ContractAddress contractAddress) {
    return create(type, contractAddress, getClass().getClassLoader());
  }

  /**
   * Create a proxy instance to call smart contract corresponding to {@code type}.
   *
   * @param <ContractT> a smart contract interface type which extends {@link SmartContract}
   * @param type a proxy type
   * @param contractAddress a contract address
   * @param classLoader a class loader used in making proxy instance
   * @return a proxy instance
   */
  @SuppressWarnings("unchecked")
  public <ContractT extends SmartContract> ContractT create(final Class<ContractT> type,
      final ContractAddress contractAddress, final ClassLoader classLoader) {
    logger.debug("Create contract client type: {}, contract address: {}, class loader: {}", type,
        contractAddress, classLoader);
    final ContractInvocationHandler handler = new ContractInvocationHandler(contractAddress);
    return (ContractT) Proxy.newProxyInstance(classLoader, new Class<?>[] {type}, handler);
  }

}
