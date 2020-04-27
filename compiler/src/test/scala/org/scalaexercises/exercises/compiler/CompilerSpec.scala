/*
 * Copyright 2020 47 Degrees Open Source <https://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.scalaexercises.compiler

import org.scalaexercises.definitions.{BuildInfo, Library}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.reflect.internal.util.{AbstractFileClassLoader, BatchSourceFile}
import scala.tools.nsc.io.VirtualDirectory
import scala.tools.nsc.{Global, Settings}

class CompilerSpec extends AnyFunSpec with Matchers {

  val metaInfoCode =
    """
      import scala.Predef._

      /** This object was generated by sbt-buildinfo. */
      case object LibMetaInfo extends org.scalaexercises.definitions.BuildInfo {
        /** The value is "exercises-stdlib". */
        val name: String = "exercises-stdlib"
        /** The value is "0.2.3-SNAPSHOT". */
        val version: String = "0.2.3-SNAPSHOT"
        /** The value is "2.11.7". */
        val scalaVersion: String = "2.11.7"
        /** The value is "org.scala-exercises". */
        val organization: String = "org.scala-exercises"
        /** The value is scala.collection.Seq("sonatype-snapshots: https://oss.sonatype.org/content/repositories/snapshots", "sonatype-releases: https://oss.sonatype.org/content/repositories/releases"). */
        val resolvers: scala.collection.Seq[String] = scala.collection.Seq("sonatype-snapshots: https://oss.sonatype.org/content/repositories/snapshots", "sonatype-releases: https://oss.sonatype.org/content/repositories/releases")
        /** The value is scala.collection.Seq("org.scala-lang:scala-library:2.11.7", "org.scala-lang:scala-compiler:2.11.7:ensime-internal", "org.scala-lang:scala-library:2.11.7:ensime-internal", "org.scala-lang:scala-reflect:2.11.7:ensime-internal", "org.scala-lang:scalap:2.11.7:ensime-internal", "com.chuusai:shapeless:2.2.5", "org.scalatest:scalatest:2.2.4", "org.scala-exercises:exercise-compiler:0.2.3-SNAPSHOT", "org.scala-exercises:definitions:0.2.3-SNAPSHOT", "org.scalacheck:scalacheck:1.12.5", "com.github.alexarchambault:scalacheck-shapeless_1.12:0.3.1"). */
        val libraryDependencies: scala.collection.Seq[String] = scala.collection.Seq("org.scala-lang:scala-library:2.11.7", "org.scala-lang:scala-compiler:2.11.7:ensime-internal", "org.scala-lang:scala-library:2.11.7:ensime-internal", "org.scala-lang:scala-reflect:2.11.7:ensime-internal", "org.scala-lang:scalap:2.11.7:ensime-internal", "com.chuusai:shapeless:2.2.5", "org.scalatest:scalatest:2.2.4", "org.scala-exercises:exercise-compiler:0.2.3-SNAPSHOT", "org.scala-exercises:definitions:0.2.3-SNAPSHOT", "org.scalacheck:scalacheck:1.12.5", "com.github.alexarchambault:scalacheck-shapeless_1.12:0.3.1")
        override val toString: String = {
          "name: %s, version: %s, scalaVersion: %s, organization: %s, resolvers: %s, libraryDependencies: %s" format (
            name, version, scalaVersion, organization, resolvers, libraryDependencies
          )
        }
      }
                     """

  val metaInfo = globalUtil
    .load(metaInfoCode)
    .loadClass("LibMetaInfo$")
    .getField("MODULE$")
    .get(null)
    .asInstanceOf[BuildInfo]

  describe("library compilation") {
    it("works") {

      val code = """
      /** This is the sample library.
        * @param name Sample Library
        */
      object SampleLibrary extends org.scalaexercises.definitions.Library {
        override def owner = "scala-exercises"
        override def repository = "site"
        override def sections = List(
          Section1
        )
        override def logoPath = "logo_path"
      }

      /** This is section 1.
        * It has a multi line description.
        *
        * @param name Section 1
        */
      object Section1 extends org.scalaexercises.definitions.Section {
        /** This is example exercise 1! */
        def example1() = { 1 }

        /** This is example exercise 2! */
        def example2() = {
          println("this is some code!")
          println("does it work?")
          (5 + 500)
        }
      }
      """

      val classLoader = globalUtil.load(code)
      val library = classLoader
        .loadClass("SampleLibrary$")
        .getField("MODULE$")
        .get(null)
        .asInstanceOf[Library]

      val path = "(internal)"
      val res = Compiler().compile(
        library,
        code :: Nil,
        path :: Nil,
        metaInfo,
        "/",
        "sample",
        fetchContributors = false
      )
      assert(res.isRight, s"""; ${res.fold(identity, _ => "")}""")
    }

    it("fails if sections list is empty") {

      val code = """
      /** This is the sample library.
        * @param name Sample Library
        */
      object SampleLibrary extends org.scalaexercises.definitions.Library {
        override def owner = "scala-exercises"
        override def repository = "site"
        override def sections = Nil
        override def logoPath = "logo_path"
      }"""

      val classLoader = globalUtil.load(code)
      val library = classLoader
        .loadClass("SampleLibrary$")
        .getField("MODULE$")
        .get(null)
        .asInstanceOf[Library]

      val path = "(internal)"
      val res = Compiler().compile(
        library,
        code :: Nil,
        path :: Nil,
        metaInfo,
        "/",
        "sample",
        fetchContributors = false
      )
      assert(res.isLeft, s"""; ${res.fold(identity, _ => "")}""")
    }
  }

  object globalUtil {
    val global = new Global(new Settings {
      embeddedDefaults[CompilerSpec]
    })
    val outputTarget = new VirtualDirectory("(memory)", None)
    global.settings.outputDirs.setSingleOutput(outputTarget)

    def load(code: String): ClassLoader = {
      lazy val run = new global.Run
      run.compileSources(List(new BatchSourceFile("(inline)", code)))
      new AbstractFileClassLoader(outputTarget, classOf[CompilerSpec].getClassLoader)
    }
  }

}