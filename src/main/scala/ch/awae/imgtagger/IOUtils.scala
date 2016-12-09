package ch.awae.imgtagger

import scala.util.Try
import scala.xml.PrettyPrinter
import scala.xml.XML

import ch.awae.imgtagger.persistence.PersistenceParser
import ch.awae.imgtagger.persistence.PersistenceRoot

/**
 * General I/O helper class
 *
 * Provides I/O functionality for the whole application.
 * Some methods are synchronised with each other without affecting
 * others.
 *
 * @author Andreas Wälchli <andreas.waelchli@me.com>
 * @since ImageTagger 0.4.0
 */
object IOUtils {

  /**
   * Loads the contents of a given file into a persistence data model.
   *
   * @param file the file to load
   * @return the persistence model extracted from the `file`
   */
  def loadPersistence(file: String): Try[PersistenceRoot] = Try {
    val xml = XML loadFile file
    PersistenceParser fromXML xml
  }

  /**
   * Writes a given persistence data model into a given file.
   * If the file already exists it is replaced.
   *
   * @param file the file to write to
   * @param data the persistence data model to write to the `file`
   * @return a `Failure` if any exception occurred
   */
  def savePersistence(file: String, data: PersistenceRoot): Try[Unit] = Try {
    val xml = PersistenceParser toXML data
    val prettyXML = XML.loadString(new PrettyPrinter(50, 2).format(xml))
    XML.save(file, prettyXML, "UTF-8", true, null)
  }
}