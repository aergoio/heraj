package hera.exception;

public class WalletCommitException extends WalletException {

  private static final long serialVersionUID = 4876557554441213494L;

  public WalletCommitException(final String message) {
    super(message);
  }

  public WalletCommitException(final Throwable cause) {
    super(cause);
  }

  public WalletCommitException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
