/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.grpc;

import static org.slf4j.LoggerFactory.getLogger;

import hera.transport.ModelConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class GrpcStreamObserverAdaptor<RpcModelT, DomainModelT>
    implements io.grpc.stub.StreamObserver<RpcModelT> {

  protected final Logger logger = getLogger(getClass());

  @Getter
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
    logger.error("Streaming failed by {}", t.toString());
    delegate.onError(t);
  }

  @Override
  public void onCompleted() {
    logger.info("Streaming finished successfully");
    delegate.onCompleted();
  }

}
