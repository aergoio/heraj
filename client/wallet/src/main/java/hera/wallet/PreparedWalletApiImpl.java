/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.client.AergoClient;
import hera.key.Signer;

class PreparedWalletApiImpl implements PreparedWalletApi, ClientProvider {

  protected final AergoClient aergoClient;

  protected final TransactionApi transactionApi;

  protected final QueryApi queryApi;

  PreparedWalletApiImpl(final AergoClient aergoClient, final Signer signer,
      final TxRequester txRequester) {
    this.aergoClient = aergoClient;
    this.transactionApi = new TransactionApiImpl(this, signer, txRequester);
    this.queryApi = new QueryApiImpl(this);
  }

  @Override
  public TransactionApi transaction() {
    return this.transactionApi;
  }

  @Override
  public QueryApi query() {
    return this.queryApi;
  }

  @Override
  public AergoClient getClient() {
    return this.aergoClient;
  }

}
