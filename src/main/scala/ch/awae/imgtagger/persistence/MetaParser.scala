package ch.awae.imgtagger
package persistence

import scala.xml.Node

object MetaParser extends XMLParser[PerMeta] {

  // currently no metadata defined
  def fromXML(e: Node): PerMeta = PerMeta()

  def toXML(x: PerMeta): Node = <meta/>

}