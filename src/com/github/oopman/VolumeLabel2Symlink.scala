package com.github.oopman

import java.util

import android.content.{Context, Intent}
import android.view.View
import android.widget.AbsListView.CHOICE_MODE_SINGLE
import android.widget._
import net.rdrei.android.dirchooser.DirectoryChooserActivity
import org.scaloid.common._

class VolumeLabel2Symlink extends SActivity {

  val REQUEST_CODE_SCAN_LOCATION = 0
  val REQUEST_CODE_LINK_LOCATION = 1

  var linkLocation: STextView = null
  val scanLocations: util.ArrayList[String] = new util.ArrayList()
  var scanLocationsAdapter: ArrayAdapter[String] = null

  onCreate {
    val prefs = getSharedPreferences("VolumeLabel2Symlink", Context.MODE_PRIVATE)
    val editor = prefs.edit()

    contentView = new SVerticalLayout {
      style {
        case t: STextView => t.textSize(20.dip).<<.marginBottom(10.dip).>>
        case b: SButton => b.<<.marginBottom(10.dip).>>
      }

      STextView("VolumeLabel2Symlink")

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
      scanLocations.clear()
      scanLocations.addAll(prefs.getStringSet("scanLocations", defaultScanLocations))
      scanLocationsAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_single_choice, scanLocations)
      val scanLocationsView = SListView().adapter(scanLocationsAdapter).choiceMode(CHOICE_MODE_SINGLE)

      SButton("Remove Selected Scan Location", (view: View) => {
        val itemToRemove = scanLocationsAdapter.getItem(scanLocationsView.getCheckedItemPosition)
        scanLocations.remove(itemToRemove)
        scanLocationsAdapter.notifyDataSetChanged()
        editor.putStringSet("scanLocations", new util.HashSet[String](scanLocations))
        editor.commit()
      })

      SButton("Scan and Label", (view: View) => {
        startService(SIntent[MediaActivityIntentService].setAction(MediaActivityIntentService.ACTION_MEDIA_MOUNTED))
      })
    } padding 20.dip
  }


  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
      val editor = getSharedPreferences("VolumeLabel2Symlink", Context.MODE_PRIVATE).edit()
      val selectedDirectory = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR)
      if (requestCode == REQUEST_CODE_LINK_LOCATION) {
        editor.putString("linkLocation", selectedDirectory)
        editor.commit()
        linkLocation.setText(s"Link Location: $selectedDirectory")
      } else if (requestCode == REQUEST_CODE_SCAN_LOCATION && !scanLocations.contains(selectedDirectory)) {
        scanLocations.add(selectedDirectory)
        scanLocationsAdapter.notifyDataSetChanged()
        editor.putStringSet("scanLocations", new util.HashSet[String](scanLocations))
        editor.commit()
      }
    }
  }
}
