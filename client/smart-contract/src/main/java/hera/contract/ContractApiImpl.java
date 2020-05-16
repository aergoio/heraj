/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.model.ContractAddress;
import hera.client.AergoClient;
import hera.key.Signer;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ContractApiImpl<ContractT> implements ContractApi<ContractT>, PreparedContractApi<ContractT> {

  @NonNull
  protected final ContractAddress contractAddress;

  @NonNull
  protected final ContractT proxy;

  @NonNull
  protected final ContractInvocationHandler proxyInvocationHandler;

  @Override
  public PreparedContractApi<ContractT> with(final AergoClient aergoClient) {
    assertNotNull(aergoClient, "AergoClient must not null");
    proxyInvocationHandler.prepareClient(aergoClient);
    return this;
  }

  @Override
  public ContractT execution(final Signer signer) {
    assertNotNull(signer, "Signer must not null");
    proxyInvocationHandler.prepareSigner(signer);
    return proxy;
  }

  @Override
  public ContractT query() {
    return proxy;
  }

  @Override
  public String toString() {
    return String.format("ContractApiImpl(contractAddress=%s)", contractAddress);
  }

}
