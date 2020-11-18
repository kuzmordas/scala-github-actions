package docker.main

import cats.effect.{ExitCode, IO, IOApp}

import scala.io.Source

object Main extends IOApp {

   private def readFile(path: String): IO[String] = {
     val source = Source.fromFile(path)
     val result = source.mkString
     source.close()
     IO(result)
  }

  override def run(args: List[String]): IO[ExitCode] =
    for {
      str                <- readFile("./main/example.txt")
      _                  <- IO(println(str))
      server             <- IO(GrpcServer())
      _                  <- IO(println(s"Starting server at port 8080"))
      _                  <- server.build().compile.drain
    } yield ExitCode.Success
}
