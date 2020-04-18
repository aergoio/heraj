/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.ContextHolder;
import hera.ContextStorage;
import hera.RequestMethod;
import hera.Requester;
import hera.exception.RpcException;
import hera.exception.RpcExceptionConverter;
import hera.util.ExceptionConverter;
import java.util.List;
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

  protected <R> R request(final RequestMethod<R> requestMethod) {
    return request(requestMethod, emptyList());
  }

  protected <R> R request(final RequestMethod<R> requestMethod, final List<Object> parameters) {
    try {
      final Context context = contextStorage.get()
          .withScope(requestMethod.getName());
      logger.trace("Attach context {} to Thread {}", context, Thread.currentThread());
      ContextHolder.attach(context);
      return requester.request(requestMethod.toInvocation(parameters));
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    } finally {
      ContextHolder.remove();
    }
  }

}
