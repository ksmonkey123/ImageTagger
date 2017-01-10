package ch.awae.imgtagger
package model

import scala.collection.mutable

import ch.awae.scala.util.ref.Ref
import ch.awae.scala.util.Resettable

class CachingDataManager[Key, Value](seeds: List[Key], supplier: Key => Value, hardRange: Int, softRange: Int) extends DataManager[Key, Value] {

  val fullSize = seeds.size

  private val map: mutable.HashMap[Key, Ref[Value, _]] = mutable.HashMap.empty
  // index: sequence, value: target
  private var keySet: List[Key] = default_sorter(seeds)
  private var pointer: Int = 0

  // load map with weak refs (not loaded)
  seeds map (s => s -> Ref.weak(() => supplier(s), false)) foreach {
    case (key, value) => map(key) = value
  }
  forceRefUpdate

  def index: Int = synchronized(seeds indexOf keySet(pointer))

  def setSize = synchronized(keySet.size)

  def current: Option[(Key, Value)] = synchronized {
    if (pointer == -1)
      None
    else {
      val k = keySet(pointer)
      val v = map(k)
      Some(k -> v.get)
    }
  }

  private def forceRefUpdate = synchronized {
    // determine ranges
    val hards =
      (-hardRange to hardRange)
        .map(i => (i + pointer) %+ setSize)
        .distinct
        .map(keySet)
        .toList

    val unweak =
      (-softRange to softRange)
        .map(i => (i + pointer) %+ setSize)
        .distinct
        .map(keySet)
        .toList

    val weaks = seeds diff unweak
    val softs = unweak diff hards
    // convert refs
    for (id <- weaks) map(id) = map(id).toWeak
    for (id <- softs) map(id) = map(id).toSoft
    for (id <- hards) map(id) = map(id).toHard
  }

  def stepForward: Unit = synchronized {
    pointer = (pointer + 1) %+ setSize
    forceRefUpdate
  }

  def stepBackward: Unit = synchronized {
    pointer = (pointer - 1) %+ setSize
    forceRefUpdate
  }

  private var _filter: Resettable[Key => Boolean] = Resettable(_ => true)
  private var _sorter: Resettable[List[Key] => List[Key]] = Resettable(default_sorter)

  def filter(f: Key => Boolean): Unit = synchronized {
    _filter = _filter set f
    updateKeySet
  }

  def sort(f: List[Key] => List[Key]): Unit = synchronized {
    _sorter = _sorter set f
    updateKeySet
  }

  private def updateKeySet = {
    val activeKey = currentKey
    keySet = this._sorter()(seeds.filter(this._filter()))
    val newIndex = activeKey.map(keySet.indexOf).getOrElse(-1)
    pointer = if (newIndex == -1) if (setSize == 0) -1 else 0 else newIndex
    forceRefUpdate
  }

  def reset: Unit = {
    _filter = _filter.reset
    _sorter = _sorter.reset
    updateKeySet
  }

}