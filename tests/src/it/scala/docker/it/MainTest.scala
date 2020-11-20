package docker.it

import cats.effect.{ConcurrentEffect, ContextShift, IO, Timer}
import docker.proto.{PingRequest, PingResponse, ServerServiceFs2Grpc}
import io.grpc.{ManagedChannelBuilder, Metadata}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.concurrent.ExecutionContext

class MainTest extends AnyFlatSpec with should.Matchers with BeforeAndAfterAll {

  private val executionContext = ExecutionContext.global
  private implicit val timer: Timer[IO] = IO.timer(executionContext)
  private implicit val cs: ContextShift[IO] = IO.contextShift(executionContext)

  class Client(port: Int)(implicit concurrentEffect: ConcurrentEffect[IO], timer: Timer[IO]) {
    private val managedChannel =
      ManagedChannelBuilder
        .forAddress("localhost", port)
        .usePlaintext()
        .asInstanceOf[ManagedChannelBuilder[_]]
        .build()

    private val client = ServerServiceFs2Grpc.stub[IO](managedChannel)

    def ping(request: PingRequest): IO[PingResponse] = client.ping(request, new Metadata())
  }

  val port = 8080
  val client = new Client(port)

  override protected def beforeAll(): Unit = {
    Main.run(List()).unsafeRunAsync {
      case Right(_) => println("Server run")
      case Left(_)  => println("Server not run")
    }
  }

  "Ping response" should "be valid" in {
    val request = PingRequest("test")
    val response = client.ping(request).unsafeRunSync()
    assert(response.result == "TEST")
  }

}
