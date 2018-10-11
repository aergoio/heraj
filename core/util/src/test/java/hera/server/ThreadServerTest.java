package hera.server;

import static hera.util.ThreadUtils.trySleep;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

import hera.AbstractTestCase;
import org.junit.Test;
import org.mockito.internal.verification.AtLeast;

public class ThreadServerTest extends AbstractTestCase {
  @Test
  public void testBootAndDown() {
    final ThreadServer server = new ThreadServer();
    server.boot(true);
    trySleep(100);
    server.down(true);
    assertNull(server.getException());
  }

  @Test
  public void testServerListener() {
    final ServerListener serverListener = mock(ServerListener.class);

    final ThreadServer server = new ThreadServer();
    server.addServerListener(serverListener);
    server.boot(true);
    server.down(true);
    verify(serverListener, new AtLeast(1)).handle(any());
  }

}