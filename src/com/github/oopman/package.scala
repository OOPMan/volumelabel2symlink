package com.github

import scala.collection.JavaConverters._

package object oopman {
  val blockDeviceLabelPattern = "LABEL=\"([^\"]+)\"".r.unanchored
  val defaultLinkLocation = "/mnt/sdcard/links"
  val defaultScanLocations = Set("/mnt").asJava
}
