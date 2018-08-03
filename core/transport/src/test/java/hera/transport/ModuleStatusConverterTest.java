/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ModuleStatus;
import java.util.ArrayList;
import org.junit.Test;
import types.Rpc;

public class ModuleStatusConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<ModuleStatus, Rpc.ModuleStatus> converter = new ModuleStatusConverterFactory()
        .create();

    final ModuleStatus domainModuleStatus = new ModuleStatus();
    domainModuleStatus.setModuleName(randomUUID().toString());
    domainModuleStatus.setInternalStatus(new ArrayList<>());
    final Rpc.ModuleStatus rpcModuleStatus = converter.convertToRpcModel(domainModuleStatus);
    final ModuleStatus actualDomainModuleStatus = converter.convertToDomainModel(rpcModuleStatus);
    assertNotNull(actualDomainModuleStatus);
  }

}
