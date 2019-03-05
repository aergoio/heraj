package hera.exception;

public class UnbindedKeyStoreException extends WalletException {

  private static final long serialVersionUID = 7738223827262824420L;

  public UnbindedKeyStoreException() {
    super("KeyStore is not binded");
  }

}
