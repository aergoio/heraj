/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static java.util.Collections.EMPTY_LIST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import hera.AbstractTestCase;
import hera.ProjectFile;
import hera.util.MessagePrinter;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

public class CreateProjectTest extends AbstractTestCase {

  @Test
  @PrepareForTest(CreateProject.class)
  @SuppressWarnings("unchecked")
  public void testExecute() throws Exception {
    // Given
    final WriteProjectFile writeProjectFile = mock(WriteProjectFile.class);
    whenNew(WriteProjectFile.class).withAnyArguments().thenReturn(writeProjectFile);
    when(writeProjectFile.getProject()).thenReturn(new ProjectFile());
    mockStatic(Files.class);
    when(Files.list(any())).thenReturn(EMPTY_LIST.stream());
    when(Files.newInputStream(any()))
        .thenReturn(new ByteArrayInputStream("hello, world".getBytes()));

    // When
    final CreateProject command = new CreateProject();
    command.setPrinter(mock(MessagePrinter.class));
    command.execute();

    // Then
    verify(writeProjectFile).execute();
  }

}