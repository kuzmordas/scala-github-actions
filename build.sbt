import com.typesafe.sbt.packager.docker.{Cmd}

name := "docker"
version := "0.1"
scalaVersion := "2.13.3"

val catsVersion         = "2.0.0"
val catsEffectVersion   = "2.1.4"

lazy val dockerSettings = Seq(
  mappings in Universal += ((sourceDirectory.value) / ".." / "example.txt", "main/example.txt"),
  dockerExposedPorts    := Seq(8080),
  dockerBaseImage       := "openjdk:8",
  dockerUsername        := Some("kuzmo"),
  dockerUpdateLatest    := false,
  dockerEntrypoint      := Seq(""),
//  dockerCommands  ++= Seq(Cmd("RUN", "mkdir", "-p", "/2/opt/main")),
//  dockerCommands        ++= Seq(Cmd("COPY", "./main/example.txt", "/2/opt/main")),
  dockerCommands        ++= Seq(Cmd("CMD", "./bin/main")),
  packageName in Docker := "scala-docker",
)

lazy val proto =
  project
    .in(file("proto"))
    .enablePlugins(Fs2Grpc)
    .settings(
      name := "proto",
      scalaVersion := "2.13.3",
      scalacOptions += "-Ymacro-annotations",
      libraryDependencies ++= Seq(
        "org.typelevel"                      %% "cats-core"                               % catsVersion,
        "org.typelevel"                      %% "cats-effect"                             % catsEffectVersion,
        "com.thesamet.scalapb"               %% "scalapb-runtime"                         % scalapb.compiler.Version.scalapbVersion % "protobuf",
        "io.grpc"                            % "grpc-services"                            % scalapb.compiler.Version.grpcJavaVersion,
        "io.grpc"                            % "grpc-netty"                               % scalapb.compiler.Version.grpcJavaVersion,
        "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.10" % "1.17.0-0" % "protobuf",
        "com.thesamet.scalapb.common-protos" %% "proto-google-common-protos-scalapb_0.10" % "1.17.0-0",
        "com.thesamet.scalapb"               %% "scalapb-runtime-grpc"                    % scalapb.compiler.Version.scalapbVersion,
      )
    )

lazy val library = (project in file("lib"))
//  .enablePlugins(JavaServerAppPackaging)
//  .enablePlugins(DockerPlugin)
//  .settings(dockerSettings)
  .settings(
    name := "lib",
    scalaVersion := "2.13.3",
    scalacOptions += "-Ymacro-annotations",
    libraryDependencies ++= Seq(
      "org.typelevel"              %% "cats-core"       % catsVersion,
      "org.typelevel"              %% "cats-effect"     % catsEffectVersion)
  )

lazy val main = (project in file("main"))
  .dependsOn(proto)
  .dependsOn(library)
  .enablePlugins(Fs2Grpc)
  .enablePlugins(JavaServerAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(dockerSettings)
  .settings(
    name := "main",
    scalaVersion := "2.13.3",
    scalacOptions += "-Ymacro-annotations",
    libraryDependencies ++= Seq(
      "org.typelevel"              %% "cats-core"       % catsVersion,
      "org.typelevel"              %% "cats-effect"     % catsEffectVersion
    )
  )