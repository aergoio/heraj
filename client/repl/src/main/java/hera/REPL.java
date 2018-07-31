/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.Arrays.asList;

import hera.repl.Command;
import hera.repl.CommandContext;
import hera.repl.CommandResult;
import hera.repl.WordsProcessor;
import hera.repl.command.NoOp;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.jline.reader.LineReader;

/**
 * Read-Evaluate-Print-Loop.
 */
@RequiredArgsConstructor
public class REPL implements Runnable {

  protected final CommandContext context = new CommandContext();

  protected WordsProcessor wordsProcessor = new WordsProcessor();

  public REPL(final LineReader lineReader) {
    context.setLineReader(lineReader);
  }

  public void addCommand(String sentence, Supplier<Command> commandFactory) {
    final String[] words = sentence.split(" ");
    wordsProcessor.add(asList(words), commandFactory);
  }

  /**
   * Execute repl.
   */
  public void run() {
    while (context.isAlive()) {
      try {
        final Command command = read();
        final CommandResult result = evaluate(command);
        print(result);
      } catch (final Throwable ex) {
        ex.printStackTrace();
      }
    }
  }

  /**
   * Read operation.
   *
   * @return command to be parsed
   */
  public Command read() {
    final String line = context.getLineReader().readLine();
    if (null == line) {
      return new NoOp();
    }
    final StringTokenizer tokenizer = new StringTokenizer(line);
    final ArrayList<String> words = new ArrayList<>();
    while (tokenizer.hasMoreTokens()) {
      words.add(tokenizer.nextToken());
    }
    if (words.isEmpty()) {
      return new NoOp();
    }

    return wordsProcessor.find(words)
        .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + line));
  }

  /**
   * Evaluate operation.
   *
   * @param command command to evaluate
   *
   * @return result of evaluation
   *
   * @throws Exception Fail to execute command
   */
  public CommandResult evaluate(final Command command) throws Exception {
    return command.execute(context);
  }

  /**
   * Print operation.
   *
   * @param result result to print out
   */
  public void print(final CommandResult result) {
    System.out.println(result.toString());
  }

}
