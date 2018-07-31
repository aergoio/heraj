/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl.command;

import hera.api.model.AccountAddress;
import hera.api.model.Transaction;
import hera.repl.AccountRepository;
import hera.repl.CommandContext;
import hera.repl.CommandResult;
import hera.util.HexUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Transfer extends AbstractCommand {

  protected final AccountRepository accountRepository;

  @Override
  public CommandResult execute(final CommandContext context) throws Exception {
    if (arguments.size() < 3) {
      return new CommandResult(() -> "Arguments needed: [SENDER] [RECIPIENT] [AMOUNT]");
    } else if (3 < arguments.size()) {
      return new CommandResult(() -> "Many arguments found");
    }
    final String senderStr = arguments.get(0);
    final String recipientStr = arguments.get(1);
    final String amountStr = arguments.get(2);

    final byte[] sender = HexUtils.decode(senderStr);
    final byte[] recipient = HexUtils.decode(recipientStr);
    final long amount = Long.parseLong(amountStr);

    final Transaction transaction = new Transaction();
    transaction.setNonce(1);
    transaction.setSender(AccountAddress.of(sender));
    transaction.setRecipient(AccountAddress.of(recipient));
    transaction.setAmount(amount);
    accountRepository.sendTransaction(transaction);
    return new CommandResult(() -> amount + " transferred: " + senderStr + " -> " + recipientStr);
  }
}
