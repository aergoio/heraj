/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_VALUE_CHAIN_ID_HASH_HOLDER;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.Context;
import hera.ContextHolder;
import hera.EmptyContext;
import hera.FailoverHandler;
import hera.Invocation;
import hera.RequestMethod;
import hera.Response;
import hera.api.model.BlockchainStatus;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.exception.CommitException;
import java.util.concurrent.Callable;
import org.junit.Test;

public class InvalidChainIdHashHandlerTest extends AbstractTestCase {

  @Test
  public void testHandleWithNextRequestSuccess() throws Throwable {
    runOnOtherThread(new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        try {
          // given
          final Context context = EmptyContext.getInstance()
              .withValue(GRPC_VALUE_CHAIN_ID_HASH_HOLDER, new ChainIdHashHolder());
          ContextHolder.attach(context);

          final InvalidChainIdHashHandler failoverHandler = new InvalidChainIdHashHandler();
          final ChainIdHash chainIdHash = ChainIdHash.of(BytesValue.EMPTY);
          final BlockchainMethods mockBlockchainMethods = mock(BlockchainMethods.class);
          final RequestMethod<BlockchainStatus> blockchainStatusRequestMethod = TestRequestMethod
              .success(BlockchainStatus.newBuilder().chainIdHash(chainIdHash).build());
          when(mockBlockchainMethods.getBlockchainStatus())
              .thenReturn(blockchainStatusRequestMethod);
          failoverHandler.blockchainMethods = mockBlockchainMethods;

          // then
          final Object expected = randomUUID().toString();
          final Invocation<Object> invocation = new TestInvocation<>(
              TestRequestMethod.success(expected));
          final Exception error = new CommitException(types.Rpc.CommitStatus.TX_INTERNAL_ERROR,
              "invalid chain id hash");
          final Response<Object> response = Response.fail(error);
          final Response<Object> handled = failoverHandler.handle(invocation, response);
          assertEquals(expected, handled.getValue());
          return null;
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void testHandleWithNextRequestFailure() throws Throwable {
    runOnOtherThread(new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        try {
          // given
          final Context context = EmptyContext.getInstance()
              .withValue(GRPC_VALUE_CHAIN_ID_HASH_HOLDER, new ChainIdHashHolder());
          ContextHolder.attach(context);

          final InvalidChainIdHashHandler failoverHandler = new InvalidChainIdHashHandler();
          final ChainIdHash chainIdHash = ChainIdHash.of(BytesValue.EMPTY);
          final BlockchainMethods mockBlockchainMethods = mock(BlockchainMethods.class);
          final RequestMethod<BlockchainStatus> blockchainStatusRequestMethod = TestRequestMethod
              .success(BlockchainStatus.newBuilder().chainIdHash(chainIdHash).build());
          when(mockBlockchainMethods.getBlockchainStatus())
              .thenReturn(blockchainStatusRequestMethod);
          failoverHandler.blockchainMethods = mockBlockchainMethods;

          // then
          final Exception expected = new UnsupportedOperationException();
          final Invocation<Object> invocation = new TestInvocation<>(
              TestRequestMethod.fail(expected));
          final Exception error = new CommitException(types.Rpc.CommitStatus.TX_INTERNAL_ERROR,
              "invalid chain id hash");
          final Response<Object> response = Response.fail(error);
          final Response<Object> handled = failoverHandler.handle(invocation, response);
          assertEquals(expected, handled.getError());
          return null;
        } finally {
          ContextHolder.remove();
        }
      }
    });
  }

  @Test
  public void shouldNotHandleOnNoError() {
    final FailoverHandler failoverHandler = new InvalidChainIdHashHandler();
    final String expected = randomUUID().toString();
    final Response<String> response = Response.success(expected);
    final Response<String> handled = failoverHandler.handle(null, response);
    assertEquals(expected, handled.getValue());
  }

  @Test
  public void shouldNotHandleOnNoCommitException() {
    final FailoverHandler failoverHandler = new InvalidChainIdHashHandler();
    final Exception expected = new UnsupportedOperationException();
    final Response<Object> response = Response.fail(expected);
    final Response<Object> handled = failoverHandler.handle(null, response);
    assertEquals(expected, handled.getError());
  }

}
