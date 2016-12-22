import scala.annotation.tailrec

package ch.awae {

  package object imgtagger {

    implicit class BetterMod(val x: Int) extends AnyVal {
      def %+(y: Int) = {
        @tailrec
        def __(a: Int, b: Int): Int = {
          if (a >= b)
            __(a - b, b)
          else if (a < 0)
            __(a + b, b)
          else
            a
        }
        __(x, y)
      }
    }
  }

}