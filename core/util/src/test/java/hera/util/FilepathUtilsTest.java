/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class FilepathUtilsTest {

  @Test
  public void testGetCanonicalFragments() {
    final Object[][] testParameters =
        new Object[][] {{"/Users/tester", new String[] {"Users", "tester"}},
            {"/Users/admin/systems", new String[] {"Users", "admin", "systems"}},
            {"./tests/./subtest1", new String[] {"tests", "subtest1"}},
            {"././tests/../subtest2", new String[] {"subtest2"}}};

    for (final Object[] testParameter : testParameters) {
      String[] expected = (String[]) testParameter[1];
      String path = (String) testParameter[0];

      for (int i = 0; i < expected.length; ++i) {
        assertEquals(expected[i], FilepathUtils.getCanonicalFragments(path)[i]);
      }
    }
  }

  @Test
  public void testGetCanonicalForm() {
    final Object[][] testParameters = new Object[][] {
        {"/Users/tester", "/Users/tester"},
        {"/Users/admin/systems", "/Users/admin/systems"},
        {"./tests/./subtest1", "/tests/subtest1"},
        {"././tests/../subtest2", "/subtest2"}};

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[1];
      String path = (String) testParameter[0];
      assertEquals(expected, FilepathUtils.getCanonicalForm(path));
    }
  }

  @Test
  public void testSplit() {
    final Object[][] testParameters =
        new Object[][] {{"/Pictures/hello.jpg", new String[] {"/Pictures", "hello", "jpg"}},
            {"/tester/Desktop/study/blockchain.pdf",
                new String[] {"/tester/Desktop/study", "blockchain", "pdf"}}};

    assertNull(FilepathUtils.split(null));

    for (final Object[] testParameter : testParameters) {
      String[] expected = (String[]) testParameter[1];
      String path = (String) testParameter[0];

      for (int i = 0; i < expected.length; ++i) {
        assertEquals(expected[i], FilepathUtils.split(path)[i]);
      }
    }
  }

  @Test
  public void testGetParentPath() {
    final Object[][] testParameters = new Object[][] {{"/tester", "/"}, {"/Users/tester", "/Users"},
        {"/Users/admin/systems", "/Users/admin"}};

    assertNull(FilepathUtils.getParentPath(null));
    assertNull(FilepathUtils.getParentPath(""));
    assertNull(FilepathUtils.getParentPath("/"));
    assertNull(FilepathUtils.getParentPath("  "));

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[1];
      String path = (String) testParameter[0];
      assertEquals(expected, FilepathUtils.getParentPath(path));
    }
  }

  @Test
  public void testGetFilename() {
    final Object[][] testParameters =
        new Object[][] {{"/tester.jpg", "tester.jpg"}, {"/Users/admin/admin.jpg", "admin.jpg"},
            {"/Users/tester/alphatester/../../admin.jpg", "admin.jpg"},
            {"/Users/tester/betatester/../../tester.jpg", "tester.jpg"}};

    assertNull(FilepathUtils.getFilename(null));

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[1];
      String path = (String) testParameter[0];
      assertEquals(expected, FilepathUtils.getFilename(path));
    }
  }

  @Test
  public void testConcat() {
    final Object[][] testParameters = new Object[][] {{new String[] {"/Users"}, "/Users"},
        {new String[] {"/Users", "/tester"}, "/Users/tester"},
        {new String[] {"/Users", "/admin", "/systems"}, "/Users/admin/systems"},
        {new String[] {"/Users", "/admin", "/systems", "/subsystem"},
            "/Users/admin/systems/subsystem"}};

    try {
      FilepathUtils.concat((String[]) null);
    } catch (Exception e) {
      assertSame(NullPointerException.class, e.getClass());
    }

    for (Object[] testParameter : testParameters) {
      String expected = (String) testParameter[1];
      String[] fragments = (String[]) testParameter[0];
      assertEquals(expected, FilepathUtils.concat(fragments));
    }
  }

  @Test
  public void testAppend() {
    final Object[][] testParameters = new Object[][] {{new String[] {"/Users"}, "/Users"},
        {new String[] {"/Users", "/tester"}, "/Users/tester"},
        {new String[] {"/Users", "/admin", "/systems"}, "/Users/admin/systems"},
        {new String[] {"/Users", "/admin", "/systems", "/subsystem"},
            "/Users/admin/systems/subsystem"}};

    try {
      FilepathUtils.append((String[]) null);
    } catch (Exception e) {
      assertSame(NullPointerException.class, e.getClass());
    }

    for (Object[] testParameter : testParameters) {
      String expected = (String) testParameter[1];
      String[] fragments = (String[]) testParameter[0];
      assertEquals(expected, FilepathUtils.append(fragments));
    }
  }

  @Test
  public void testAppendWithStartAndEnd() {
    final Object[][] testParameters = new Object[][] {{new String[] {"/Users"}, 0, 0, "/Users"},
        {new String[] {"/Users", "/tester"}, 0, 1, "/Users/tester"},
        {new String[] {"/Users", "/admin", "/systems"}, 0, 2, "/Users/admin/systems"},
        {new String[] {"/Users", "/admin", "/systems"}, 1, 2, "/admin/systems"},
        {new String[] {"/Users", "/admin", "/systems"}, 2, 2, "/systems"}};

    assertEquals("", FilepathUtils.append(null, null, null));

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[3];
      String[] fragments = (String[]) testParameter[0];
      int start = (int) testParameter[1];
      int end = (int) testParameter[2];
      assertEquals(expected, FilepathUtils.append(fragments, start, end));
    }
  }
}
