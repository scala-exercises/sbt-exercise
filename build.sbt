ThisBuild / organization       := "org.scala-exercises"
ThisBuild / githubOrganization := "47degrees"
ThisBuild / scalaVersion       := V.scala212

publish / skip := true
ThisBuild / extraCollaborators += Collaborator.github("47erbot")

addCommandAlias(
  "ci-test",
  ";scalafmtCheckAll; scalafmtSbtCheck; +test; +publishLocal; sbt-exercise/scripted"
)
addCommandAlias("ci-docs", ";github; mdoc; headerCreateAll")
addCommandAlias("ci-publish", ";github; ci-release")

lazy val V = new {
  val cats: String                = "2.7.0"
  val collectioncompat: String    = "2.7.0"
  val github4s: String            = "0.31.0"
  val http4s: String              = "0.23.12"
  val runtime: String             = "0.7.0"
  val scala: String               = "2.13.8"
  val scala212: String            = "2.12.15"
  val scalacheck: String          = "1.16.0"
  val scalacheckShapeless: String = "1.3.0"
  val scalamacros: String         = "2.1.1"
  val scalariform: String         = "0.2.10"
  val scalatest: String           = "3.2.10"
}

lazy val definitions = (project in file("definitions"))
  .settings(name := "definitions")
  .settings(
    crossScalaVersions := Seq(V.scala212, V.scala),
    libraryDependencies ++= Seq(
      "org.typelevel"              %% "cats-core"                 % V.cats,
      "org.scalatest"              %% "scalatest"                 % V.scalatest,
      "org.scalacheck"             %% "scalacheck"                % V.scalacheck,
      "com.github.alexarchambault" %% "scalacheck-shapeless_1.15" % V.scalacheckShapeless
    )
  )

lazy val compiler = (project in file("compiler"))
  .settings(name := "exercise-compiler")
  .settings(
    exportJars         := true,
    crossScalaVersions := Seq(V.scala212, V.scala),
    scalacOptions -= "-Xfatal-warnings",
    libraryDependencies ++= Seq(
      "org.scala-exercises" %% "runtime" % V.runtime exclude ("org.scala-lang.modules", "scala-collection-compat"),
      "org.scala-lang"          % "scala-compiler"          % scalaVersion.value,
      "org.scala-lang.modules" %% "scala-collection-compat" % V.collectioncompat,
      "org.typelevel"          %% "cats-core"               % V.cats      % Compile,
      "org.http4s"             %% "http4s-blaze-client"     % V.http4s,
      "org.http4s"             %% "http4s-circe"            % V.http4s,
      "com.47deg"              %% "github4s"                % V.github4s,
      "org.scalariform"        %% "scalariform"             % V.scalariform,
      "org.typelevel"          %% "cats-laws"               % V.cats      % Test,
      "org.scalatest"          %% "scalatest"               % V.scalatest % Test
    )
  )
  .dependsOn(definitions)

lazy val compilerClasspath = TaskKey[Classpath]("compiler-classpath")

lazy val `sbt-exercise` = (project in file("sbt-exercise"))
  .settings(name := "sbt-exercise")
  .settings(
    scalacOptions -= "-Xfatal-warnings",
    scalacOptions += "-Ypartial-unification",
    libraryDependencies += "org.typelevel" %% "cats-core" % V.cats % Compile,
    addCompilerPlugin("org.scalamacros" % "paradise" % V.scalamacros cross CrossVersion.full),
    // Leverage build info to populate compiler classpath--
    compilerClasspath := { (compiler / Compile / fullClasspath) }.value,
    buildInfoObject   := "Meta",
    buildInfoPackage  := "org.scalaexercises.plugin.sbtexercise",
    buildInfoKeys := Seq(
      version,
      BuildInfoKey.map(compilerClasspath) { case (_, classFiles) ⇒
        ("compilerClasspath", classFiles.map(_.data))
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
      val x = (Test / compile).value
      val y = (definitions / publishLocal).value
      val z = (compiler / publishLocal).value
      ()
    }
  )
  .enablePlugins(SbtPlugin)
  .enablePlugins(BuildInfoPlugin)

lazy val documentation = project
  .settings(mdocOut := file("."))
  .settings(publish / skip := true)
  .enablePlugins(MdocPlugin)
