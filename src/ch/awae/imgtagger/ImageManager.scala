package ch.awae.imgtagger

import scala.util.Random

class ImageManager(
    private var _images: List[String],
    private var _meta: Meta = new Meta) {
  private var filterSet = _images
  private var currentIndex = 0

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
  }
  def load(meta: Meta) {
    _meta = meta
    currentIndex = 0
    filterSet = _images
  }

  // filter application
  def applyFilter(ƒ: TagFilter) = {
    val img = current
    filterSet = _images.filter(i => ƒ(_meta(i)))
    currentIndex = img.map(filterSet.indexOf).filter(_ >= 0).getOrElse(0)
  }

  // meta update
  def setTags(tags: Set[Tag]) = current foreach (_meta(_) = tags)

  // navigation
  def next = currentIndex =
    if (currentIndex >= (filterSize - 1)) 0
    else currentIndex + 1

  def previous = currentIndex =
    if (currentIndex <= 0) 0 max (filterSize - 1)
    else currentIndex - 1

  def random = currentIndex = Random.nextInt(filterSize)

}