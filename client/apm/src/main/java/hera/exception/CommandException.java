package hera.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandException extends RuntimeException {
  @Getter
  protected final String userMessage;

  public CommandException(final Throwable cause) {
    super(cause);
    this.userMessage = null;
  }

  public CommandException(final String userMessage, final String developerMessage) {
    super(developerMessage);
    this.userMessage = userMessage;
  }

  public CommandException(final String userMessage, final Throwable cause) {
    super(cause);
    this.userMessage = userMessage;
  }

}
