/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static java.lang.String.format;

import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.IntStream;

public class SourcePrinter implements Function<String, String> {

  /**
   * Create source with left-side linenumber.
   *
   * @param script raw script text
   *
   * @return formatted string
   */
  public String apply(final String script) {
    final String[] lines = script.split("\\r?\\n");
    final int digit = (int) (Math.log10(lines.length) + 1);

    StringJoiner joiner = new StringJoiner("\n");
    IntStream.range(0, lines.length)
        .mapToObj(lineNumber -> format("%1$" + digit + "s |", lineNumber + 1) + lines[lineNumber])
        .forEach(joiner::add);
    return joiner.toString();
  }

}
