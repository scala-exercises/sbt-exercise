import org.scalaexercises.plugin.sbtexercise.ExerciseCompilerPlugin

val pluginVersion = System.getProperty("plugin.version")

lazy val content = (project in file("content"))
  .enablePlugins(ExerciseCompilerPlugin)
  .settings(
    scalaVersion := "2.12.16",
    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      Resolver.defaultLocal
    ),
    libraryDependencies ++= Seq(
      "org.scala-exercises" %% "runtime"           % "0.6.0",
      "org.scala-exercises" %% "exercise-compiler" % pluginVersion changing (),
      "org.scala-exercises" %% "definitions"       % pluginVersion changing ()
    )
  )

lazy val contentInPackages = (project in file("contentinpackages"))
  .enablePlugins(ExerciseCompilerPlugin)
  .settings(
    scalaVersion := "2.12.16",
    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      Resolver.defaultLocal
    ),
    libraryDependencies ++= Seq(
      "org.scala-exercises" %% "runtime"           % "0.6.0",
      "org.scala-exercises" %% "exercise-compiler" % pluginVersion changing (),
      "org.scala-exercises" %% "definitions"       % pluginVersion changing ()
    )
  )

lazy val check = (project in file("check"))
  .dependsOn(content, contentInPackages)
  .settings(
    scalaVersion := "2.12.16",
    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      Resolver.defaultLocal
    )
  )
