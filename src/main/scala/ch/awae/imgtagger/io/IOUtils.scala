package ch.awae.imgtagger.io

import scala.util.Try
import scala.xml.XML

import ch.awae.imgtagger.persistence.PersistenceParser
import ch.awae.imgtagger.persistence.PersistenceRoot
import scala.xml.PrettyPrinter

object IOUtils {

  def loadPersistence(file: String): Try[PersistenceRoot] = Try {
    val xml = XML loadFile file
    PersistenceParser fromXML xml
  }

  def savePersistence(file: String, data: PersistenceRoot): Try[Unit] = Try {
    val xml = PersistenceParser toXML data
    val prettyXML = XML.loadString(new PrettyPrinter(50, 2).format(xml))
    XML.save(file, prettyXML, "UTF-8", true, null)
  }
}