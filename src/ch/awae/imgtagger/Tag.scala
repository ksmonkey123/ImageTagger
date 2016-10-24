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

  def niceString(f: ƒ): String = f match {
    case TrueFilter => "no filter"
    case HasTag(r) => r.regex
    case NotFilter(ƒ) => "! ( " + niceString(ƒ) + " )"
    case AndFilter(ƒ0, ƒ1) => "( " + niceString(ƒ0) + " && " + niceString(ƒ1) + " )"
    case IOrFilter(ƒ0, ƒ1) => "( " + niceString(ƒ0) + " || " + niceString(ƒ1) + " )"
    case XOrFilter(ƒ0, ƒ1) => "( " + niceString(ƒ0) + " != " + niceString(ƒ1) + " )"
    case EquFilter(ƒ0, ƒ1) => "( " + niceString(ƒ0) + " == " + niceString(ƒ1) + " )"
    case ArrFilter(ƒ0, ƒ1) => "( " + niceString(ƒ0) + " => " + niceString(ƒ1) + " )"
    case x => x.toString
  }

}