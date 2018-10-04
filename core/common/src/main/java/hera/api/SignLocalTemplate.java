/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.key.KeyPair;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class SignLocalTemplate implements SignOperation {

  @Getter
  protected final SignEitherOperation signEitherOperation;

  public SignLocalTemplate(final KeyPair keyPair) {
    this(new SignLocalEitherTemplate(keyPair));
  }

  @Override
  public Signature sign(Transaction transaction) {
    return signEitherOperation.sign(transaction).getResult();
  }

  @Override
  public boolean verify(Transaction transaction) {
    return signEitherOperation.verify(transaction).getResult();
  }

}
