val scala3Version = "3.4.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala-web-app",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    libraryDependencies += "org.scala-stm" %% "scala-stm" % "0.11.1", // for STM
    libraryDependencies += "com.github.mifmif" % "generex" % "1.0.2", // for random strings with regex
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5", // scala-logging (slf4j)
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.3.5", // scala-logging (slf4j)
    libraryDependencies += "org.postgresql" % "postgresql" % "42.7.3",
    libraryDependencies += "io.github.cdimascio" % "dotenv-java" % "2.3.2",
    libraryDependencies += "redis.clients" % "jedis" % "5.1.3"


  )
