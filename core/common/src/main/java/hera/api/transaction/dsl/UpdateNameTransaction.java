package hera.api.transaction.dsl;

import hera.api.model.Identity;
import hera.api.model.Name;

public interface UpdateNameTransaction extends AergoTransaction {

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

    /**
     * Accept name which is supposed to be owned by other account.
     *
     * @param name an name which is supposed to be owned by other account
     * @return next state after accepting name
     */
    WithChainIdHashAndSenderAndNonceAndName name(Name name);
  }

  interface WithChainIdHashAndSenderAndNonceAndName {
    /**
     * Accept new owner of name.
     *
     * @param nextOwner an next owner of name
     * @return next state after accepting name
     */
    WithReady nextOwner(Identity nextOwner);
  }

  interface WithReady extends BuildReady {
  }

}
