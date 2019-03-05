package hera.exception;

public class WalletRpcException extends WalletException {

  private static final long serialVersionUID = 3288436141766204723L;

  public WalletRpcException(final String message) {
    super(message);
  }

  public WalletRpcException(final Throwable cause) {
    super(cause);
  }

  public WalletRpcException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
