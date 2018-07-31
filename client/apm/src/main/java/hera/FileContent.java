/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileContent implements Comparable<FileContent> {
  @Getter
  @NonNull
  protected final String path;

  protected final Supplier<InputStream> conentSupplier;

  public InputStream open() throws IOException {
    return conentSupplier.get();
  }

  @Override
  public String toString() {
    return path;
  }

  @Override
  public int compareTo(final FileContent o) {
    return this.path.compareTo(o.path);
  }

  public File getFileFrom(File base) {
    return new File(base, path);
  }
}
