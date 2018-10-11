/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.ContractEitherOperation;
import hera.api.ContractOperation;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.util.DangerousSupplier;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class ContractTemplate implements ContractOperation {

  protected final ContractEitherOperation contractEitherOperation;

  public ContractTemplate(final ManagedChannel channel, final Context context) {
    this(newFutureStub(channel), context);
  }

  public ContractTemplate(final AergoRPCServiceFutureStub aergoService, final Context context) {
    this(new ContractEitherTemplate(aergoService, context));
  }

  @Override
  public ContractTxReceipt getReceipt(final ContractTxHash contractTxHash) {
    return contractEitherOperation.getReceipt(contractTxHash).getResult();
  }

  @Override
  public ContractTxHash deploy(final AccountAddress creator,
      final DangerousSupplier<byte[]> rawContractCode) {
    return contractEitherOperation.deploy(creator, rawContractCode).getResult();
  }

  @Override
  public ContractInterface getContractInterface(final ContractAddress contractAddress) {
    return contractEitherOperation.getContractInterface(contractAddress).getResult();
  }

  @Override
  public ContractTxHash execute(final AccountAddress executor,
      final ContractAddress contractAddress, final ContractFunction contractFunction,
      final Object... args) {
    return contractEitherOperation.execute(executor, contractAddress, contractFunction, args)
        .getResult();
  }

  @Override
  public ContractResult query(ContractAddress contractAddress, ContractFunction contractFunction,
      Object... args) {
    return contractEitherOperation.query(contractAddress, contractFunction, args).getResult();
  }

}
