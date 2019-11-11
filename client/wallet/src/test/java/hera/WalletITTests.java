/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.it.BlockIT;
import hera.it.ChainIT;
import hera.it.ContractIT;
import hera.it.LegacyWalletIT;
import hera.it.SendIT;
import hera.it.StakeVoteIT;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    SendIT.class,
    StakeVoteIT.class,
    ContractIT.class,
    BlockIT.class,
    ChainIT.class,
    LegacyWalletIT.class,
})
public class WalletITTests {

}
