package ch.awae.imgtagger
package model

/**
 * Read-Only $DM with optional caching.
 *
 * The $DM must ensure thread safety and data
 * consistency. Data is indexed and the indexing must
 * be changeable through re-indexing. Re-Indexing must
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
trait DataManager[+Key, +Value] {

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
  @inline
  def >> = stepForward

  /**
   * Selects the ''previous'' element according to the currently
   * used indexing rules.
   *
   * @note by default this is an alias for `stepBackward`
   */
  @inline
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
   * Re-Indexes the elements according to the provided indexing function.
   *
   * The indexing function `f` provides a `List` with the new indices when
   * given the full list of filtered elements. The following requirement
   * must be satisfied:
   *   - The index `List` provided by `f` must have the same number of
   *       elements as the provided element `List`. (This is equal to `setSize`)
   *   - All indices must fall into the range `[0, setSize-1]`.
   *   - All indices must be unique.
   *   - Therefore all numbers in the range `[0, setSize-1]` must appear
   *       ''exactly'' once.
   *
   * @note $atomic
   *
   * @param f an indexing function satisfying the requirements listed above. $notnull.
   * 					The default value for `f` is a sequential indexer that indexes the list in order.
   * @throws IllegalArgumentException if the indexing function `f` violates a requirement listed above.
   * @throws NullPointerException if the indexing function `f` is `null`.
   */
  def reindex(f: List[Key] => List[Int] = (l: List[Key]) => (0 until l.length).toList): Unit

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

}