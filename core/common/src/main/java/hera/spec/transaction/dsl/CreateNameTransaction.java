package hera.spec.transaction.dsl;

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
  }

  interface WithReady extends BuildReady {
  }

}
