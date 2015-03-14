package com.github.oopman

import android.graphics.Color
import org.scaloid.common._

class VolumeLabel2Symlink extends SActivity {

  onCreate {
    contentView = new SVerticalLayout {
      style {
        case t: STextView => t textSize 10.dip
      }
      STextView("VolumeLabel2Symlink").textSize(20.dip).marginBottom(20.dip)
      STextView("The app is now monitoring for attached block devices with labels. Symlinks will be created under /mnt/labelled")
    } padding 20.dip
  }

}
