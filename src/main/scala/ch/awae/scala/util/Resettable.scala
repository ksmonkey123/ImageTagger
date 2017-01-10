package ch.awae.scala.util

trait Resettable[A] {
  def apply(): A
  def set(a: A): Resettable[A]
  def reset: Resettable[A]
}

object Resettable {
  def apply[A](default: A): Resettable[A] = new DefaultResettable(default)
  def apply[A](value: A, default: A): Resettable[A] = Resettable(default) set value
}

private class DefaultResettable[A](value: A) extends Resettable[A] {
  def apply() = value
  def set(a: A) = new NonDefaultResettable(a, this)
  def reset = this
}

private class NonDefaultResettable[A](value: A, default: Resettable[A]) extends Resettable[A] {
  def apply() = value
  def set(a: A) = new NonDefaultResettable(a, default)
  def reset = default
}