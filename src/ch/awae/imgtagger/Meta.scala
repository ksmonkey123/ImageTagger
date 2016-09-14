package ch.awae.imgtagger

import scala.collection.mutable
import scala.collection.SortedMap.Default

class Meta extends Serializable {

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
}