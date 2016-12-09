package test

import ch.awae.imgtagger.persistence.PerImage
import ch.awae.imgtagger.persistence.PerImageTag
import ch.awae.imgtagger.persistence.ImageParser
import ch.awae.imgtagger.persistence.PersistenceRoot
import ch.awae.imgtagger.persistence.PerMeta
import ch.awae.imgtagger.persistence.PersistenceParser
import scala.xml.PrettyPrinter

object TEst extends App {

  val p1 = PersistenceRoot(
    "1.0",
    PerImage("aaa", PerImageTag("123") :: PerImageTag("456") :: Nil) ::
      PerImage("bbb", PerImageTag("789") :: Nil) ::
      Nil, PerMeta())

  println(p1)

  val xml = PersistenceParser toXML p1

  println(new PrettyPrinter(30, 2).format(xml))

  val p2 = PersistenceParser fromXML xml

  println(p2)

  println(p1 == p2)

}