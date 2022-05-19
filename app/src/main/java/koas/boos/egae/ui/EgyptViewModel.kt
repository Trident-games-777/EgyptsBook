package koas.boos.egae.ui

import android.app.Application
import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.onesignal.OneSignal
import koas.boos.egae.utils.RemoteConfigUtils
import java.util.*

const val SHARED_PREF = "shared_pref"
const val LINK = "link"
const val FIRST_LAUNCH = "first_launch"

class EgyptViewModel(
    app: Application
) : AndroidViewModel(app) {
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    private var deepLinkFetched = false
    private var appsFlyerDataFetched = false

    private var deepLink: String = ""
    private var appsFlyerData: MutableMap<String, Any>? = null

    val firstLink: MutableLiveData<String> = MutableLiveData()

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.apply {
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(RemoteConfigUtils.DEFAULTS)
            fetchAndActivate().addOnCompleteListener {
                if (it.isSuccessful) {
                    initOneSignal()
                }
            }
        }
        fetchAppsFlyerData()
        fetchAppLinkData()
    }

    private fun initOneSignal() {
        OneSignal.initWithContext(getApplication())
        OneSignal.setAppId(remoteConfig.getString(RemoteConfigUtils.ONE_SIGNAL_APP_ID))
    }

    private fun fetchAppsFlyerData() {
        AppsFlyerLib.getInstance().init(
            remoteConfig.getString(RemoteConfigUtils.APPS_FLYER_DEV_KEY),
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    appsFlyerDataFetched = true
                    appsFlyerData = p0
                    if (deepLinkFetched) firstLink.postValue(parseLink(deepLink, appsFlyerData))
                }

                override fun onConversionDataFail(p0: String?) {}
                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {}
                override fun onAttributionFailure(p0: String?) {}
            },
            getApplication()
        )
        AppsFlyerLib.getInstance().start(getApplication())
    }

    private fun fetchAppLinkData() {
        AppLinkData.fetchDeferredAppLinkData(getApplication()) {
            deepLinkFetched = true
            deepLink = it?.targetUri.toString()
            if (appsFlyerDataFetched) firstLink.postValue(parseLink(deepLink, appsFlyerData))
        }
    }

    private fun parseLink(deepLink: String, data: MutableMap<String, Any>?): String {
        val gadid = AdvertisingIdClient.getAdvertisingIdInfo(getApplication()).id.toString()

        val link = remoteConfig.getString(RemoteConfigUtils.BASE_LINK).toUri().buildUpon().apply {
            appendQueryParameter("OBv97TC3BY", "zpa5pyQXqM")
            appendQueryParameter("LJfzdO7tF4", TimeZone.getDefault().id)
            appendQueryParameter("bA6XwEk530", gadid)
            appendQueryParameter("VGyfC0Wl8N", deepLink)
            appendQueryParameter("fZeTy4Rsxe", data?.get("media_source").toString())
            appendQueryParameter(
                "aNrCB7pE18",
                AppsFlyerLib.getInstance().getAppsFlyerUID(getApplication())
            )
            appendQueryParameter("HMCGu9qL9P", data?.get("adset_id").toString())
            appendQueryParameter("PWXH3nddSH", data?.get("campaign_id").toString())
            appendQueryParameter("Pr764juXmy", data?.get("campaign").toString())
            appendQueryParameter("HtReitxCS6", data?.get("adset").toString())
            appendQueryParameter("Fho0Gk3LQ8", data?.get("adgroup").toString())
            appendQueryParameter("84ntskaBfH", data?.get("orig_cost").toString())
            appendQueryParameter("txxQk7LuZI", data?.get("af_siteid").toString())
        }.toString()
        return link
    }

    fun sendOneSignalTag() {
        val campaign = appsFlyerData?.get("campaign").toString()
        val key = "key2"

        if (campaign == "null" && deepLink == "null") {
            OneSignal.sendTag(key, "organic")
        } else if (deepLink != "null") {
            OneSignal.sendTag(key, deepLink.replace("myapp://", "").substringBefore("/"))
        } else if (campaign != "null") {
            OneSignal.sendTag(key, campaign.substringBefore("_"))
        }
    }

    fun saveLinkToPref(link: String) {
        val sharedPref = getApplication<Application>()
            .getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(LINK, link)
            apply()
        }
    }

    fun checkFirstLaunch(): Boolean {
        val sharePref = getApplication<Application>()
            .getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        return if (sharePref.getBoolean(FIRST_LAUNCH, true)) {
            with(sharePref.edit()) {
                putBoolean(FIRST_LAUNCH, false)
                apply()
            }
            true
        } else {
            false
        }
    }
}