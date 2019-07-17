package hera.spec.transaction.dsl;

import hera.api.model.Name;

public interface CreateNameTransaction {

  interface WithNothing extends NeedChainIdHash<WithChainIdHash> {
  }

  interface WithChainIdHash extends NeedSender<WithChainIdHashAndSender> {
  }

  interface WithChainIdHashAndSender extends NeedNonce<WithChainIdHashAndSenderAndNonce> {
  }

  interface WithChainIdHashAndSenderAndNonce {
    /**
     * Accept name to be created and owned by sender.
     *
     * @param name an name to be created.
     * @return next state after accepting name
     */
    WithReady name(String name);

    /**
     * Accept name to be created and owned by sender.
     *
     * @param name an name to be created.
     * @return next state after accepting name
     */
    WithReady name(Name name);
  }

  interface WithReady extends BuildReady {
  }

}
