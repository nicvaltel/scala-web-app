val scala3Version = "3.4.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala-web-app",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    libraryDependencies += "org.scala-stm" %% "scala-stm" % "0.11.1",
    libraryDependencies += "com.github.mifmif" % "generex" % "1.0.2"

  )
