/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;

import hera.api.model.BytesValue;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.HerajException;
import hera.key.KeyPair;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class SignLocalTemplate implements SignOperation {

  @Getter
  protected final KeyPair keyPair;

  @Override
  public ResultOrError<Signature> sign(Transaction transaction) {
    try {
      final BytesValue signature = keyPair.sign(transaction.calculateHash().getBytesValue().get());
      transaction.setSignature(Signature.of(signature, null));
      return success(Signature.of(signature, transaction.calculateHash()));
    } catch (Exception e) {
      return fail(new HerajException(e));
    }
  }

  @Override
  public ResultOrError<Boolean> verify(Transaction transaction) {
    try {
      final BytesValue signature = transaction.getSignature().getSign();
      transaction.setSignature(null);
      return success(keyPair.verify(transaction.calculateHash().getBytesValue().get(), signature));
    } catch (Exception e) {
      return fail(new HerajException(e));
    }
  }

}
