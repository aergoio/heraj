/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.UUID.randomUUID;

import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class FutureChainTest extends AbstractTestCase {

  @Test
  public void testOnSuccess() {
    ResultOrErrorFuture<Integer> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();
    FutureChain<String, Integer> callback = new FutureChain<>(nextFuture, String::length);
    callback.onSuccess(randomUUID().toString());
    nextFuture.get().getResult();
  }

  @Test(expected = Exception.class)
  public void testOnFailure() {
    ResultOrErrorFuture<Integer> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();
    FutureChain<String, Integer> callback = new FutureChain<>(nextFuture, String::length);
    callback.onFailure(new UnsupportedOperationException());
    nextFuture.get().getResult();
  }
}
