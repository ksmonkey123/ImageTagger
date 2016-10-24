package ch.awae.imgtagger

import scala.collection.mutable
import scala.collection.SortedMap.Default
import scala.collection.immutable.HashMap

class Meta {

  private var filterCache = List.empty[TagFilter]

  private val tags = new mutable.HashMap[String, Set[Tag]] {
    override def default(key: String) = Set.empty[Tag]
  }

  /**
   * Remove all keys that are not in the list. Keys not
   * in the map do not have to be added, since the default
   * value for undefined keys is the empty set.
   */
  def adjustRegistry(files: List[String]) = {
    tags.keySet diff files.toSet foreach tags.remove
  }

  /**
   * Returns the tags for a file name
   */
  def apply(file: String) = tags(file)

  def update(file: String, tag: Set[Tag]) = tags(file) = tag;

  def filters = filterCache

  def cacheFilter(ƒ: TagFilter) =
    filterCache = (ƒ :: filterCache).toStream.distinct.take(10).toList

  // =====================================================================

  /**
   * Import Constructor
   */
  def this(p: PersistenceContainer) {
    this()

    if (p.data.contains("tags")) {
      val tagMap = p.data("tags").asInstanceOf[mutable.HashMap[String, Set[String]]]
      for ((f, ts) <- tagMap) {
        tags(f) = ts.map(Tag(_))
      }
    }

    if (p.data.contains("filters")) {
      val filterList = p.data("filters").asInstanceOf[List[String]]
      filterCache = filterList.map(QueryParser.fullParse)
    }
  }

  /**
   * Export Function
   */
  def persistence = {
    val tagMap = tags.map(e => e._1 -> e._2.map(_.text))
    val filterList = filterCache.map(TagFilter.niceString(_))

    PersistenceContainer(HashMap(
      "tags" -> tagMap,
      "filters" -> filterList))
  }

}