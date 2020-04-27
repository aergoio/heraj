/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static hera.api.model.BytesValue.of;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Authentication;
import hera.api.model.BlockHash;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractTxHash;
import hera.api.model.EventFilter;
import hera.api.model.Fee;
import hera.api.model.Identity;
import hera.api.model.Name;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import hera.util.Base58Utils;
import java.io.InputStream;
import java.util.List;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({
    "javax.crypto.*",
    "javax.management.*",
    "javax.net.ssl.*",
    "javax.security.*",
    "org.bouncycastle.*"})
public abstract class AbstractTestCase {

  protected final transient Logger logger = getLogger(getClass());

  protected final ChainIdHash anyChainIdHash = ChainIdHash
      .of(BytesValue.of(randomUUID().toString().getBytes()));
  protected final Signer anySigner = new AergoKeyGenerator().create();
  protected final AccountAddress anyAccountAddress = new AergoKeyGenerator().create().getAddress();
  protected final long anyBlockNumber = Math.abs(randomUUID().toString().hashCode());
  protected final long anyNonce = Math.abs(randomUUID().toString().hashCode());
  protected final Aer anyAmount = Aer.AERGO_ONE;
  protected final Fee anyFee = Fee.of(Math.abs(randomUUID().toString().hashCode()));
  protected final BytesValue anyPayload = BytesValue.of(randomUUID().toString().getBytes());


  protected final RawTransaction anyRawTransaction;
  protected final Transaction anyTransaction;

  protected final Name anyName = Name.of(randomUUID().toString());

  protected final String anyVoteId = randomUUID().toString();
  protected final List<String> anyCandidates = asList(randomUUID().toString(),
      randomUUID().toString());

  protected final BlockHash anyBlockHash = BlockHash
      .of(BytesValue.of(randomUUID().toString().getBytes()));
  protected final TxHash anyTxHash = TxHash.of(BytesValue.of(randomUUID().toString().getBytes()));
  protected final int anySize = Math.abs(randomUUID().toString().hashCode());
  protected final long anyHeight = Math.abs(randomUUID().toString().hashCode());

  protected final ContractAddress anyContractAddress = new AergoKeyGenerator().create().getAddress()
      .adapt(ContractAddress.class);
  protected final ContractTxHash anyContractTxHash = ContractTxHash
      .of(BytesValue.of(randomUUID().toString().getBytes()));
  protected final ContractDefinition anyDefinition = ContractDefinition.newBuilder()
      .encodedContract(Base58Utils.encodeWithCheck(new byte[]{ContractDefinition.PAYLOAD_VERSION}))
      .build();
  protected final ContractInvocation anyInvocation = ContractInvocation.newBuilder()
      .address(anyContractAddress)
      .functionName(randomUUID().toString())
      .build();
  protected final EventFilter anyEventFilter = EventFilter.newBuilder(anyContractAddress).build();

  protected final Identity anyIdentity = new AergoKeyGenerator().create().getAddress();
  protected final String anyPassword = randomUUID().toString();
  protected final Authentication anyAuthentication = Authentication.of(anyIdentity, anyPassword);

  {
    final AergoKey signer = new AergoKeyGenerator().create();
    anyRawTransaction = RawTransaction
        .newBuilder(ChainIdHash.of(BytesValue.EMPTY))
        .from(signer.getAddress())
        .to(signer.getAddress())
        .amount(Aer.ZERO)
        .nonce(1L)
        .build();
    anyTransaction = signer.sign(anyRawTransaction);
  }

  protected InputStream open(final String ext) {
    final String path = "/" + getClass().getName().replace('.', '/') + "." + ext;
    logger.trace("Path: {}", path);
    System.out.println(path);
    return getClass().getResourceAsStream(path);
  }

}
