package koas.boos.egae.utils

object RemoteConfigUtils {
    const val ONE_SIGNAL_APP_ID = "oneSignalAppId"
    const val APPS_FLYER_DEV_KEY = "appsFlyerDevKey"
    const val BASE_LINK = "baseLink"
    val DEFAULTS: HashMap<String, Any> =
        hashMapOf(
            ONE_SIGNAL_APP_ID to "06f67cfb-cc4a-48d0-9951-dfc9ac132271",
            APPS_FLYER_DEV_KEY to "ZoJqA4vad4c8rC3pShwMUc",
            BASE_LINK to "eresaww.monster/egyaso.php"
        )
}