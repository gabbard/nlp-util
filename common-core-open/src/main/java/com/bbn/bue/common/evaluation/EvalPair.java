package com.bbn.bue.common.evaluation;

import com.google.common.base.Function;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A pair of things being compared to each other, where one is in some sense "the correct one" (the
 * key).
 *
 * @param <KeyT>  The type of the "correct" item
 * @param <TestT> The type of the other item
 */
public final class EvalPair<KeyT, TestT> {

  private final KeyT keyItem;
  private final TestT testItem;

  private EvalPair(final KeyT keyItem, final TestT testItem) {
    this.keyItem = checkNotNull(keyItem);
    this.testItem = checkNotNull(testItem);
  }

  /**
   * The correct item.
   */
  public KeyT key() {
    return keyItem;
  }

  /**
   * The other item
   */
  public TestT test() {
    return testItem;
  }

  /**
   * Create an {@code EvalPair}. The "correct" (key) item comes first.
   */
  public static <KeyT, TestT> EvalPair<KeyT, TestT> of(
      KeyT key, TestT test) {
    return new EvalPair<KeyT, TestT>(key, test);
  }

  /**
   * Turns a Guava {@link Function} into another {@code Function} which operates over both sides of
   * an {@link EvalPair}.
   */
  public static <F, T, KeyT extends F, TestT extends F>
  Function<EvalPair<? extends KeyT, ? extends TestT>, EvalPair<T, T>> functionOnBoth(
      final Function<F, T> func) {
    return new Function<EvalPair<? extends KeyT, ? extends TestT>, EvalPair<T, T>>() {
      @Override
      public EvalPair<T, T> apply(final EvalPair<? extends KeyT, ? extends TestT> input) {
        return EvalPair.of(func.apply(input.key()), func.apply(input.test()));
      }

      @Override
      public String toString() {
        return "EvalPair.functionOnBoth(" + func + ")";
      }
    };
  }
}
