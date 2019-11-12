/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InternalCommitException extends RpcException {

  private static final long serialVersionUID = -4627737844188433581L;

  @Getter
  protected final types.Rpc.CommitStatus commitStatus;

  @Getter
  protected final String message;

}
