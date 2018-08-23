/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import com.google.protobuf.ByteString;
import hera.AbstractTestCase;
import hera.api.model.NodeStatus;
import org.junit.Test;
import types.Rpc;

public class NodeStatusConverterTest extends AbstractTestCase {

  protected static final String nodeStateInJson = "{\n"
      + "    \"AccountsSvc\": {\n"
      + "        \"component\": {\n"
      + "            \"status\": \"started\",\n"
      + "            \"acc_processed_msg\": 3,\n"
      + "            \"acc_queued_msg\": 3,\n"
      + "            \"msg_latency\": \"38.13µs\",\n"
      + "            \"error\": \"\"\n"
      + "        }\n"
      + "    },\n"
      + "    \"ChainSvc\": {\n"
      + "        \"blockchain\": {\n"
      + "            \"orphan\": 0\n"
      + "        },\n"
      + "        \"component\": {\n"
      + "            \"status\": \"started\",\n"
      + "            \"acc_processed_msg\": 67,\n"
      + "            \"acc_queued_msg\": 67,\n"
      + "            \"msg_latency\": \"39.881µs\",\n"
      + "            \"error\": \"\"\n"
      + "        }\n"
      + "    },\n"
      + "    \"MemPoolSvc\": {\n"
      + "        \"component\": {\n"
      + "            \"status\": \"started\",\n"
      + "            \"acc_processed_msg\": 45,\n"
      + "            \"acc_queued_msg\": 45,\n"
      + "            \"msg_latency\": \"35.468µs\",\n"
      + "            \"error\": \"\"\n"
      + "        },\n"
      + "        \"mempool\": {\n"
      + "            \"cache_len\": 1,\n"
      + "            \"orphan\": 1\n"
      + "        }\n"
      + "    },\n"
      + "    \"p2pSvc\": {\n"
      + "        \"component\": {\n"
      + "            \"status\": \"started\",\n"
      + "            \"acc_processed_msg\": 25,\n"
      + "            \"acc_queued_msg\": 31,\n"
      + "            \"msg_latency\": \"37.541µs\",\n"
      + "            \"error\": \"\"\n"
      + "        }\n"
      + "    },\n"
      + "    \"rpc\": {\n"
      + "        \"component\": {\n"
      + "            \"status\": \"started\",\n"
      + "            \"acc_processed_msg\": 3,\n"
      + "            \"acc_queued_msg\": 3,\n"
      + "            \"msg_latency\": \"44.167µs\",\n"
      + "            \"error\": \"\"\n"
      + "        }\n"
      + "    }\n"
      + "}\n"
      + "";

  @Test
  public void testConvert() {
    final ByteString byteString = ByteString.copyFromUtf8(nodeStateInJson);
    final Rpc.SingleBytes rawModuleStatus =
        Rpc.SingleBytes.newBuilder().setValue(byteString).build();
    final ModelConverter<NodeStatus, Rpc.SingleBytes> converter =
        new NodeStatusConverterFactory().create();
    final NodeStatus actualDomainNodeStatus = converter.convertToDomainModel(rawModuleStatus);
    assertNotNull(actualDomainNodeStatus);
  }

}
