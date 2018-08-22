/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.service;

import static hera.util.FilepathUtils.getCanonicalFragments;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import com.fasterxml.jackson.databind.ObjectMapper;
import hera.BuildResult;
import hera.build.web.exception.HttpException;
import hera.build.web.exception.ResourceNotFoundException;
import hera.build.web.model.BuildSummary;
import hera.util.HexUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;

public class BuildService extends AbstractService {
  protected final List<String> uuids = new ArrayList<>();
  protected final Map<String, BuildResult> uuid2buildResult = new HashMap<>();

  public void save(final BuildResult buildResult) {
    uuid2buildResult.put(buildResult.getUuid(), buildResult);
    uuids.add(buildResult.getUuid());
  }

  public Optional<BuildResult> get(final String uuid) {
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
          .map(BuildSummary::new)
          .collect(toList());
    }
  }

  @Override
  public void handle(
      final String target, final Request baseRequest,
      final HttpServletRequest request, final HttpServletResponse response)
      throws IOException, ServletException {

    logger.trace("Target: {}, Base request: {}, Request: {}, Response: {}",
        target, baseRequest, request, response);

    final String[] fragments = getCanonicalFragments(target);
    logger.debug("Path fragments: {}", asList(fragments));
    if (2 != fragments.length) {
      return;
    }
    if (!"build".equals(fragments[0])) {
      return;
    }
    final String buildUuid = fragments[1];

    final ObjectMapper mapper = new ObjectMapper();
    try {
      final BuildResult build = get(buildUuid).orElseThrow(ResourceNotFoundException::new);
      final byte[] body = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(build);
      logger.debug("Body:\n{}", HexUtils.dump(body));
      response.setStatus(SC_OK);
      response.setContentType("application/json");
      response.setContentLength(body.length);
      try (final OutputStream in = response.getOutputStream()) {
        in.write(body);
      }
    } catch (final HttpException ex) {
      logger.debug("Status code: {}", ex.getStatusCode());
      response.setStatus(ex.getStatusCode());
    } catch (final Throwable ex) {
      logger.error("Unexpected exception:", ex);
      response.setStatus(SC_INTERNAL_SERVER_ERROR);
    }
    baseRequest.setHandled(true);
  }
}
