/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class FilenameUtilsTest {

  @Test
  public void testGetExtensionOnFilename() {
    final Object[][] testParameters = new Object[][] {{"HelloWorld.png", "png"},
        {"Hello.World.jpg", "jpg"}, {"Hello_World.gif", "gif"}};

    assertNull(FilenameUtils.getExtension(null));

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[1];
      String filename = (String) testParameter[0];
      assertEquals(expected, FilenameUtils.getExtension(filename));
    }
  }

  @Test
  public void testGetExtensionOnFilenameAndIsGreedy() {
    final Object[][] testParameters =
        new Object[][] {{"HelloWorld.png", false, "png"}, {"HelloWorld.jpg", true, "jpg"},
            {"Hello.World.gif", false, "gif"}, {"Hello.World.bmp", true, "World.bmp"}};

    assertNull(FilenameUtils.getExtension(null, false));
    assertNull(FilenameUtils.getExtension(null, true));

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[2];
      String filename = (String) testParameter[0];
      boolean isGreedy = (boolean) testParameter[1];
      assertEquals(expected, FilenameUtils.getExtension(filename, isGreedy));
    }
  }

  @Test
  public void testStripExtension() {
    final Object[][] testParameters =
        new Object[][] {{"HelloWorld.png", "HelloWorld"}, {"Hello.World.jpg", "Hello.World"}};

    assertNull(FilenameUtils.stripExtension(null));

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[1];
      String filename = (String) testParameter[0];
      assertEquals(expected, FilenameUtils.stripExtension(filename));
    }
  }

  @Test
  public void testMakeFilename() {
    final Object[][] testParameters = new Object[][] {{"HelloWorld", "", "HelloWorld"},
        {"HelloWorld", " ", "HelloWorld"}, {"HelloWorld", "\n", "HelloWorld"}, {"", "png", ".png"},
        {" ", "jpg", ".jpg"}, {"\n", "gif", ".gif"}, {"HelloWorld", "cgm", "HelloWorld.cgm"},
        {"Hello.World", "svg", "Hello.World.svg"}};

    assertNull(FilenameUtils.makeFilename(null, "png"));
    assertNull(FilenameUtils.makeFilename("HelloWorld", null));

    for (final Object[] testParameter : testParameters) {
      String expected = (String) testParameter[2];
      String name = (String) testParameter[0];
      String ext = (String) testParameter[1];
      assertEquals(expected, FilenameUtils.makeFilename(name, ext));
    }
  }
}
