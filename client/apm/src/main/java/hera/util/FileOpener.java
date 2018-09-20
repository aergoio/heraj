package hera.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileOpener implements DangerousSupplier<InputStream> {

  protected final Path path;

  @Override
  public InputStream get() throws IOException {
    return Files.newInputStream(path);
  }
}
