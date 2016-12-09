package ch.awae.imgtagger
package persistence

import scala.xml.Node

object ImageParser extends XMLParser[PerImage] {

  def toXML(x: PerImage): Node =
    <image name={ x.name }>
      {
        x.tags map
          (_.value) map
          (t => <tag value={ t }/>)
      }
    </image>

  def fromXML(e: Node): PerImage = {
    val name = e \@ "name"
    val tags = e \ "tag" map (_ \@ "value") map PerImageTag

    PerImage(name, tags.toList)
  }

}