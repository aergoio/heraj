package hera.build;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import hera.AbstractTestCase;
import hera.build.web.model.BuildDetails;
import hera.test.TestReportNode;
import hera.util.MessagePrinter;
import org.junit.Test;
import org.mockito.internal.verification.AtLeast;

public class ConsoleServerTest extends AbstractTestCase {
  @Test
  public void testProcess() {
    final BuildDetails buildDetails = new BuildDetails();
    buildDetails.getUnitTestReport().add(new TestReportNode());
    final ConsoleServer consoleServer = new ConsoleServer();
    consoleServer.setPrinter(mock(MessagePrinter.class));
    consoleServer.boot();
    try {
      consoleServer.process(buildDetails);
      verify(consoleServer.getPrinter(), new AtLeast(1)).println(any(), any());
    } finally {
      consoleServer.down();
    }
  }
}