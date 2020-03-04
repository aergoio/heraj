/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.BytesValue;
import hera.api.model.CreateName;
import hera.api.model.Name;
import org.junit.Test;

public class CreateNamePayloadConverterTest extends AbstractTestCase {

  @Test
  public void testConvertToPayload() {
    final PayloadConverter<CreateName> converter = new CreateNamePayloadConverter();
    final CreateName createName = CreateName.newBuilder()
        .name(Name.of(randomUUID().toString()))
        .build();
    final BytesValue payload = converter.convertToPayload(createName);
    assertNotNull(payload);
  }

}
