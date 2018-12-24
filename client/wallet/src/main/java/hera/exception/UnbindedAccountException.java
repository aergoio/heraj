package hera.exception;

public class UnbindedAccountException extends WalletException {

  private static final long serialVersionUID = 7740298335550923475L;

  public UnbindedAccountException() {
    super("An account is not binded. Bind account by unlocking");
  }

}
