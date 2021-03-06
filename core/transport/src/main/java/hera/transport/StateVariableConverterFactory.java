/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.model.StateVariable;
import org.slf4j.Logger;
import types.Blockchain;

@ApiAudience.Private
@ApiStability.Unstable
public class StateVariableConverterFactory {

  protected final transient Logger logger = getLogger(getClass());

  protected final Function1<StateVariable, Blockchain.StateVar> domainConverter =
      new Function1<StateVariable, Blockchain.StateVar>() {

        @Override
        public Blockchain.StateVar apply(final StateVariable domainStateVariable) {
          throw new UnsupportedOperationException();
        }
      };

  protected final Function1<Blockchain.StateVar, StateVariable> rpcConverter =
      new Function1<Blockchain.StateVar, StateVariable>() {

        @Override
        public StateVariable apply(final Blockchain.StateVar rpcStateVariable) {
          logger.trace("Rpc state variable to convert: {}", rpcStateVariable);

          final StateVariable domainStateVariable = StateVariable.newBuilder()
              .name(rpcStateVariable.getName())
              .type(rpcStateVariable.getType())
              .build();
          logger.trace("Domain state variable converted: {}", domainStateVariable);
          return domainStateVariable;
        }
      };

  public ModelConverter<StateVariable, Blockchain.StateVar> create() {
    return new ModelConverter<>(domainConverter,
        rpcConverter);
  }

}
