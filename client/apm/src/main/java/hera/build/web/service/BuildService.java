/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.service;

import static java.util.stream.Collectors.toList;

import hera.build.web.model.BuildDetails;
import hera.build.web.model.BuildSummary;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

public class BuildService extends AbstractService {

  @Getter
  protected LiveUpdateService liveUpdateService = new LiveUpdateService();

  protected final List<String> uuids = new ArrayList<>();

  protected final Map<String, BuildDetails> uuid2buildResult = new HashMap<>();

  /**
   * Save build result for web request.
   *
   * @param buildDetails build result
   */
  public void save(final BuildDetails buildDetails) {
    uuid2buildResult.put(buildDetails.getUuid(), buildDetails);
    logger.info("New build detected: {}", buildDetails);
    uuids.add(0, buildDetails.getUuid());
    try {
      liveUpdateService.notifyChange(buildDetails.getSummary());
    } catch (final Throwable ex) {
      logger.trace("Ignore exception: {}", ex.getClass());
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