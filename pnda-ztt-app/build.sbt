name := "pnda-ztt-app"

version := "0.0.3"

scalaVersion := "2.10.6"

enablePlugins(UniversalPlugin)

packageZipTarball in Universal := {
  val originalFileName = (packageZipTarball in Universal).value
  val (base, ext) = originalFileName.baseAndExt
  val newFileName = file(originalFileName.getParent) / (base + ".tar.gz")
  IO.move(originalFileName, newFileName)
  newFileName
}

libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "1.6.0" % "provided",
    "org.apache.spark" %% "spark-streaming" % "1.6.0" % "provided",
    "org.apache.spark" %% "spark-streaming-kafka" % "1.6.0" ,
    "org.apache.kafka" %% "kafka" % "0.8.2.1",
    "org.apache.avro" % "avro" % "1.7.7",
    "joda-time" % "joda-time" % "2.8.1" % "provided",
    "log4j" % "log4j" % "1.2.17" % "provided",
    "org.apache.httpcomponents" % "httpcore" % "4.2.5" % "provided",
    "org.apache.httpcomponents" % "httpclient" % "4.2.5" % "provided",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.2.3" % "provided",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.2.3",
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.10" % "2.2.3" % "provided",
    "org.springframework" % "spring-core" % "4.3.10.RELEASE",
    "org.scalatest" % "scalatest_2.10" % "3.0.1" % "test"
)

EclipseKeys.withSource := true
