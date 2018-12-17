package hera.exception;

public class LockedAccountException extends WalletException {

  private static final long serialVersionUID = 5722845663930655767L;

  public LockedAccountException() {
    super("Unlock account first");
  }

}
