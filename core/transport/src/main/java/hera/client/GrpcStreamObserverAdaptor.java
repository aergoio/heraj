/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.slf4j.LoggerFactory.getLogger;

import hera.exception.ConnectionException;
import hera.exception.HerajException;
import hera.exception.TransportExceptionConverter;
import hera.transport.ModelConverter;
import hera.util.ExceptionConverter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class GrpcStreamObserverAdaptor<RpcModelT, DomainModelT>
    implements io.grpc.stub.StreamObserver<RpcModelT> {

  protected final transient Logger logger = getLogger(getClass());

  protected final ExceptionConverter<HerajException> exceptionConverter =
      new TransportExceptionConverter();

  protected final io.grpc.Context.CancellableContext context;

  protected final hera.api.model.StreamObserver<DomainModelT> delegate;

  protected final ModelConverter<DomainModelT, RpcModelT> converter;

  @Override
  public void onNext(final RpcModelT value) {
    final DomainModelT converted = converter.convertToDomainModel(value);
    logger.debug("Streaming next: {}", converted);
    delegate.onNext(converted);
  }

  @Override
  public void onError(final Throwable t) {
    final HerajException converted = exceptionConverter.convert(t);
    logger.error("Streaming failed by {}", converted.toString());
    if (converted instanceof ConnectionException) {
      logger.debug("Stop subscription by connection error");
      context.cancel(converted);
    }
    delegate.onError(t);
  }

  @Override
  public void onCompleted() {
    logger.debug("Streaming finished successfully");
    delegate.onCompleted();
  }

}
