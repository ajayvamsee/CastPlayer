package com.ajayvamsee.castplayer

import android.graphics.Point
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.ajayvamsee.castplayer.mediaplayer.LocalPlayerActivity
import com.ajayvamsee.castplayer.utils.CustomVolleyRequest
import com.ajayvamsee.castplayer.utils.MediaItem
import com.ajayvamsee.castplayer.utils.Utils
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.google.sample.cast.refplayer.R
import java.util.*

/**
 * Created by Ajay Vamsee on 11/28/2022.
 * Time : 15:56 HRS
 */
class LocalPlayer : AppCompatActivity() {

    private var mVideoView: VideoView? = null
    private var mTitleView: TextView? = null
    private var mDescriptionView: TextView? = null
    private var mStartText: TextView? = null
    private var mEndText: TextView? = null
    private var mSeekbar: SeekBar? = null
    private var mPlayPause: ImageView? = null
    private var mLoading: ProgressBar? = null
    private var mControllers: View? = null
    private var mContainer: View? = null
    private var mCoverArt: NetworkImageView? = null
    private var mSeekbarTimer: Timer? = null
    private var mControllersTimer: Timer? = null
    private var mPlaybackState: PlaybackState? = null
    private val looper = Looper.getMainLooper()
    private val mAspectRatio = 72f / 128
    private var mSelectedMedia: MediaItem? = null
    private var mControllersVisible = false
    private var mDuration = 0
    private var mAuthorView: TextView? = null
    private var mPlayCircle: ImageButton? = null
    private var mLocation: PlaybackLocation? = null

    /**
     * Indicates whether we are doing a local or remote playback
     */
    enum class PlaybackLocation {
        LOCAL, REMOTE
    }

    /**
     * List of various state that we can be in
     */

