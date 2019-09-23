/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.function;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import org.junit.Test;

public class FunctionsTest extends AbstractTestCase {

  @Test
  public void testInvoke() {
    final int[] expected = {
        randomUUID().hashCode(),
        randomUUID().hashCode(),
        randomUUID().hashCode(),
        randomUUID().hashCode(),
        randomUUID().hashCode(),
        randomUUID().hashCode(),
    };

    final Function<?>[] functions = {
        new Function0<Object>() {
          @Override
          public Object apply() {
            return expected[0];
          }
        },
        new Function1<Object, Object>() {
          @Override
          public Object apply(Object t1) {
            return expected[1];
          }
        },
        new Function2<Object, Object, Object>() {
          @Override
          public Object apply(Object t1, Object t2) {
            return expected[2];
          }
        },
        new Function3<Object, Object, Object, Object>() {
          @Override
          public Object apply(Object t1, Object t2, Object t3) {
            return expected[3];
          }
        },
        new Function4<Object, Object, Object, Object, Object>() {
          @Override
          public Object apply(Object t1, Object t2, Object t3, Object t4) {
            return expected[4];
          }
        },
        new Function5<Object, Object, Object, Object, Object, Object>() {
          @Override
          public Object apply(Object t1, Object t2, Object t3, Object t4, Object t5) {
            return expected[5];
          }
        },
    };

    for (int i = 0; i < functions.length; ++i) {
      final Function<?> f = functions[i];
      final Object[] args = new Object[i];
      final Object actual = Functions.invoke(f, asList(args));
      assertEquals(expected[i], actual);
    }
  }

  @Test
  public void testCompose0() {
    final String ret = randomUUID().toString();
    Function0<String> f = new Function0<String>() {
      @Override
      public String apply() {
        return ret;
      }
    };
    Function1<String, Integer> s = new Function1<String, Integer>() {
      @Override
      public Integer apply(final String t) {
        return t.length();
      }
    };
    Function0<Integer> composed = Functions.compose(f, s);

    final Integer expected = ret.length();
    assertEquals(expected, composed.apply());
  }

  @Test
  public void testCompose1() {
    final String ret = randomUUID().toString();
    Function1<String, String> f = new Function1<String, String>() {
      @Override
      public String apply(final String t) {
        return ret;
      }
    };
    Function1<String, Integer> s = new Function1<String, Integer>() {
      @Override
      public Integer apply(final String t) {
        return t.length();
      }
    };
    Function1<String, Integer> composed = Functions.compose(f, s);

    final Integer expected = ret.length();
    assertEquals(expected, composed.apply(randomUUID().toString()));
  }

  @Test
  public void testCompose2() {
    final String ret = randomUUID().toString();
    Function2<String, String, String> f = new Function2<String, String, String>() {
      @Override
      public String apply(final String t1, final String t2) {
        return ret;
      }
    };
    Function1<String, Integer> s = new Function1<String, Integer>() {
      @Override
      public Integer apply(final String t) {
        return t.length();
      }
    };
    Function2<String, String, Integer> composed = Functions.compose(f, s);

    final Integer expected = ret.length();
    assertEquals(expected, composed.apply(randomUUID().toString(), randomUUID().toString()));
  }

  @Test
  public void testCompose3() {
    final String ret = randomUUID().toString();
    Function3<String, String, String, String> f = new Function3<String, String, String, String>() {
      @Override
      public String apply(final String t1, final String t2, final String t3) {
        return ret;
      }
    };
    Function1<String, Integer> s = new Function1<String, Integer>() {
      @Override
      public Integer apply(final String t) {
        return t.length();
      }
    };
    Function3<String, String, String, Integer> composed = Functions.compose(f, s);

    final Integer expected = ret.length();
    assertEquals(expected,
        composed.apply(randomUUID().toString(), randomUUID().toString(), randomUUID().toString()));
  }

  @Test
  public void testCompose4() {
    final String ret = randomUUID().toString();
    Function4<String, String, String, String, String> f =
        new Function4<String, String, String, String, String>() {
          @Override
          public String apply(final String t1, final String t2, final String t3, final String t4) {
            return ret;
          }
        };
    Function1<String, Integer> s = new Function1<String, Integer>() {
      @Override
      public Integer apply(final String t) {
        return t.length();
      }
    };
    Function4<String, String, String, String, Integer> composed = Functions.compose(f, s);

    final Integer expected = ret.length();
    assertEquals(expected, composed.apply(randomUUID().toString(), randomUUID().toString(),
        randomUUID().toString(), randomUUID().toString()));
  }

}
