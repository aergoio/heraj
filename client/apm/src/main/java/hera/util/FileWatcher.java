/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.ArrayUtils.isEmpty;
import static hera.util.ObjectUtils.nvl;
import static hera.util.ThreadUtils.trySleep;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toList;

import hera.server.ServerEvent;
import hera.server.ThreadServer;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

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

  /**
   * Any change event type.
   */
  public static final int ANY_CHANGED = 0x18;

  /**
   * Interval to check.
   */
  @Getter
  @Setter
  protected long interval = 300;

  /**
   * File's last changed time.
   */
  protected HashMap<File, Long> base2lastModified = new HashMap<>();

  protected Set<File> previouslyChecked = new HashSet<>();

  protected final File base;

  protected Set<String> ignores = new HashSet<>();

  /**
   * Constructor with watch service and base path.
   *
   * @param basePath path to monitor
   */
  public FileWatcher(final File basePath) {
    this.base = basePath;
  }

  public void addIgnore(final String name) {
    ignores.add(name);
  }

  @Override
  protected void process() throws Exception {
    trySleep(getInterval());

    logger.trace("Base: {}", base);
    final Long lastModifiedInCache = nvl(this.base2lastModified.get(base), Long.valueOf(0));
    long max = 0;

    logger.trace("Base's last modified - Cache: {}", lastModifiedInCache);

    final HashSet<File> checkedFiles = new HashSet<>();
    final HashSet<File> changed = new HashSet<>();
    final Queue<File> files = new LinkedList<>();
    files.add(base);

    while (!files.isEmpty()) {
      final File file = files.remove();
      logger.trace("File: {}", file);
      final long lastModifiedInFile = file.lastModified();
      max = Math.max(lastModifiedInFile, max);

      if (lastModifiedInCache < lastModifiedInFile) {
        changed.add(file);
      }
      checkedFiles.add(file);
      final File[] children = file.listFiles();

      if (!isEmpty(children)) {
        logger.trace("Find {} files", children.length);
        files.addAll(stream(children)
            .filter(child -> !ignores.contains(child.getName())).collect(toList()));
      }
    }
    logger.debug("Changed: {}", changed);

    HashSet<File> intersect = (HashSet<File>) checkedFiles.clone();
    intersect.retainAll(previouslyChecked);

    logger.debug("Intersection: {}", intersect);

    final HashSet<File> added = new HashSet<>(checkedFiles);
    added.removeAll(intersect);
    final HashSet<File> removed = new HashSet<>(previouslyChecked);
    removed.removeAll(intersect);

    base2lastModified.put(base, max);
    previouslyChecked = checkedFiles;

    if (!added.isEmpty()) {
      logger.info("{} added", added);
      fireEvent(new ServerEvent(this, FILE_ADDED, unmodifiableCollection(added)));
    }
    if (!removed.isEmpty()) {
      logger.info("{} removed", removed);
      fireEvent(new ServerEvent(this, FILE_REMOVED, unmodifiableCollection(removed)));
    }
    if (!changed.isEmpty()) {
      logger.info("{} changed", changed);
      fireEvent(new ServerEvent(this, FILE_CHANGED, unmodifiableCollection(changed)));
    }

    final Set<File> any = new HashSet<>();
    any.addAll(added);
    any.addAll(removed);
    any.addAll(changed);
    if (!any.isEmpty()) {
      fireEvent(new ServerEvent(this, ANY_CHANGED, unmodifiableCollection(any)));
    }
  }
}
