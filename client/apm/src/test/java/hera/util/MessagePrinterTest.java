package hera.util;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import hera.AbstractTestCase;
import java.io.PrintStream;
import java.util.HashMap;
import org.junit.Test;

public class MessagePrinterTest extends AbstractTestCase {

  @Test
  public void testFormat() {
    final String resetCode = randomUUID().toString();
    final String blue = randomUUID().toString();
    final HashMap<String, String> colors = new HashMap<>();
    colors.put("blue", blue);
    final MessagePrinter printer = new MessagePrinter(mock(PrintStream.class));
    printer.setResetCode(resetCode);
    printer.setColors(colors);
    final String encoded = printer.format("<blue>hello, world</blue>");
    assertTrue(encoded.startsWith(blue));
    assertTrue(encoded.contains("hello"));
    assertTrue(encoded.contains("world"));
    assertTrue(encoded.endsWith(resetCode));
  }

}