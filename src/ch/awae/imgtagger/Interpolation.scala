package ch.awae.imgtagger

import java.awt.Graphics2D
import java.awt.RenderingHints

sealed class Interpolation(val title: String, val hint: AnyRef) {
  def activate(graphics: Graphics2D) = graphics setRenderingHint (RenderingHints.KEY_INTERPOLATION, hint)
}

object Interpolation {

  case object NEAREST_NEIGHBOUR extends Interpolation(
    "nearest neighbour",
    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
  case object BILINEAR extends Interpolation(
    "bilinear",
    RenderingHints.VALUE_INTERPOLATION_BILINEAR)
  case object BICUBIC extends Interpolation(
    "bicubic",
    RenderingHints.VALUE_INTERPOLATION_BICUBIC)

  def getAll = NEAREST_NEIGHBOUR :: BILINEAR :: BICUBIC :: Nil

}