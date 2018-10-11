package hera.server;

import static hera.util.ThreadUtils.trySleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    server.boot();
    trySleep(1000);
    server.down();
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

  @Test
  public void shouldFailOnInitializationError() {
    final ThreadServer server = new ThreadServer() {
      @Override
      protected void initialize() throws Exception {
        throw new IllegalArgumentException();
      }
    };
    server.boot(true);
    server.down(true);
    assertNotNull(server.getException());
  }

  @Test
  public void shouldFailOnProcessError() {
    final ThreadServer server = new ThreadServer() {
      @Override
      protected void process() throws Exception {
        throw new IllegalArgumentException();
      }
    };
    server.boot(true);
    trySleep(100);
    assertNotNull(server.getException());
    assertEquals(ServerStatus.TERMINATED, server.getStatus());
  }

}