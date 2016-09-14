package ch.awae.imgtagger

import java.io.File

object Test extends App {

  val images = IO.listImages
  val meta = IO.readMeta

  val manager = new WindowManager
  manager.init
  manager setImages images
  manager setMeta meta
}