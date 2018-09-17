/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;

import hera.custom.Adaptor;
import hera.custom.AdaptorManager;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;

public class AbstractAergoApi implements AergoApi, Adaptor {

  protected final transient Logger logger = getLogger(getClass());

  public SignOperation getSignOperation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public AccountOperation getAccountOperation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BlockOperation getBlockOperation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BlockChainOperation getBlockChainOperation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public TransactionOperation getTransactionOperation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ContractOperation getContractOperation() {
    throw new UnsupportedOperationException();
  }

  public <AdapteeT> List<? extends AdapteeT> getCandidates(
      final Class<AdapteeT> candidateClass) {
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
