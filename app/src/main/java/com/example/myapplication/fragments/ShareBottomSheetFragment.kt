package com.example.myapplication.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.helpers.AppPackage
import com.example.myapplication.models.Story
import com.example.myapplication.models.StoryElement
import com.example.myapplication.R
import com.example.myapplication.viewmodels.StoryViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ShareBottomSheetFragment : BottomSheetDialogFragment {
    var bitmap: Bitmap? = null
    var bitmapLocation: String? = null
    var bundle: Bundle? = null
    var appPackageName: String? = null
    var imageItems: List<StoryElement>? = null
    var imageLocation: Array<String>

    constructor(bitmapLocation: String?) {
        this.bitmapLocation = bitmapLocation
    }

    constructor(bundle: Bundle, bitmap: Bitmap?, imageItems: List<StoryElement>?) {
        this.bundle = bundle
        this.bitmap = bitmap
        this.imageItems = imageItems
        imageLocation = bundle.getStringArray("imageLocation")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.share_bottom_sheet_fragment, null, false)
        val navigationView: BottomNavigationView = view.findViewById(R.id.nav_view)
        if (bitmapLocation != null) {
            navigationView.menu.findItem(R.id.device_share).isVisible = false
        }
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        return view
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val googlePlayUrl = "https://play.google.com/store/apps/details?id="
        when (item.itemId) {
            R.id.device_share -> {
                saveImage()
                dismiss()
            }
            R.id.facebook_share -> {
                appPackageName = "com.facebook.katana"
                if (AppPackage.isPackageInstalled(appPackageName, activity!!.packageManager)) {
                    onShare(appPackageName)
                } else {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayUrl + appPackageName)))
                }
                dismiss()
            }
            R.id.instagram_share -> {
                appPackageName = "com.instagram.android"
                if (AppPackage.isPackageInstalled(appPackageName, activity!!.packageManager)) {
                    onShare(appPackageName)
                } else {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayUrl + appPackageName)))
                }
                dismiss()
            }
        }
        true
    }

    fun onShare(packageName: String?) {
        val uri: Uri
        uri = if (bitmapLocation == null) {
            val path = MediaStore.Images.Media.insertImage(activity!!.contentResolver,
                    bitmap, "Design", null)
            Uri.parse(path)
        } else {
            Uri.parse(bitmapLocation)
        }
        val share = Intent(Intent.ACTION_SEND)
        //Intent share = new Intent("com.instagram.share.ADD_TO_STORY");
        share.type = "image/*"
        share.setPackage(packageName)
        share.putExtra(Intent.EXTRA_STREAM, uri)
        share.putExtra(Intent.EXTRA_TEXT, "I created something cool!")
        startActivity(Intent.createChooser(share, "Share Your Story!"))
    }

    fun saveImage(): String {
        val bytes = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(Environment.getExternalStorageDirectory().toString() + "/app/image/")
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }
        try {
            val f = File(wallpaperDirectory, Calendar.getInstance()
                    .timeInMillis.toString() + ".jpg")
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(context, arrayOf(f.path), arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)
            if (bundle!!.getBoolean("isUpdate")) {
                //UpdateDB(f.getAbsolutePath());
            } else {
                addToDB(f.absolutePath)
            }
            val snackbar = Snackbar.make(activity!!.findViewById(R.id.templateloader), "Image Saved", Snackbar.LENGTH_SHORT)
            //            .setAction("UNDO", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Respond to the click, such as by undoing the modification that caused
//                    // this message to be displayed
//                    //Log.d("here", "here");
//                }
//            });
            snackbar.show()
            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }

    /*public void UpdateDB(String filepath) {
        databaseHandler.updateUserTemplate(bundle.getString("user_template_id"), bundle.getString("title"), filepath);

        for (int i = 0; i < imageItems.size(); i++) {
            imageItems.get(i).getImage_location(imageLocation[i]);
        }
        databaseHandler.updateImages(bundle.getString("user_template_id"), imageItems);
    }*/
    fun addToDB(filepath: String?) {
        var text = bundle!!.getString("title")
        if (bundle!!.getString("title").isEmpty() || text.trim { it <= ' ' }.length == 0) {
            text = "My Story"
        }
        val storyViewModel = ViewModelProviders.of(this).get(StoryViewModel::class.java)
        storyViewModel.insert(Story(bundle!!.getInt("template_id"), 0, text, filepath!!))
    }
}