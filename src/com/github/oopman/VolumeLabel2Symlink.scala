package com.github.oopman

import android.app.{PendingIntent, AlarmManager}
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.view.View
import eu.chainfire.libsuperuser.Shell
import org.scaloid.common._
//import com.github.oopman.PerformCheckReceiver
import scala.collection.JavaConverters._

import scala.concurrent.{ExecutionContext, Future}

class VolumeLabel2Symlink extends SActivity {
  implicit val exec = ExecutionContext.fromExecutor(
    AsyncTask.THREAD_POOL_EXECUTOR)


  onCreate {
    val labelledBlockDevicePattern = "LABEL=\"([^\"]+)\"".r.unanchored
    val blockDevicePattern = "/dev/block/([^:]+)".r.unanchored
//    val mountEntryPattern = "/mnt/usb/([\\S]+)".r.unanchored

    contentView = new SVerticalLayout {
      style {
        case t: STextView => t textSize 10.dip
      }
      STextView("VolumeLabel2Symlink").textSize(20.dip).marginBottom(20.dip)
      STextView("Click the button to trigger a check for labelled block devices. Symlinks will be created under /mnt/usb")
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

    /*
    alarmManager.setRepeating(
      AlarmManager.RTC_WAKEUP,
      System.currentTimeMillis(),
      1000,
      PendingIntent.getBroadcast(ctx, 0, SIntent[PerformCheckReceiver], 0))
    */
  }

}
