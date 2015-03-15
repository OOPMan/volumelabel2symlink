package com.github.oopman

import android.content.{Intent, Context, BroadcastReceiver}
import android.os.Environment
import android.util.Log

class MediaActivityReceiver extends BroadcastReceiver {
  val TAG = "MediaActivityReceiver"
  override def onReceive(context: Context, intent: Intent): Unit = {
    Log.d(TAG, "entered onReceive")

    Environment.getExternalStorageState match {
      case message @ Environment.MEDIA_MOUNTED =>
        Log.d(TAG, message)
        MediaActivityIntentService.performAction(context, MediaActivityIntentService.ACTION_MEDIA_MOUNTED)
        //TODO: Handle Mounted
      case message @ Environment.MEDIA_REMOVED =>
        Log.d(TAG, message)
        MediaActivityIntentService.performAction(context, MediaActivityIntentService.ACTION_MEDIA_UNMOUNTED)
        //TODO: Handle Removed
      case _ =>

    Log.d(TAG, "exited onReceive")
    }
  }
}
