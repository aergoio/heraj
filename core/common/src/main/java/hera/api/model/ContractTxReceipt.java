/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.Collections.emptyList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
@Builder(builderMethodName = "newBuilder")
public class ContractTxReceipt {

  @NonNull
  protected final TxReceipt txReceipt;

  @NonNull
  @Default
  @Getter
  protected final BytesValue bloom = BytesValue.EMPTY;

  @NonNull
  @Default
  @Getter
  protected final List<Event> events = emptyList();

  public ContractAddress getContractAddress() {
    return txReceipt.getAccountAddress().adapt(ContractAddress.class);
  }

  public String getStatus() {
    return this.txReceipt.getStatus();
  }

  @Deprecated
  public ContractResult getRet() {
    return ContractResult.of(BytesValue.of(this.txReceipt.getResult().getBytes()));
  }

  public String getResult() {
    return this.txReceipt.getResult();
  }

  public TxHash getTxHash() {
    return this.txReceipt.getTxHash();
  }

  public Aer getFeeUsed() {
    return this.txReceipt.getFeeUsed();
  }

  public Aer getCumulativeFeeUsed() {
    return this.txReceipt.getCumulativeFeeUsed();
  }

  public long getBlockNumber() {
    return this.txReceipt.getBlockNumber();
  }

  public BlockHash getBlockHash() {
    return this.txReceipt.getBlockHash();
  }

  public int getIndexInBlock() {
    return this.txReceipt.getIndexInBlock();
  }

  public AccountAddress getSender() {
    return this.txReceipt.getSender();
  }

  public AccountAddress getRecipient() {
    return this.txReceipt.getRecipient();
  }

  public boolean isFeeDelegation() {
    return this.txReceipt.isFeeDelegation();
  }

  public long getGasUsed() {
    return this.txReceipt.getGasUsed();
  }
}
