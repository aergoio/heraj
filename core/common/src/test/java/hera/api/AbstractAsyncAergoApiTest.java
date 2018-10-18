/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import org.junit.Test;

public class AbstractAsyncAergoApiTest {

  protected final AbstractAsyncAergoApi aergoApi = new AbstractAsyncAergoApi();

  @Test(expected = UnsupportedOperationException.class)
  public void testGetAccountAsyncOperation() {
    aergoApi.getAccountAsyncOperation();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetBlockAsyncOperation() {
    aergoApi.getBlockAsyncOperation();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetBlockchainAsyncOperation() {
    aergoApi.getBlockchainAsyncOperation();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetTransactionAsyncOperation() {
    aergoApi.getTransactionAsyncOperation();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetContractAsyncOperation() {
    aergoApi.getContractAsyncOperation();
  }

}
