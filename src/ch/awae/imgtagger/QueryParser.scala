package ch.awae.imgtagger

import ch.awae.imgtagger.Token._
import scala.annotation.tailrec
import ch.awae.imgtagger.TagFilter._

object QueryParser {

  lazy val fullParse =
    Tokeniser.tokenise andThen
      implicitAnd andThen
      shunt andThen
      compile

  val shunt = (tokens: List[Token]) => {

    var input = tokens
    var stack = List.empty[OperatorToken]
    var output = List.empty[Token]

    // process tokens
    while (!input.isEmpty) {
      // process token without popping
      input.head match {
        case t: TagToken => output ::= t
        case op: OperatorToken =>
          while (!stack.isEmpty && op.rank <= stack.head.rank && stack.head != LParToken) {
            output ::= stack.head
            stack = stack.tail
          }
          stack ::= op
        case RParToken =>
          while (!stack.isEmpty && stack.head != LParToken) {
            output ::= stack.head
            stack = stack.tail
          }
          if (stack.isEmpty)
            throw new RuntimeException("mismatched parentheses")
          stack = stack.tail
      }
      // pop processed token and continue
      input = input.tail
    }

    // empty operator stack into output
    while (!stack.isEmpty) {
      if (stack.head == Token.LParToken)
        throw new RuntimeException("mismatched parentheses")
      output ::= stack.head
      stack = stack.tail
    }

    // invert output stack
    output.reverse

  }

  val implicitAnd = (tokens: List[Token]) => {
    var stack = List.empty[Token]

    for (t <- tokens) {
      if (stack.isEmpty)
        stack ::= t
      else if (stack.head.isInstanceOf[TagToken] && t.isInstanceOf[TagToken])
        stack = t :: AndToken :: stack
      else
        stack ::= t
    }

    stack.reverse
  }

  val compile = (tokens: List[Token]) => {
    @tailrec
    def step(queue: List[Token], stack: List[TagFilter]): TagFilter =
      if (queue.isEmpty)
        if (stack.size == 0)
          return TrueFilter
        else if (stack.size == 1)
          return stack.head
        else
          throw new RuntimeException("unreduced stack")
      else
        queue.head match {
          case TagToken(s) =>
            return step(queue.tail, HasTag(s.r) :: stack)
          case NotToken =>
            return step(queue.tail, NotFilter(stack.head) :: stack.tail)
          case AndToken =>
            return step(queue.tail, AndFilter(stack.head, stack.tail.head) :: stack.tail.tail)
          case IOrToken =>
            return step(queue.tail, IOrFilter(stack.head, stack.tail.head) :: stack.tail.tail)
          case XOrToken =>
            return step(queue.tail, XOrFilter(stack.head, stack.tail.head) :: stack.tail.tail)
          case EquToken =>
            return step(queue.tail, EquFilter(stack.head, stack.tail.head) :: stack.tail.tail)
          case ArrToken =>
            return step(queue.tail, ArrFilter(stack.tail.head, stack.head) :: stack.tail.tail)
          case x =>
            throw new RuntimeException(s"cannot parse token $x")
        }
    step(tokens, List.empty)
  }

}