name := "PageRankSpark"

version := "0.1"

scalaVersion := "2.11.8"

// https://mvnrepository.com/artifact/org.scalatest/scalatest
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.0" //% "provided"

// https://mvnrepository.com/artifact/org.scala-lang.modules/scala-xml
libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.1.1"

// https://mvnrepository.com/artifact/com.typesafe/config
libraryDependencies += "com.typesafe" % "config" % "1.2.1"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.0" //% "provided"

// https://mvnrepository.com/artifact/com.databricks/spark-xml
libraryDependencies += "com.databricks" %% "spark-xml" % "0.5.0"

Compile / unmanagedJars := (baseDirectory.value ** "*.jar").classpath

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
assemblyJarName in assembly := "pagerank.jar"
mainClass in (Compile, run) := Some("pagerank")

scalacOptions += "-target:jvm-1.8"
javacOptions in (Compile, compile) ++= Seq("-source", "1.8", "-target", "1.8", "-g:lines")