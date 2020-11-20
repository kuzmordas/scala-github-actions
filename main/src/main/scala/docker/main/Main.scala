package docker.main

import cats.effect.{ExitCode, IO, IOApp}
import docker.main.Util.readFile

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      str <- IO(readFile("./main/example.txt"))
      _ <- IO(println(str))
      server <- IO(GrpcServer())
      _ <- IO(println(s"Starting server at port 8080"))
      _ <- server.build().compile.drain
    } yield ExitCode.Success
}
