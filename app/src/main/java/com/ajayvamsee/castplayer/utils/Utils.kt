package com.ajayvamsee.castplayer.utils

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.view.WindowManager
import android.widget.Toast
import com.google.sample.cast.refplayer.R
import java.lang.Exception

/**
 * Created by Ajay Vamsee on 11/25/2022.
 * Time : 12:57 HRS
 */
object Utils {
    private val TAG = "Utils"

    /**
     * Returns the screen/display size
     *
     */
@JvmStatic
    fun getDisplaySize(context: Context): Point {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val width = display.width
        val height = display.height
        return Point(width, height)
    }

    /**
     * Returns `true` if and only if the screen orientation is portrait.
     */
    @JvmStatic
    fun isOrientationPortrait(context: Context): Boolean {
        return (context.resources.configuration.orientation
                == Configuration.ORIENTATION_PORTRAIT)
    }

    /**
     * Shows an error dialog with a given text message.
     */
    @JvmStatic
    fun showErrorDialog(context: Context?, errorString: String?) {
        AlertDialog.Builder(context).setTitle(R.string.error)
            .setMessage(errorString)
            .setPositiveButton(R.string.ok) { dialog, id -> dialog.cancel() }
            .create()
            .show()
    }

    /**
     * Shows an "Oops" error dialog with a text provided by a resource ID
     */
    @JvmStatic
    fun showOopsDialog(context: Context, resourceId: Int) {
        AlertDialog.Builder(context).setTitle(R.string.oops)
            .setMessage(context.getString(resourceId))
            .setPositiveButton(R.string.ok) { dialog, id -> dialog.cancel() }
            .setIcon(R.drawable.ic_action_alerts_and_states_warning)
            .create()
            .show()
    }

    /**
     * Gets the version of app.
     */
    @JvmStatic
    fun getAppVersionName(context: Context): String? {
        var versionString: String? = null
        try {
            val info = context.packageManager.getPackageInfo(context.packageName,
                0 /* basic info */)
            versionString = info.versionName
        } catch (e: Exception) {
            // do nothing
        }
        return versionString
    }

    /**
     * Shows a (long) toast.
     */
    @JvmStatic
    fun showToast(context: Context, resourceId: Int) {
        Toast.makeText(context, context.getString(resourceId), Toast.LENGTH_LONG).show()
    }

    /**
     * Formats time from milliseconds to hh:mm:ss string format.
     */
    @JvmStatic
    fun formatMillis(millisec: Int): String {
        var seconds = (millisec / 1000)
        val hours = seconds / (60 * 60)
        seconds %= 60 * 60
        val minutes = seconds / 60
        seconds %= 60
        val time = if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
        return time
    }
}