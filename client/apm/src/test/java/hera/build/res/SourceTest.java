package hera.build.res;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import hera.AbstractTestCase;
import hera.ProjectFile;
import hera.build.ResourceManager;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

public class SourceTest extends AbstractTestCase {
  @Test
  public void testFromImport() {
    assertEquals("fjdkjfkdjf", Source.fromImport("import \"fjdkjfkdjf\""));
    assertEquals("fjdkjfkdjf", Source.fromImport("import 'fjdkjfkdjf'"));
    assertEquals(null, Source.fromImport("import fjdkjfkdjf"));
  }

  @Test
  public void testReadImports() throws IOException {
    final ProjectFile projectFile = new ProjectFile();
    final Project project = new Project(randomUUID().toString(), projectFile);
    final Source source = new Source(project, randomUUID().toString());
    source.setContentProvider(() -> openWithExtensionAs("lua"));
    final List<String> libraries = source.readImports();
    assertEquals(3, libraries.size());
  }

  @Test
  public void testGetBody() throws Exception {
    final ProjectFile projectFile = new ProjectFile();
    final Project project = new Project(randomUUID().toString(), projectFile);
    final Source source = new Source(project, randomUUID().toString());
    source.setContentProvider(() -> openWithExtensionAs("lua"));
    assertFalse(source.getBody().get().trim().isEmpty());
  }

  @Test
  public void testBind() throws Exception {
    final ProjectFile projectFile = new ProjectFile();
    final Project project = new Project(randomUUID().toString(), projectFile);
    final Source source = new Source(project, randomUUID().toString());
    final ResourceManager resourceManager = mock(ResourceManager.class);
    source.bind(resourceManager, "../subsource.lua");
    verify(resourceManager).getResource(anyString());
    source.bind(resourceManager, "subpackage");
    verify(resourceManager).getPackage(anyString());
  }
}