package hera.build;

import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.ProjectFile;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

public class PackageManagerTest extends AbstractTestCase {
  @Test
  @PrepareForTest(ProjectFile.class)
  public void testFind() throws IOException {
    final String packageName = randomUUID().toString();
    final ProjectFile projectFile = new ProjectFile();
    final PackageManager packageManager = new PackageManager();
    PowerMockito.mockStatic(ProjectFile.class);
    when(ProjectFile.from(any(Path.class))).thenReturn(projectFile);
    packageManager.find(packageName);
  }

}