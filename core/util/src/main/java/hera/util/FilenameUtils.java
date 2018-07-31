/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.StringUtils.isEmpty;
import static hera.util.StringUtils.nvl;

public class FilenameUtils {

  private static final char EXTENSION_SEPARATOR = '.';

  /**
   * Return the extension from {@code filename}.
   * <p>
   *   Extract the last fragment from {@link #EXTENSION_SEPARATOR} separated strings
   * </p>
   *
   * @param filename file name
   *
   * @return file's extension
   */
  public static String getExtension(final String filename) {
    return getExtension(filename, false);
  }

  /**
   * Return the extension part from {@code filename}.
   * <p>
   *   Extract the last fragment from {@link #EXTENSION_SEPARATOR} separated string
   *   if {@code isGreedy} is {@code false}
   *   Extract from hte second fragment to last if {@code isGreedy} is {@code true}
   * </p>
   *
   * @param filename file name
   * @param isGreedy  flag if greedy
   *
   * @return extension
   */
  public static String getExtension(final String filename, final boolean isGreedy) {
    if (null == filename) {
      return null;
    }
    final int dotIndex = isGreedy ? filename.indexOf(EXTENSION_SEPARATOR)
        : filename.lastIndexOf(EXTENSION_SEPARATOR);
    return (dotIndex < 0) ? "" : filename.substring(dotIndex + 1);
  }

  /**
   * Remove extension from {@code filename}.
   * <p>
   *   Use {@link #EXTENSION_SEPARATOR} as separator
   * </p>
   *
   * @param filename filename
   * @return extension removed string
   */
  public static String stripExtension(final String filename) {
    if (null == filename) {
      return null;
    }
    final int index = filename.lastIndexOf(EXTENSION_SEPARATOR);
    return (index < 0) ? filename : filename.substring(0, index);
  }

  /**
   * Concatenate file basename and extension.
   *
   * @param name file's base name
   * @param ext  file's extension
   *
   * @return filename
   */
  public static String makeFilename(final String name, final String ext) {
    if (null == name || null == ext) {
      return null;
    }
    if (isEmpty(ext)) {
      return name;
    }
    final StringBuilder buffer = new StringBuilder();
    if (!isEmpty(name)) {
      buffer.append(nvl(name));
    }
    buffer.append('.');
    buffer.append(ext);

    return buffer.toString();
  }
}
