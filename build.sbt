import android.Keys._

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

android.Plugin.androidBuild

name := "volumelabel2symlink"

scalaVersion := "2.11.5"

proguardCache in Android ++= Seq(
  ProguardCache("org.scaloid") % "org.scaloid"
)

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize", "-keepattributes Signature", "-printseeds target/seeds.txt", "-printusage target/usage.txt"
  , "-dontwarn scala.collection.**" // required from Scala 2.11.4
)

libraryDependencies ++= Seq(
  "org.scaloid" %% "scaloid" % "3.6.1-10" withSources() withJavadoc(),
  aar("eu.chainfire" % "libsuperuser" % "1.0.0.201503122108")
)

resolvers += "bintray-chainfire-maven" at "http://dl.bintray.com/chainfire/maven"

scalacOptions in Compile += "-feature"

run <<= run in Android

install <<= install in Android

retrolambdaEnable in Android := false
