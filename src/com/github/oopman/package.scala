package com.github

import scala.collection.JavaConverters._

package object oopman {
  val blockDeviceLabelPattern = "LABEL=\"([^\"]+)\"".r.unanchored
  val defaultLinkLocation = "/mnt/usb"
  val defaultLinkFolderName = "links"
  val defaultScanLocations = Set("/mnt/usb").asJava
  val linkMethods = Map(
    0 -> "Symlink Mount",
    1 -> "Rebind Mount",
    2 -> "Both")
  val linkMethodsLabels = (for ((value, label) <- linkMethods) yield label).toArray
  val linkMethodsValues = (for ((value, label) <- linkMethods) yield value).toArray
}
