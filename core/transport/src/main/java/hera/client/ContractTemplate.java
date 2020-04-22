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
import hera.api.model.TxHash;
import hera.key.Signer;
import java.util.Arrays;
import java.util.List;

class ContractTemplate extends AbstractTemplate implements ContractOperation {

  protected final ContractMethods contractMethods = new ContractMethods();

  ContractTemplate(final ContextStorage<Context> contextStorage) {
    super(contextStorage);
  }

  @Override
  public ContractTxReceipt getReceipt(final ContractTxHash contractTxHash) {
    return getContractTxReceipt(contractTxHash.adapt(TxHash.class));
  }

  @Override
  public ContractTxReceipt getContractTxReceipt(final TxHash txHash) {
    return request(contractMethods.getContractTxReceipt(), Arrays.<Object>asList(txHash));
  }

  @Override
  public ContractTxHash deploy(final Account creator, final ContractDefinition contractDefinition,
      final long nonce) {
    return deploy(creator.getKey(), contractDefinition, nonce, Fee.ZERO);
  }

  @Override
  public ContractTxHash deploy(final Account creator, final ContractDefinition contractDefinition,
      final long nonce, final Fee fee) {
    return deploy(creator.getKey(), contractDefinition, nonce, fee);
  }

  @Override
  public ContractTxHash deploy(final Signer signer, final ContractDefinition contractDefinition,
      final long nonce, final Fee fee) {
    return deployTx(signer, contractDefinition, nonce, fee).adapt(ContractTxHash.class);
  }

  @Override
  public ContractTxHash deployTx(final Signer signer, final ContractDefinition contractDefinition,
      final long nonce, final Fee fee) {
    return request(contractMethods.getDeployTx(),
        Arrays.<Object>asList(signer, contractDefinition, nonce, fee));
  }

  @Override
  public ContractTxHash redeploy(final Signer signer, final ContractAddress existingContract,
      final ContractDefinition contractDefinition, final long nonce, final Fee fee) {
    return redeployTx(signer, existingContract, contractDefinition, nonce, fee)
        .adapt(ContractTxHash.class);
  }

  @Override
  public ContractTxHash redeployTx(final Signer signer, final ContractAddress existingContract,
      final ContractDefinition contractDefinition, final long nonce, final Fee fee) {
    return request(contractMethods.getRedeployTx(),
        Arrays.asList(signer, existingContract, contractDefinition, nonce, fee));
  }

  @Override
  public ContractInterface getContractInterface(final ContractAddress contractAddress) {
    return request(contractMethods.getContractInterface(), Arrays.<Object>asList(contractAddress));
  }

  @Override
  public ContractTxHash execute(final Account executor, final ContractInvocation contractInvocation,
      final long nonce) {
    return execute(executor.getKey(), contractInvocation, nonce, Fee.ZERO);
  }

  @Override
  public ContractTxHash execute(final Account executor, final ContractInvocation contractInvocation,
      final long nonce, final Fee fee) {
    return execute(executor.getKey(), contractInvocation, nonce, fee);
  }

  @Override
  public ContractTxHash execute(final Signer signer, final ContractInvocation contractInvocation,
      final long nonce, final Fee fee) {
    return executeTx(signer, contractInvocation, nonce, fee).adapt(ContractTxHash.class);
  }

  @Override
  public ContractTxHash executeTx(final Signer signer, final ContractInvocation contractInvocation,
      final long nonce, final Fee fee) {
    return request(contractMethods.getExecuteTx(),
        Arrays.asList(signer, contractInvocation, nonce, fee));
  }

  @Override
  public ContractResult query(final ContractInvocation contractInvocation) {
    return request(contractMethods.getQuery(), Arrays.<Object>asList(contractInvocation));
  }

  @Override
  public List<Event> listEvents(final EventFilter filter) {
    return request(contractMethods.getListEvent(), Arrays.<Object>asList(filter));
  }

  @Override
  public Subscription<Event> subscribeEvent(final EventFilter filter,
      final StreamObserver<Event> observer) {
    return request(contractMethods.getSubscribeEvent(), Arrays.asList(filter, observer));
  }

}
