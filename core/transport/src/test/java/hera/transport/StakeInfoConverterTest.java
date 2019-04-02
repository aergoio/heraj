/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.copyFrom;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Aer;
import hera.api.model.StakeInfo;
import org.junit.Test;
import types.Rpc;

public class StakeInfoConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<StakeInfo, Rpc.Staking> converter =
        new StakeInfoConverterFactory().create();

    final Rpc.Staking rpcStakingInfo = Rpc.Staking.newBuilder()
        .setAmount(copyFrom(Aer.ONE))
        .setWhen(10000L)
        .build();
    final StakeInfo domainStakingInfo = converter.convertToDomainModel(rpcStakingInfo);
    assertNotNull(domainStakingInfo);
  }

}
