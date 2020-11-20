package docker.lib

import cats.implicits.catsKernelStdMonoidForString
import cats.kernel.Monoid

object Utils {

  def add(s1: String, s2: String): String =
    Monoid.combine(s1, s2)

}
