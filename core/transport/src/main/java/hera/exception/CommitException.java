/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ApiAudience.Public
@ApiStability.Unstable
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CommitException extends HerajException {

  private static final long serialVersionUID = -3973554154789144558L;

  public enum CommitStatus {
    OK,
    NONCE_TOO_LOW,
    TX_ALREADY_EXISTS,
    TX_INVALID_HASH,
    TX_INVALID_SIGNATURE,
    TX_INVALID_FORMAT,
    INSUFFICIENT_BALANCE,
    TX_HAS_SAME_NONCE,
    INTERNAL_ERROR,
    UNRECOGNIZED
  }

  @Getter
  protected final CommitStatus commitStatus;

  @Getter
  protected final String message;

  /**
   * Make a {@code CommitException} with rpc {@link types.Rpc.CommitStatus}.
   *
   * @param rpcCommitStatus a rpc commit status
   * @param message         a message
   */
  public CommitException(final types.Rpc.CommitStatus rpcCommitStatus, final String message) {
    this.message = message;
    switch (rpcCommitStatus) {
      case TX_OK:
        this.commitStatus = CommitStatus.OK;
        break;
      case TX_NONCE_TOO_LOW:
        this.commitStatus = CommitStatus.NONCE_TOO_LOW;
        break;
      case TX_ALREADY_EXISTS:
        this.commitStatus = CommitStatus.TX_ALREADY_EXISTS;
        break;
      case TX_INVALID_HASH:
        this.commitStatus = CommitStatus.TX_INVALID_HASH;
        break;
      case TX_INVALID_SIGN:
        this.commitStatus = CommitStatus.TX_INVALID_SIGNATURE;
        break;
      case TX_INVALID_FORMAT:
        this.commitStatus = CommitStatus.TX_INVALID_FORMAT;
        break;
      case TX_INSUFFICIENT_BALANCE:
        this.commitStatus = CommitStatus.INSUFFICIENT_BALANCE;
        break;
      case TX_HAS_SAME_NONCE:
        this.commitStatus = CommitStatus.TX_HAS_SAME_NONCE;
        break;
      case TX_INTERNAL_ERROR:
        this.commitStatus = CommitStatus.INTERNAL_ERROR;
        break;
      default:
        this.commitStatus = CommitStatus.UNRECOGNIZED;
        break;
    }
  }

  @Override
  public String getLocalizedMessage() {
    return commitStatus.toString() + " " + message;
  }

}
