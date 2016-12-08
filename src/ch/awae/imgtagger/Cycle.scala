package ch.awae.imgtagger

import scala.util.Random

trait Cycle {
  def goto(id: Int)
  def next: Int
  def prev: Int
  def current: Int
  def peekNext = apply(1)
  def apply(i: Int): Int
}

class LinearCycle(val size: Int) extends Cycle {

  private var _current = 0

  def goto(id: Int) = if (size > 0) _current = id % size
  def current = _current
  def next = {
    _current = if (size > 0) (_current + 1) % size else 0
    _current
  }
  def prev = {
    _current = if (size > 0) (_current - 1 + size) % size else 0
    _current
  }

  def apply(i: Int) =
    if (i >= 0) {
      if (size > 0) (_current + i) % size else 0
    } else {
      if (size > 0) (_current + (size - ((-i % size)))) % size else 0
    }
}

class RandomCycle(val size: Int) extends Cycle {
  private var index = 0
  private val cycle = Stream.continually(Random.nextInt(size)).distinct.take(size).toList

  def current = cycle(index)
  def next = if (size > 0) {
    index = (index + 1) % size
    cycle(index)
  } else 0
  def prev = if (size > 0) {
    index = (index - 1 + size) % size
    cycle(index)
  } else 0
  def goto(id: Int) = index = cycle.indexOf(id)
  def apply(i: Int) = if (size > 0) cycle(if (i >= 0) {
    if (size > 0) (index + i) % size else 0
  } else {
    if (size > 0) (index + (size - ((-i % size)))) % size else 0
  })
  else 0

}