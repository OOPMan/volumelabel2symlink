package com.github.oopman

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.view.View
import eu.chainfire.libsuperuser.Shell
import org.scaloid.common._
import scala.collection.JavaConverters._
import net.rdrei.android.dirchooser.{DirectoryChooserFragment, DirectoryChooserActivity}

import scala.concurrent.{ExecutionContext, Future}

class VolumeLabel2Symlink extends SActivity with DirectoryChooserFragment.OnFragmentInteractionListener {
  implicit val exec = ExecutionContext.fromExecutor(
    AsyncTask.THREAD_POOL_EXECUTOR)

  val labelledBlockDevicePattern = "LABEL=\"([^\"]+)\"".r.unanchored
  val blockDevicePattern = "/dev/block/([^:]+)".r.unanchored
  //    val mountEntryPattern = "/mnt/usb/([\\S]+)".r.unanchored
  val defaultLinkLocation = "/mnt/usb"
  val defaultScanLocations = Set("/mnt/usb").asJava
  val REQUEST_CODE_SCAN_LOCATION = 0

  var linkLocation: STextView = null
  var scanLocations: SListView = null
  var directoryChooserDialog: DirectoryChooserFragment = null


  onCreate {
    val prefs = getPreferences(0)

    contentView = new SVerticalLayout {
      style {
        case t: STextView => t textSize 10.dip
      }

      directoryChooserDialog = DirectoryChooserFragment.newInstance("links", "/")

      STextView("VolumeLabel2Symlink").textSize(20.dip).marginBottom(20.dip)
      SButton("Set Link Location", (view: View) => {
        //TODO: Display Folder chooser
      })
      linkLocation = STextView("Link location: " + prefs.getString("linkLocation", defaultLinkLocation))
      SButton("Add Scan Location", /*/(view: View) =>*/ {
        directoryChooserDialog.show(getFragmentManager(), null)
      })
      //TODO: ListAdaptor
      scanLocations = SListView().adapter(SArrayAdapter(prefs.getStringSet("scanLocations", defaultScanLocations)))
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

  override def onSelectDirectory(s: String): Unit = {
    val prefs = getPreferences(0)
    val editor = prefs.edit()
    val currentScanLocations = prefs.getStringSet("scanLocations", defaultScanLocations)
    val newScanLocations = (Set(s) ++ currentScanLocations.asScala).asJava
    scanLocations.adapter(SArrayAdapter(newScanLocations))
    editor.putStringSet("scanLocations", newScanLocations)
    editor.commit()
    directoryChooserDialog.dismiss()
  }

  override def onCancelChooser(): Unit = {
    directoryChooserDialog.dismiss()
  }
}
