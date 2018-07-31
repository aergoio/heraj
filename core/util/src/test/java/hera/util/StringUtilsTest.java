/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;

public class StringUtilsTest {
  
  @Test
  public void testIsEmpty() {
    final Object[][] testParameters =
        new Object[][] {{"", true}, {" ", true}, {"\n", true}, {"hello, world", false}};

    for (final Object[] testParameter : testParameters) {
      boolean expected = (boolean) testParameter[1];
      CharSequence str = (CharSequence) testParameter[0];
      assertEquals(expected, StringUtils.isEmpty(str));
    }
  }

  @Test
  public void testLength() {
    final Object[][] testParameters = new Object[][] {{"", 0}, {" ", 1}, {"hello, world", 12}};

    for (final Object[] testParameter : testParameters) {
      int expected = (int) testParameter[1];
      CharSequence str = (CharSequence) testParameter[0];
      assertEquals(expected, StringUtils.length(str));
    }
  }

  @Test
  public void testHasLength() {
    final Object[][] testParameters =
        new Object[][] {{"", false}, {" ", true}, {"\n", true}, {"hello, world", true}};

    for (final Object[] testParameter : testParameters) {
      boolean expected = (boolean) testParameter[1];
      CharSequence str = (CharSequence) testParameter[0];
      assertEquals(expected, StringUtils.hasLength(str));
    }
  }

  @Test
  public void testHasText() {
    final Object[][] testParameters =
        new Object[][] {{"", false}, {" ", false}, {"\n", false}, {"hello, world", true}};

    for (final Object[] testParameter : testParameters) {
      boolean expected = (boolean) testParameter[1];
      CharSequence str = (CharSequence) testParameter[0];
      assertEquals(expected, StringUtils.hasText(str));
    }
  }

  @Test
  public void testNvl() {
    assertEquals("", StringUtils.nvl(null, null));

    for (int i = 0; i < 100; ++i) {
      assertNotNull(StringUtils.nvl(randomUUID().toString(), randomUUID().toString()));
    }
    for (int i = 0; i < 100; ++i) {
      assertNotNull(StringUtils.nvl(randomUUID().toString(), randomUUID().toString(),
          randomUUID().toString()));
    }
  }

  @Test
  public void testSplitOnList() {
    final Object[][] testParameters =
        new Object[][] {{"2018-06-12", "-", Arrays.asList("2018", "06", "12")},
            {"2019.07.14", ".", Arrays.asList("2019", "07", "14")},
            {"2020/08/16", "/", Arrays.asList("2020", "08", "16")}};

    assertNull(StringUtils.split(null, null));
    assertNull(StringUtils.split("2021_09_18", null));
    assertNull(StringUtils.split(null, "_"));

    for (final Object[] testParameter : testParameters) {
      @SuppressWarnings("unchecked")
      List<String> expected = (List<String>) testParameter[2];
      String string = (String) testParameter[0];
      String delimiter = (String) testParameter[1];

      for (int i = 0; i < StringUtils.split(string, delimiter).size(); ++i) {
        assertEquals(expected.get(i), StringUtils.split(string, delimiter).get(i));
      }
    }
  }

