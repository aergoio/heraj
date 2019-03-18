/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.grpc;

import static org.slf4j.LoggerFactory.getLogger;

import hera.exception.RpcConnectionException;
import hera.exception.RpcException;
import hera.exception.RpcExceptionConverter;
import hera.transport.ModelConverter;
import hera.util.ExceptionConverter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class GrpcStreamObserverAdaptor<RpcModelT, DomainModelT>
    implements io.grpc.stub.StreamObserver<RpcModelT> {

  protected final Logger logger = getLogger(getClass());

  protected final ExceptionConverter<RpcException> exceptionConverter = new RpcExceptionConverter();

  protected final io.grpc.Context.CancellableContext context;

  protected final hera.api.model.StreamObserver<DomainModelT> delegate;

  protected final ModelConverter<DomainModelT, RpcModelT> converter;

  @Override
  public void onNext(final RpcModelT value) {
    final DomainModelT converted = converter.convertToDomainModel(value);
    logger.info("Streaming next: {}", converted);
    delegate.onNext(converted);
  }

  @Override
  public void onError(final Throwable t) {
    final RpcException converted = exceptionConverter.convert(t);
    logger.error("Streaming failed by {}", converted.toString());
    if (converted instanceof RpcConnectionException) {
      logger.info("Stop subscription by connection error");
      context.cancel(converted);
    }
    delegate.onError(t);
  }

  @Override
  public void onCompleted() {
    logger.info("Streaming finished successfully");
    delegate.onCompleted();
  }

}
