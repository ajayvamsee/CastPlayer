package com.ajayvamsee.castplayer

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.ajayvamsee.castplayer.browser.VideoBrowserFragment
import com.ajayvamsee.castplayer.settings.CastPreference
import com.google.sample.cast.refplayer.R

class VideoBrowserActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_browser)

        setUpActionBar()
    }

    private fun setUpActionBar() {
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar?.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.browse,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i:Intent
        when(item.itemId){
            R.id.action_settings ->{
                i = Intent(this@VideoBrowserActivity,CastPreference::class.java)
                startActivity(i)
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object{
        private const val TAG = "VideoBrowserActivity"
    }
}