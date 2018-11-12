/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionVerificationException extends RpcException {

  private static final long serialVersionUID = 308408228893339199L;

  public enum VerifyStatus {
    VERIFY_STATUS_OK,
    VERIFY_STATUS_SIGN_NOT_MATCH,
    VERIFY_STATUS_INVALID_HASH,
    UNRECOGNIZED
  }

  @Getter
  protected final VerifyStatus verifyStatus;

  /**
   * Make a {@code TransactionVerificationException} with rpc {@link types.Rpc.VerifyStatus}.
   *
   * @param rpcVerifyStatus rpc verify status
   */
  public TransactionVerificationException(types.Rpc.VerifyStatus rpcVerifyStatus) {
    switch (rpcVerifyStatus) {
      case VERIFY_STATUS_OK:
        this.verifyStatus = VerifyStatus.VERIFY_STATUS_OK;
        break;
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
