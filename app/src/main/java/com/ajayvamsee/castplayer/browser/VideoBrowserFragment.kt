package com.ajayvamsee.castplayer.browser

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajayvamsee.castplayer.mediaplayer.LocalPlayerActivity
import com.ajayvamsee.castplayer.utils.MediaItem
import com.google.sample.cast.refplayer.R

class VideoBrowserFragment : Fragment(), VideoListAdapter.ItemClickListener,
    LoaderManager.LoaderCallbacks<List<MediaItem>?> {

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: VideoListAdapter? = null
    private var mEmptyView: View? = null
    private var mLoadingView: View? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_browser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRecyclerView = view.findViewById(R.id.list) as RecyclerView
        mEmptyView = view.findViewById(R.id.empty_view)
        mLoadingView = view.findViewById(R.id.progress_indicator)

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        mRecyclerView!!.layoutManager = layoutManager
        mAdapter = VideoListAdapter(this, this.requireContext())
        mRecyclerView!!.adapter = mAdapter

        LoaderManager.getInstance(this).initLoader(0, null, this)

    }

    override fun itemClicked(v: View?, item: MediaItem?, position: Int) {
        val transitionName = getString(R.string.transition_image)

        val viewHolder =
            mRecyclerView!!.findViewHolderForLayoutPosition(position) as VideoListAdapter.ViewHolder?

        val imagePair =
            androidx.core.util.Pair.create(viewHolder!!.imageView as View, transitionName)
        val options =
            ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), imagePair)
        val intent = Intent(activity, LocalPlayerActivity::class.java)
        intent.putExtra("media", item!!.toBundle())
        intent.putExtra("shouldStart", false)

        ActivityCompat.startActivity(requireActivity(), intent, options.toBundle())
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<MediaItem>?> {
        return VideoItemLoader(activity, CATALOG_URL)
    }

    override fun onLoadFinished(loader: Loader<List<MediaItem>?>, data: List<MediaItem>?) {
        mAdapter!!.setData(data)
        mLoadingView!!.visibility = View.GONE
        mEmptyView!!.visibility = if (null == data || data.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onLoaderReset(loader: Loader<List<MediaItem>?>) {
        mAdapter!!.setData(null)
    }

    companion object {
        private const val TAG = "VideoBrowserFragment"
        private const val CATALOG_URL =
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/f.json"
    }


}