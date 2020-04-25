/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.emptyList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ApiAudience.Private
@ApiStability.Unstable
public abstract class RequestMethod<T> {

  /**
   * Get name of method.
   *
   * @return an name
   * @throws UnsupportedOperationException if name is not defined
   */
  public String getName() {
    throw new UnsupportedOperationException("Name is not defined");
  }

  /**
   * Invoke current method.
   *
   * @return a return value
   * @throws Exception on invocation failure
   */
  public T invoke() throws Exception {
    return invoke(emptyList());
  }

  /**
   * Invoke current method with {@code parameters}.
   *
   * @param parameters parameters used in invocation
   * @return a return value
   * @throws Exception on invocation failure
   */
  public T invoke(final List<Object> parameters) throws Exception {
    validate(parameters);
    return runInternal(parameters);
  }

  /**
   * Validte parameters. Default behavior does nothing. Override it to add validation logic.
   *
   * @param parameters parameters used in invocation
   */
  protected void validate(final List<Object> parameters) {
    // default : do nothing
  }

  /**
   * Check if {@code parameters} at {@code index} is {@code clazz} type.
   *
   * @param parameters parameters used in invocation
   * @param index      an index
   * @param clazz      a class to check
   * @throws IllegalArgumentException if type not matches
   */
  protected void validateType(final List<Object> parameters, final int index,
      final Class<?> clazz) {
    if (parameters.size() <= index) {
      throw new IllegalArgumentException(
          String.format("No parameter at index %d (expected: %s)%n", index, clazz));
    }

    final Object parameter = parameters.get(index);
    if (!clazz.isInstance(parameter)) {
      throw new IllegalArgumentException(String
          .format("Parameter at index %d is invalid (expected: %s, actual: %s)%n", index, clazz,
              parameter.getClass()));
    }
  }

  /**
   * Check it condition is true.
   *
   * @param condition a condition
   * @param message   a message on failure
   * @throws IllegalArgumentException if condition is false
   */
  protected void validateValue(final boolean condition, final String message) {
    if (!condition) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Request method implementation.
   *
   * @param parameters parameters used in invocation
   * @return a return value of invocation
   * @throws Exception on invocation failure
   */
  protected abstract T runInternal(final List<Object> parameters) throws Exception;

  /**
   * Convert to invocation.
   *
   * @return an invocation
   */
  public Invocation<T> toInvocation() {
    return toInvocation(emptyList());
  }

  /**
   * Convert to invocation with {@code parameters}.
   *
   * @param parameters parameters used in invocation
   * @return an invocation
   */
  public Invocation<T> toInvocation(final List<Object> parameters) {
    assertNotNull(parameters);
    return new PlainInvocation<>(this, parameters);
  }

  @Override
  public String toString() {
    return "RequestMethod(name=" + getName() + ")";
  }

  @Override
  public boolean equals(final Object obj) {
    if (null == obj) {
      return false;
    }
    if (!(obj instanceof RequestMethod)) {
      return false;
    }
    return ((RequestMethod<?>) obj).getName().equals(getName());
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @ToString
  @EqualsAndHashCode
  private static class PlainInvocation<T> implements Invocation<T> {

    @Getter
    protected final RequestMethod<T> requestMethod;

    @Getter
    protected final List<Object> parameters;

    @Override
    public T invoke() throws Exception {
      return requestMethod.invoke(parameters);
    }

    @Override
    public Invocation<T> withParameters(final List<Object> parameters) {
      assertNotNull(parameters, "Parameters must not null");
      return new PlainInvocation<>(requestMethod, parameters);
    }
  }

}
