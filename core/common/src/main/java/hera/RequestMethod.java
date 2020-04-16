/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.emptyList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.exception.HerajException;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
public abstract class RequestMethod<T> {

  public String getName() {
    throw new UnsupportedOperationException("Name isn't defined");
  }

  public T invoke() throws Exception {
    return invoke(emptyList());
  }

  public T invoke(final List<Object> parameters) throws Exception {
    validate(parameters);
    return runInternal(parameters);
  }

  protected void validate(final List<Object> parameters) {
    // default : do nothing
  }

  protected void validateType(final List<Object> parameters, final int index,
      final Class<?> clazz) {
    if (parameters.size() <= index) {
      throw new HerajException(
          String.format("No parameter at index %d (expected: %s)%n", index, clazz));
    }

    final Object parameter = parameters.get(index);
    if (!clazz.isInstance(parameter)) {
      throw new HerajException(String
          .format("Parameter at index %d is not %s (expected: %s)%n", index, clazz,
              parameter.getClass()));
    }
  }

  protected void validateValue(final boolean condition, final String message) {
    if (!condition) {
      throw new HerajException(message);
    }
  }

  protected abstract T runInternal(final List<Object> parameters) throws Exception;

  public Invocation<T> toInvocation() {
    return toInvocation(emptyList());
  }

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

  @RequiredArgsConstructor
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
