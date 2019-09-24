/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.client.it.AccountOperationIT;
import hera.client.it.AergoClientIT;
import hera.client.it.BlockOperationIT;
import hera.client.it.BlockchainOperationIT;
import hera.client.it.ContractOperationIT;
import hera.client.it.KeyStoreOperationIT;
import hera.client.it.TransactionOperationIT;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    AergoClientIT.class,
    AccountOperationIT.class,
    KeyStoreOperationIT.class,
    BlockOperationIT.class,
    BlockchainOperationIT.class,
    TransactionOperationIT.class,
    ContractOperationIT.class
})
public class ITTests {

}
