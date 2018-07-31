/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import hera.Project;

public class UpdatePackage extends AbstractCommand {
  @Override
  public void execute() throws Exception {
    final Project project = readProject();
  }
}
