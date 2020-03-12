package hera.api.transaction.dsl;

import hera.api.model.Name;

public interface CreateNameTransaction extends AergoTransaction {

  interface WithNothing extends NeedChainIdHash<WithChainIdHash> {

  }

  interface WithChainIdHash extends NeedSender<WithChainIdHashAndSender> {

  }

  interface WithChainIdHashAndSender {

    /**
     * Accept name to be created and owned by sender.
     *
     * @param name an name to be created.
     * @return next state after accepting name
     */
    WithChainIdHashAndSenderAndName name(String name);

    /**
     * Accept name to be created and owned by sender.
     *
     * @param name an name to be created.
     * @return next state after accepting name
     */
    WithChainIdHashAndSenderAndName name(Name name);
  }

  interface WithChainIdHashAndSenderAndName extends NeedNonce<WithReady> {

  }

  interface WithReady extends BuildReady {

  }

}
