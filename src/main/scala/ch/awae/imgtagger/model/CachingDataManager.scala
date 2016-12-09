package ch.awae.imgtagger
package model

import scala.collection.mutable
import ch.awae.scala.util.ref.Ref
import ch.awae.scala.util.ref.RefType
import scala.annotation.tailrec

class CachingDataManager[Key, Value](seeds: List[Key], supplier: Key => Value, hardRange: Int, softRange: Int) extends DataManager[Key, Value] {

  val fullSize = seeds.size

  private val map: mutable.HashMap[Key, Ref[Value, _]] = mutable.HashMap.empty
  private var indexTable: List[Int] = (0 until fullSize).toList
  private var pointer: Int = 0

  // load map with weak refs (not loaded)
  seeds map (s => s -> Ref.weak(() => supplier(s), false)) foreach {
    case (key, value) => map(key) = value
  }
  forceRefUpdate

  def index: Int = synchronized(indexTable(pointer))

  def setSize = synchronized(indexTable.size)

  def current: Option[(Key, Value)] = synchronized {
    if (pointer == -1)
      None
    else {
      val k = seeds(indexTable(pointer))
      val v = map(k)
      Some(k -> v.get)
    }
  }

  private def hasSoft = indexTable.size > (2 * hardRange + 1)
  private def hasWeak = indexTable.size > (2 * softRange + 1)

  private def forceRefUpdate = synchronized {
    // determine ranges
    val hards = (for (i <- -hardRange to hardRange) yield indexTable(pointer + i)).toList.distinct map (seeds(_))
    val unweak = (for (i <- -softRange to softRange) yield indexTable(pointer + i)).toList.distinct map (seeds(_))
    val weaks = indexTable map (seeds(_)) diff unweak
    val softs = unweak diff hards
    // convert refs
    for (id <- weaks) map(id) = map(id).toWeak
    for (id <- softs) map(id) = map(id).toSoft
    for (id <- hards) map(id) = map(id).toHard
  }

  @tailrec
  private def mod(x: Int, n: Int): Int =
    if (x >= n)
      mod(x - n, n)
    else if (x < 0)
      mod(x + n, n)
    else
      x

  def stepForward: Unit = synchronized {
    if (hasWeak) {
      val id_1 = seeds(indexTable(mod(pointer - softRange, setSize)))
      map(id_1) = map(id_1).toWeak
      val id_2 = seeds(indexTable(mod(pointer + softRange + 1, setSize)))
      map(id_2) = map(id_2).toSoft
    }
    if (hasSoft) {
      val id_1 = seeds(indexTable(mod(pointer - hardRange, setSize)))
      map(id_1) = map(id_1).toSoft
      val id_2 = seeds(indexTable(mod(pointer + hardRange + 1, setSize)))
      map(id_2) = map(id_2).toHard
    }
    pointer = mod(pointer + 1, setSize)
  }

  def stepBackward: Unit = synchronized {
    pointer = mod(pointer - 1, setSize)
    if (hasWeak) {
      val id_1 = seeds(indexTable(mod(pointer - softRange, setSize)))
      map(id_1) = map(id_1).toSoft
      val id_2 = seeds(indexTable(mod(pointer + softRange + 1, setSize)))
      map(id_2) = map(id_2).toWeak
    }
    if (hasSoft) {
      val id_1 = seeds(indexTable(mod(pointer - hardRange, setSize)))
      map(id_1) = map(id_1).toHard
      val id_2 = seeds(indexTable(mod(pointer + hardRange + 1, setSize)))
      map(id_2) = map(id_2).toSoft
    }
  }

}