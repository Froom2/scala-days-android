import Libraries.android._
import Libraries.macroid._

android.Plugin.androidBuild

platformTarget in Android := Versions.androidPlatformV

name := """scala-days-android"""

organization := "com.fortysevendeg"

organizationName := "47 Degrees"

organizationHomepage := Some(new URL("http://47deg.com"))

version := Versions.appV

scalaVersion := Versions.scalaV

scalacOptions ++= Seq("-feature", "-deprecation")

credentials += Credentials(new File(Path.userHome.absolutePath + "/.ivy2/.credentials"))

resolvers ++= Settings.resolvers

libraryDependencies ++= Seq(
  aar(macroidRoot),
  aar(androidAppCompat),
  aar(androidCardView),
  aar(androidRecyclerview),
  aar(macroidExtras),
  compilerPlugin(Libraries.wartRemover))

run <<= run in Android

proguardScala in Android := true

useProguard in Android := true

proguardOptions in Android ++= Settings.proguardCommons

apkbuildExcludes in Android ++= Seq(
  "META-INF/LICENSE",
  "META-INF/LICENSE.txt",
  "META-INF/NOTICE",
  "META-INF/NOTICE.txt")