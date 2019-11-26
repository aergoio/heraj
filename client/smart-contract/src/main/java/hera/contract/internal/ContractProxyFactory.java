/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract.internal;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.ContractAddress;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.slf4j.Logger;

public class ContractProxyFactory<ContractT> {

  protected final transient Logger logger = getLogger(getClass());

  /**
   * Create a proxy instance to call smart contract corresponding to {@code type}. Class loader is
   * set by {@code getClass().getClassLoader()}.
   *
   * @param contractAddress a contract address
   * @param type a proxy type
   *
   * @return a proxy instance
   */
  public ContractT create(final ContractAddress contractAddress, final Class<ContractT> type) {
    return create(contractAddress, type, getClass().getClassLoader());
  }

  /**
   * Create a proxy instance to call smart contract corresponding to {@code type}.
   *
   * @param contractAddress a contract address
   * @param type a proxy type
   * @param classLoader a class loader used in making proxy instance
   *
   * @return a proxy instance
   */
  @SuppressWarnings("unchecked")
  public ContractT create(final ContractAddress contractAddress, final Class<ContractT> type,
      final ClassLoader classLoader) {
    logger.debug("Create contract proxy with address: {}, type: {}, classLoader: {}",
        contractAddress, type, classLoader);
    final InvocationHandler handler = new ContractInvocationHandler(contractAddress);
    final ContractT contract =
        (ContractT) Proxy.newProxyInstance(classLoader,
            new Class<?>[] {type, ContractInvocationPreparable.class}, handler);
    return contract;
  }

}
