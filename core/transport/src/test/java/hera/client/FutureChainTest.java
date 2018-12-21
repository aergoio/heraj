/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.tupleorerror.FunctionChain.of;
import static java.util.UUID.randomUUID;

import hera.AbstractTestCase;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.RpcException;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class FutureChainTest extends AbstractTestCase {

  @Test
  public void testOnSuccess() {
    ResultOrErrorFuture<Integer> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();
    FutureChain<String, Integer> callback = new FutureChain<>(nextFuture, context);
    callback.setSuccessHandler(s -> of(() -> s.length()));
    callback.onSuccess(randomUUID().toString());
    nextFuture.get().getResult();
  }

  @Test(expected = RpcException.class)
  public void testOnSuccessWithoutHandler() {
    ResultOrErrorFuture<Integer> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();
    FutureChain<String, Integer> callback = new FutureChain<>(nextFuture, context);
    callback.onSuccess(randomUUID().toString());
    nextFuture.get().getResult();
  }

  @Test(expected = Exception.class)
  public void testOnFailure() {
    ResultOrErrorFuture<Integer> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();
    FutureChain<String, Integer> callback = new FutureChain<>(nextFuture, context);
    callback.setSuccessHandler(s -> of(() -> s.length()));
    callback.onFailure(new UnsupportedOperationException());
    nextFuture.get().getResult();
  }

  @Test(expected = NullPointerException.class)
  public void testCreation() {
    new FutureChain<>(null, null);
  }

}
