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
      + "   \"AccountsSvc\": {\n"
      + "       \"status\": \"started\",\n"
      + "       \"acc_processed_msg\": 104,\n"
      + "       \"msg_queue_len\": 104,\n"
      + "       \"msg_latency\": \"47.169µs\",\n"
      + "       \"error\": \"\",\n"
      + "       \"actor\": null\n"
      + "   },\n"
      + "   \"ChainSvc\": {\n"
      + "       \"status\": \"started\",\n"
      + "       \"acc_processed_msg\": 24160,\n"
      + "       \"msg_queue_len\": 24160,\n"
      + "       \"msg_latency\": \"40.64µs\",\n"
      + "       \"error\": \"\",\n"
      + "       \"actor\": {\n"
      + "           \"orphan\": 0\n"
      + "       }\n"
      + "   },\n"
      + "   \"MemPoolSvc\": {\n"
      + "       \"status\": \"started\",\n"
      + "       \"acc_processed_msg\": 16044,\n"
      + "       \"msg_queue_len\": 16044,\n"
      + "       \"msg_latency\": \"43.539µs\",\n"
      + "       \"error\": \"\",\n"
      + "       \"actor\": {\n"
      + "           \"cache_len\": 0,\n"
      + "           \"orphan\": 0\n"
      + "       }\n"
      + "   },\n"
      + "   \"p2pSvc\": {\n"
      + "       \"status\": \"started\",\n"
      + "       \"acc_processed_msg\": 8027,\n"
      + "       \"msg_queue_len\": 8027,\n"
      + "       \"msg_latency\": \"40.533µs\",\n"
      + "       \"error\": \"\",\n"
      + "       \"actor\": null\n"
      + "   },\n"
      + "   \"rpc\": {\n"
      + "       \"status\": \"started\",\n"
      + "       \"acc_processed_msg\": 12,\n"
      + "       \"msg_queue_len\": 12,\n"
      + "       \"msg_latency\": \"44.571µs\",\n"
      + "       \"error\": \"\",\n"
      + "       \"actor\": null\n"
      + "   }\n"
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
