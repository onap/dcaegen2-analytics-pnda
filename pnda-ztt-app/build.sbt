name := "pnda-ztt-app"

version := "0.0.4"

scalaVersion := "2.11.8"

enablePlugins(UniversalPlugin)

packageZipTarball in Universal := {
  val originalFileName = (packageZipTarball in Universal).value
  val (base, ext) = originalFileName.baseAndExt
  val newFileName = file(originalFileName.getParent) / (base + ".tar.gz")
  IO.move(originalFileName, newFileName)
  newFileName
}

libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "2.3.0" % "provided",
    "org.apache.spark" %% "spark-streaming" % "2.3.0" % "provided",
    "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.3.3" ,
    "org.apache.kafka" %% "kafka" % "0.11.0.3",
    "org.apache.avro" % "avro" % "1.7.7",
    "joda-time" % "joda-time" % "2.8.1" % "provided",
    "log4j" % "log4j" % "1.2.17" % "provided",
    "org.apache.httpcomponents" % "httpcore" % "4.2.5" % "provided",
    "org.apache.httpcomponents" % "httpclient" % "4.2.5" % "provided",
    "com.fasterxml.jackson.core" % "jackson-core" % "2.6.7" % "provided",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7" % "provided",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.6.7",
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.6.7.1" % "provided",
    "org.springframework" % "spring-core" % "4.3.10.RELEASE",
    "org.scalatest" % "scalatest_2.11" % "3.0.5" % "test",
    "org.scala-lang" % "scala-reflect" % "2.11.8",
    "org.scala-lang" % "scala-compiler" % "2.11.8" % "provided",
    "org.scalactic" % "scalactic_2.11" % "3.2.0-SNAP10"
)

EclipseKeys.withSource := true
