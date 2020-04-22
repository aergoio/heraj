/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.StateVariable;
import org.junit.Test;
import types.Blockchain;

public class StakeVariableConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<StateVariable, Blockchain.StateVar> converter =
        new StateVariableConverterFactory().create();

    final Blockchain.StateVar rpcStakingInfo = Blockchain.StateVar
        .newBuilder()
        .build();
    final StateVariable domainStateVariable = converter.convertToDomainModel(rpcStakingInfo);
    assertNotNull(domainStateVariable);
  }

}
