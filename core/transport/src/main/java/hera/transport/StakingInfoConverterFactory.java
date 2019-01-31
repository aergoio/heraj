/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.parseToAer;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.StakingInfo;
import org.slf4j.Logger;
import types.Rpc;

public class StakingInfoConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<StakingInfo, Rpc.Staking> domainConverter =
      new Function1<StakingInfo, Rpc.Staking>() {

        @Override
        public Rpc.Staking apply(StakingInfo domainStakingInfo) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Rpc.Staking, StakingInfo> rpcConverter =
      new Function1<Rpc.Staking, StakingInfo>() {

        @Override
        public StakingInfo apply(final Rpc.Staking rpcStakingInfo) {
          logger.trace("Rpc staking info to convert: {}", rpcStakingInfo);
          final StakingInfo domainStakingInfo = new StakingInfo(AccountAddress.of(BytesValue.EMPTY),
              parseToAer(rpcStakingInfo.getAmount()),
              rpcStakingInfo.getWhen());
          logger.trace("Domain staking info converted: {}", domainStakingInfo);
          return domainStakingInfo;
        }
      };

  public ModelConverter<StakingInfo, Rpc.Staking> create() {
    return new ModelConverter<StakingInfo, Rpc.Staking>(domainConverter, rpcConverter);
  }

}
