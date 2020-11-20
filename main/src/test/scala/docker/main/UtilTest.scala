package docker.main

import org.scalatest.flatspec.AnyFlatSpec

class UtilTest extends AnyFlatSpec {
  "Read file" should "be valid" in {
    val r = Util.readFile("./main/example.txt")
    assert(r == "hello from server!")
  }
}
