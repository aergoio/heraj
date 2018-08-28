/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import lombok.Getter;

public class CommitException extends RpcException {

  private static final long serialVersionUID = -4798911407894712354L;

  enum CommitStatus {
    UNRECOGNIZED,
    COMMIT_STATUS_NONCE_TOO_LOW,
    COMMIT_STATUS_INVALID_ARGUMENT,
    COMMIT_STATUS_TX_ALREADY_EXISTS,
    COMMIT_STATUS_TX_INTERNAL_ERROR
  }

  @Getter
  protected CommitStatus commitStatus = CommitStatus.UNRECOGNIZED;

  /**
   * Make a {@code CommitException} with rpc {@link types.Rpc.CommitStatus}.
   *
   * @param rpcCommitStatus rpc commit status
   */
  public CommitException(types.Rpc.CommitStatus rpcCommitStatus) {
    switch (rpcCommitStatus) {
      case COMMIT_STATUS_NONCE_TOO_LOW:
        this.commitStatus = CommitStatus.COMMIT_STATUS_NONCE_TOO_LOW;
        break;
      case COMMIT_STATUS_INVALID_ARGUMENT:
        this.commitStatus = CommitStatus.COMMIT_STATUS_INVALID_ARGUMENT;
        break;
      case COMMIT_STATUS_TX_ALREADY_EXISTS:
        this.commitStatus = CommitStatus.COMMIT_STATUS_TX_ALREADY_EXISTS;
        break;
      case COMMIT_STATUS_TX_INTERNAL_ERROR:
        this.commitStatus = CommitStatus.COMMIT_STATUS_TX_INTERNAL_ERROR;
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

}
