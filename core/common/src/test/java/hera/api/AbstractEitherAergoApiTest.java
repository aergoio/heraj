/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import org.junit.Test;

public class AbstractEitherAergoApiTest {

  protected final AbstractEitherAergoApi aergoApi = new AbstractEitherAergoApi();

  @Test(expected = UnsupportedOperationException.class)
  public void testGetAccountEitherOperation() {
    aergoApi.getAccountEitherOperation();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetBlockEitherOperation() {
    aergoApi.getBlockEitherOperation();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetBlockchainEitherOperation() {
    aergoApi.getBlockchainEitherOperation();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetTransactionEitherOperation() {
    aergoApi.getTransactionEitherOperation();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetContractEitherOperation() {
    aergoApi.getContractEitherOperation();
  }

}
