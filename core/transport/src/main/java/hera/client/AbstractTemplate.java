/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.ContextHolder;
import hera.ContextStorage;
import hera.Requester;
import hera.exception.RpcException;
import hera.exception.RpcExceptionConverter;
import hera.util.ExceptionConverter;
import java.util.concurrent.Callable;
import org.slf4j.Logger;

abstract class AbstractTemplate {

  protected final transient Logger logger = getLogger(getClass());

  protected final Requester requester = new DecoratingRequester();

  protected final ExceptionConverter<RpcException> exceptionConverter = new RpcExceptionConverter();

  protected final ContextStorage<Context> contextStorage;

  AbstractTemplate(final ContextStorage<Context> contextStorage) {
    assertNotNull(contextStorage, "ContextStorage must not null");
    this.contextStorage = contextStorage;
  }

  protected <R> R request(final Callable<R> callable) {
    try {
      final Context current = contextStorage.get();
      logger.trace("Attach context {} to Thread {}", current, Thread.currentThread());
      ContextHolder.attach(current);
      return callable.call();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    } finally {
      ContextHolder.remove();
    }
  }

}
