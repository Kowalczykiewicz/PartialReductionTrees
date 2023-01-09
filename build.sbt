name := "partial-reduction-trees"
version := "0.1"
scalaVersion := "3.2.1"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "2.0.5",
  ("com.storm-enroute" %% "scalameter-core" % "0.21").cross(CrossVersion.for3Use2_13)
)

libraryDependencies ++= Seq(
  "dev.zio" %% "zio-test"          % "2.0.5" % Test,
  "dev.zio" %% "zio-test-sbt"      % "2.0.5" % Test,
  "dev.zio" %% "zio-test-magnolia" % "2.0.5" % Test
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

Test / parallelExecution := true

fork := true

outputStrategy := Some(StdoutOutput)

connectInput := true