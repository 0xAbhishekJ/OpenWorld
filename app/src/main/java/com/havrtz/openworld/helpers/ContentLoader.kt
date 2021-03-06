package com.havrtz.openworld.helpers

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.AsyncTask
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.havrtz.openworld.R

object ContentLoader {
    /**
     * Used to loads fragments
     */
    fun loadActivity(activity1: Activity?, activity2: Activity?): Boolean {
        ActivityLoader().execute(activity1, activity2)
        return true
    }

    /** TODO Make this class jump between activities  */
    private class ActivityLoader : AsyncTask<Activity?, Void?, Activity>() {
        var i: Intent? = null

        override fun onPostExecute(activity: Activity) {
            activity.startActivity(i, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        }

        override fun onPreExecute() {}
        override fun onProgressUpdate(vararg values: Void?) {}
        override fun doInBackground(vararg activities: Activity?): Activity? {
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                Thread.interrupted()
            }
            i = Intent(activities[0], activities[1]?.javaClass)
            i!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            return activities[0]
        }
    }
    /** TODO Make this class jump between activities  */
}