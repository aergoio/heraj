/*
 * @copyright defined in LICENSE.txt
 */

package hera.contract;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.Fee;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.client.TxRequestFunction;
import hera.client.TxRequester;
import hera.exception.HerajException;
import hera.key.Signer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ContractInvocationHandler implements InvocationHandler, ClientPrepareable, SignerPreparable {

  protected final transient Logger logger = getLogger(getClass());

  // fixme: no other way to keep client and signer?
  protected final ThreadLocal<AergoClient> clientCabinet = new ThreadLocal<>();
  protected final ThreadLocal<Signer> signerCabinet = new ThreadLocal<>();

  @NonNull
  protected final ContractAddress contractAddress;
  @NonNull
  protected final TxRequester txRequester;

  protected final Object lock = new Object();
  protected volatile ContractInterface cached;

  @Override
  public void prepareClient(final AergoClient aergoClient) {
    assertNotNull(aergoClient, "AergoClient must not null");
    logger.trace("Prepare client: {}", aergoClient);
    this.clientCabinet.set(aergoClient);
  }

  @Override
  public void prepareSigner(final Signer signer) {
    assertNotNull(signer, "Signer must not null");
    logger.trace("Prepare signer: {}", signer);
    this.signerCabinet.set(signer);
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args)
      throws Throwable {
    try {
      logger.debug("Proxy: {}, Method: {}, Args: {}", proxy.getClass(), method.getName(),
          (args == null) ? null : asList(args));

      final ContractInterface contractInterface = getContractInterface();
      final ContractFunction function = contractInterface.findFunction(method.getName());
      final ContractInvocation contractInvocation = contractInterface.newInvocationBuilder()
          .function(method.getName())
          .args(filterFee(args))
          .build();
      logger.debug("Generated invocation: {}", contractInvocation);

      Object ret;
      final Class<?> returnType = method.getReturnType();
      if (Void.TYPE.equals(returnType) || TxHash.class.equals(returnType)) {
        logger.debug("Return type present.. treat as contract execution");
        if (function.isView()) {
          throw new HerajException(
              "Unable to execute with function registered with abi.register_view()");
        }

        final Fee fee = parseFee(args);
        ret = txRequester.request(getClient(), getSigner(), new TxRequestFunction() {
          @Override
          public TxHash apply(final Signer signer, final Long nonce) {
            return getClient().getContractOperation()
                .executeTx(signer, contractInvocation, nonce, fee);
          }
        });
      } else {  // query
        logger.debug("Return type present.. treat as contract query");
        if (!function.isView()) {
          throw new HerajException(
              "Unable to query with function registered with abi.register()");
        }

        final ContractResult result = getClient().getContractOperation().query(contractInvocation);
        ret = result.bind(returnType);
      }
      return ret;
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    } finally {
      flushCabinet();
    }
  }

  protected ContractInterface getContractInterface() {
    if (null == this.cached) {
      synchronized (lock) {
        if (null == this.cached) {
          this.cached = getClient().getContractOperation()
              .getContractInterface(this.contractAddress);
        }
      }
    }
    return this.cached;
  }

  protected List<Object> filterFee(final Object[] args) {
    if (null == args) {
      return emptyList();
    }

    final List<Object> filtered = new ArrayList<>();
    for (final Object arg : args) {
      if (!(arg instanceof Fee)) {
        filtered.add(arg);
      }
    }
    return filtered;
  }

  protected Fee parseFee(final Object[] args) {
    if (null == args) {
      return Fee.INFINITY;
    }

    Fee fee = Fee.INFINITY;
    for (final Object arg : args) {
      if (arg instanceof Fee) {
        fee = (Fee) arg;
      }
    }
    return fee;
  }

  protected AergoClient getClient() {
    final AergoClient client = this.clientCabinet.get();
    if (null == client) {
      throw new HerajException("Prepared client is null");
    }
    return client;
  }

  protected Signer getSigner() {
    final Signer signer = this.signerCabinet.get();
    if (null == signer) {
      throw new HerajException("Prepared signer is null");
    }
    return signer;
  }

  protected void flushCabinet() {
    logger.trace("Flush prepared");
    clientCabinet.remove();
    signerCabinet.remove();
  }

}

