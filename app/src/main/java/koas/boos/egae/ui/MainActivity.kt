package koas.boos.egae.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import koas.boos.egae.R
import koas.boos.egae.WebViewActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val factory = EgyptViewModelFactory(application)
        val viewModel = ViewModelProvider(this, factory)[EgyptViewModel::class.java]

        if (viewModel.checkFirstLaunch()) {
            Log.d("YYY", "First launch")
            viewModel.firstLink.observe(this) { link ->
                Log.d("YYY", "Link = $link")
                viewModel.saveLinkToPref(link)
                viewModel.sendOneSignalTag()

                with(getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE).edit()) {
                    putBoolean(FIRST_LAUNCH, false)
                    apply()
                }
                startWebView(link)
            }
        } else {
            Log.d("YYY","Not first launch")
            val url = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE).getString(LINK, "")
            Log.d("YYY", "Link = $url")
            startWebView(url!!)
        }
    }

    private fun startWebView(link: String) {
        with(Intent(this, WebViewActivity::class.java)) {
            putExtra("link", link)
            startActivity(this)
        }
        finish()
    }

    private fun checks(): Boolean {
        val places = arrayOf(
            "/sbin/", "/system/bin/", "/system/xbin/",
            "/data/local/xbin/", "/data/local/bin/",
            "/system/sd/xbin/", "/system/bin/failsafe/",
            "/data/local/"
        )
        try {
            for (where in places) {
                if (File(where + "su").exists()) return true
            }
        } catch (ignore: Throwable) {
        }
        return false
    }

    private fun tracks(): String {
        return Settings.Global.getString(this.contentResolver, Settings.Global.ADB_ENABLED)
            ?: "null"
    }

}