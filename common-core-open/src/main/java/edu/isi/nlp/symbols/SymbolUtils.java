package edu.isi.nlp.symbols;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import edu.isi.nlp.converters.StringConverter;
import java.util.Map;

/**
 * Utility methods for {link Symbol}s
 *
 * @author rgabbard
 * @author clignos
 */
@Beta
public class SymbolUtils {

  private SymbolUtils() {
    throw new UnsupportedOperationException();
  }

  private static final Ordering<Symbol> symbolStringOrdering =
      Ordering.natural().onResultOf(desymbolizeFunction());

  /**
   * An ordering which compares <code>Symbol</code>s by the <code>String</code>s used to create
   * them.
   */
  public static Ordering<Symbol> byStringOrdering() {
    return symbolStringOrdering;
  }

  public static StringConverter<Symbol> StringToSymbol() {
    return StringToSymbolConverter.INSTANCE;
  }

  private enum StringToSymbolConverter implements StringConverter<Symbol> {
    INSTANCE;

    @Override
    public Symbol decode(final String s) {
      return Symbol.from(s);
    }
  }

  /**
   * Returns a function that transforms a {@link Symbol} into a {@link String} using {@link
   * Symbol#asString()}.
   */
  public static Function<Symbol, String> desymbolizeFunction() {
    return DesymbolizeFunction.INSTANCE;
  }

  private enum DesymbolizeFunction implements Function<Symbol, String> {
    INSTANCE;

    @Override
    public String apply(final Symbol s) {
      return s.asString();
    }
  }

  /**
   * Returns a function that transforms a {@link String} into a {@link Symbol} using {@link
   * Symbol#from(String)}.
   */
  public static Function<String, Symbol> symbolizeFunction() {
    return SymbolizeFunction.INSTANCE;
  }

  private enum SymbolizeFunction implements Function<String, Symbol> {
    INSTANCE;

    @Override
    public Symbol apply(final String s) {
      return Symbol.from(s);
    }
  }

  /**
   * Creates a <code>Set</code> of Symbols from some strings. The returned <code>Set</code> is
   * immutable.
   *
   * @param strings No string may be null.
   */
  public static ImmutableSet<Symbol> setFrom(final Iterable<String> strings) {
    checkNotNull(strings);

    return FluentIterable.from(strings).transform(symbolizeFunction()).toSet();
  }

  /**
   * Creates a <code>List</code> of Symbols from some strings. The returned <code>List</code> is
   * immutable.
   *
   * @param strings No string may be null.
   */
  public static ImmutableList<Symbol> listFrom(final Iterable<String> strings) {
    checkNotNull(strings);
    return FluentIterable.from(strings).transform(symbolizeFunction()).toList();
  }

  public static ImmutableSet<String> toStringSet(final Iterable<Symbol> syms) {
    final ImmutableSet.Builder<String> ret = ImmutableSet.builder();

    for (final Symbol sym : syms) {
      ret.add(sym.toString());
    }

    return ret.build();
  }

  public static ImmutableSet<Symbol> setFrom(final String... strings) {
    final ImmutableSet.Builder<Symbol> ret = ImmutableSet.builder();
    for (final String s : strings) {
      ret.add(Symbol.from(s));
    }
    return ret.build();
  }

  /**
   * Returns a lowercased version of the specified symbol, where lowercasing is done by {@link
   * String#toLowerCase()}.
   */
  public static Symbol lowercaseSymbol(Symbol s) {
    return Symbol.from(s.toString().toLowerCase());
  }

  /**
   * Creates a map of {@link Symbol}s from a map of {@link String}s. No keys or values may be null.
   */
  public static ImmutableMap<Symbol, Symbol> mapFrom(Map<String, String> stringMap) {
    final ImmutableMap.Builder<Symbol, Symbol> ret = ImmutableMap.builder();

    for (Map.Entry<String, String> stringEntry : stringMap.entrySet()) {
      ret.put(Symbol.from(stringEntry.getKey()), Symbol.from(stringEntry.getValue()));
    }

    return ret.build();
  }

  public static Symbol concat(Symbol x, Symbol y) {
    return Symbol.from(x.asString() + y.asString());
  }

  public static Symbol concat(String x, Symbol y) {
    return Symbol.from(Symbol.from(x) + y.asString());
  }

  public static Symbol concat(Symbol x, String y) {
    return Symbol.from(x.asString() + y);
  }
}
