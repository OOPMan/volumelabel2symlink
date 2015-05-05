package com.github.oopman

import android.util.Log
import com.snappydb.DBFactory
import eu.chainfire.libsuperuser.Application
import android.content.{Intent, Context, BroadcastReceiver}

class BootCompleteReceiver extends BroadcastReceiver {
  val TAG = "BootCompleteReceiver"

  override def onReceive(context: Context, intent: Intent): Unit = {
    Log.d(TAG, "entered onReceive")
    // Clear relevant keys in the SnappyDB instance for the context
    val db = DBFactory.open(context)
    for (key <- db.findKeys("vl2s")) {
      db.del(key)
    }
    db.close()
    Log.d(TAG, "exited onReceive")
  }
}
