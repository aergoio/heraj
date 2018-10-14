/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;

public class ContractCallTest {

  public static final String ENCODED_ADDRESS =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  @Test
  public void testBuild() throws IOException {
    final String args = randomUUID().toString();
    final ContractCall contractCall = new ContractCall(ContractAddress.of(() -> ENCODED_ADDRESS),
        new ContractFunction(), Arrays.asList(new Object[] {args}));
    assertEquals(contractCall,
        ContractCall.newBuilder().setAddress(ContractAddress.of(() -> ENCODED_ADDRESS))
            .setFunction(new ContractFunction()).setArgs(args).build());
  }

}
