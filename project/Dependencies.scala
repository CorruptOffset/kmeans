import sbt.Keys.libraryDependencies
import sbt._

object Dependencies {

  object Paralell{
   val akkaStream = "org.scala-lang.modules" %% "scala-parallel-collections" % "0.2.0"
  }

  object Test{
    private val version = "3.1.1"
    val scalatic =  "org.scalactic" %% "scalactic" % version
    val scalaTest = "org.scalatest" %% "scalatest" % version % "test"
  }


}
