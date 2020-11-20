package docker.main

import org.scalatest.flatspec.AnyFlatSpec

class ServiceTest extends AnyFlatSpec {
  "Process string" should "be valid" in {
    val r = Service.processString("hello")
    assert(r == "hello".toUpperCase)
  }
}
