/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.transaction;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInvocation;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class ContractInvocationInfoExtractorTest extends AbstractTestCase {

  @Test
  public void testExtract() {
    // when
    final ContractAddress address =
        new AergoKeyGenerator().create().getAddress().adapt(ContractAddress.class);
    final ContractFunction function = new ContractFunction(randomUUID().toString());
    final Map<String, Object> map = new HashMap<>();
    map.put(randomUUID().toString(), randomUUID().toString());
    final List<Object> args = asList(new Object[] {
        randomUUID().toString(),
        true,
        null,
        3,
        asList(new Object[] {randomUUID().toString(), randomUUID().toString()}),
        map
    });
    // final Object mapArg = new BigNumber("3000");
    // final Object bignumArg = new BigNumber("3000");

    final ContractInvocation expected = ContractInvocation.newBuilder()
        .address(address)
        .function(function)
        .args(args)
        .amount(Aer.AERGO_ONE)
        .build();
    final AergoKey key = new AergoKeyGenerator().create();
    final RawTransaction rawTransaction = RawTransaction.newInvokeContractBuilder()
        .chainIdHash(ChainIdHash.of(BytesValue.EMPTY))
        .from(key.getAddress())
        .nonce(1L)
        .invocation(expected)
        .build();
    final Transaction signed = key.sign(rawTransaction);

    // then
    final TransactionInfoExtractor<ContractInvocation> extractor =
        new TransactionInfoExtractorFactory().create(ContractInvocation.class);
    final ContractInvocation actual = extractor.extract(signed);
    assertEquals(expected, actual);
  }

}
