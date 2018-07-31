/*
 * @copyright defined in LICENSE.txt
 */

package hera.exec;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Optional.ofNullable;

import hera.Context;
import hera.REPL;
import hera.api.AergoApi;
import hera.repl.AccountRepository;
import hera.repl.command.CreateAccount;
import hera.repl.command.Exit;
import hera.repl.command.ListAccounts;
import hera.repl.command.LockAccount;
import hera.repl.command.RemoveAccount;
import hera.repl.command.Transfer;
import hera.repl.command.UnlockAccount;
import hera.repl.repository.JdbcAccountRepository;
import hera.repl.repository.ServerAccountRepository;
import hera.strategy.AergoClientApiStrategy;
import hera.strategy.NettyConnectStrategy;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class REPLLauncher {

  protected AccountRepository getJdbcAccountRepository() throws ClassNotFoundException {
    // Check user home
    final String userHomePath = ofNullable(System.getProperty("user.home"))
        .orElseGet(() -> System.getenv("HOME"));
    assertNotNull(userHomePath);

    Class.forName("org.h2.Driver");
    return new JdbcAccountRepository(() -> {
      try {
        return DriverManager.getConnection("jdbc:h2:file:" + userHomePath + "/.aergo/h2db");
      } catch (final SQLException e) {
        throw new IllegalArgumentException(e);
      }
    });
  }

  protected AccountRepository getServerAccountRepository() {
    final AergoApi aergoApi = new Context()
        .addStrategy(new NettyConnectStrategy())
        .addStrategy(new AergoClientApiStrategy())
        .api();
    return new ServerAccountRepository(aergoApi);
  }

  /**
   * Entry point for repl.
   *
   * @param args user arguments
   *
   * @throws Exception Failure in repl
   */
  public static void main(final String[] args) throws Exception {
    new REPLLauncher().run();
  }

  /**
   * Run repl launch.
   *
   * @throws Exception On failure of launch
   */
  public void run() throws Exception {
    // Load jdbc driver and set repository up
    final AccountRepository accountRepository = getServerAccountRepository();

    // Set jline up
    final Terminal terminal = TerminalBuilder.terminal();
    final LineReader lineReader = LineReaderBuilder.builder()
        .terminal(terminal)
        .appName("aergo")
        .build();

    // Assemble repl
    final REPL repl = new REPL(lineReader);
    repl.addCommand("exit", Exit::new);
    repl.addCommand("quit", Exit::new);
    repl.addCommand("list accounts", () -> new ListAccounts(accountRepository));
    repl.addCommand("create account", () -> new CreateAccount(accountRepository));
    repl.addCommand("remove account", () -> new RemoveAccount(accountRepository));
    repl.addCommand("lock", () -> new LockAccount(accountRepository));
    repl.addCommand("unlock", () -> new UnlockAccount(accountRepository));
    repl.addCommand("transfer", () -> new Transfer(accountRepository));
    repl.run();

    accountRepository.close();
  }

}
