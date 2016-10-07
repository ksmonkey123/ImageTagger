package ch.awae.imgtagger

import scala.util.Random

class ImageManager(
    private var _images: List[String],
    private var _meta: Meta = new Meta) {
  private var filterSet = _images
  private var currentIndex = 0
  private var cycle: Cycle = new LinearCycle(filterSet.size)

  def meta = _meta
  def index = currentIndex + 1
  def totalSize = _images.size
  def filterSize = filterSet.size
  def current = Option(filterSet.applyOrElse(currentIndex, (_: Int) => null))
  def currentTags = current.map(_meta(_))

  // contentUpdate
  def load(imgs: List[String]) {
    _images = imgs
    currentIndex = 0
    filterSet = _images
    cycle = new LinearCycle(filterSet.size)
  }
  def load(meta: Meta) {
    _meta = meta
    currentIndex = 0
    filterSet = _images
    cycle = new LinearCycle(filterSet.size)
  }

  private var rand = false

  def randomise(b: Boolean) = if (b != rand) {
    rand = b
    val s = filterSet.size
    cycle = if (rand) new RandomCycle(s) else new LinearCycle(s)
    cycle.goto(currentIndex)
  }

  // filter application
  def applyFilter(ƒ: TagFilter) = {
    val img = current
    filterSet = _images.filter(i => ƒ(_meta(i)))
    val s = filterSet.size
    cycle = if (rand) new RandomCycle(s) else new LinearCycle(s)
    currentIndex = img.map(filterSet.indexOf).filter(_ >= 0).getOrElse(0)
    cycle.goto(currentIndex)
  }

  // meta update
  def setTags(tags: Set[Tag]) = current foreach (_meta(_) = tags)

  // navigation
  def next = currentIndex = cycle.next

  def previous = currentIndex = cycle.prev

}