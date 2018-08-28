/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import lombok.Getter;

public class TransactionVerificationException extends RpcException {

  private static final long serialVersionUID = -4798911407894712354L;

  enum VerifyStatus {
    UNRECOGNIZED,
    VERIFY_STATUS_SIGN_NOT_MATCH,
    VERIFY_STATUS_INVALID_HASH
  }

  @Getter
  protected VerifyStatus verifyStatus = VerifyStatus.UNRECOGNIZED;

  /**
   * Make a {@code TransactionVerificationException} with rpc {@link types.Rpc.VerifyStatus}.
   *
   * @param rpcVerifyStatus rpc verify status
   */
  public TransactionVerificationException(types.Rpc.VerifyStatus rpcVerifyStatus) {
    switch (rpcVerifyStatus) {
      case VERIFY_STATUS_SIGN_NOT_MATCH:
        this.verifyStatus = VerifyStatus.VERIFY_STATUS_SIGN_NOT_MATCH;
        break;
      case VERIFY_STATUS_INVALID_HASH:
        this.verifyStatus = VerifyStatus.VERIFY_STATUS_INVALID_HASH;
        break;
      default:
        this.verifyStatus = VerifyStatus.UNRECOGNIZED;
        break;
    }
  }

  @Override
  public String getLocalizedMessage() {
    return verifyStatus.toString();
  }

}
