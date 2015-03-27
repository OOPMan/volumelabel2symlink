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

    def createLocation(location: String) = {
      Shell.SU.run(List(
        s"""mkdir -p "$location" """,
        s"""chmod 777 "$location" """
      ).asJava)
    }

    def symlinkLocation(source: String, destination: String) = {
      Shell.SU.run(s"""ln -s "$source" "$destination" """)
    }

    def rebindLocation(source: String, destination: String) = {
      Shell.SU.run(List(
        s"""mkdir -p "$destination" """,
        s"""chmod 777 "$destination" """,
        s"""busybox mount --rbind "$source" "$destination" """).asJava)
    }

    val mountOutput = Shell.SU.run("mount").asScala
    intent.getAction match {
      case MediaActivityIntentService.ACTION_MEDIA_MOUNTED =>
        val prefs = getSharedPreferences("VolumeLabel2Symlink", Context.MODE_PRIVATE)
        val linkLocation = prefs.getString("linkLocation", defaultLinkLocation)
        val symlinkLinkLocation = s"""$linkLocation/symlinks"""
        val mountLinkLocation = s"""$linkLocation/mounts"""
        val scanLocations = prefs.getStringSet("scanLocations", defaultScanLocations).asScala
        val linkMethod = prefs.getInt("linkMethod", 0)

        // TODO: Handle the scenario where linkLocation is on a read-only file-system
        createLocation(linkLocation)
        linkMethod match {
          case 0 =>
            createLocation(symlinkLinkLocation)
          case 1 =>
            createLocation(mountLinkLocation)
          case 2 =>
            createLocation(symlinkLinkLocation)
            createLocation(mountLinkLocation)
        }

        for (scanLocation <- scanLocations; mountPoint <- mountOutput) {
          val mountEntryPattern = s"^(\\S+)\\s+($scanLocation/\\S+)\\s+".r.unanchored
          mountPoint match {
            case mountEntryPattern(devicePath, mount) =>
              for (blkidOutputLine <- Shell.SU.run(s"blkid $devicePath").asScala) blkidOutputLine match {
                case blockDeviceLabelPattern(deviceLabel) =>
                  linkMethod match {
                    case 0 =>
                      symlinkLocation(mount, s"$symlinkLinkLocation/$deviceLabel")
                      Application.toast(this, s"Symlinked $mount to $symlinkLinkLocation/$deviceLabel")
                    case 1 =>
                      rebindLocation(mount, s"$mountLinkLocation/$deviceLabel")
                      Application.toast(this, s"""Rebound $mount as $mountLinkLocation/$deviceLabel""")
                    case 2 =>
                      symlinkLocation(mount, s"$symlinkLinkLocation/$deviceLabel")
                      rebindLocation(mount, s"$mountLinkLocation/$deviceLabel")
                      Application.toast(this, s"Symlinked $mount to $symlinkLinkLocation/$deviceLabel and rebound $mount as $mountLinkLocation/$deviceLabel")
                  }
                case _ =>
              }
            case _ =>
          }
        }

      case MediaActivityIntentService.ACTION_MEDIA_UNMOUNTED =>
      case _ =>
    }
//    Application.toast(this, intent.getAction)
  }
}
