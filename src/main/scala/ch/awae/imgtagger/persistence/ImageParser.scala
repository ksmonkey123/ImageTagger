package ch.awae.imgtagger
package persistence

import scala.xml.Node

/**
 * Parser for a single image entry.
 *
 * The XML syntax is:{{{
 * <image name="\$name">
 *   <tag value="\$tag"/>
 *   { /* remaining tags listed here */ }
 * </image>
 * }}}
 *
 * @author Andreas Wälchli <andreas.waelchli@me.com>
 * @since 0.4.0
 */
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