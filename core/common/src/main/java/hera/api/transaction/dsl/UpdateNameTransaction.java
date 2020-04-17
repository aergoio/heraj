package hera.api.transaction.dsl;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Identity;
import hera.api.model.Name;

@ApiAudience.Public
@ApiStability.Unstable
public interface UpdateNameTransaction extends AergoTransaction {

  interface WithNothing extends NeedChainIdHash<WithChainIdHash> {

  }

  interface WithChainIdHash extends NeedSender<WithChainIdHashAndSender> {

  }

  interface WithChainIdHashAndSender {

    /**
     * Accept name which is supposed to be owned by other account.
     *
     * @param name an name which is supposed to be owned by other account
     * @return next state after accepting name
     */
    WithChainIdHashAndSenderAndName name(String name);

    /**
     * Accept name which is supposed to be owned by other account.
     *
     * @param name an name which is supposed to be owned by other account
     * @return next state after accepting name
     */
    WithChainIdHashAndSenderAndName name(Name name);
  }

  interface WithChainIdHashAndSenderAndName {

    /**
     * Accept new owner of name.
     *
     * @param nextOwner an next owner of name
     * @return next state after accepting name
     */
    WithChainIdHashAndSenderAndNameAndNextOwner nextOwner(Identity nextOwner);
  }

  interface WithChainIdHashAndSenderAndNameAndNextOwner extends NeedNonce<WithReady> {

  }

  interface WithReady extends BuildReady {

  }

}
