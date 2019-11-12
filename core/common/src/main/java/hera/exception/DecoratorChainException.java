/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import hera.api.function.FunctionDecorator;

/**
 * An error to keep exception in a decorator chain of {@link FunctionDecorator}.
 */
public class DecoratorChainException extends HerajException {

  private static final long serialVersionUID = 8413911651198429198L;

  /**
   * DecoratorChainException constructor.
   *
   * @param cause a cause of decorator chain. It its {@link DecoratorChainException}, keep cause of
   *        it.
   */
  public DecoratorChainException(final Throwable cause) {
    super((cause instanceof DecoratorChainException) ? cause.getCause() : cause);
  }

  /**
   * DecoratorChainException constructor.
   *
   * @param message a message
   * @param cause a cause of decorator chain. It its {@link DecoratorChainException}, keep cause of
   *        it.
   */
  public DecoratorChainException(final String message, final Throwable cause) {
    super(message, (cause instanceof DecoratorChainException) ? cause.getCause() : cause);
  }

}
