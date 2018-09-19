package hera.build;

import static hera.build.ResourceManagerMock.dir;
import static hera.build.ResourceManagerMock.file;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import hera.ProjectFile;
import hera.build.res.BuildResource;
import hera.build.res.Project;
import hera.build.web.model.BuildDetails;
import org.junit.Test;

public class ConcatenatorTest extends AbstractTestCase {

  @Test
  public void testVisit() {
    final ProjectFile projectFile = new ProjectFile();
    projectFile.setName("test/test");
    projectFile.setSource("source.lua");
    projectFile.setTarget("target.lua");
    final Project project = new Project(".", projectFile);
    final String base = "/" + getClass().getName().replace('.', '/') + "/";
    final ResourceManager resourceManager = new ResourceManagerMock(
        project,
        dir(
            ".",
            file("aergo.json", () -> open(base + "aergo.json")),
            file("source.lua", () -> open(base + "source.lua")),
            dir("subdir",
                file("ref1.lua", () -> open(base + "subdir/ref1.lua"))
            )
        )
    );
    final Concatenator concatenator = new Concatenator(resourceManager);
    final BuildDetails buildDetails =
        concatenator.visit(new BuildResource(project, "target.lua"));
    logger.debug("Build result: {}", buildDetails.getResult());
    assertTrue(buildDetails.getResult().contains("hello"));
    assertTrue(buildDetails.getResult().contains("world"));

  }

}