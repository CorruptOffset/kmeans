import Dependencies.{Test, _}

val scala213 = "2.13.1"
val scala212 = "2.12.10"

name := "K_Means"
version := "0.1"
scalaVersion := scala213

lazy val supportedScalaVersions = List(scala213, scala212)


val kMeans = Project(id = "kmeans",base = file("."))
  .settings(
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Seq(
      Test.scalaTest,
      Test.scalatic
    ))


