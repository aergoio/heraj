/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.custom.Adaptor;
import hera.custom.AdaptorManager;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class AbstractAsyncAergoApi implements AergoAsyncApi, Adaptor {

  protected final transient Logger logger = getLogger(getClass());

  @Override
  public AccountAsyncOperation getAccountAsyncOperation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public KeyStoreAsyncOperation getKeyStoreAsyncOperation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BlockAsyncOperation getBlockAsyncOperation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BlockchainAsyncOperation getBlockchainAsyncOperation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public TransactionAsyncOperation getTransactionAsyncOperation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ContractAsyncOperation getContractAsyncOperation() {
    throw new UnsupportedOperationException();
  }

  public <AdapteeT> List<? extends AdapteeT> getCandidates(final Class<AdapteeT> candidateClass) {
    return AdaptorManager.getInstance().getAdaptors(candidateClass);
  }

  @Override
  public <OperationT> Optional<OperationT> adapt(final Class<OperationT> operationClass) {
    final List<? extends OperationT> candidates = getCandidates(operationClass);
    if (candidates.isEmpty()) {
      return empty();
    }
    if (1 < candidates.size()) {
      throw new IllegalStateException();
    }
    final OperationT op = candidates.get(0);
    logger.debug("Custom operation: {}", op);
    return ofNullable(op);
  }

}
