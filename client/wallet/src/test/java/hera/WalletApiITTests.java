/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.it.BlockIT;
import hera.it.ChainIT;
import hera.it.ContractIT;
import hera.it.LegacyContractIT;
import hera.it.LegacyQueryIT;
import hera.it.LegacySendIT;
import hera.it.LegacyStakeVoteIT;
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
    LegacySendIT.class,
    LegacyStakeVoteIT.class,
    LegacyContractIT.class,
    LegacyQueryIT.class,
})
public class WalletApiITTests {

}
