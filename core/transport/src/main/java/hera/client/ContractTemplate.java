/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.ContextStorage;
import hera.api.ContractOperation;
import hera.api.model.Account;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.Fee;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.key.Signer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

class ContractTemplate extends AbstractTemplate implements ContractOperation {

  protected final ContractMethods contractMethods = new ContractMethods();

  ContractTemplate(final ContextStorage<Context> contextStorage) {
    super(contextStorage);
  }

  @Override
  public ContractTxReceipt getReceipt(final ContractTxHash contractTxHash) {
    return request(new Callable<ContractTxReceipt>() {
      @Override
      public ContractTxReceipt call() throws Exception {
        return requester.request(contractMethods
            .getTxReceipt()
            .toInvocation(Arrays.<Object>asList(contractTxHash)));
      }
    });
  }

  @Override
  public ContractTxHash deploy(final Account creator, final ContractDefinition contractDefinition,
      final long nonce) {
    throw new UnsupportedOperationException("Use Signer instead");
  }

  @Override
  public ContractTxHash deploy(final Account creator, final ContractDefinition contractDefinition,
      final long nonce, final Fee fee) {
    throw new UnsupportedOperationException("Use Signer instead");
  }

  @Override
  public ContractTxHash deploy(final Signer signer, final ContractDefinition contractDefinition,
      final long nonce, final Fee fee) {
    return request(new Callable<ContractTxHash>() {
      @Override
      public ContractTxHash call() throws Exception {
        return requester.request(contractMethods
            .getDeploy()
            .toInvocation(Arrays.asList(signer, contractDefinition, nonce, fee)));
      }
    });
  }

  @Override
  public ContractTxHash redeploy(final Signer signer, final ContractAddress existingContract,
      final ContractDefinition contractDefinition, final long nonce, final Fee fee) {
    return request(new Callable<ContractTxHash>() {
      @Override
      public ContractTxHash call() throws Exception {
        return requester.request(contractMethods
            .getReDeploy()
            .toInvocation(
                Arrays.asList(signer, existingContract, contractDefinition, nonce, fee)));
      }
    });
  }

  @Override
  public ContractInterface getContractInterface(final ContractAddress contractAddress) {
    return request(new Callable<ContractInterface>() {
      @Override
      public ContractInterface call() throws Exception {
        return requester.request(contractMethods
            .getContractInterface()
            .toInvocation(Arrays.<Object>asList(contractAddress)));
      }
    });
  }

  @Override
  public ContractTxHash execute(final Account executor, final ContractInvocation contractInvocation,
      final long nonce) {
    throw new UnsupportedOperationException("Use Signer instead");
  }

  @Override
  public ContractTxHash execute(final Account executor, final ContractInvocation contractInvocation,
      final long nonce, final Fee fee) {
    throw new UnsupportedOperationException("Use Signer instead");
  }

  @Override
  public ContractTxHash execute(final Signer signer, final ContractInvocation contractInvocation,
      final long nonce, final Fee fee) {
    return request(new Callable<ContractTxHash>() {
      @Override
      public ContractTxHash call() throws Exception {
        return requester.request(contractMethods
            .getExecute()
            .toInvocation(Arrays.asList(signer, contractInvocation, nonce, fee)));
      }
    });
  }

  @Override
  public ContractResult query(final ContractInvocation contractInvocation) {
    return request(new Callable<ContractResult>() {
      @Override
      public ContractResult call() throws Exception {
        return requester.request(contractMethods
            .getQuery()
            .toInvocation(Arrays.<Object>asList(contractInvocation)));
      }
    });
  }

  @Override
  public List<Event> listEvents(final EventFilter filter) {
    return request(new Callable<List<Event>>() {
      @Override
      public List<Event> call() throws Exception {
        return requester.request(contractMethods
            .getListEvent()
            .toInvocation(Arrays.<Object>asList(filter)));
      }
    });
  }

  @Override
  public Subscription<Event> subscribeEvent(final EventFilter filter,
      final StreamObserver<Event> observer) {
    return request(new Callable<Subscription<Event>>() {
      @Override
      public Subscription<Event> call() throws Exception {
        return requester.request(contractMethods
            .getSubscribeEvent()
            .toInvocation(Arrays.<Object>asList(filter, observer)));
      }
    });
  }

}
