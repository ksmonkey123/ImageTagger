package test

import ch.awae.imgtagger.IOUtils
import ch.awae.imgtagger.persistence.PerImage
import ch.awae.imgtagger.persistence.PerImageTag
import ch.awae.imgtagger.persistence.PerMeta
import ch.awae.imgtagger.persistence.PersistenceRoot

object TEst extends App {

  val p1 = PersistenceRoot(
    "1.1",
    PerImage("aaa", PerImageTag("123") :: PerImageTag("456") :: Nil) ::
      PerImage("bbb", PerImageTag("789") :: Nil) ::
      Nil, PerMeta())

  val file = "temp.itm"

  IOUtils.savePersistence(file, p1)

  val p2 = IOUtils.loadPersistence(file)

  println(p1 == p2.get)

}