/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

  protected static final String NULL_STRING = "<<null>>";

  protected static final String EMPTY_STRING = "";



  /* Inspection */

  /**
   * Return true if <code>str</code> is either null or meaningless string. Otherwise return false.
   *
   * @param str string to inspect
   * @return whether a string is meaningless or not
   * @see StringUtils#hasText(CharSequence)
   */
  public static boolean isEmpty(final CharSequence str) {
    if (null == str) {
      return true;
    }

    for (int i = 0, n = str.length(); i < n; ++i) {
      if (Character.isWhitespace(str.charAt(i))) {
        continue;
      }
      return false;
    }
    return true;
  }

  /**
   * <p>
   * Get length of <code>str</code>. If <code>str</code> is <code>null</code>, then return 0.
   * </p>
   *
   * @param str string to get length
   * @return a length of <code>str</code>
   */
  public static int length(final CharSequence str) {
    if (null == str) {
      return 0;
    }
    return str.length();
  }

  /**
   * Return true if <code>str</code> is neither <code>null</code> nor empty. Otherwise, return
   * false.
   *
   * @param str string to inspect
   * @return whether string has a length or not
   */
  public static boolean hasLength(final CharSequence str) {
    return 0 < length(str);
  }

  /**
   * Check whether <code>str</code> is a meaningful string or not.
   *
   * @param str string to inspect
   * @return whether str is meanful or not
   * @see StringUtils#isEmpty(CharSequence)
   */
  public static boolean hasText(final CharSequence str) {
    return !isEmpty(str);
  }

  /* Conversion */

  /**
   * Return an <code>non-null</code> string by inspecting array <code>strs</code>. If
   * <code>strs</code> is null, return empty string <code>""</code>.
   *
   * @param strs variable argument of string
   * @return converted string
   */
  public static String nvl(final String... strs) {
    String val = ObjectUtils.nvl(strs);
    if (null == val) {
      return EMPTY_STRING;
    }
    return val;
  }

  /**
   * Split {@code string} with a {@code delimiter}.
   *
   * @param string string to split
   * @param delimiter a delimiter
   * @return list of string splitted by a delimiter
   */
  public static List<String> split(final String string, final String delimiter) {
    if (null == string || null == delimiter) {
      return null;
    }
    final ArrayList<String> words = new ArrayList<>();

    int from = 0;
    int to = 0;
    int wordLength = delimiter.length();

    while (0 <= (to = string.indexOf(delimiter, from))) {
      words.add(string.substring(from, to));
      from = to + wordLength;
    }

    if (from < string.length()) {
      words.add(string.substring(from));
    }

    return words;
  }

  /**
   * Split {@code string} with a {@code delimiter} and return {@code index}-th string.
   *
   * @param string string to split
   * @param delimiter a delimiter
   * @param index string index
   * @return index-th string splitted by a delimiter
   */
  public static String split(final String string, final String delimiter, final int index) {
    List<String> words = split(string, delimiter);
    if (words != null && 0 <= index && index < words.size()) {
      return words.get(index);
    }

    return null;
  }

  /**
   * Remove space in left of the <code>str</code>.
   *
   * @param str target string
   * @return string with left space removed
   */
  public static String ltrim(final String str) {
    if (str == null) {
      return EMPTY_STRING;
    }

    if (!hasLength(str)) {
      return str;
    }

    final char[] chs = str.toCharArray();
    for (int i = 0, n = chs.length; i < n; ++i) {
      if (!Character.isWhitespace(chs[i])) {
        return new String(chs, i, str.length() - i);
      }
    }
    return EMPTY_STRING;
  }

  /**
   * Remove space in right of the <code>str</code>.
   *
   * @param str target string
   * @return string with right space removed
   */
  public static String rtrim(final String str) {
    if (str == null) {
      return EMPTY_STRING;
    }

    if (!hasLength(str)) {
      return str;
    }

    final char[] chs = str.toCharArray();
    for (int i = chs.length - 1, j = chs.length; 0 < j; --i, --j) {
      if (!Character.isWhitespace(chs[i])) {
        return new String(chs, 0, j);
      }
    }
    return EMPTY_STRING;
  }


  /**
   * Remove space on both sides of <code>str</code>.
   *
   * @param str target string
   * @return string with both sides of space removed
   */
  public static String trim(final String str) {
    return rtrim(ltrim(str));
  }

  /**
   * <p>
   * Concat {@code values} to {@code token}. If {@code values} is empty, return an empty string.
   * </p>
   *
   * @param values {@link Iterable} which contain strings
   * @param token concat target
   * @return concatted string
   */
  public static String join(final Iterable<?> values, final String token) {
    boolean init = false;

    final StringBuilder buffer = new StringBuilder();

    for (final Object value : values) {
      if (init) {
        buffer.append(token);
      }
      init = true;
      buffer.append(value);
    }

    return buffer.toString();
  }

  /**
   * <p>
   * Concat {@code values} to {@code token}. If {@code values} is empty, return an empty string.
   * </p>
   *
   * @param values {@link Object} which contain strings
   * @param token concat target
   * @return concatenated string
   */
  public static String join(final Object[] values, final String token) {
    return join(asList(values), token);
  }

  /**
   * Make a string by repeating <code>symbol</code> <code>n</code> times.
   *
   * @param word word to repeat
   * @param repeat repeat times
   * @return generated string
   */
  public static String multiply(final String word, final int repeat) {
    if (null == word) {
      return EMPTY_STRING;
    }

    final StringBuilder buffer = new StringBuilder();
    multiply(buffer, word, repeat);
    return buffer.toString();
  }

  /**
   * Make a string by repeating <code>symbol</code> <code>n</code> times.
   *
   * @param buffer {@link StringBuilder} to append word
   * @param word word to repeat
   * @param repeat repeat times
   */
  public static void multiply(final StringBuilder buffer, final String word, final int repeat) {
    if (null == word) {
      return;
    }

    for (int i = 0; i < repeat; ++i) {
      buffer.append(word);
    }
  }

  /**
   * Alias of {@link #multiply(String, int)}.
   *
   * @param word word to repeat
   * @param repeat repeat times
   * @return generated string
   * @see #multiply(String, int)
   */
  public static String repeat(final String word, final int repeat) {
    return multiply(word, repeat);
  }

  /**
   * Set 1st character to lower case.
   *
   * @param str target string
   * @return string with 1st character in lower case
   */
  public static String uncapitalize(final String str) {
    if (null == str) {
      return null;
    }

    return Character.toLowerCase(str.charAt(0)) + str.substring(1);
  }

  /**
   * Return count of matching character in <code>str</code>.
   * 
   * @param str target string
   * @param ch character to match
   * @return matching count
   */
  public static int countMatches(final String str, final char ch) {
    if (isEmpty(str)) {
      return 0;
    }
    int count = 0;
    for (int i = 0; i < str.length(); i++) {
      if (ch == str.charAt(i)) {
        count++;
      }
    }
    return count;
  }

}
