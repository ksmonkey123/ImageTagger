package ch.awae.imgtagger

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.MediaTracker

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.language.postfixOps
import scala.util.Failure
import scala.util.Left
import scala.util.Right
import scala.util.Success
import scala.util.Try

import javax.swing.JPanel
import java.awt.RenderingHints
import javax.swing.SwingUtilities

class WindowManager {

  private val window = new Window(this)
  private val manager = new ImageManager(List.empty)
  private var currentImage: (String, Image) = _
  private var nextImage: (String, Future[Image]) = _
  private var player: AutoPlayController = _
  private var meta: Meta = _

  // OPS
  def setImages(imgs: List[String]) = {
    manager load imgs
    loadImage
  }
  def setMeta(m: Meta) = {
    manager load m
    meta = m
    window.setPopupFilters(meta.filters)
    loadImage
  }

  def loadImage {
    window.lockNull(manager.filterSize == 0)
    window.title = manager.current.getOrElse(null) + " (" + manager.index + "/" + manager.filterSize + "/" + manager.totalSize + ")"
    window.tags = manager.currentTags.getOrElse(Set.empty[Tag]).toList.map(_.text + " ").sorted./:("")(_ + _)
    if (manager.current.isDefined) {
      val current = manager.current.get
      if (currentImage == null || currentImage._1 != current) {
        if (nextImage != null && nextImage._1 == current)
          currentImage = (current, Await.result(nextImage._2, Duration.Inf))
        else
          currentImage = current -> imgload(current)
        val nx = manager.nextImg
        if (nx.isDefined) {
          val nxt = nx.get
          if (nxt != null && (nextImage == null || nxt != nextImage._1))
            nextImage = nxt -> Future(imgload(nxt))
        }
      }
    } else {
      currentImage = null
      nextImage = null
    }
    window.repaint
  }

  // ACTIONS

  def autoplay(b: Boolean) = {
    if (player != null)
      player.interrupt()
    player = null
    if (b)
      player = new AutoPlayController(window.delay, window.random, this)
    window.lockControls(b)
  }

  def filterApplied = {
    val filter = Try(QueryParser.fullParse(window.filter))
    filter match {
      case Success(f) =>
        manager applyFilter f
        window filterMessage Left(TagFilter.niceString(f))
        meta.cacheFilter(f)
        IO.writeMeta(meta)
        window.setPopupFilters(meta.filters)
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
    manager.randomise(window.random)
    manager.next
    loadImage
  }
  def navPrev = {
    manager.randomise(window.random)
    manager.previous
    loadImage
  }

  // WINDOW OPS
  def init {
    window init;
    window show
  }

  def imgload(image: String) = {
    val i = IO.getImage(image)
    val tracker = new MediaTracker(window.imagePanel)
    tracker.addImage(i, 1)
    tracker.waitForID(1)
    i
  }

  private var interpol: Interpolation = Interpolation.BICUBIC

  def interpolationSelected(i: Interpolation) = {
    interpol = i
    window.repaint
  }

  def drawImage(p: JPanel, g: Graphics) = {
    g.setColor(Color.BLACK)
    g.fillRect(0, 0, p.getWidth, p.getHeight)
    if (currentImage != null && currentImage._2 != null) {
      val g2 = g.create.asInstanceOf[Graphics2D]
      val img = currentImage._2

      g2.translate(0.5 * p.getWidth, 0.5 * p.getHeight)

      val x = img.getWidth(null).toDouble
      val y = img.getHeight(null).toDouble
      val X = p.getWidth.toDouble
      val Y = p.getHeight.toDouble

      val ƒ = X / Y
      val ∫ = x / y

      val a = if (∫ < ƒ) y * ƒ else x
      val b = if (∫ < ƒ) y else x / ƒ

      g2.scale(X / a, Y / b)
      interpol activate g2
      g2.drawImage(img, -(x / 2).toInt, -(y / 2).toInt, Color.WHITE, null)
    }
  }
}