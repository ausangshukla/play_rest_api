import sbt.Keys._

lazy val GatlingTest = config("gatling") extend Test

scalaVersion in ThisBuild := "2.12.6"

crossScalaVersions := Seq("2.11.12", "2.12.6")

def gatlingVersion(scalaBinVer: String): String = scalaBinVer match {
  case "2.11" => "2.2.5"
  case "2.12" => "2.3.1"
}

libraryDependencies += guice
libraryDependencies += "org.joda" % "joda-convert" % "1.9.2"
libraryDependencies += "net.logstash.logback" % "logstash-logback-encoder" % "4.11"

libraryDependencies += "com.netaporter" %% "scala-uri" % "0.4.16"
libraryDependencies += "net.codingwell" %% "scala-guice" % "4.2.1"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion(scalaBinaryVersion.value) % Test
libraryDependencies += "io.gatling" % "gatling-test-framework" % gatlingVersion(scalaBinaryVersion.value) % Test
libraryDependencies += "io.strongtyped" %% "active-slick" % "0.3.5"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "3.0.3",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.3",
  "com.h2database" % "h2" % "1.4.197",
  "mysql" % "mysql-connector-java" % "6.0.6"
)

libraryDependencies += "com.pauldijou" %% "jwt-play-json" % "0.18.0"
libraryDependencies += "org.typelevel" %% "cats-core" % "1.4.0"
libraryDependencies += "io.kanaka" %% "play-monadic-actions" % "2.1.0"
libraryDependencies += "io.kanaka" %% "play-monadic-actions-cats" % "2.1.0"

libraryDependencies += "com.alexitc" %% "play-request-tracer" % "0.1.0"

// The Play project itself
lazy val root = (project in file("."))
  .enablePlugins(Common, PlayScala, GatlingPlugin)
  .configs(GatlingTest)
  .settings(inConfig(GatlingTest)(Defaults.testSettings): _*)
  .settings(
    name := """rest-api""",
    scalaSource in GatlingTest := baseDirectory.value / "/gatling/simulation"
  )


initialCommands in console := """
  |import play.api._
  |val env = Environment(new java.io.File("."), this.getClass.getClassLoader, Mode.Dev)
  |val context = ApplicationLoader.createContext(env)
  |val loader = ApplicationLoader(context)
  |val app = loader.load(context)
  |Play.start(app)
""".trim.stripMargin

scalacOptions += "-Ypartial-unification"