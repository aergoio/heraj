package hera.api.tupleorerror;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface GetNotThrowingFuture<V> extends Future<V> {

  V get();

  V get(long timeout, TimeUnit unit);

}
