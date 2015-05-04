import android.Keys._

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

android.Plugin.androidBuild

name := "volumelabel2symlink"

scalaVersion := "2.11.6"

proguardCache in Android ++= Seq(
  ProguardCache("org.scaloid") % "org.scaloid"
)

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize", "-keepattributes Signature", "-printseeds target/seeds.txt", "-printusage target/usage.txt"
  , "-dontwarn scala.collection.**" // required from Scala 2.11.4
)

libraryDependencies ++= Seq(
  "org.scaloid" %% "scaloid" % "3.6.1-10" withSources() withJavadoc(),
  aar("com.snappydb" % "snappydb-lib" % "0.5.0") withSources() withJavadoc(),
  "com.esotericsoftware.kryo" % "kryo" % "2.24.0" withSources() withJavadoc(),
  "com.gu" % "option" % "1.3",
  "com.android.support" % "support-annotations" % "21.0.3",
  aar("eu.chainfire" % "libsuperuser" % "1.0.0.201503122108") withSources() withJavadoc()/*,
  aar("net.rdrei.android.dirchooser" % "library" % "2.1") withSources() withJavadoc()*/
)

resolvers ++= Seq(
  "bintray-chainfire-maven" at "http://dl.bintray.com/chainfire/maven",
  "Guardian-github" at "http://guardian.github.com/maven/repo-releases"
)

scalacOptions in Compile += "-feature"

run <<= run in Android

install <<= install in Android

retrolambdaEnable in Android := false

localAars in Android += baseDirectory.value / "lib" / "Android-DirectoryChooser.0a723d0de1f053955ef14df6d37bd8fa65069040.aar"
