/*
 * @copyright defined in LICENSE.txt
 */

package hera.repl;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class WordsProcessor {
  protected Supplier<Command> commandFactory;

  protected Map<String, WordsProcessor> adjacents = new HashMap<>();

  /**
   * Register word chain and command factory.
   *
   * @param words word chain
   * @param commandFactory command factory
   */
  public void add(final List<String> words, final Supplier<Command> commandFactory) {
    if (null == words || words.isEmpty()) {
      this.commandFactory = commandFactory;
    } else {
      final String word = words.get(0);
      final List<String> remainder = words.subList(1, words.size());
      final WordsProcessor next = new WordsProcessor();
      next.add(remainder, commandFactory);
      adjacents.put(word, next);
    }
  }

  /**
   * Find command factory and create command toSecureAccount it.
   *
   * @param words word path
   *
   * @return created command if exists
   */
  public Optional<Command> find(final List<String> words) {
    if (null == commandFactory) {
      if (null == words || words.isEmpty()) {
        return empty();
      } else {
        final String word = words.get(0);
        final List<String> remainder = words.subList(1, words.size());
        return ofNullable(adjacents.get(word)).flatMap(next -> next.find(remainder));
      }
    } else {
      final Command command = commandFactory.get();
      command.setArguments(ofNullable(words).orElse(emptyList()));
      return ofNullable(command);
    }
  }
}
