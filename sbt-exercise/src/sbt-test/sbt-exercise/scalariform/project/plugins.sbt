resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.typesafeRepo("releases")
)

{
  val pluginVersion = System.getProperty("plugin.version")
  if (pluginVersion == null)
    throw new RuntimeException(
      """|The system property 'plugin.version' is not defined.
                                  |Specify this property using the scriptedLaunchOpts -D.""".stripMargin
    )
  else addSbtPlugin("org.scala-exercises" % "sbt-exercise" % pluginVersion)
}
addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.3")
