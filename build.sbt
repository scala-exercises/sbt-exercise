ThisBuild / organization := "org.scala-exercises"
ThisBuild / githubOrganization := "47degrees"
ThisBuild / scalaVersion := V.scala212

addCommandAlias(
  "ci-test",
  ";scalafmtCheckAll; scalafmtSbtCheck; +test; +publishLocal; sbt-exercise/test; sbt-exercise/scripted"
)
addCommandAlias("ci-docs", ";github; project-docs/mdoc; headerCreateAll")

lazy val V = new {
  val cats: String                = "2.1.1"
  val collectioncompat: String    = "2.1.6"
  val github4s: String            = "0.24.0"
  val http4s: String              = "0.21.3"
  val runtime: String             = "0.6.0"
  val scala: String               = "2.13.2"
  val scala212: String            = "2.12.11"
  val scalacheck: String          = "1.14.3"
  val scalacheckShapeless: String = "1.2.5"
  val scalamacros: String         = "2.1.1"
  val scalariform: String         = "0.2.10"
  val scalatest: String           = "3.1.1"
}

lazy val root = project
  .in(file("."))
  .settings(moduleName := "sbt-exercise")
  .settings(skip in publish := true)
  .aggregate(definitions, compiler, `sbt-exercise`)
  .dependsOn(definitions, compiler, `sbt-exercise`)

lazy val definitions = (project in file("definitions"))
  .settings(name := "definitions")
  .settings(
    crossScalaVersions := Seq(V.scala212, V.scala),
    libraryDependencies ++= Seq(
      "org.typelevel"              %% "cats-core"                 % V.cats,
      "org.scalatest"              %% "scalatest"                 % V.scalatest,
      "org.scalacheck"             %% "scalacheck"                % V.scalacheck,
      "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % V.scalacheckShapeless
    )
  )

lazy val compiler = (project in file("compiler"))
  .settings(name := "exercise-compiler")
  .settings(
    exportJars := true,
    crossScalaVersions := Seq(V.scala212, V.scala),
    scalacOptions -= "-Xfatal-warnings",
    libraryDependencies ++= Seq(
      "org.scala-exercises"    %% "runtime"                 % V.runtime,
      "org.scala-lang"         % "scala-compiler"           % scalaVersion.value,
      "org.scala-lang.modules" %% "scala-collection-compat" % V.collectioncompat,
      "org.typelevel"          %% "cats-core"               % V.cats % Compile,
      "org.http4s"             %% "http4s-blaze-client"     % V.http4s,
      "org.http4s"             %% "http4s-circe"            % V.http4s,
      "com.47deg"              %% "github4s"                % V.github4s,
      "org.scalariform"        %% "scalariform"             % V.scalariform,
      "org.typelevel"          %% "cats-laws"               % V.cats % Test,
      "org.scalatest"          %% "scalatest"               % V.scalatest % Test
    )
  )
  .dependsOn(definitions)

lazy val compilerClasspath = TaskKey[Classpath]("compiler-classpath")

lazy val `sbt-exercise` = (project in file("sbt-exercise"))
  .settings(name := "sbt-exercise")
  .settings(
    scalacOptions -= "-Xfatal-warnings",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % V.cats % Compile
    ),
    scalacOptions += "-Ypartial-unification",
    addCompilerPlugin("org.scalamacros" % "paradise" % V.scalamacros cross CrossVersion.full),
    // Leverage build info to populate compiler classpath--
    compilerClasspath := { fullClasspath in (compiler, Compile) }.value,
    buildInfoObject := "Meta",
    buildInfoPackage := "org.scalaexercises.plugin.sbtexercise",
    buildInfoKeys := Seq(
      version,
      BuildInfoKey.map(compilerClasspath) {
        case (_, classFiles) ⇒ ("compilerClasspath", classFiles.map(_.data))
      }
    )
  )
  .settings(
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    // Publish definitions before running scripted
    scriptedDependencies := {
      val x = (compile in Test).value
      val y = (publishLocal in definitions).value
      val z = (publishLocal in compiler).value
      ()
    }
  )
  .enablePlugins(SbtPlugin)
  .enablePlugins(BuildInfoPlugin)

lazy val `project-docs` = (project in file(".docs"))
  .aggregate(definitions, compiler)
  .settings(moduleName := "sbt-exercise-project-docs")
  .settings(mdocIn := file(".docs"))
  .settings(mdocOut := file("."))
  .settings(skip in publish := true)
  .enablePlugins(MdocPlugin)