  @Test
  public void testSplitOnString() {
    final Object[][] testParameters = new Object[][] {{"2018-06-12", "-", 0, "2018"},
        {"2019.07.14", ".", 1, "07"}, {"2020/08/16", "/", 2, "16"}};

    assertNull(StringUtils.split(null, null, -1));
    assertNull(StringUtils.split(null, null, 0));
    assertNull(StringUtils.split(null, null, 3));
    assertNull(StringUtils.split("2021_09_18", null, -1));
    assertNull(StringUtils.split("2021_09_18", null, 0));
    assertNull(StringUtils.split("2021_09_18", null, 3));
    assertNull(StringUtils.split(null, "_", -1));
    assertNull(StringUtils.split(null, "_", 0));
    assertNull(StringUtils.split(null, "_", 3));
    assertNull(StringUtils.split("2021_09_18", "_", -1));
    assertNull(StringUtils.split("2021_09_18", "_", 3));

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[3];
      String string = (String) testParameter[0];
      String delimiter = (String) testParameter[1];
      int index = (int) testParameter[2];
      assertEquals(expected, StringUtils.split(string, delimiter, index));
    }
  }

  @Test
  public void testLtrim() {
    final Object[][] testParameters =
        new Object[][] {{"", ""}, {"   hello, world", "hello, world"}};

    assertEquals("", StringUtils.ltrim(null));

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[1];
      String str = (String) testParameter[0];
      assertEquals(expected, StringUtils.ltrim(str));
    }
  }

  @Test
  public void testRtrim() {
    final Object[][] testParameters =
        new Object[][] {{"", ""}, {"hello, world   ", "hello, world"}};

    assertEquals("", StringUtils.rtrim(null));

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[1];
      String str = (String) testParameter[0];
      assertEquals(expected, StringUtils.rtrim(str));
    }
  }

  @Test
  public void testTrim() {
    final Object[][] testParameters =
        new Object[][] {{"", ""}, {"   hello, world   ", "hello, world"}};

    assertEquals("", StringUtils.trim(null));

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[1];
      String str = (String) testParameter[0];
      assertEquals(expected, StringUtils.trim(str));
    }
  }

  @Test
  public void testJoinOnIterable() {
    final Object[][] testParameters =
        new Object[][] {{Arrays.asList("Hello", "World"), "-", "Hello-World"},
            {new HashSet<String>(Arrays.asList("A", "B", "C")), ".", "A.B.C"}};

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[2];
      @SuppressWarnings("unchecked")
      Iterable<String> values = (Iterable<String>) testParameter[0];
      String token = (String) testParameter[1];
      assertEquals(expected, StringUtils.join(values, token));
    }
  }

  @Test
  public void testJoinOnObject() {
    final Object[][] testParameters =
        new Object[][] {{new String[] {"Hello", "World"}, "-", "Hello-World"},
            {new String[] {"A", "B", "C"}, ".", "A.B.C"}};

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[2];
      String[] values = (String[]) testParameter[0];
      String token = (String) testParameter[1];
      assertEquals(expected, StringUtils.join(values, token));
    }
  }

  @Test
  public void testMultiplyOnString() {
    final Object[][] testParameters =
        new Object[][] {{"Hello", 0, ""}, {"Hello", 1, "Hello"}, {"Hello", 2, "HelloHello"}};

    assertEquals("", StringUtils.multiply(null, -1));
    assertEquals("", StringUtils.multiply(null, 0));
    assertEquals("", StringUtils.multiply(null, 1));

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[2];
      String word = (String) testParameter[0];
      int repeat = (int) testParameter[1];
      assertEquals(expected, StringUtils.multiply(word, repeat));
    }
  }

  @Test
  public void testMultiplyOnVoid() {
    final Object[][] testParameters = new Object[][] {{new StringBuilder(), "Hello", 0, ""},
        {new StringBuilder(), "Hello", 1, "Hello"},
        {new StringBuilder(), "Hello", 2, "HelloHello"}};

    StringBuilder sb = new StringBuilder();
    StringUtils.multiply(sb, null, -1);
    assertEquals("", sb.toString());
    sb = new StringBuilder();
    StringUtils.multiply(sb, null, 0);
    assertEquals("", sb.toString());
    sb = new StringBuilder();
    StringUtils.multiply(sb, null, 1);
    assertEquals("", sb.toString());

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[3];
      StringBuilder buffer = (StringBuilder) testParameter[0];
      String word = (String) testParameter[1];
      int repeat = (int) testParameter[2];
      StringUtils.multiply(buffer, word, repeat);
      assertEquals(expected, buffer.toString());
    }
  }

  @Test
  public void testRepeat() {
    final Object[][] testParameters =
        new Object[][] {{"Hello", 0, ""}, {"Hello", 1, "Hello"}, {"Hello", 2, "HelloHello"}};

    assertEquals("", StringUtils.repeat(null, -1));
    assertEquals("", StringUtils.repeat(null, 0));
    assertEquals("", StringUtils.repeat(null, 1));

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[2];
      String word = (String) testParameter[0];
      int repeat = (int) testParameter[1];
      assertEquals(expected, StringUtils.repeat(word, repeat));
    }
  }

  @Test
  public void testUncapitalize() {
    final Object[][] testParameters = new Object[][] {{"hello", "hello"}, {"Hello", "hello"}};

    assertNull(StringUtils.uncapitalize(null));

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[1];
      String str = (String) testParameter[0];
      assertEquals(expected, StringUtils.uncapitalize(str));
    }
  }

  @Test
  public void testCountMatches() {
    final Object[][] testParameters = new Object[][] {{"hello, world", 'h', 1},
        {"hello, world", 'o', 2}, {"hello, world", 'l', 3}};

    assertEquals(0, StringUtils.countMatches(null, 'a'));
    assertEquals(0, StringUtils.countMatches("", 'a'));
    assertEquals(0, StringUtils.countMatches(" ", 'a'));
    assertEquals(0, StringUtils.countMatches("\n", 'a'));

    for (final Object[] testParameter : testParameters) {
      int expected = (int) testParameter[2];
      String str = (String) testParameter[0];
      char ch = (char) testParameter[1];
      assertEquals(expected, StringUtils.countMatches(str, ch));
    }
  }

  @Test
  public void shouldBeTrueOnNull() {
    assertTrue(StringUtils.isEmpty(null));
  }

  @Test
  public void shouldBeFalseOnNull() {
    assertFalse(StringUtils.hasLength(null));
    assertFalse(StringUtils.hasText(null));
  }

  @Test
  public void shouldBeZeroOnNull() {
    assertEquals(0, StringUtils.length(null));
  }
}
