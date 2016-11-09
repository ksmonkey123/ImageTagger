package ch.awae.imgtagger

import javax.swing.UIManager
import javax.swing.UnsupportedLookAndFeelException

object ScalaLauncher {

  def launch {

    try {
      UIManager setLookAndFeel UIManager.getSystemLookAndFeelClassName
    } catch {
      case e: ClassNotFoundException => System.err println "ClassNotFoundException: " + e.getMessage
      case e: InstantiationException => System.err println "InstantiationException: " + e.getMessage
      case e: IllegalAccessException => System.err println "IllegalAccessException: " + e.getMessage
      case e: UnsupportedLookAndFeelException => System.err println "UnsupportedLookAndFeelException: " + e.getMessage
    }

    val imgs = IO.listImages
    val meta = IO.readMeta
    val mngr = new WindowManager

    mngr.init
    mngr setImages imgs
    mngr setMeta meta
  }

}