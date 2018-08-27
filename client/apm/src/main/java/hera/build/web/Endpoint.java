/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web;

import static hera.util.FilepathUtils.getCanonicalFragments;
import static java.util.Arrays.asList;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.databind.ObjectMapper;
import hera.build.web.exception.HttpException;
import hera.build.web.exception.ResourceNotFoundException;
import hera.build.web.model.BuildDetails;
import hera.build.web.model.BuildSummary;
import hera.build.web.service.BuildService;
import hera.build.web.service.LiveUpdateSession;
import hera.util.FilepathUtils;
import hera.util.HexUtils;
import hera.util.IoUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.MimeTypes.Type;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;

public class Endpoint extends WebSocketServlet {

  protected final transient Logger logger = getLogger(getClass());

  protected final ObjectMapper mapper = new ObjectMapper();

  @Getter
  protected BuildService buildService;

  public void setBuildService(final BuildService buildService) {
    this.buildService = buildService;
    LiveUpdateSession.setManager(buildService.getLiveUpdateService());
  }

  @Override
  public void configure(final WebSocketServletFactory factory) {
    factory.register(LiveUpdateSession.class);
  }


  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {
    logger.trace("Request: {}, Response: {}", req, resp);

    final String requestUri = req.getRequestURI();
    logger.debug("Request uri: {}", requestUri);
    final String[] fragments = getCanonicalFragments(requestUri);
    logger.debug("Path fragments: {}", asList(fragments));
    try {
      if (Arrays.equals(new String[]{"builds"}, fragments)) {
        final List<BuildSummary> summaries = buildService.list(null, 5);
        writeResponse(summaries, resp);
      } else if (2 == fragments.length && "build".equals(fragments[0])) {
        final String buildUuid = fragments[1];

        final BuildDetails build = buildService.get(buildUuid)
            .orElseThrow(ResourceNotFoundException::new);
        writeResponse(build, resp);
      } else {
        getResource(req, resp);
      }
    } catch (final HttpException ex) {
      logger.debug("Status code: {}", ex.getStatusCode());
      resp.setStatus(ex.getStatusCode());
    } catch (final Throwable ex) {
      logger.error("Unexpected exception:", ex);
      resp.setStatus(SC_INTERNAL_SERVER_ERROR);
    }
  }

  protected void getResource(final HttpServletRequest req, final HttpServletResponse resp)
      throws IOException {
    final String requestUri = req.getRequestURI();
    final String resourcePath = FilepathUtils.append("/public", requestUri);
    logger.debug("Resource path: {}", resourcePath);
    try (final InputStream in = getClass().getResourceAsStream(resourcePath)) {
      if (null == in) {
        resp.setStatus(SC_NOT_FOUND);
        return;
      }
      resp.setContentType(MimeTypes.getDefaultMimeByExtension(requestUri));
      try (final OutputStream out = resp.getOutputStream()) {
        IoUtils.redirect(in, out);
        resp.setStatus(SC_OK);
      }
    }
  }

  protected void writeResponse(final Object obj, final HttpServletResponse res) throws IOException {
    logger.debug("Response: {}", obj);
    final byte[] body = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(obj);
    logger.trace("Body:\n{}", HexUtils.dump(body));
    res.setContentType(Type.APPLICATION_JSON.asString());
    try (final OutputStream in = res.getOutputStream()) {
      res.setStatus(SC_OK);
      in.write(body);
    }
  }
}
