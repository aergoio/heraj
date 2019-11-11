/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.it.AccountOperationIT;
import hera.it.AergoClientIT;
import hera.it.BlockOperationIT;
import hera.it.BlockchainOperationIT;
import hera.it.ContractOperationIT;
import hera.it.KeyStoreOperationIT;
import hera.it.TransactionOperationIT;
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
