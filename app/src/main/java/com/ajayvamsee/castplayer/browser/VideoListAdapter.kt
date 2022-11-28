package com.ajayvamsee.castplayer.browser

import android.content.ClipDescription
import android.content.Context
import android.icu.text.CaseMap.Title
import android.media.browse.MediaBrowser
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ajayvamsee.castplayer.utils.CustomVolleyRequest
import com.ajayvamsee.castplayer.utils.MediaItem
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.google.sample.cast.refplayer.R

/**
 * Created by Ajay Vamsee on 11/24/2022.
 * Time : 19:14 HRS
 */
class VideoListAdapter(
    private val mClickListener: ItemClickListener,
    context: Context
) : RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {

    private val mAppContext : Context
    private var videos : List<MediaItem>? = null


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val context = viewGroup.context
        val parent = LayoutInflater.from(context).inflate(R.layout.browse_row,viewGroup,false)
        return ViewHolder.newInstance(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = videos!![position]
        holder.setTitle(item.title)
        holder.setDescription(item.studio)
        holder.setImage(item.getImage(0), context = mAppContext)
        holder.mImageView.setOnClickListener{
            mClickListener.itemClicked(it,item,position)
        }
        holder.mTextContainer.setOnClickListener{
            mClickListener.itemClicked(it,item, position)
        }
    }

    override fun getItemCount(): Int {
        return if(videos == null) 0 else videos!!.size
    }

    class ViewHolder private constructor(
        private val mParent: View,
        val mImageView: NetworkImageView,
        val mTextContainer: View,
        private val mTitleView: TextView,
        private val mDescription: TextView
    ) : RecyclerView.ViewHolder(mParent) {
        private var mImageLoader: ImageLoader ? = null

        fun setTitle(title: String?){
            mTitleView.text = title
        }

        fun setDescription(description: String?){
            mDescription.text = description
        }

        fun setImage(imgUrl: String?, context: Context){
            mImageLoader =CustomVolleyRequest.getInstance(context)?.imageLoader

            mImageLoader!![imgUrl,ImageLoader.getImageListener(mImageView,0,0)]

            mImageView.setImageUrl(imgUrl,mImageLoader)
        }

        fun setOnClickListener(listener: View.OnClickListener){
            mParent.setOnClickListener(listener)
        }

        val imageView:ImageView
        get() = mImageView

        companion object{
            fun newInstance(parent:View):ViewHolder{
                val imgView = parent.findViewById<View>(R.id.imageView1) as NetworkImageView
                val titleView = parent.findViewById<View>(R.id.textView1) as TextView
                val descriptionView = parent.findViewById<View>(R.id.textView2) as TextView
                val textContainer = parent.findViewById<View>(R.id.text_container)

                return ViewHolder(parent,imgView,textContainer, titleView,descriptionView)
            }
        }

    }

    fun setData(data:List<MediaItem>?){
        videos = data
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    interface ItemClickListener {
        fun itemClicked(v: View?, item: MediaItem?, position: Int)
    }

    companion object{
        private const val mAspectRatio = 9f/16f
    }

    init {
        mAppContext = context.applicationContext
    }
}