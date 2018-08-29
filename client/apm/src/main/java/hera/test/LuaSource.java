/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import static hera.util.ValidationUtils.assertTrue;
import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

import hera.util.IntRange;
import java.util.StringJoiner;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;

public class LuaSource {
  protected final transient Logger logger = getLogger(getClass());

  protected static String[] splitToLines(final String script) {
    return script.split("\\r?\\n");
  }

  /*
  protected static String[] select(String[] lines, final int from, final int to) {
    assertTrue(from < to);
    final IntRange range = new IntRange(0, lines.length).select(new IntRange(from, to));
    final String[] part = new String[range.v2 - range.v1];
    System.arraycopy(lines, range.v1, part, 0, part.length);
    return part;
  }*/

  @RequiredArgsConstructor
  @ToString
  class SourceLine {

    @Getter
    protected final int linenumber;
    @Getter
    protected final String text;
  }

  @Getter
  protected final String script;

  @Getter
  protected final SourceLine[] lines;

  /**
   * Constructor with raw script content.
   *
   * @param script raw text
   */
  public LuaSource(final String script) {
    this.script = script;
    final String[] lines = splitToLines(script);
    this.lines = new SourceLine[lines.length];
    for (int i = 0, n = lines.length; i < n; ++i) {
      this.lines[i] = new SourceLine(i + 1, lines[i]);
      logger.trace("Line: {}", this.lines[i]);
    }
  }

  /**
   * Create source with left-side linenumber.
   *
   * @return formatted string
   */
  @Override
  public String toString() {
    return toString(0, lines.length);
  }

  /**
   * Create source with left-side linenumber.
   *
   * @param from start line number
   * @param to end line number
   * @return formatted string
   */
  public String toString(final int from, final int to) {
    logger.debug("{} ~ {}", from, to);
    final int digit = (int) (Math.log10(lines.length) + 1);

    assertTrue(from < to);
    final IntRange range = new IntRange(0, lines.length).select(new IntRange(from, to));
    logger.trace("{}", range);

    StringJoiner joiner = new StringJoiner("\n");
    IntStream.range(range.v1, range.v2)
        .mapToObj(lineNumber -> format("%1$" + digit + "s |", lines[lineNumber].getLinenumber())
            + lines[lineNumber].getText())
        .forEach(joiner::add);
    return joiner.toString();
  }
}
