package ch.awae.imgtagger
package persistence

import scala.xml.Node

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