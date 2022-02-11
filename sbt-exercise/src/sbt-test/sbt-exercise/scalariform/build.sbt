val pluginVersion = System.getProperty("plugin.version")

lazy val root = (project in file("."))
  .settings(
    scalaVersion := "2.12.15",
    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      Resolver.defaultLocal
    ),
    libraryDependencies ++= Seq(
      "org.scala-exercises" %% "exercise-compiler" % pluginVersion changing ()
    ),
    dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.0.1"
  )
  .enablePlugins(ExerciseCompilerPlugin)
