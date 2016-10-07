package ch.awae.imgtagger

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.MediaTracker

import scala.language.postfixOps

import javax.swing.JPanel
import scala.util.Left
import scala.util.Try
import scala.util.Success
import scala.util.Failure

class WindowManager {

  private val window = new Window(this)
  private val manager = new ImageManager(List.empty)
  private var currentImage: (String, Image) = _
  private var player: AutoPlayController = _

  // OPS
  def setImages(imgs: List[String]) = {
    manager load imgs
    loadImage
  }
  def setMeta(m: Meta) = {
    manager load m
    loadImage
  }
  def loadImage {
    window.title = manager.current.getOrElse(null) + " (" + manager.index + "/" + manager.filterSize + "/" + manager.totalSize + ")"
    window.tags = manager.currentTags.getOrElse(Set.empty[Tag]).toList.map(_.text + " ").sorted./:("")(_ + _)
    if (manager.current.isDefined) {
      val current = manager.current.get
      if (currentImage == null || currentImage._1 != current) {
        currentImage = current -> IO.getImage(current)
      }
    } else
      currentImage = null
    window.repaint
  }

  // ACTIONS

  def navRandom = {
    manager.random
    loadImage
  }

  def autoplay(b: Boolean) = {
    if (player != null)
      player.interrupt()
    player = null
    if (b)
      player = new AutoPlayController(window.delay, window.random, this)
    window.lockControls(b)
  }

  def filterApplied = {
    val ƒ = Tokeniser.tokenise andThen QueryParser.shunt andThen QueryParser.compile
    val filter = Try(ƒ(window.filter))
    filter match {
      case Success(f) =>
        manager applyFilter f
        window filterMessage Left(TagFilter.niceString(f))
      case Failure(t) =>
        window filterMessage Right(t.toString + ": " + t.getMessage)
    }
    loadImage
  }
  def tagsSaved = {
    val list = List(window.tags.split(" "): _*).filterNot(_.isEmpty()).map(s => if (s.matches("[a-zA-Z0-9_]+")) Left(s) else Right(s))
    val illegals = list.filter(_.isRight)
    if (illegals.isEmpty) {
      window.tagsError(None)
      manager.setTags(list.filter(_.isLeft).map(_.left.get).map(new Tag(_)).toSet)
      IO.writeMeta(manager.meta)
    } else
      window.tagsError(Some(illegals.map(_.right.get)./:("illegal tags: ")(_ + _ + ", ")))
  }
  def navNext = {
    manager.next
    loadImage
  }
  def navPrev = {
    manager.previous
    loadImage
  }

  // WINDOW OPS
  def init {
    window init;
    window show
  }

  def drawImage(p: JPanel, g: Graphics) = {
    g.setColor(Color.BLACK)
    g.fillRect(0, 0, p.getWidth, p.getHeight)
    if (currentImage != null && currentImage._2 != null) {
      val g2 = g.create.asInstanceOf[Graphics2D]
      val img = currentImage._2

      g2.translate(0.5 * p.getWidth, 0.5 * p.getHeight)

      val tracker = new MediaTracker(p)
      tracker.addImage(img, 0)
      tracker.waitForAll()

      val x = img.getWidth(null).toDouble
      val y = img.getHeight(null).toDouble
      val X = p.getWidth.toDouble
      val Y = p.getHeight.toDouble

      val ƒ = X / Y
      val ∫ = x / y

      val a = if (∫ < ƒ) y * ƒ else x
      val b = if (∫ < ƒ) y else x / ƒ

      g2.scale(X / a, Y / b)

      g2.drawImage(img, -(x / 2).toInt, -(y / 2).toInt, Color.WHITE, null)

      tracker.removeImage(img)
    }
  }
}