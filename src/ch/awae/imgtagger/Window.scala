package ch.awae.imgtagger

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Graphics

import scala.language.implicitConversions
import scala.language.postfixOps

import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.JButton
import java.awt.Color
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.JSeparator
import javax.swing.SwingConstants
import javax.swing.JCheckBox
import com.sun.xml.internal.ws.api.Component
import javax.swing.JComponent
import javax.swing.event.ChangeListener

class Window(val manager: WindowManager) {

  case class FunctionalActionListener(ƒ: ActionEvent => Unit) extends ActionListener {
    override def actionPerformed(e: ActionEvent) = ƒ(e)
  }

  private implicit def tuple2Dimension(a: (Int, Int)): Dimension = new Dimension(a._1, a._2)

  private val frame = new JFrame

  private val imagePanel = new JPanel {
    override def paint(g: Graphics) = manager.drawImage(this, g)
  }

  private val footPanel = new JPanel

  private val navPanel = new JPanel
  private val navNext = new JButton(">")
  private val navPrev = new JButton("<")
  private val navAuto = new JCheckBox("autoplay")
  private val navRand = new JCheckBox("random")
  private val navDelay = new JTextField(2)

  private val filterPanel = new JPanel
  private val filterField = new JTextField
  private val filterOK = new JButton("Filter")

  private val tagPanel = new JPanel
  private val tagField = new JTextField
  private val tagSave = new JButton("Save")

  private val errorColor = Color.RED.brighter()
  private var fieldColor: Color = _

  def init {
    import BorderLayout.{ CENTER, NORTH, SOUTH }
    import BoxLayout.{ LINE_AXIS, PAGE_AXIS }

    def fix(c: JComponent) = c setMaximumSize c.getPreferredSize

    frame setTitle "Image Tagger"
    frame setLayout new BorderLayout
    frame setDefaultCloseOperation JFrame.EXIT_ON_CLOSE

    imagePanel setMinimumSize (400, 300)
    imagePanel setPreferredSize (400, 300)
    frame add (imagePanel, CENTER)

    // FILTER PANEL SETUP
    filterPanel setLayout new BoxLayout(filterPanel, LINE_AXIS)
    fix(filterOK)
    filterPanel add filterField
    filterPanel add filterOK
    frame add (filterPanel, NORTH)

    // FOOTER SETUP
    footPanel setLayout new BoxLayout(footPanel, PAGE_AXIS)

    // TAG PANEL SETUP
    tagPanel setLayout new BoxLayout(tagPanel, LINE_AXIS)
    fix(tagSave)
    tagPanel add tagField
    tagPanel add tagSave
    footPanel add tagPanel

    // NAV PANEL SETUP
    navPanel setLayout new BoxLayout(navPanel, LINE_AXIS)
    fix(navPrev)
    fix(navNext)
    fix(navAuto)
    fix(navRand)
    fix(navDelay)
    navDelay setText "5"
    navPanel add navPrev
    navPanel add navAuto
    navPanel add navDelay
    navPanel add navRand
    navPanel add navNext
    footPanel add navPanel

    frame add (footPanel, SOUTH)

    // REGISTER LISTENERS
    navPrev addActionListener FunctionalActionListener(_ => manager.navPrev)
    navNext addActionListener FunctionalActionListener(_ => manager.navNext)
    tagSave addActionListener FunctionalActionListener(_ => manager.tagsSaved)
    filterOK addActionListener FunctionalActionListener(_ => manager.filterApplied)
    navAuto addActionListener FunctionalActionListener(_ => manager.autoplay(navAuto.isSelected()))
    // FINISH SETUP
    frame pack

    fieldColor = tagField.getBackground
  }

  def random = navRand.isSelected

  def repaint = { frame.repaint() }

  def show = frame setVisible true

  def title = frame getTitle
  def title_=(t: String) = frame setTitle t

  def tags: String = tagField getText
  def tags_=(t: String) = {
    tagField setText t
    tagsError(None)
  }

  def delay = try {
    val a = navDelay.getText.toDouble
    if (a > 0) a else {
      navDelay.setText("5")
      5
    }
  } catch {
    case _: NumberFormatException =>
      navDelay.setText("5")
      5
  }

  def tagsError(message: Option[String]) = message match {
    case None =>
      tagField.setToolTipText(null)
      tagField.setBackground(fieldColor)
    case Some(msg) =>
      tagField.setToolTipText(msg)
      tagField.setBackground(errorColor)
  }

  def filterMessage(message: Either[String, String]) = message match {
    case Left(msg) =>
      filterField.setToolTipText(msg)
      filterField.setBackground(fieldColor)
    case Right(msg) =>
      filterField.setToolTipText(msg)
      filterField.setBackground(errorColor)
  }

  def filter: String = filterField getText

  def lockControls(b: Boolean) = {
    List(navPrev, navNext, navRand, navDelay, filterField, filterOK, tagField, tagSave) foreach (_.setEnabled(!b))
  }

}