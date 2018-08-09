/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import hera.ProjectFile;

public class UpdatePackage extends AbstractCommand {
  @Override
  public void execute() throws Exception {
    final ProjectFile project = readProject();
  }
}
