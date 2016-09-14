package ch.awae.imgtagger

import scala.util.matching.Regex

case class Tag(text: String)

sealed class TagFilter(private val ƒ: Set[Tag] => Boolean) {
  def apply(tags: Set[Tag]) = ƒ(tags)
}

object TagFilter {

  private type ƒ = TagFilter

  case object TrueFilter extends ƒ(_ => true)

  case class HasTag(regex: Regex) extends ƒ(
    _.find(_.text matches regex.regex).isDefined)

  case class NotFilter(f: ƒ) extends ƒ(
    !f(_))

  case class AndFilter(f0: ƒ, f1: ƒ) extends ƒ(
    t => f0(t) && f1(t))

  case class IOrFilter(f0: ƒ, f1: ƒ) extends ƒ(
    t => f0(t) || f1(t))

  case class XOrFilter(f0: ƒ, f1: ƒ) extends ƒ(
    t => f0(t) ^ f1(t))

  case class EquFilter(f0: ƒ, f1: ƒ) extends ƒ(
    t => f0(t) == f1(t))

  case class ArrFilter(f0: ƒ, f1: ƒ) extends ƒ(
    t => !f0(t) || f1(t))

}