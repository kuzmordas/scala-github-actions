package docker.main

import cats.effect.{ConcurrentEffect, ContextShift, IO, Timer}
import docker.proto.{PingRequest, PingResponse, ServerServiceFs2Grpc}
import io.grpc.{Metadata, ServerBuilder}
import io.grpc.protobuf.services.ProtoReflectionService
import org.lyranthe.fs2_grpc.java_runtime.implicits.fs2GrpcSyntaxServerBuilder

object GrpcServer {
  def apply()(implicit concurrentEffect: ConcurrentEffect[IO], contextShift: ContextShift[IO], timer: Timer[IO]): GrpcServer = {
    new GrpcServer()(concurrentEffect, contextShift, timer)
  }
}

class GrpcServer()(implicit concurrentEffect: ConcurrentEffect[IO], contextShift: ContextShift[IO], timer: Timer[IO]) {

  val service: ServerServiceFs2Grpc[IO, Metadata] = (request: PingRequest, ctx: Metadata) =>
    IO(PingResponse(Service.processString(request.message)))

  def build(): fs2.Stream[IO, Nothing] =
    ServerBuilder
      .forPort(8080)
      .addService(ServerServiceFs2Grpc.bindService(service))
      .asInstanceOf[ServerBuilder[_]]
      .addService(ProtoReflectionService.newInstance())
      .asInstanceOf[ServerBuilder[_]]
      .stream[IO]
      .evalMap(server => IO(server.start()))
      .evalMap(_ => IO.never)
}
