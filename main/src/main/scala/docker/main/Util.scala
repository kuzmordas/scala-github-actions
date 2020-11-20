package docker.main

import scala.io.Source

object Util {

  def readFile(path: String): String = {
    val source = Source.fromFile(path)
    val result = source.mkString
    source.close()
    result
  }

}
