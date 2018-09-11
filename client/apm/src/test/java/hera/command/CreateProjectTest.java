/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import hera.AbstractTestCase;
import hera.ProjectFile;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

public class CreateProjectTest extends AbstractTestCase {

  @Test
  @PrepareForTest(CreateProject.class)
  public void testExecute() throws Exception {
    // Given
    final WriteProjectFile writeProjectFile = mock(WriteProjectFile.class);
    whenNew(WriteProjectFile.class).withAnyArguments().thenReturn(writeProjectFile);
    when(writeProjectFile.getProject()).thenReturn(new ProjectFile());

    // When
    final CreateProject command = new CreateProject();
    command.execute();

    // Then
    verify(writeProjectFile).execute();
  }

}