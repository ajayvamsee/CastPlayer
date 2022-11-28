package com.ajayvamsee.castplayer.browser

import android.content.Context
import android.util.Log
import androidx.loader.content.AsyncTaskLoader
import com.ajayvamsee.castplayer.utils.MediaItem

/**
 * Created by Ajay Vamsee on 11/24/2022.
 * Time : 19:13 HRS
 */
class VideoItemLoader(context: Context?,private val mUrl:String) : AsyncTaskLoader<List<MediaItem>?>(context!!){
    override fun loadInBackground(): List<MediaItem>? {
        return try {
           VideoProvider.buildMedia(mUrl)
        } catch (e:Exception){
            Log.d(TAG, "loadInBackground: ",e)
            null
        }
    }

    override fun onStopLoading() {
        super.onStopLoading()

        cancelLoad()
    }

    override fun onStartLoading() {
        super.onStartLoading()

        forceLoad()
    }
    companion object{
        private const val TAG = "VideoItemLoader"
    }

}
