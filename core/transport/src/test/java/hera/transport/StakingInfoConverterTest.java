/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.copyFrom;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Aer;
import hera.api.model.StakingInfo;
import org.junit.Test;
import types.Rpc;

public class StakingInfoConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<StakingInfo, Rpc.Staking> converter =
        new StakingInfoConverterFactory().create();

    final Rpc.Staking rpcStakingInfo = Rpc.Staking.newBuilder()
        .setAmount(copyFrom(Aer.ONE))
        .setWhen(10000L)
        .build();
    final StakingInfo domainStakingInfo = converter.convertToDomainModel(rpcStakingInfo);
    assertNotNull(domainStakingInfo);
  }

}
