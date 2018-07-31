/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.fasterxml.jackson.databind.ObjectMapper;
import hera.AbstractTestCase;
import hera.Project;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

public class ReadProjectFileTest extends AbstractTestCase {
  @Test
  @PrepareForTest(ReadProjectFile.class)
  public void testExecute() throws IOException {
    final String projectFileContent = new ObjectMapper().writeValueAsString(new Project());

    // Given
    mockStatic(Files.class);
    final BufferedReader bufferedReader = new BufferedReader(new StringReader(projectFileContent));
    when(Files.newBufferedReader(any(Path.class))).thenReturn(bufferedReader);

    // When
    final ReadProjectFile command = new ReadProjectFile();
    command.setArguments(singletonList(randomUUID().toString()));
    command.execute();

    // Then
  }

}