/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.parseToAer;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function1;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.StakeInfo;
import org.slf4j.Logger;
import types.Rpc;

public class StakeInfoConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<StakeInfo, Rpc.Staking> domainConverter =
      new Function1<StakeInfo, Rpc.Staking>() {

        @Override
        public Rpc.Staking apply(StakeInfo domainStakingInfo) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Rpc.Staking, StakeInfo> rpcConverter =
      new Function1<Rpc.Staking, StakeInfo>() {

        @Override
        public StakeInfo apply(final Rpc.Staking rpcStakingInfo) {
          logger.trace("Rpc staking info to convert: {}", rpcStakingInfo);
          final Aer parsedAer = parseToAer(rpcStakingInfo.getAmount());
          final StakeInfo domainStakingInfo = new StakeInfo(AccountAddress.of(BytesValue.EMPTY),
              parsedAer.equals(Aer.EMPTY) ? Aer.ZERO : parsedAer,
              rpcStakingInfo.getWhen());
          logger.trace("Domain staking info converted: {}", domainStakingInfo);
          return domainStakingInfo;
        }
      };

  public ModelConverter<StakeInfo, Rpc.Staking> create() {
    return new ModelConverter<StakeInfo, Rpc.Staking>(domainConverter, rpcConverter);
  }

}
