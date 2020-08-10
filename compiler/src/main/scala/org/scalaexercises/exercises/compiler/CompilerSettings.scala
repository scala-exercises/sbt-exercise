package org.scalaexercises.exercises.compiler

object CompilerSettings {

  private def classPathOfClass(className: String): List[String] = {
    val resource = className.split('.').mkString("/", "/", ".class")
    val path     = getClass.getResource(resource).getPath
    if (path.indexOf("file:") >= 0) {
      val indexOfFile      = path.indexOf("file:") + 5
      val indexOfSeparator = path.lastIndexOf('!')
      List(path.substring(indexOfFile, indexOfSeparator))
    } else {
      require(path.endsWith(resource))
      List(path.substring(0, path.length - resource.length + 1))
    }
  }

  private lazy val compilerPath =
    try classPathOfClass("scala.tools.nsc.Interpreter")
    catch {
      case e: Throwable =>
        throw new RuntimeException(
          "Unable to load Scala interpreter from classpath (scala-compiler jar is missing?)",
          e
        )
    }

  private lazy val libPath =
    try classPathOfClass("scala.AnyVal")
    catch {
      case e: Throwable =>
        throw new RuntimeException(
          "Unable to load scala base object from classpath (scala-library jar is missing?)",
          e
        )
    }

  lazy val paths: List[String] = compilerPath ::: libPath
}
