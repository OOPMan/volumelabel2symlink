package com.github.oopman

import android.app.IntentService
import android.content.{Context, Intent}
import android.os.Bundle
import eu.chainfire.libsuperuser.Application
import org.scaloid.common.{SContext, SIntent}

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
  override def onHandleIntent(intent: Intent): Unit = {
    Application.toast(this, intent.getAction)

  }
}
