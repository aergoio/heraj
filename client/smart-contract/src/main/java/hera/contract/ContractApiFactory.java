/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.ContractAddress;
import hera.api.model.Time;
import hera.api.model.TryCountAndInterval;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.NonceRefreshingTxRequester;
import hera.client.TxRequester;
import java.lang.reflect.Proxy;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class ContractApiFactory {

  public static final TryCountAndInterval DEFAULT_TRY_COUNT_AND_INTERVAL = TryCountAndInterval
      .of(3, Time.of(100L));

  protected final transient Logger logger = getLogger(getClass());

  /**
   * Create a contract api to call smart contract corresponding to {@code type}. Class loader is set
   * by {@code getClass().getClassLoader()}.
   *
   * @param <ContractT>     a smart contract interface type
   * @param contractAddress a contract address
   * @param type            a proxy type
   * @return a proxy instance
   */
  public <ContractT> ContractApi<ContractT> create(final ContractAddress contractAddress,
      final Class<ContractT> type) {
    return create(contractAddress, type, DEFAULT_TRY_COUNT_AND_INTERVAL,
        getClass().getClassLoader());
  }

  /**
   * Create a contract api to call smart contract corresponding to {@code type}. Class loader is set
   * by {@code getClass().getClassLoader()}.
   *
   * @param <ContractT>         a smart contract interface type
   * @param contractAddress     a contract address
   * @param type                a proxy type
   * @param tryCountAndInterval a try count and interval on nonce failure
   * @return a proxy instance
   */
  public <ContractT> ContractApi<ContractT> create(final ContractAddress contractAddress,
      final Class<ContractT> type, final TryCountAndInterval tryCountAndInterval) {
    return create(contractAddress, type, tryCountAndInterval, getClass().getClassLoader());
  }

  /**
   * Create a contract api to call smart contract corresponding to {@code type}.
   *
   * @param <ContractT>     a smart contract interface type
   * @param contractAddress a contract address
   * @param type            a proxy type
   * @param classLoader     a class loader used in making proxy instance
   * @return a proxy instance
   */
  public <ContractT> ContractApi<ContractT> create(final ContractAddress contractAddress,
      final Class<ContractT> type, final ClassLoader classLoader) {
    return create(contractAddress, type, DEFAULT_TRY_COUNT_AND_INTERVAL, classLoader);
  }

  /**
   * Create a contract api to call smart contract corresponding to {@code type}.
   *
   * @param <ContractT>         a smart contract interface type
   * @param contractAddress     a contract address
   * @param type                a proxy type
   * @param tryCountAndInterval a try count and interval on nonce failure
   * @param classLoader         a class loader used in making proxy instance
   * @return a proxy instance
   */
  @SuppressWarnings("unchecked")
  public <ContractT> ContractApi<ContractT> create(final ContractAddress contractAddress,
      final Class<ContractT> type, final TryCountAndInterval tryCountAndInterval,
      final ClassLoader classLoader) {
    logger.debug("Create contract contract address: {}, interface: {}, class loader: {}",
        contractAddress, type, classLoader);
    final TxRequester txRequester = new NonceRefreshingTxRequester(tryCountAndInterval,
        new SimpleNonceProvider());
    final ContractInvocationHandler handler = new ContractInvocationHandler(contractAddress,
        txRequester);
    final ContractT contract = (ContractT) Proxy
        .newProxyInstance(classLoader, new Class<?>[]{type}, handler);
    return new ContractApiImpl<>(contract, handler);
  }

}
