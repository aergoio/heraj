/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.BigNumber;
import hera.api.model.BytesValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class AergoJsonMapperTest extends AbstractTestCase {

  protected final JsonMapper mapper = new AergoJsonMapper();

  @Test
  public void testBigNumber() {
    final BigNumber expected = BigNumber.of("1000");
    final BytesValue serialized = mapper.marshal(expected);
    final BigNumber actual = mapper.unmarshal(serialized, BigNumber.class);
    assertEquals(expected, actual);
  }

  @Test
  public void testList() {
    final Map<String, Object> map = new HashMap<>();
    map.put(randomUUID().toString(), randomUUID().toString());
    final List<Object> expected = asList(randomUUID().toString(),
        true,
        null,
        3,
        asList(randomUUID().toString(), randomUUID().toString()),
//        BigNumber.of("3000"),
        map);
    final BytesValue serialized = mapper.marshal(expected);
    final List<Object> actual = mapper.unmarshal(serialized, List.class);
    assertEquals(expected, actual);
  }

  @Test
  public void testMap() {
    final Map<String, String> expected = new HashMap<>();
    expected.put(randomUUID().toString(), randomUUID().toString());
    expected.put(randomUUID().toString(), randomUUID().toString());
    final BytesValue serialized = mapper.marshal(expected);
    final Map<?, ?> actual = mapper.unmarshal(serialized, Map.class);
    assertEquals(expected, actual);
  }

}
