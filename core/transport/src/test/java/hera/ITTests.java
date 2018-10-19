/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.client.it.AccountOperationIT;
import hera.client.it.BlockOperationIT;
import hera.client.it.ContractOperationIT;
import hera.client.it.TransactionOperationIT;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    AccountOperationIT.class,
    BlockOperationIT.class,
    TransactionOperationIT.class,
    ContractOperationIT.class
})
public class ITTests {

}
