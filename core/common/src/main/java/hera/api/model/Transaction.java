/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@Getter
@ToString
@EqualsAndHashCode
@Builder(builderMethodName = "newBuilder")
public class Transaction {

  @NonNull
  protected final RawTransaction rawTransaction;

  @NonNull
  protected final Signature signature;

  @NonNull
  protected final TxHash hash;

  @NonNull
  @Default
  protected final BlockHash blockHash = BlockHash.EMPTY;

  @Default
  protected final int indexInBlock = 0;

  @Default
  protected final boolean confirmed = false;

  @RequiredArgsConstructor
  public enum TxType {
    UNRECOGNIZED(-1),
    /**
     * Keep it for backward compatibility.
     *
     * @deprecated Use {@link TxType#TRANSFER}, {@link TxType#CALL}, {@link TxType#DEPLOY} instead
     */
    NORMAL(0),
    GOVERNANCE(1),
    REDEPLOY(2),
    FEE_DELEGATION(3),
    TRANSFER(4),
    CALL(5),
    DEPLOY(6);

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
