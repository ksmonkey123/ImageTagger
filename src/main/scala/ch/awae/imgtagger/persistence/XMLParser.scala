package ch.awae.imgtagger
package persistence

import scala.xml.Elem
import scala.xml.Node

trait XMLParser[T] {

  def toXML(x: T): Node

  def fromXML(e: Node): T

}