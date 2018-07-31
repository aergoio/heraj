/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import hera.AbstractTestCase;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

public class WriteProjectFileTest extends AbstractTestCase {

  @Test
  @PrepareForTest(WriteProjectFile.class)
  public void testExecute() throws Exception {
    // Given
    mockStatic(Files.class);
    final BufferedWriter bufferedWriter = new BufferedWriter(new StringWriter());
    when(Files.newBufferedWriter(any(Path.class))).thenReturn(bufferedWriter);

    // When
    final WriteProjectFile command = new WriteProjectFile();
    command.setArguments(singletonList(randomUUID().toString()));
    command.execute();

    // Then
  }

}