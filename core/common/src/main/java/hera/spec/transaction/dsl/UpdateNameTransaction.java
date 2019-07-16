package hera.spec.transaction.dsl;

import hera.api.model.AccountAddress;

public interface UpdateNameTransaction {

  interface WithNothing extends NeedChainIdHash<WithChainIdHash> {
  }

  interface WithChainIdHash extends NeedSender<WithChainIdHashAndSender> {
  }

  interface WithChainIdHashAndSender extends NeedNonce<WithChainIdHashAndSenderAndNonce> {
  }

  interface WithChainIdHashAndSenderAndNonce {
    /**
     * Accept name which is supposed to be owned by other account.
     *
     * @param name an name which is supposed to be owned by other account
     * @return next state after accepting name
     */
    WithChainIdHashAndSenderAndNonceAndName name(String name);
  }

  interface WithChainIdHashAndSenderAndNonceAndName {
    /**
     * Accept new owner of name.
     *
     * @param newOwner an new owner of name
     * @return next state after accepting name
     */
    WithReady newOwner(AccountAddress newOwner);
  }

  interface WithReady extends BuildReady {
  }

}
