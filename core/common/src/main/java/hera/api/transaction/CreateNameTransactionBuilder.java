/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Aer;
import hera.api.model.ChainIdHash;
import hera.api.model.CreateName;
import hera.api.model.Identity;
import hera.api.model.Name;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction.TxType;
import hera.api.transaction.dsl.CreateNameTransaction;
import hera.api.transaction.dsl.CreateNameTransaction.WithChainIdHash;
import hera.api.transaction.dsl.CreateNameTransaction.WithChainIdHashAndSender;
import hera.api.transaction.dsl.CreateNameTransaction.WithChainIdHashAndSenderAndName;
import hera.api.transaction.dsl.CreateNameTransaction.WithReady;


@ApiAudience.Public
@ApiStability.Unstable
public class CreateNameTransactionBuilder implements
    CreateNameTransaction.WithNothing,
    CreateNameTransaction.WithChainIdHash,
    CreateNameTransaction.WithChainIdHashAndSender,
    CreateNameTransaction.WithChainIdHashAndSenderAndName,
    CreateNameTransaction.WithReady {

  protected final PlainTransactionBuilder delegate = new PlainTransactionBuilder();

  protected final PayloadConverter<CreateName> payloadConverter = new CreateNamePayloadConverter();

  protected Name name;

  @Override
  public WithChainIdHash chainIdHash(final ChainIdHash chainIdHash) {
    this.delegate.chainIdHash(chainIdHash);
    return this;
  }

  @Override
  public WithChainIdHashAndSender from(final String sender) {
    this.delegate.from(sender);
    return this;
  }

  @Override
  public WithChainIdHashAndSender from(final Identity sender) {
    this.delegate.from(sender);
    return this;
  }

  @Override
  public WithChainIdHashAndSenderAndName name(final String name) {
    assertNotNull(name);
    return name(new Name(name));
  }

  @Override
  public WithChainIdHashAndSenderAndName name(final Name name) {
    assertNotNull(name);
    this.name = name;
    return this;
  }

  @Override
  public WithReady nonce(long nonce) {
    this.delegate.nonce(nonce);
    return this;
  }

  @Override
  public RawTransaction build() {
    this.delegate.to(Name.AERGO_NAME);
    this.delegate.amount(Aer.AERGO_ONE);
    final CreateName createName = CreateName.newBuilder()
        .name(name)
        .build();
    this.delegate.payload(payloadConverter.convertToPayload(createName));
    this.delegate.type(TxType.GOVERNANCE);
    return this.delegate.build();
  }
}
