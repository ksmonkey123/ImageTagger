package ch.awae.imgtagger

import scala.util.Random

trait Cycle {
  def goto(id: Int)
  def next: Int
  def prev: Int
  def current: Int
}

class LinearCycle(val size: Int) extends Cycle {

  private var _current = 0

  def goto(id: Int) = _current = id % size
  def current = _current
  def next = {
    _current = (_current + 1) % size
    _current
  }
  def prev = {
    _current = (_current - 1 + size) % size
    _current
  }

}

class RandomCycle(val size: Int) extends Cycle {
  private var index = 0
  private val cycle = Stream.continually(Random.nextInt(size)).distinct.take(size).toList

  def current = cycle(index)
  def next = {
    index = (index + 1) % size
    cycle(index)
  }
  def prev = {
    index = (index - 1 + size) % size
    cycle(index)
  }
  def goto(id: Int) = index = cycle.indexOf(id)

}