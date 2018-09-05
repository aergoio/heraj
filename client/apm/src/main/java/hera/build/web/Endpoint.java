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
import hera.ProjectFile;
import hera.build.web.exception.HttpException;
import hera.build.web.exception.ResourceNotFoundException;
import hera.build.web.model.BuildDetails;
import hera.build.web.model.BuildSummary;
import hera.build.web.model.ServiceError;
import hera.build.web.service.BuildService;
import hera.build.web.service.ConfigurationService;
import hera.build.web.service.ContractService;
import hera.build.web.service.LiveUpdateSession;
import hera.util.ExceptionUtils;
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
import lombok.Setter;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.MimeTypes.Type;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;

public class Endpoint extends WebSocketServlet {

  protected final transient Logger logger = getLogger(getClass());

  protected final ObjectMapper mapper = new ObjectMapper();

  @Getter
  @Setter
  protected ConfigurationService configurationService;

  @Getter
  protected BuildService buildService;

  @Getter
  @Setter
  protected ContractService contractService;

  public void setBuildService(final BuildService buildService) {
    this.buildService = buildService;
    LiveUpdateSession.setManager(buildService.getLiveUpdateService());
  }

  @Override
  public void configure(final WebSocketServletFactory factory) {
    factory.register(LiveUpdateSession.class);
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {
    logger.trace("Request: {}, Response: {}", req, resp);

    final String requestUri = req.getRequestURI();
    logger.debug("Request uri: {}", requestUri);
    final String[] fragments = getCanonicalFragments(requestUri);
    logger.debug("Path fragments: {}", asList(fragments));
    try {
      if (0 < fragments.length && "build".equals(fragments[0])) {
        if (3 == fragments.length && "deploy".equals(fragments[2])) {
          // /build/{uuid}/deploy
          final String uuid = fragments[1];
          final BuildDetails buildDetails = buildService.get(uuid)
              .orElseThrow(() -> new ResourceNotFoundException(uuid + " not found"));
          contractService.deploy(buildDetails);
          writeResponse(null, resp);
          return;
        }
      }
      getResource(req, resp);
    } catch (final Throwable ex) {
      logger.error("Unexpected exception:", ex);
      writeError(ex, resp);
    }
  }

  @Override
  protected void doGet(
      final HttpServletRequest req,
      final HttpServletResponse resp) throws IOException {
    logger.trace("Request: {}, Response: {}", req, resp);

    final String requestUri = req.getRequestURI();
    logger.debug("Request uri: {}", requestUri);
    final String[] fragments = getCanonicalFragments(requestUri);
    logger.debug("Path fragments: {}", asList(fragments));
    try {
      if (Arrays.equals(new String[] {"project"}, fragments)) {
        final ProjectFile projectFile = configurationService.getProjectFile()
            .orElseThrow(ResourceNotFoundException::new);
        writeResponse(projectFile, resp);
        return;
      } else if (0 < fragments.length && "contract".equals(fragments[0])) {
        if (1 < fragments.length) {
          final String contractAddress = fragments[1];
          if (2 < fragments.length && "abi".equals(fragments[2])) {
            final String endpoint = req.getParameter("endpoint");
            writeResponse(contractService.getAbi(contractAddress), resp);
            return;
          }
        }
      } else if (Arrays.equals(new String[] {"builds"}, fragments)) {
        // /builds
        final List<BuildSummary> summaries = buildService.list(null, 5);
        writeResponse(summaries, resp);
        return;
      } else if (0 < fragments.length && "build".equals(fragments[0])) {
        if (2 == fragments.length && "build".equals(fragments[0])) {
          // /build/{uuid}
          final String buildUuid = fragments[1];

          final BuildDetails build = buildService.get(buildUuid)
              .orElseThrow(ResourceNotFoundException::new);
          writeResponse(build, resp);
          return;
        }
      }
      getResource(req, resp);
    } catch (final Throwable ex) {
      writeError(ex, resp);
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

  protected void writeError(final Throwable ex, final HttpServletResponse res) throws IOException {
    logger.debug("Exception:", ex);
    if (ex instanceof HttpException) {
      final HttpException httpException = (HttpException) ex;
      res.setStatus(httpException.getStatusCode());
      final byte[] body = mapper.writerWithDefaultPrettyPrinter()
          .writeValueAsBytes(new ServiceError(ex.getMessage(), ExceptionUtils.getStackTraceOf(ex)));
      logger.trace("Body:\n{}", HexUtils.dump(body));
      res.setContentType(Type.APPLICATION_JSON.asString());
      try (final OutputStream in = res.getOutputStream()) {
        in.write(body);
      }
    } else {
      res.setStatus(SC_INTERNAL_SERVER_ERROR);
    }
  }

  protected void writeResponse(final Object obj, final HttpServletResponse res) throws IOException {
    logger.debug("Response: {}", obj);
    res.setStatus(SC_OK);
    if (null != obj) {
      final byte[] body = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(obj);
      logger.trace("Body:\n{}", HexUtils.dump(body));
      res.setContentType(Type.APPLICATION_JSON.asString());
      try (final OutputStream in = res.getOutputStream()) {
        in.write(body);
      }
    }
  }
}
