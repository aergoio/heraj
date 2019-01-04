/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.custom.Adaptor;
import hera.custom.AdaptorManager;
import java.util.List;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class AbstractEitherAergoApi implements AergoEitherApi, Adaptor {

  protected final transient Logger logger = getLogger(getClass());

  @Override
  public AccountEitherOperation getAccountEitherOperation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public KeyStoreEitherOperation getKeyStoreEitherOperation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BlockEitherOperation getBlockEitherOperation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BlockchainEitherOperation getBlockchainEitherOperation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public TransactionEitherOperation getTransactionEitherOperation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ContractEitherOperation getContractEitherOperation() {
    throw new UnsupportedOperationException();
  }

  public <AdapteeT> List<? extends AdapteeT> getCandidates(final Class<AdapteeT> candidateClass) {
    return AdaptorManager.getInstance().getAdaptors(candidateClass);
  }

  @Override
  public <OperationT> OperationT adapt(final Class<OperationT> operationClass) {
    final List<? extends OperationT> candidates = getCandidates(operationClass);
    if (candidates.isEmpty()) {
      return null;
    }
    if (1 < candidates.size()) {
      throw new IllegalStateException();
    }
    final OperationT op = candidates.get(0);
    logger.debug("Custom operation: {}", op);
    return op;
  }

}
