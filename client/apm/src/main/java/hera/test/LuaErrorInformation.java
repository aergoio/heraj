/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import lombok.Getter;

public class LuaErrorInformation {

  @Getter
  protected final String message;

  @Getter
  protected final int lineNumber;

  @Getter
  protected final int columnNumber;

  /**
   * Constructor with not parsed error message.
   * <p>
   * error message format is next:
   * script:{linenumber} {message}
   * </p>
   *
   * @param errorMessage not parsed error message
   */
  public LuaErrorInformation(final String errorMessage) {
    final int messageIndex = errorMessage.indexOf(" ");
    message = errorMessage.substring(messageIndex + 1);
    final int colonIndex = errorMessage.indexOf(":");
    final String lineNumberStr = errorMessage.substring(colonIndex + 1, messageIndex).trim();
    lineNumber = Integer.parseInt(lineNumberStr);
    columnNumber = -1;
  }

  /**
   * Constructor with parsed error message.
   *
   * @param message       error message
   * @param lineNumber    line number
   * @param columnNumber  column number
   */
  public LuaErrorInformation(final String message, final int lineNumber, final int columnNumber) {
    this.message = message;
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
  }

  @Override
  public String toString() {
    return lineNumber + ":" + columnNumber + " - Message: " + message;
  }
}
