package ch.awae.imgtagger
package model

import sun.security.util.Length

/**
 * Read-Only $DM with optional caching.
 *
 * The $DM must ensure thread safety and data
 * consistency. Data is sorted and the sorting must
 * be changeable through re-sorting. Re-Sorting must
 * also be thread safe. The $DM is always keeping track
 * of on ''selected'' element. Other items can be selected
 * by use of the navigation methods `stepForward`,
 * `stepBackward`, `<<` and `>>`. Elements can be filtered.
 * The navigation methods always operate on the filtered
 * element set. There is always exactly one selected element.
 * The only exceptions are if the element set is empty (`fullSize == 0`)
 * and/or the current filtered element set is empty (`setSize == 0`)
 *
 * @author Andreas Wälchli <andreas.waelchli@me.com>
 * @since 0.4.0
 *
 * @tparam Key the type of the key instances
 * @tparam Value the type of the value instances managed by this $DM
 * @define DM `DataManager`
 * @define atomic '''''The execution of this method must be thread-safe and virtually atomic. This may require synchronisation.'''''
 * @define notnull ''May not be ''`null`
 */
trait DataManager[Key, Value] {

  /**
   * The index of the current element.
   * If the current result set is empty (`setSize == 0`),
   * the index is `-1`
   *
   * @note $atomic
   *
   * @return an integer in the range `[0, setSize-1]`
   * or `-1` iff `setSize == 0`
   */
  def index: Int

  /**
   * The size of the current result set. On initialisation
   * no filter should be active and the elements should be
   * indexed sequentially.
   *
   * @note $atomic
   *
   * @return an integer in the range `[0, fullSize]`
   */
  def setSize: Int

  /**
   * The full number of elements in the $DM
   *
   *
   * @note $atomic
   *
   * @return a positive integer (or zero)
   */
  def fullSize: Int

  /**
   * Selects the ''next'' element according to the currently
   * used indexing rules.
   *
   * @note $atomic
   */
  def stepForward: Unit

  /**
   * Selects the ''previous'' element according to the currently
   * used indexing rules.
   *
   * @note $atomic
   */
  def stepBackward: Unit

  /**
   * Selects the ''next'' element according to the currently
   * used indexing rules.
   *
   * @note by default this is an alias for `stepForward`
   */
  def >> = stepForward

  /**
   * Selects the ''previous'' element according to the currently
   * used indexing rules.
   *
   * @note by default this is an alias for `stepBackward`
   */
  def << = stepBackward

  /**
   * Provides the currently selected key-value pair.
   *
   * @note $atomic
   *
   * @return `Some` element or `None` iff no element is selected (`index == -1`)
   */
  def current: Option[(Key, Value)]

  @inline
  def currentValue = current map (_._2)

  @inline
  def currentKey = current map (_._1)

  /**
   * Sorts the elements according to the provided sorting function.
   *
   * The sorting function `f` sorts a list of elements:
   *   - All elements in the input list must appear in the output
   *   		list ''exactly'' once.
   *   - All elements of the output list must be unique.
   *   - No new elements may be introduced into the output list.
   *   - Therefore the output list must be a permutation of the input list.
   *
   * @note $atomic
   *
   * @param f a sorting function satisfying the requirements listed above. $notnull.
   * 					The default value for `f` is taken from the `default_sorter`.
   * @throws IllegalArgumentException if the sorting function `f` violates a requirement listed above.
   * @throws NullPointerException if the sorting function `f` is `null`.
   */
  def sort(f: List[Key] => List[Key] = default_sorter): Unit

  /**
   * the default sorter
   *
   * By default this returns the input list itself
   */
  def default_sorter = (l: List[Key]) => l

  /**
   * Filters the elements according to the provided predicate.
   *
   * @note $atomic
   *
   * @param f a predicate selecting elements. $notnull.
   * 					The default value for `f` is a predicate with a fixed return value of `true`.
   * @throws NullPointerException if the filtering predicate `f` is `null`.
   */
  def filter(f: Key => Boolean = _ => true): Unit

  /**
   * Resets the data manager.
   *
   * Resets the filter and the sorter to the `default_sorter`.
   * Jumps back to the first image (`index = 0`).
   *
   * @note $atomic
   */
  def reset: Unit

}