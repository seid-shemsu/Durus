package et.islam.durus.admob

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


const val AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917" // DEV
//const val AD_UNIT_ID = "ca-app-pub-9027325451795162/3105460972" // PROD

class AdMobViewModel(context: Context) : ViewModel() {

    var isLoading = false
    var rewardedAd: RewardedAd? = null

    private val _adSuccessfulLoadedState = MutableLiveData(false)
    val adSuccessfulLoadedState: LiveData<Boolean> = _adSuccessfulLoadedState

    init {
        loadRewardedAd(context)
    }

    fun loadRewardedAd(context: Context) {
        if (rewardedAd == null) {
            isLoading = true
            val adRequest = AdRequest.Builder().build()

            RewardedAd.load(
                context,
                AD_UNIT_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        isLoading = false
                        rewardedAd = null
                        _adSuccessfulLoadedState.value = false
                        Log.e("Error", adError.message)
                        Log.e("Error", adError.domain)
                        Log.e("Error", "${adError.responseInfo}")
                    }

                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        isLoading = false
                        _adSuccessfulLoadedState.value = true
                        Log.e("Success", "--------------------------")
                    }
                }
            )
        }
    }

    fun showRewardedVideo(context: Context, onAdDismissed: () -> Unit) {
        val activity = context as Activity

        if (rewardedAd != null) {
            rewardedAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Ad was dismissed.
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null
                        _adSuccessfulLoadedState.value = false
                        loadRewardedAd(context)
                        onAdDismissed()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        // Ad failed to show.
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null
                        _adSuccessfulLoadedState.value = false
                        loadRewardedAd(context)
                        onAdDismissed()
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Ad showed fullscreen content.
                        // Called when ad is dismissed.
                    }
                }

            rewardedAd?.show(
                activity
            ) {
            }
        }
    }
}