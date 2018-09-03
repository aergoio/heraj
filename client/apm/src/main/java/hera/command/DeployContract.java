/*
 * @copyright defined in LICENSE.txt
 */

package hera.command;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.AergoApi;
import hera.api.ContractOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.ContractTxHash;
import hera.util.DangerousSupplier;
import java.io.InputStream;
import lombok.Getter;
import lombok.Setter;

public class DeployContract extends AbstractCommand {

  @Getter
  @Setter
  protected DangerousSupplier<AergoApi> apiSupplier;

  @Getter
  @Setter
  protected DangerousSupplier<Account> account;

  @Getter
  @Setter
  protected DangerousSupplier<InputStream> payload;

  @Getter
  protected ContractTxHash contractTxHash;

  @Override
  public void execute() throws Exception {
    final AergoApi aergoApi = apiSupplier.get();
    final ContractOperation contractOperation = aergoApi.getContractOperation();
    final AccountAddress accountAddress = this.account.get().getAddress();
    logger.debug("Address: {}", accountAddress);
    contractTxHash = contractOperation.deploy(accountAddress, payload).getResult();
    assertNotNull(contractTxHash);
  }
}
