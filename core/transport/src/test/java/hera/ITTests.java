/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.client.it.AccountTemplateIT;
import hera.client.it.BlockChainTemplateIT;
import hera.client.it.BlockTemplateIT;
import hera.client.it.ContractTemplateIT;
import hera.client.it.TransactionTemplateIT;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    AccountTemplateIT.class,
    BlockChainTemplateIT.class,
    BlockTemplateIT.class,
    TransactionTemplateIT.class,
    ContractTemplateIT.class
})
public class ITTests {

}
