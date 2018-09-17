package hera.util;

import java.io.IOException;

public class DummyMessagePrinter implements MessagePrinter {
  public static MessagePrinter getInstance() {
    return new DummyMessagePrinter();
  }

  @Override
  public void print(String format, Object... args) {
  }

  @Override
  public void println() {
  }

  @Override
  public void println(String format, Object... args) {
  }

  @Override
  public void flush() throws IOException {
  }
}
