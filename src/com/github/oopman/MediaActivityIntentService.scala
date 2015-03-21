package com.github.oopman

import android.app.IntentService
import android.content.{Context, Intent}
import android.os.Bundle
import eu.chainfire.libsuperuser.{Shell, Application}
import org.scaloid.common.{SContext, SIntent}
import scala.collection.JavaConverters._

object MediaActivityIntentService {
  val ACTION_MEDIA_MOUNTED = "media_mount"
  val ACTION_MEDIA_UNMOUNTED = "media_unmounted"
  val ACTION_BOOT_COMPLETE = "boot_complete"

  def performAction(context: Context, action: String, extras: Bundle = Bundle.EMPTY): Unit = (context, action, extras) match {
    case (_, "", _) =>
    case (c: Context, s: String, e: Bundle) =>
      implicit val ctx = context
      ctx.startService(SIntent[MediaActivityIntentService].setAction(action).putExtras(extras))
  }
}

class MediaActivityIntentService extends IntentService("MediaActivityIntentService") with SContext {
  override def onHandleIntent(intent: Intent) = {
    val mountOutput = Shell.SU.run("mount").asScala
    intent.getAction match {
      case MediaActivityIntentService.ACTION_MEDIA_MOUNTED =>
        val prefs = getSharedPreferences("VolumeLabel2Symlink", Context.MODE_PRIVATE)
        val linkLocation = prefs.getString("linkLocation", defaultLinkLocation)
        val scanLocations = prefs.getStringSet("scanLocations", defaultScanLocations).asScala
        //TODO: Ensure link location exists
        for (scanLocation <- scanLocations; mountPoint <- mountOutput) {
          val mountEntryPattern = s"(\\S+)\\s+($scanLocation\\S*)\\s+".r.unanchored
          mountPoint match {
            case mountEntryPattern(devicePath, mount) =>
              for (blkidOutputLine <- Shell.SU.run(s"blkid $devicePath").asScala) blkidOutputLine match {
                case blockDeviceLabelPattern(deviceLabel) =>
                  Shell.SU.run(s"ln -s \"$mount\" \"$linkLocation/$deviceLabel\"")
                case _ =>
              }
            case _ =>
          }
        }

      case MediaActivityIntentService.ACTION_MEDIA_UNMOUNTED =>
    }
    Application.toast(this, intent.getAction)
  }
}
