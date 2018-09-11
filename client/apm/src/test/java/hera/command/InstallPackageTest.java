/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import hera.AbstractTestCase;
import hera.FileSet;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

public class InstallPackageTest extends AbstractTestCase {

  @Test
  @PrepareForTest(InstallPackage.class)
  public void testExecute() throws Exception {
    // Given
    final CloneGit cloneGit = mock(CloneGit.class);
    whenNew(CloneGit.class).withAnyArguments().thenReturn(cloneGit);
    when(cloneGit.getFileSet()).thenReturn(new FileSet());

    // When
    final InstallPackage command = new InstallPackage();
    command.setArguments(singletonList(randomUUID().toString() + "/" + randomUUID().toString()));
    command.execute();

    // Then
  }

}