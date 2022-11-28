package com.ajayvamsee.castplayer.utils

import android.os.Bundle
import java.sql.Wrapper

/**
 * Created by Ajay Vamsee on 11/25/2022.
 * Time : 12:24 HRS
 */
class MediaItem {
    var title:String?= null
    var subTitle:String?= null
    var studio:String? =  null
    var url:String? =null
    var contentType:String? = null
    var duration = 0
    val images = ArrayList<String>()

    fun addImage(url: String) {
        images.add(url)
    }


    fun addImage(url:String,index:Int){
        if(index<images.size){
            images[index] = url
        }
    }

    fun getImage(index: Int):String?{
        return if(index < images.size){
            images[index]
        } else null
    }

    fun hasImage():Boolean{
        return !images.isEmpty()
    }

    fun toBundle():Bundle{
        val wrapper = Bundle()
        wrapper.putString(KEY_TITLE,title)
        wrapper.putString(KEY_SUBTITLE,subTitle)
        wrapper.putString(KEY_URL,url)
        wrapper.putString(KEY_STUDIO,studio)
        wrapper.putStringArrayList(KEY_IMAGES,images)
        wrapper.putString(KEY_CONTENT_TYPE,"video/mp4")

        return wrapper
    }


    companion object{
        const val KEY_TITLE = "title"
        const val KEY_SUBTITLE = "subtitle"
        const val KEY_STUDIO = "studio"
        const val KEY_URL = "movie-urls"
        const val KEY_IMAGES = "images"
        const val KEY_CONTENT_TYPE = "content-type"

        @JvmStatic
        fun fromBundle(wrapper: Bundle?): MediaItem?{
            if(null == wrapper){
                return null
            }

            val media = MediaItem()
            media.url = wrapper.getString(KEY_URL)
            media.title = wrapper.getString(KEY_TITLE)
            media.subTitle = wrapper.getString(KEY_SUBTITLE)
            media.studio = wrapper.getString(KEY_STUDIO)
            media.images.addAll(wrapper.getStringArrayList(KEY_IMAGES)!!)
            media.contentType = wrapper.getString(KEY_CONTENT_TYPE)

            return media
        }
    }
}