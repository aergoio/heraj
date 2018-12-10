package hera.exception;

public class UnlockedAccountException extends WalletException {

  private static final long serialVersionUID = 5722845663930655767L;

  public UnlockedAccountException() {
    super("Unlock account first");
  }

}
