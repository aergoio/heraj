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

  protected ClassLoader classLoader = getClass().getClassLoader();

  /**
   * Set class loader used in making proxy instance.
   * 
   * @param classLoader a class loader
   * @return an instance of this
   */
  public SmartContractFactory withClassLoader(final ClassLoader classLoader) {
    this.classLoader = classLoader;
    return this;
  }

  /**
   * Create a proxy instance to call smart contract corresponding to {@code type}.
   *
   * @param <T> a smart contract interface type
   * @param type a proxy type
   * @param contractAddress a contract address
   * @return a proxy instance
   */
  @SuppressWarnings("unchecked")
  public <T> T create(final Class<T> type, final ContractAddress contractAddress) {
    logger.debug("Create contract client type: {}, contract address: {}", type, contractAddress);
    final ContractInvocationHandler handler = new ContractInvocationHandler(contractAddress);
    return (T) Proxy.newProxyInstance(this.classLoader, new Class<?>[] {type}, handler);
  }

}
