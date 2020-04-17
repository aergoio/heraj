/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;

public class RequestMethodTest extends AbstractTestCase {

  @Test
  public void testInvoke() throws Exception {
    final RequestMethod<?> requestMethod = spy(RequestMethod.class);
    final String expected = randomUUID().toString();
    when(requestMethod.runInternal(anyList())).thenReturn(expected);
    final Object actual = requestMethod.invoke();
    assertEquals(expected, actual);
  }

  @Test
  public void testValidateType() throws Exception {
    final List<Object> parameters = Arrays.<Object>asList("string", 1, new LinkedList<>());
    final List<Class<?>> types = Arrays.<Class<?>>asList(String.class, Integer.class,
        LinkedList.class);
    final RequestMethod<?> requestMethod = spy(RequestMethod.class);
    for (int i = 0; i < parameters.size(); ++i) {
      requestMethod.validateType(parameters, i, types.get(i));
    }
  }

  @Test
  public void shouldValidateTypeThrowExceptionOnInvalidType() throws Exception {
    // given
    final List<Object> parameters = Arrays.<Object>asList("string", 1, new LinkedList<>());
    final RequestMethod<?> requestMethod = spy(RequestMethod.class);

    try {
      // when
      requestMethod.validateType(parameters, 4, String.class);
      fail("Should throw IllegalArgumentException on illegal index");
    } catch (IllegalArgumentException e) {
      // then
    }

    try {
      // when
      requestMethod.validateType(parameters, 0, Integer.class);
      fail("Should throw IllegalArgumentException on illegal type");
    } catch (IllegalArgumentException e) {
      // then
    }
  }

  @Test
  public void testValidateValue() throws Exception {
    final RequestMethod<?> requestMethod = spy(RequestMethod.class);
    requestMethod.validateValue(true, randomUUID().toString());
  }

  @Test
  public void shouldValidateValueThrowErrorOnFalseCondition() throws Exception {
    final RequestMethod<?> requestMethod = spy(RequestMethod.class);
    final String expected = randomUUID().toString();
    try {
      requestMethod.validateValue(false, expected);
      fail("Should throw IllegalArgumentException on false condition");
    } catch (IllegalArgumentException e) {
      assertEquals(expected, e.getMessage());
      // then
    }
  }

  @Test
  public void testToInvocation() throws Exception {
    final RequestMethod<?> requestMethod = spy(RequestMethod.class);
    final String expected = randomUUID().toString();
    when(requestMethod.runInternal(anyList())).thenReturn(expected);
    final Invocation<?> invocation = requestMethod.toInvocation();
    final Object actual = invocation.invoke();
    assertEquals(expected, actual);
  }

}
