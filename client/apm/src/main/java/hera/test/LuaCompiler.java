/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import static hera.util.IoUtils.from;
import static hera.util.IoUtils.redirect;
import static org.slf4j.LoggerFactory.getLogger;

import hera.exception.BuildException;
import hera.util.Base58Utils;
import hera.util.DangerousSupplier;
import hera.util.IoUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import org.slf4j.Logger;

public class LuaCompiler {

  protected final transient Logger logger = getLogger(getClass());

  protected String buildCommand() {
    return "aergoluac --payload";
  }

  /**
   * Compile {@code source}.
   *
   * @param source source to compile
   *
   * @return compiled result
   *
   * @throws BuildException Fail to compile
   */
  public LuaBinary compile(
      final DangerousSupplier<InputStream> source)
      throws BuildException {
    try {
      final String[] envParameters = System.getenv().entrySet().stream()
          .map(e -> e.getKey() + "=" + e.getValue())
          .toArray(String[]::new);
      final Process process = Runtime.getRuntime().exec(buildCommand(), envParameters);
      try (
          final Reader compilerStdErr = new InputStreamReader(process.getErrorStream());
          final Reader compilerStdOut = new InputStreamReader(process.getInputStream())
      ) {
        try (
            final InputStream sourceIn = source.get();
            final OutputStream compilerIn = process.getOutputStream()) {
          redirect(sourceIn, compilerIn);
        }
        final int exitCode = process.waitFor();
        if (0 != exitCode) {
          final String errorOut = from(compilerStdErr);
          logger.warn("Compiler Error: {}", errorOut);
          throw new BuildException("Fail to aergoluac");
        }
        final String base58Encoded = IoUtils.from(compilerStdOut);
        logger.info("Encoded: {}", base58Encoded);
        return new LuaBinary(() -> new ByteArrayInputStream(Base58Utils.decode(base58Encoded)));
      }
    } catch (final BuildException ex) {
      throw ex;
    } catch (final Throwable ex) {
      throw new BuildException(ex);
    }
  }
}
