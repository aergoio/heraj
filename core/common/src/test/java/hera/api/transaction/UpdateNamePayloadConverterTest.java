/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.BytesValue;
import hera.api.model.Name;
import hera.api.model.UpdateName;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class UpdateNamePayloadConverterTest extends AbstractTestCase {

  @Test
  public void testConvertToPayload() {
    final PayloadConverter<UpdateName> converter = new UpdateNamePayloadConverter();
    final UpdateName updateName = UpdateName.newBuilder()
        .name(Name.of(randomUUID().toString()))
        .nextOwner(new AergoKeyGenerator().create().getAddress())
        .build();
    final BytesValue payload = converter.convertToPayload(updateName);
    assertNotNull(payload);
  }

}
