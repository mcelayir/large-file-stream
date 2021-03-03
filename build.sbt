name := "large-file-distributed"

version := "0.1"

scalaVersion := "2.13.5"

val AkkaVersion = "2.6.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.7",
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "2.0.2",

  "org.apache.kafka" % "kafka-clients" % "2.7.0",
  "com.typesafe.play" %% "play-json" % "2.9.2"
)
