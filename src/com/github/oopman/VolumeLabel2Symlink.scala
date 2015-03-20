package com.github.oopman

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.view.View
import eu.chainfire.libsuperuser.Shell
import org.scaloid.common._
import scala.collection.JavaConverters._
import net.rdrei.android.dirchooser.DirectoryChooserActivity

import scala.concurrent.{ExecutionContext, Future}

class VolumeLabel2Symlink extends SActivity {
  implicit val exec = ExecutionContext.fromExecutor(
    AsyncTask.THREAD_POOL_EXECUTOR)

  val labelledBlockDevicePattern = "LABEL=\"([^\"]+)\"".r.unanchored
  val blockDevicePattern = "/dev/block/([^:]+)".r.unanchored
  //    val mountEntryPattern = "/mnt/usb/([\\S]+)".r.unanchored
  val defaultLinkLocation = "/mnt/usb"
  val defaultScanLocations = Set("/mnt/usb").asJava
  val REQUEST_CODE_SCAN_LOCATION = 0
  val REQUEST_CODE_LINK_LOCATION = 1

  var linkLocation: STextView = null
  var scanLocations: SListView = null


  onCreate {
    val prefs = getPreferences(0)

    contentView = new SVerticalLayout {
      style {
        case t: STextView => t textSize 10.dip
      }

      STextView("VolumeLabel2Symlink").textSize(20.dip).marginBottom(20.dip)
      SButton("Set Link Location", (view: View) => {
        startActivityForResult(
          SIntent[DirectoryChooserActivity]
            .putExtra(DirectoryChooserActivity.EXTRA_INITIAL_DIRECTORY, "/")
            .putExtra(DirectoryChooserActivity.EXTRA_NEW_DIR_NAME, "links"),
          REQUEST_CODE_LINK_LOCATION)
      })
      linkLocation = STextView("Link location: " + prefs.getString("linkLocation", defaultLinkLocation))
      SButton("Add Scan Location", (view: View) => {
        startActivityForResult(
          SIntent[DirectoryChooserActivity]
            .putExtra(DirectoryChooserActivity.EXTRA_INITIAL_DIRECTORY, "/")
            .putExtra(DirectoryChooserActivity.EXTRA_NEW_DIR_NAME, "links"),
          REQUEST_CODE_SCAN_LOCATION)
      })
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


  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit = {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
      val prefs = getPreferences(0)
      val editor = prefs.edit()
      val selectedDirectory = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR)
      if (requestCode == REQUEST_CODE_LINK_LOCATION) {
        linkLocation.setText(s"Link Location: $selectedDirectory")
        editor.putString("linkLocation", selectedDirectory)
        editor.commit()
      } else if (requestCode == REQUEST_CODE_SCAN_LOCATION) {
        val currentScanLocations = prefs.getStringSet("scanLocations", defaultScanLocations)
        val newScanLocations = (Set(selectedDirectory) ++ currentScanLocations.asScala).asJava
        scanLocations.adapter(SArrayAdapter(newScanLocations))
        editor.putStringSet("scanLocations", newScanLocations)
        editor.commit()
      }
    }
  }
}
