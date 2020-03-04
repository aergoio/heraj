package hera.api.transaction.dsl;

public interface NeedNonce<NextStateT> {

  /**
   * Accept {@code nonce} to be used in transaction.
   *
   * @param nonce a nonce
   * @return next state after accepting nonce
   */
  NextStateT nonce(long nonce);

}
