/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CommitException extends RpcException {

  private static final long serialVersionUID = -3973554154789144558L;

  public enum CommitStatus {
    OK,
    NONCE_TOO_LOW,
    TX_ALREADY_EXISTS,
    TX_INVALID_HASH,
    TX_INVALID_SIGNATURE,
    TX_INVALID_FORMAT,
    INSUFFICIENT_BALANCE,
    INTERNAL_ERROR,
    UNRECOGNIZED
  }

  @Getter
  protected final CommitStatus commitStatus;

  /**
   * Make a {@code CommitException} with rpc {@link types.Rpc.CommitStatus}.
   *
   * @param rpcCommitStatus rpc commit status
   */
  public CommitException(types.Rpc.CommitStatus rpcCommitStatus) {
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
    return commitStatus.toString();
  }

  @Override
  public CommitException clone() {
    return new CommitException(getCommitStatus());
  }

}