    enum class PlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_player)

        loadViews();

        setupControlsCallbacks()

        val bundle = intent.extras
        if(bundle!=null){
            mSelectedMedia = MediaItem.fromBundle(intent.getBundleExtra("media"))
            val shouldStartPlayback = bundle.getBoolean("shouldStart")
            val startPosition = bundle.getInt("startPosition",0)
            mVideoView!!.setVideoURI(Uri.parse(mSelectedMedia!!.url))

            if(shouldStartPlayback){
                mPlaybackState = PlaybackState.PLAYING
                updatePlayButton(mPlaybackState)
                updatePlaybackLocation(PlaybackLocation.LOCAL)

                if(startPosition>0){
                    mVideoView!!.seekTo(startPosition)
                }

                mVideoView!!.start()
                startControllersTimer()
            }else{
                updatePlaybackLocation(PlaybackLocation.LOCAL)
                mPlaybackState = PlaybackState.IDLE
                updatePlayButton(mPlaybackState)
            }
        }

        if(mTitleView !=null) {
            updateMetaData(true)
        }
    }

    private fun updateMetaData(b: Boolean) {
        val displaySize: Point
        if(!b){
            mDescriptionView!!.visibility = View.GONE
            mTitleView!!.visibility = View.GONE
            mAuthorView!!.visibility = View.GONE
            displaySize = Utils.getDisplaySize(this)
            val lp = RelativeLayout.LayoutParams(
                displaySize.x,
                displaySize.y + supportActionBar!!.height
            )
            lp.addRule(RelativeLayout.CENTER_IN_PARENT)
            mVideoView!!.layoutParams = lp
            mVideoView!!.invalidate()
        } else{
            mDescriptionView!!.text = mSelectedMedia!!.subTitle
            mTitleView!!.text = mSelectedMedia!!.title
            mAuthorView!!.text = mSelectedMedia!!.studio
            mDescriptionView!!.visibility = View.VISIBLE
            mTitleView!!.visibility = View.VISIBLE
            mAuthorView!!.visibility = View.VISIBLE
            displaySize = Utils.getDisplaySize(this)
            val lp =
                RelativeLayout.LayoutParams(displaySize.x, (displaySize.x * mAspectRatio).toInt())
            lp.addRule(RelativeLayout.BELOW, R.id.toolbar)
            mVideoView!!.layoutParams = lp
            mVideoView!!.invalidate()
        }
    }


    private fun updatePlaybackLocation(location: PlaybackLocation) {
        mLocation = location;
        if (location == PlaybackLocation.LOCAL) {
            if (mPlaybackState == PlaybackState.PLAYING ||
                mPlaybackState == PlaybackState.BUFFERING
            ) {
                setCoverArtStatus(null)
                startControllersTimer()
            } else {
                stopControllersTimers()
                setCoverArtStatus(mSelectedMedia!!.getImage(0))
            }
        } else {
            stopControllersTimers()
            setCoverArtStatus(mSelectedMedia!!.getImage(0))
            updateControllersVisibility(false)
        }

    }

    private fun play(position: Int){
        startControllersTimer()
        when(mLocation){
            PlaybackLocation.LOCAL ->{
                mVideoView!!.seekTo(position)
                mVideoView!!.start()
            }
            PlaybackLocation.REMOTE ->{
                mPlaybackState = PlaybackState.BUFFERING
                updatePlayButton(mPlaybackState)
            }
            else ->{}
        }
        restartTrickPlayTImer()
    }

    private fun togglePlayback() {
        stopControllersTimers()
        when (mPlaybackState) {
            PlaybackState.PAUSED -> when (mLocation) {
                PlaybackLocation.LOCAL -> {
                    mVideoView!!.start()
                    mPlaybackState = PlaybackState.PLAYING
                    startControllersTimer()
                    restartTrickPlayTImer()
                    updatePlaybackLocation(PlaybackLocation.LOCAL)
                }
                PlaybackLocation.REMOTE -> {

                }
                else -> {}
            }
            PlaybackState.PLAYING -> {
                mPlaybackState = PlaybackState.PAUSED
                mVideoView!!.pause()
            }
            PlaybackState.IDLE -> when (mLocation) {
                PlaybackLocation.LOCAL -> {
                    mVideoView!!.setVideoURI(Uri.parse(mSelectedMedia!!.url))
                    mVideoView!!.seekTo(0)
                    mVideoView!!.start()
                    mPlaybackState = PlaybackState.PLAYING
                    restartTrickPlayTImer()
                    updatePlaybackLocation(PlaybackLocation.LOCAL)
                }
                PlaybackLocation.REMOTE -> {

                }
                else -> {}
            }
            else -> {}
        }
        updatePlayButton(mPlaybackState)
    }

    private fun setCoverArtStatus(url: String?) {
        if (url != null) {
            val mImageLoader = CustomVolleyRequest.getInstance(this.applicationContext)
                ?.imageLoader
            mImageLoader?.get(url, ImageLoader.getImageListener(mCoverArt, 0, 0))
            mCoverArt!!.setImageUrl(url, mImageLoader)
            mCoverArt!!.visibility = View.VISIBLE
            mVideoView!!.visibility = View.INVISIBLE
        } else {
            mCoverArt!!.visibility = View.GONE
            mVideoView!!.visibility = View.VISIBLE
        }
    }

    private fun stopTrickPlayTimer() {
        if (mSeekbarTimer != null) {
            mSeekbarTimer!!.cancel()
        }
    }

    private fun restartTrickPlayTImer() {
        stopTrickPlayTimer()
        mSeekbarTimer = Timer()
        mSeekbarTimer!!.scheduleAtFixedRate(UpdateSeekbarTask(), 100, 1000)
    }

    private fun startControllersTimer() {
        if (mControllersTimer != null) {
            mControllersTimer!!.cancel()
        }

        if (mLocation == PlaybackLocation.REMOTE) {
            return
        }

        mControllersTimer = Timer()
        mControllersTimer!!.schedule(HideControllersTask(), 5000)
    }

    private fun stopControllersTimers() {
        if (mControllersTimer != null) {
            mControllersTimer!!.cancel()
        }
    }

    private fun updateControllersVisibility(b: Boolean) {
        if (b) {
            supportActionBar!!.show()
            mControllers!!.visibility = View.VISIBLE
        } else {
            if (!Utils.isOrientationPortrait(this)) {
                supportActionBar!!.hide()
            }
            mControllers!!.visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
        if (mLocation == PlaybackLocation.LOCAL) {
            if (mSeekbarTimer != null) {
                mSeekbarTimer!!.cancel()
                mSeekbarTimer = null
            }
            if (mControllersTimer != null) {
                mControllersTimer!!.cancel()
            }
            // since we are playing locally, we need to stop the playback of
            // video (if user is not watching, pause it!)
            mVideoView!!.pause()
            mPlaybackState = PlaybackState.PAUSED
            updatePlayButton(PlaybackState.PAUSED)
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        stopControllersTimers()
        stopTrickPlayTimer()
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        updatePlaybackLocation(PlaybackLocation.LOCAL)
        super.onResume()
    }

    private inner class HideControllersTask : TimerTask() {
        override fun run() {
            looper.thread.join().apply {
                updateControllersVisibility(false)
                mControllersVisible = false
            }
        }


    }

    private inner class UpdateSeekbarTask : TimerTask() {
        override fun run() {
            looper.thread.join().apply {
                if (mLocation == PlaybackLocation.LOCAL) {
                    val currentPos = mVideoView!!.currentPosition
                    updateSeekbar(currentPos, mDuration)
                }
            }
        }

    }

    private fun setupControlsCallbacks() {
        mVideoView!!.setOnErrorListener { _, what, extra ->
            val msg: String = if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
                getString(R.string.video_error_media_load_timeout)
            } else if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                getString(R.string.video_error_server_unaccessible)
            } else {
                getString(R.string.video_error_unknown_error)
            }
            Utils.showErrorDialog(this@LocalPlayer, msg)
            mVideoView!!.stopPlayback()
            mPlaybackState = PlaybackState.IDLE
            updatePlayButton(mPlaybackState)
            true
        }

        mVideoView!!.setOnPreparedListener { mp ->
            mDuration = mp.duration
            mEndText!!.text = Utils.formatMillis(mDuration)
            mSeekbar!!.max = mDuration
            restartTrickPlayTImer()
        }

        mVideoView!!.setOnCompletionListener {
            OnCompletionListener {
                stopTrickPlayTimer()
                mPlaybackState = PlaybackState.IDLE
                updatePlayButton(mPlaybackState)
            }
        }

        mVideoView!!.setOnTouchListener { _, _ ->
            if (!mControllersVisible) {
                updateControllersVisibility(true)
            }
            startControllersTimer()
            false
        }

        mSeekbar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {
                stopTrickPlayTimer()
                mVideoView!!.pause()
                stopControllersTimers()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (mPlaybackState == PlaybackState.PLAYING) {
                    play(seekBar!!.progress)
                } else if (mPlaybackState != PlaybackState.IDLE) {
                    mVideoView!!.seekTo(seekBar!!.progress)
                }
                startControllersTimer()
            }

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                mStartText!!.text = Utils.formatMillis(p1)
            }

        })

        mPlayPause!!.setOnClickListener {
            if (mLocation == PlaybackLocation.LOCAL) {
                togglePlayback()
            }
        }
    }

    private fun updateSeekbar(position: Int, duration: Int) {
        mSeekbar!!.progress = position
        mSeekbar!!.max = duration
        mStartText!!.text = Utils.formatMillis(position)
        mEndText!!.text = Utils.formatMillis(duration)
    }

    private fun updatePlayButton(state: PlaybackState?) {
        val isConnected = false
        mControllers!!.visibility = if (isConnected) View.GONE else View.VISIBLE
        mPlayCircle!!.visibility = if (isConnected) View.GONE else View.VISIBLE

        when (state) {
            PlaybackState.PLAYING -> {
                mLoading!!.visibility = View.INVISIBLE
                mPlayPause!!.visibility = View.VISIBLE
                mPlayPause!!.setImageDrawable(
                    resources.getDrawable(R.drawable.ic_av_pause_dark)
                )
                mPlayCircle!!.visibility = if (isConnected) View.VISIBLE else View.GONE
            }
            PlaybackState.IDLE -> {
                mPlayCircle!!.visibility = View.VISIBLE
                mControllers!!.visibility = View.GONE
                mCoverArt!!.visibility = View.VISIBLE
                mVideoView!!.visibility = View.INVISIBLE
            }
            PlaybackState.PAUSED -> {
                mLoading!!.visibility = View.INVISIBLE
                mPlayPause!!.visibility = View.VISIBLE
                mPlayPause!!.setImageDrawable(
                    resources.getDrawable(R.drawable.ic_av_play_dark)
                )
                mPlayCircle!!.visibility = if (isConnected) View.VISIBLE else View.GONE
            }
            PlaybackState.BUFFERING -> {
                mPlayPause!!.visibility = View.INVISIBLE
                mLoading!!.visibility = View.VISIBLE
            }
            else -> {}
        }

    }



    private fun loadViews() {
        mVideoView = findViewById<View>(R.id.videoView1) as VideoView
        mTitleView = findViewById<View>(R.id.textView1) as TextView
        mDescriptionView = findViewById<View>(R.id.textView2) as TextView
        mDescriptionView!!.movementMethod = ScrollingMovementMethod()
        mAuthorView = findViewById<View>(R.id.textView3) as TextView
        mStartText = findViewById<View>(R.id.startText) as TextView
        mStartText!!.text = Utils.formatMillis(0)
        mEndText = findViewById<View>(R.id.endText) as TextView
        mSeekbar = findViewById<View>(R.id.seekBar1) as SeekBar
        mPlayPause = findViewById<View>(R.id.imageView2) as ImageView
        mLoading = findViewById<View>(R.id.progressBar1) as ProgressBar
        mControllers = findViewById(R.id.controllers)
        mContainer = findViewById(R.id.container)
        mCoverArt = findViewById<View>(R.id.coverArtView) as NetworkImageView
        ViewCompat.setTransitionName(mCoverArt!!, getString(R.string.transition_image))
        mPlayCircle = findViewById<View>(R.id.play_circle) as ImageButton
        mPlayCircle!!.setOnClickListener { togglePlayback() }
    }

    companion object {
        private const val TAG = "LocalPlayerActivity"
    }
}



