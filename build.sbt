name := "zio"
version := "0.1"
scalaVersion := "3.2.0"


lazy val zioVersion = "2.0.2"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-test" % zioVersion,
  "dev.zio" %% "zio-json" % "0.3.0",
  "dev.zio" %% "zio-test-sbt" % zioVersion,
  "dev.zio" %% "zio-streams" % zioVersion,
  "dev.zio" %% "zio-test-junit" % zioVersion,
  "dev.zio" %% "zio-http" % "3.0.0-RC1"
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")