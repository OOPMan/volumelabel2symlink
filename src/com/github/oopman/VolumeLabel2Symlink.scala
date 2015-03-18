package com.github.oopman

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.view.View
import eu.chainfire.libsuperuser.Shell
import org.scaloid.common._
import scala.collection.JavaConverters._

import scala.concurrent.{ExecutionContext, Future}

class VolumeLabel2Symlink extends SActivity {
  implicit val exec = ExecutionContext.fromExecutor(
    AsyncTask.THREAD_POOL_EXECUTOR)

  val labelledBlockDevicePattern = "LABEL=\"([^\"]+)\"".r.unanchored
  val blockDevicePattern = "/dev/block/([^:]+)".r.unanchored
  //    val mountEntryPattern = "/mnt/usb/([\\S]+)".r.unanchored
  val defaultLinkLocation = "/mnt/usb"
  val defaultScanLocations = Set("/mnt/usb")

  onCreate {
    val prefs = getPreferences(0)

    contentView = new SVerticalLayout {
      style {
        case t: STextView => t textSize 10.dip
      }
      STextView("VolumeLabel2Symlink").textSize(20.dip).marginBottom(20.dip)
      STextView("Click the button to trigger a check for labelled block devices. Symlinks will be created under /mnt/usb")
      SButton("Set Link Location", (view: View) => {
        //TODO: Display Folder chooser
      })
      SButton("Add Scan Location", (view: View) => {
        //TODO: Display Folder chooser
      })
      //TODO: ListAdaptor
      val scanLocations = SListView().adapter(SArrayAdapter(prefs.getStringSet("scanLocations", defaultScanLocations.asJava)))
      SButton("Remove Selected Scan Location", (view: View) => {
        //TODO: Implement
      })
      SButton("Scan and Label", (view: View) => {
        Future {
          alert("Results",
            (for (resultLine <- Shell.SU.run("blkid").asScala) yield (resultLine, resultLine) match {
              case (blockDevicePattern(device), labelledBlockDevicePattern(label)) =>
                Shell.SU.run(s"""ln -s /mnt/usb/$device /mnt/usb/"$label" """)
                s"Linked /mnt/usb/$device to /mnt/usb/$label"
              case _ => ""
            }) filter {
              _.length > 0
            } mkString "\n")
        }
      })
    } padding 20.dip
  }

}
