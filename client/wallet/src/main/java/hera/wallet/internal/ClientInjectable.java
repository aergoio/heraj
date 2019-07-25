package hera.wallet.internal;

import hera.client.AergoClient;

public interface ClientInjectable {
  void setClient(AergoClient client);
}
