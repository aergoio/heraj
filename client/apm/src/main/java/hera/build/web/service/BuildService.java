/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.service;

import static java.util.stream.Collectors.toList;

import hera.build.web.model.BuildDetails;
import hera.build.web.model.BuildSummary;
import hera.util.DangerousConsumer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Named;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Named
public class BuildService extends AbstractService {
  protected final AtomicInteger sequence = new AtomicInteger();

  protected final LinkedList<String> uuids = new LinkedList<>();

  protected final Map<String, BuildDetails> uuid2buildResult = new HashMap<>();

  protected final List<DangerousConsumer<BuildSummary>> listeners = new ArrayList<>();

  public void addListener(final DangerousConsumer<BuildSummary> listener) {
    this.listeners.add(listener);
  }

  /**
   * Save build result for web request.
   *
   * @param buildDetails build result
   */
  public void save(final BuildDetails buildDetails) {
    buildDetails.setSequence(sequence.incrementAndGet());
    uuid2buildResult.put(buildDetails.getUuid(), buildDetails);
    logger.info("New build detected: {}", buildDetails);
    uuids.addFirst(buildDetails.getUuid());
    while (100 < uuids.size()) {
      final String uuid = uuids.removeLast();
      uuid2buildResult.remove(uuid);
    }
    for (final DangerousConsumer<BuildSummary> listener : listeners) {
      try {
        listener.accept(buildDetails.getSummary());
      } catch (final Throwable ex) {
        logger.trace("Ignore exception: {}", ex.getClass());
      }
    }
  }

  public Optional<BuildDetails> get(final String uuid) {
    return Optional.ofNullable(uuid2buildResult.get(uuid));
  }

  /**
   * List build summaries.
   *
   * @param from starting point
   * @param requestSize needed size
   *
   * @return build summaries
   */
  public List<BuildSummary> list(final String from, final int requestSize) {
    int fromIndex = (null == from) ? 0 : uuids.indexOf(from);
    int toIndex = Math.min(fromIndex + requestSize, uuids.size());
    if (fromIndex < 0) {
      return null;
    } else {
      return uuids.subList(fromIndex, toIndex).stream()
          .map(uuid2buildResult::get)
          .map(BuildDetails::getSummary)
          .collect(toList());
    }
  }
}