package hera.util;

import static hera.util.StringUtils.removeSuffix;
import static hera.util.ValidationUtils.assertEquals;
import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertTrue;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import lombok.Getter;
import lombok.Setter;

public class AnsiMessagePrinter implements MessagePrinter {

  protected static final String COLOR_RESET   = "\u001B[0m";

  protected static final String COLOR_BLACK   = "\u001B[30m";
  protected static final String COLOR_RED     = "\u001B[31m";
  protected static final String COLOR_GREEN   = "\u001B[32m";
  protected static final String COLOR_YELLOW  = "\u001B[33m";
  protected static final String COLOR_BLUE    = "\u001B[34m";
  protected static final String COLOR_MAGENTA = "\u001B[35m";
  protected static final String COLOR_CYAN    = "\u001B[36m";
  protected static final String COLOR_WHITE   = "\u001B[37m";

  protected static final String COLOR_BRIGHT_BLACK    = removeSuffix(COLOR_BLACK, "m") + ";1m";
  protected static final String COLOR_BRIGHT_RED      = removeSuffix(COLOR_RED, "m") + ";1m";
  protected static final String COLOR_BRIGHT_GREEN    = removeSuffix(COLOR_GREEN, "m") + ";1m";
  protected static final String COLOR_BRIGHT_YELLOW   = removeSuffix(COLOR_YELLOW, "m") + ";1m";
  protected static final String COLOR_BRIGHT_BLUE     = removeSuffix(COLOR_BLUE, "m") + ";1m";
  protected static final String COLOR_BRIGHT_MAGENTA  = removeSuffix(COLOR_MAGENTA, "m") + ";1m";
  protected static final String COLOR_BRIGHT_CYAN     = removeSuffix(COLOR_CYAN, "m") + ";1m";
  protected static final String COLOR_BRIGHT_WHITE    = removeSuffix(COLOR_WHITE, "m") + ";1m";

  protected static final String BG_BLACK = "\u001B[40m";
  protected static final String BG_RED = "\u001B[41m";
  protected static final String BG_GREEN = "\u001B[42m";
  protected static final String BG_YELLOW = "\u001B[43m";
  protected static final String BG_BLUE = "\u001B[44m";
  protected static final String BG_MAGENTA = "\u001B[45m";
  protected static final String BG_CYAN = "\u001B[46m";
  protected static final String BG_WHITE = "\u001B[47m";

  protected static final String BG_BRIGHT_BLACK = removeSuffix(BG_BLACK, "m") + ";1m";
  protected static final String BG_BRIGHT_RED = removeSuffix(BG_RED, "m") + ";1m";
  protected static final String BG_BRIGHT_GREEN = removeSuffix(BG_GREEN, "m") + ";1m";
  protected static final String BG_BRIGHT_YELLOW = removeSuffix(BG_YELLOW, "m") + ";1m";
  protected static final String BG_BRIGHT_BLUE = removeSuffix(BG_BLUE, "m") + ";1m";
  protected static final String BG_BRIGHT_MAGENTA = removeSuffix(BG_MAGENTA, "m") + ";1m";
  protected static final String BG_BRIGHT_CYAN = removeSuffix(BG_CYAN, "m") + ";1m";
  protected static final String BG_BRIGHT_WHITE = removeSuffix(BG_WHITE, "m") + ";1m";

  @Getter
  @Setter
  protected String resetCode = COLOR_RESET;

  @Getter
  @Setter
  protected Map<String, String> colors = new HashMap<>();

  protected final PrintStream out;

