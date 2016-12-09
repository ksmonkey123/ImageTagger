package ch.awae.imgtagger
package persistence

import scala.xml.Node

/**
 * Parser for a single meta entry.
 *
 * Currently the meta tag is empty as there is no meta data supported.
 *
 * The XML syntax is:{{{
 * <meta/>
 * }}}
 *
 * @author Andreas Wälchli <andreas.waelchli@me.com>
 * @since 0.4.0
 */
object MetaParser extends XMLParser[PerMeta] {

  // currently no metadata defined
  def fromXML(e: Node): PerMeta = PerMeta()

  def toXML(x: PerMeta): Node = <meta/>

}