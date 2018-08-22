/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import hera.server.ServerEvent;
import hera.server.ThreadServer;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class FileWatcher extends ThreadServer implements Runnable {

  /**
   * Reset.
   */
  public static final int RESET = 0x11;

  /**
   * File add event type.
   */
  public static final int FILE_ADDED = 0x12;

  /**
   * File remove event type.
   */
  public static final int FILE_REMOVED = 0x13;

  /**
   * File modification event type.
   */
  public static final int FILE_CHANGED = 0x14;

  protected final WatchService watchService;

  protected final Path basePath;

  protected final Map<WatchKey, Path> key2path = new HashMap<>();

  /**
   * Constructor with watch service and base path.
   *
   * @param watchService watch service
   * @param basePath path to monitor
   * @throws IOException Fail to register monitor using nio api
   */
  public FileWatcher(final WatchService watchService, final Path basePath) throws IOException {
    this.watchService = watchService;
    this.basePath = basePath;
    logger.info("Watch {}", basePath);

    Files.walkFileTree(basePath, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult preVisitDirectory(
          final Path dir, final BasicFileAttributes attrs) throws IOException {
        attach(dir);
        return CONTINUE;
      }
    });
  }

  protected void attach(final Path dir) throws IOException {
    final WatchKey watchKey =
        dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW);
    key2path.put(watchKey, dir.toAbsolutePath());
  }

  protected void detach(final Path dir) throws IOException {
    key2path.keySet().stream()
        .filter(key -> dir.equals(key2path.get(key)))
        .forEach(key2path::remove);
  }

  @Override
  protected void process() throws Exception {
    final WatchKey key = watchService.take();
    if (null == key) {
      return;
    }

    for (final WatchEvent<?> event : key.pollEvents()) {
      logger.trace("Event: {}", event);
      ServerEvent serverEvent = null;

      Path path = null;
      int eventType = -1;
      final Kind kind = event.kind();
      if (ENTRY_CREATE.equals(kind)) {
        eventType = FILE_ADDED;
        final Path parent = key2path.get(key);
        path = parent.resolve((Path) event.context());
        if (Files.isDirectory(path)) {
          attach(path);
        }
      } else if (ENTRY_MODIFY.equals(kind)) {
        eventType = FILE_CHANGED;
        final Path parent = key2path.get(key);
        path = parent.resolve((Path) event.context());
      } else if (ENTRY_DELETE.equals(kind)) {
        eventType = FILE_REMOVED;
        final Path parent = key2path.get(key);
        path = parent.resolve((Path) event.context());
        if (Files.isDirectory(path)) {
          detach(path);
        }
      } else if (OVERFLOW.equals(kind)) {
        eventType = RESET;
      } else {
        throw new IllegalStateException("Unknown kind: " + kind);
      }
      serverEvent = new ServerEvent(this, eventType, null, path);
      fireEvent(serverEvent);
    }
    key.reset();
  }
}
