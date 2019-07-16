/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@ApiAudience.Public
@ApiStability.Unstable
@Value
@Builder(builderMethodName = "newBuilder")
public class Transaction {

  @NonNull
  RawTransaction rawTransaction;

  @NonNull
  @Default
  Signature signature = Signature.newBuilder().build();

  @NonNull
  @Default
  TxHash hash = TxHash.of(BytesValue.EMPTY);

  @NonNull
  @Default
  BlockHash blockHash = BlockHash.of(BytesValue.EMPTY);

  int indexInBlock;

  @Default
  boolean confirmed = false;

  @RequiredArgsConstructor
  public enum TxType {
    UNRECOGNIZED(-1), NORMAL(0), GOVERNANCE(1), REDEPLOY(2);

    @Getter
    private final int intValue;
  }

  public ChainIdHash getChainIdHash() {
    return rawTransaction.getChainIdHash();
  }

  public AccountAddress getSender() {
    return rawTransaction.getSender();
  }

  public AccountAddress getRecipient() {
    return rawTransaction.getRecipient();
  }

  public Aer getAmount() {
    return rawTransaction.getAmount();
  }

  public Long getNonce() {
    return rawTransaction.getNonce();
  }

  public Fee getFee() {
    return rawTransaction.getFee();
  }

  public BytesValue getPayload() {
    return rawTransaction.getPayload();
  }

  public TxType getTxType() {
    return rawTransaction.getTxType();
  }

}
