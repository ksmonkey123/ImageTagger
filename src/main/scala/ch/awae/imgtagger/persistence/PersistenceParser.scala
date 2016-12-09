package ch.awae.imgtagger
package persistence

import scala.xml.Node

/**
 * Parser for a complete persitence model instance
 *
 * The XML syntax is:{{{
 * <imgtagger version="\$version">
 *   { /* meta data here */ }
 *   <images>
 *     { /* list of images here */ }
 *   </images>
 * </imgtagger>
 * }}}
 *
 * @author Andreas Wälchli <andreas.waelchli@me.com>
 * @since 0.4.0
 */
object PersistenceParser extends XMLParser[PersistenceRoot] {
  def toXML(root: PersistenceRoot): Node =
    <imgtagger version={ root.version }>
      { MetaParser toXML root.meta }
      <images>
        { root.images map ImageParser.toXML }
      </images>
    </imgtagger>

  def fromXML(xml: Node): PersistenceRoot = {
    val version = xml \@ "version"
    val meta = MetaParser.fromXML(xml.\("meta")(0))
    val images = (xml \ "images" \ "image") map ImageParser.fromXML

    PersistenceRoot(version, images.toList, meta)
  }

}