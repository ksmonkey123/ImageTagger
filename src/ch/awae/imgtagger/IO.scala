package ch.awae.imgtagger

import java.io.File
import scala.io.Source
import javax.imageio.ImageIO
import java.util.Objects
import java.io.ObjectInputStream
import java.io.FileInputStream
import java.io.ObjectOutputStream
import java.io.FileOutputStream
import java.awt.Toolkit
import java.awt.image.BufferedImage

object IO {

  def listImages: List[String] =
    new File(".")
      .list()
      .toList
      .filter(f => f.endsWith(".jpg") || f.endsWith(".png") || f.endsWith(".jpeg"))
      .sorted

  def getImage(img: String) = {
    Toolkit.getDefaultToolkit.getImage(img)
  }
  def readMeta = {
    val file = new File("tags.meta")
    if (!file.exists())
      new Meta
    else {
      val ois = new ObjectInputStream(new FileInputStream(new File("tags.meta")))
      val obj = ois.readObject.asInstanceOf[PersistenceContainer]
      ois.close
      new Meta(obj)
    }
  }

  def writeMeta(meta: Meta) = {
    val oos = new ObjectOutputStream(new FileOutputStream(new File("tags.meta")))
    oos writeObject meta.persistence
    oos.close
  }

}