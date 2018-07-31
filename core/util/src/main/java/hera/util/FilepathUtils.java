/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.StringUtils.EMPTY_STRING;
import static hera.util.StringUtils.isEmpty;

import java.io.File;
import java.util.Stack;

public class FilepathUtils {

  private static final char DIRECTORY_SEPARATOR_CHAR = '/';

  private static final String DIRECTORY_SEPARATOR = "" + DIRECTORY_SEPARATOR_CHAR;

  private static final String CURRENT_DIR = ".";
  private static final String PARENT_DIR = "..";

  /**
   * Return canonical fragments from {@code path}.
   * <p>
   *   You should read {@link File#getCanonicalPath()} if you don't know canonical form for path.
   *   This method return directory names in array.
   *   It will skip if meet current directory string(".")
   *   It will remove back if meet parent directory string("..")
   * </p>
   *
   * @param path path
   *
   * @return separated string array
   */
  public static String[] getCanonicalFragments(final String path) {
    final String safe = path.replace(File.separatorChar, DIRECTORY_SEPARATOR_CHAR);

    final Stack<String> stack = new Stack<>();

    for (final String fragment : safe.split(DIRECTORY_SEPARATOR)) {
      if (isEmpty(fragment)) {
        continue;
      } else if (CURRENT_DIR.equals(fragment)) {
        continue;
      } else if (PARENT_DIR.equals(fragment)) {
        if (stack.isEmpty()) {
          throw new IllegalArgumentException();
        }
        stack.pop();
      } else {
        stack.push(fragment);
      }
    }

    return stack.toArray(new String[stack.size()]);
  }

  /**
   * Return canonical form for {@code path}.
   * <p>
   *   Concatenate the fragments from {@link #getCanonicalFragments(String)}
   * </p>
   *
   * @param path path
   *
   * @return canonical path
   *
   * @see #getCanonicalFragments(String)
   */
  public static String getCanonicalForm(final String path) {
    final String[] fragments = getCanonicalFragments(path);
    if (0 == fragments.length) {
      return DIRECTORY_SEPARATOR;
    }
    final StringBuilder buffer = new StringBuilder();

    for (final String f : fragments) {
      buffer.append("/");
      buffer.append(f);
    }

    return buffer.toString();
  }

  /**
   * Split path to directory, file basename and extension.
   *
   * @param path path
   *
   * @return split string array
   */
  public static String[] split(final String path) {
    if (null == path) {
      return null;
    }

    final String parent = getParentPath(path);
    final String filename = getFilename(path);
    final String name = FilenameUtils.stripExtension(filename);
    final String ext = FilenameUtils.getExtension(filename);

    return new String[] {parent, name, ext};
  }

  /**
   * Return parent directory's path of {@code path}.
   * <p>
   *   Return {@code null} if {@code path} is null or parent path is unknown path.
   * </p>
   *
   * @param path file path
   * @return parent path
   */
  public static String getParentPath(final String path) {
    if (null == path) {
      return null;
    }

    if (EMPTY_STRING.equals(path) || DIRECTORY_SEPARATOR.equals(path)) {
      return null;
    }

    final int index = path.lastIndexOf(DIRECTORY_SEPARATOR);

    if (0 == index) {
      return DIRECTORY_SEPARATOR;
    } else if (index < 0) {
      return null;
    }

    return path.substring(0, index);
  }

  /**
   * Return filename from {@code path}.
   *
   * @param path file path
   *
   * @return filename
   */
  public static String getFilename(final String path) {
    if (null == path) {
      return null;
    }
    final String canonicalPath = getCanonicalForm(path);
    final int index = canonicalPath.lastIndexOf(DIRECTORY_SEPARATOR);

    return (index < 0) ? canonicalPath : canonicalPath.substring(index + 1);
  }

  /**
   * alias of {@link #append(String...)}.
   *
   * @param fragments path fragments
   *
   * @return concatenated path
   *
   * @see #append(String...)
   */
  public static String concat(final String... fragments) {

    return append(fragments);
  }

  /**
   * Append {@code fragment} and return path.
   *
   * @param fragments path fragments
   *
   * @return concatenated path
   */
  public static String append(final String... fragments) {
    return append(fragments, 0, fragments.length - 1);
  }

  /**
   * Extract sub fragments from {@code start} to {@code to} and append.
   *
   * @param fragments fragments
   * @param start start index
   * @param end end index
   *
   * @return concatenated path
   */
  public static String append(final String[] fragments, final int start, final int end) {

    final StringBuilder buffer = new StringBuilder();

    boolean isFirst = true;
    for (int i = start; i <= end; ++i) {
      final String fragment = fragments[i];
      if (null == fragment) {
        continue;
      }
      final char[] chs = fragment.toCharArray();
      int from = 0;
      int to = chs.length;

      // Remove directory separator in prefix.
      while (from < to && DIRECTORY_SEPARATOR_CHAR == chs[from]) {
        ++from;
      }

      // Remove directory separator suffix.
      while (from < to && DIRECTORY_SEPARATOR_CHAR == chs[to - 1]) {
        --to;
      }

      if (!isFirst || 0 != from) {
        // Add separator
        buffer.append(DIRECTORY_SEPARATOR_CHAR);
      }

      // append file path
      buffer.append(chs, from, to - from);

      isFirst = false;
    }

    return buffer.toString();
  }

}
