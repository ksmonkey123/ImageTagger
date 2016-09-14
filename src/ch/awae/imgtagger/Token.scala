package ch.awae.imgtagger

sealed trait Token

sealed class OperatorToken(val rank: Int) extends Token

object Token {
  case class TagToken(tag: String) extends Token

  case object LParToken extends OperatorToken(3)
  case object RParToken extends Token

  case object NotToken extends OperatorToken(2)
  case object AndToken extends OperatorToken(1)
  case object IOrToken extends OperatorToken(1)
  case object XOrToken extends OperatorToken(1)
  case object EquToken extends OperatorToken(1)
  case object ArrToken extends OperatorToken(1)
}

object Tokeniser {

  import Token._

  val tokenise = (input: String) =>
    Stream(input.split(" "): _*)
      .filterNot(_.isEmpty())
      .map(_ match {
        case "(" => LParToken
        case ")" => RParToken
        case "&" | "&&" | "AND" => AndToken
        case "|" | "||" | "OR" | "IOR" => IOrToken
        case "^" | "XOR" => XOrToken
        case "=" | "==" => EquToken
        case "!" | "NOT" => NotToken
        case "->" | "=>" => ArrToken
        case s => TagToken(s)
      })
      .toList

}