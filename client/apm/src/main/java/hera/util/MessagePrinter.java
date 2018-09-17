package hera.util;

import java.io.Flushable;

public interface MessagePrinter extends Flushable {
  void print(final String format, Object...args);

  void println();

  void println(final String format, Object... args);
}
