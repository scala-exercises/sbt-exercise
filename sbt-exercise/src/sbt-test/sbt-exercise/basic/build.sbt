import org.scalaexercises.plugin.sbtexercise.ExerciseCompilerPlugin

val pluginVersion = System.getProperty("plugin.version")

// Required to prevent errors for eviction from binary incompatible dependency
// resolutions.
// See also: https://github.com/scala-exercises/exercises-cats/pull/267
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % "always"

lazy val content = (project in file("content"))
  .enablePlugins(ExerciseCompilerPlugin)
  .settings(
    scalaVersion := "2.12.15",
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
    scalaVersion := "2.12.15",
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
    scalaVersion := "2.12.15",
    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      Resolver.defaultLocal
    )
  )
