package com.github.oopman

import android.util.Log
import eu.chainfire.libsuperuser.Application
import android.content.{Intent, Context, BroadcastReceiver}

class BootCompleteReceiver extends BroadcastReceiver {
  val TAG = "BootCompleteReceiver"

  override def onReceive(context: Context, intent: Intent): Unit = {
    Log.d(TAG, "entered onReceive")
    //TODO: Try using this receiver to enable the MediaActivityReceiver
    Log.d(TAG, "exited onReceive")
  }
}
