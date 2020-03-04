/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.Aer;
import hera.api.model.BigNumber;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInvocation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class ContractInvocationPayloadConverterTest extends AbstractTestCase {

  @Test
  public void testConvertBetween() {
    // when
    final ContractFunction function = new ContractFunction(randomUUID().toString());
    final Map<String, Object> map = new HashMap<>();
    map.put(randomUUID().toString(), randomUUID().toString());
    final List<Object> args = asList(new Object[] {
        randomUUID().toString(),
        true,
        null,
        3,
        asList(new Object[] {randomUUID().toString(), randomUUID().toString()}),
        BigNumber.of("3000"),
        map
    });

    final PayloadConverter<ContractInvocation> converter = new ContractInvocationPayloadConverter();
    final ContractInvocation expected = ContractInvocation.newBuilder()
        .function(function)
        .args(args)
        .address(ContractAddress.EMPTY)
        .amount(Aer.EMPTY)
        .build();
    final BytesValue payload = converter.convertToPayload(expected);
    final ContractInvocation actual = converter.parseToModel(payload);
    assertEquals(expected, actual);
  }

}
