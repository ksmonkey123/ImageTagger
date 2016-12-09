package ch.awae.imgtagger
package persistence

import scala.xml.Node

/**
 * Parser from and to XML
 *
 * @author Andreas Wälchli <andreas.waelchli@me.com>
 * @since 0.4.0
 *
 * @tparam A the type this parser handles
 */
trait XMLParser[A] {

  /**
   * converts an object into an XML node
   *
   * @param elem the object to convert
   * @return the resulting `Node`
   */
  def toXML(elem: A): Node

  /**
   * converts an XML node into an object
   *
   * @param xml the `Node` to convert
   * @return the resulting object
   */
  def fromXML(xml: Node): A

}