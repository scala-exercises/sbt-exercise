addSbtPlugin("com.eed3si9n"              % "sbt-buildinfo"             % "0.10.0")
addSbtPlugin("org.scalameta"             % "sbt-mdoc"                  % "2.2.24")
addSbtPlugin("org.scalameta"             % "sbt-scalafmt"              % "2.4.6")
addSbtPlugin("com.github.sbt"            % "sbt-ci-release"            % "1.5.10")
addSbtPlugin("de.heikoseeberger"         % "sbt-header"                % "5.6.0")
addSbtPlugin("com.alejandrohdezma"       % "sbt-github"                % "0.11.2")
addSbtPlugin("com.alejandrohdezma"       % "sbt-github-header"         % "0.11.2")
addSbtPlugin("com.alejandrohdezma"       % "sbt-github-mdoc"           % "0.11.2")
addSbtPlugin("com.alejandrohdezma"       % "sbt-remove-test-from-pom"  % "0.1.0")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"              % "0.1.20")
addSbtPlugin("ch.epfl.scala"             % "sbt-missinglink"           % "0.3.3")
addSbtPlugin("com.github.cb372"          % "sbt-explicit-dependencies" % "0.2.16")

libraryDependencies += "com.spotify" % "missinglink-core" % "0.2.5"

addDependencyTreePlugin
