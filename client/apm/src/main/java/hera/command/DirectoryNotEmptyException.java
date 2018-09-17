package hera.command;

import hera.exception.CommandException;
import java.nio.file.Path;

public class DirectoryNotEmptyException extends CommandException {
  public DirectoryNotEmptyException(Path path) {
    super("<yellow>" + path.toString() + "</yellow> is not empty."
        + " You can reset project with <blue>-f</blue> but It make aergo.json overwritten!!");
  }

}