  /**
   * Constructor with stream.
   *
   * @param out stream to print out
   */
  public AnsiMessagePrinter(final PrintStream out) {
    this.out = out;
    colors.put("black", COLOR_BLACK);
    colors.put("red", COLOR_RED);
    colors.put("green", COLOR_GREEN);
    colors.put("yellow", COLOR_YELLOW);
    colors.put("blue", COLOR_BLUE);
    colors.put("magenta", COLOR_MAGENTA);
    colors.put("cyan", COLOR_CYAN);
    colors.put("white", COLOR_WHITE);

    colors.put("bright_black", COLOR_BRIGHT_BLACK);
    colors.put("bright_red", COLOR_BRIGHT_RED);
    colors.put("bright_green", COLOR_BRIGHT_GREEN);
    colors.put("bright_yellow", COLOR_BRIGHT_YELLOW);
    colors.put("bright_blue", COLOR_BRIGHT_BLUE);
    colors.put("bright_magenta", COLOR_BRIGHT_MAGENTA);
    colors.put("bright_cyan", COLOR_BRIGHT_CYAN);
    colors.put("bright_white", COLOR_BRIGHT_WHITE);

    colors.put("bg_black", BG_BLACK);
    colors.put("bg_red", BG_RED);
    colors.put("bg_green", BG_GREEN);
    colors.put("bg_yellow", BG_YELLOW);
    colors.put("bg_blue", BG_BLUE);
    colors.put("bg_magenta", BG_MAGENTA);
    colors.put("bg_cyan", BG_CYAN);
    colors.put("bg_white", BG_WHITE);

    colors.put("bg_bright_black", BG_BRIGHT_BLACK);
    colors.put("bg_bright_red", BG_BRIGHT_RED);
    colors.put("bg_bright_green", BG_BRIGHT_GREEN);
    colors.put("bg_bright_yellow", BG_BRIGHT_YELLOW);
    colors.put("bg_bright_blue", BG_BRIGHT_BLUE);
    colors.put("bg_bright_magenta", BG_BRIGHT_MAGENTA);
    colors.put("bg_bright_cyan", BG_BRIGHT_CYAN);
    colors.put("bg_bright_white", BG_BRIGHT_WHITE);
  }

  protected String format(String message) {
    if (null == message) {
      return message;
    }
    final StringReader reader = new StringReader(message);
    final StringWriter writer = new StringWriter();
    final StringBuilder color = new StringBuilder();

    final Stack<String> tags = new Stack<>();
    int ch = 0;
    final int ST_NORMAL = 0;
    final int ST_ESCAPE = 1;
    final int ST_TAG_OPEN = 2;
    final int ST_OPEN_COLOR = 3;
    final int ST_CLOSE_COLOR = 4;
    final int CH_ESCAPE = '!';
    final int CH_TAG_START = '<';
    final int CH_TAG_CLOSE = '/';
    final int CH_TAG_END = '>';
    int state = ST_NORMAL;
    try {
      while (0 <= (ch = reader.read())) {
        switch (state) {
          case ST_NORMAL:
            if (CH_ESCAPE == ch) {
              state = ST_ESCAPE;
            } else if (CH_TAG_START == ch) {
              state = ST_TAG_OPEN;
            } else {
              writer.write((char) ch);
            }
            break;
          case ST_ESCAPE:
            writer.write((char) ch);
            state = ST_NORMAL;
            break;
          case ST_TAG_OPEN:
            if (ch == CH_TAG_CLOSE) {
              state = ST_CLOSE_COLOR;
            } else {
              state = ST_OPEN_COLOR;
              color.append((char) ch);
            }
            break;
          case ST_OPEN_COLOR:
            if (ch == CH_TAG_END) {
              final String colorName = color.toString();
              // check valid
              tags.push(colorName);
              color.delete(0, color.length());
              final String colorCode = colors.get(colorName);
              assertNotNull(colorCode, "Unknown color: " + colorName);
              writer.write(colorCode);
              state = ST_NORMAL;
            } else {
              color.append((char) ch);
            }
            break;
          case ST_CLOSE_COLOR:
            if (ch == CH_TAG_END) {
              final String colorName = color.toString();
              // check valid
              final String openColorName = tags.pop();
              assertEquals(openColorName, colorName,
                  "The closing tag not matched: " + openColorName + ", " + colorName);
              color.delete(0, color.length());
              if (tags.isEmpty()) {
                writer.write(resetCode);
              } else {
                writer.write(colors.get(tags.peek()));
              }
              state = ST_NORMAL;
            } else {
              color.append((char) ch);
            }
            break;

          default:
            throw new IllegalStateException();
        }
      }
    } catch (IOException ex) {
      throw new IllegalStateException();
    }
    assertEquals(ST_NORMAL, state, "Expression is invalid");
    assertTrue(tags.isEmpty(), "The closing tag missed: " + tags);
    return writer.toString();
  }

  public void print(final String format, Object...args) {
    out.print(this.format(String.format(format, args)));
  }

  public void println() {
    out.println();
  }

  public void println(final String format, Object... args) {
    out.println(this.format(String.format(format, args)));
  }

  @Override
  public void flush() {
    out.flush();
  }
}
