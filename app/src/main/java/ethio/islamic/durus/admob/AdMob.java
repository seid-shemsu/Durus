package ethio.islamic.durus.admob;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import ethio.islamic.durus.BuildConfig;

public class AdMob {
    private static final String DEV_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"; // DEV
    private static final String PROD_AD_UNIT_ID = "ca-app-pub-9027325451795162/5635643085"; // PROD

    private boolean isLoading = false;
    private RewardedAd rewardedAd = null;
    private static Context context;

    private final MutableLiveData<Boolean> _adSuccessfulLoadedState = new MutableLiveData<>(false);
    public LiveData<Boolean> adSuccessfulLoadedState = _adSuccessfulLoadedState;

    // Private constructor to prevent instantiation from other classes
    private AdMob(Context context) {
        loadRewardedAd(context);
    }

    // Singleton instance holder
    private static class Holder {
        private static final AdMob INSTANCE = new AdMob(context);
    }

    // Public method to get the singleton instance
    public static AdMob getInstance(Context con) {
        context = con;
        return Holder.INSTANCE;
    }


    private InterstitialAd interstitialAd;
    public void loadRewardedAd(Context context) {
        /*if (rewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            String ad;
            if (BuildConfig.DEBUG) {
                ad = DEV_AD_UNIT_ID;
            } else {
                ad = PROD_AD_UNIT_ID;
            }
            RewardedAd.load(
                    context,
                    ad,
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(LoadAdError adError) {
                            isLoading = false;
                            rewardedAd = null;
                            _adSuccessfulLoadedState.setValue(false);
                            Log.e("Error", adError.getMessage());
                            Log.e("Error", adError.getDomain());
                            Log.e("Error", String.valueOf(adError.getResponseInfo()));
                        }

                        @Override
                        public void onAdLoaded(RewardedAd ad) {
                            rewardedAd = ad;
                            isLoading = false;
                            _adSuccessfulLoadedState.setValue(true);
                            Log.e("Success", "--------------------------");
                        }
                    }
            );
        }*/
        if (interstitialAd == null && !isLoading) {
            isLoading = true;
            String ad;
            if (BuildConfig.DEBUG) {
                ad = DEV_AD_UNIT_ID;
            } else {
                ad = PROD_AD_UNIT_ID;
            }

            InterstitialAd.load(
                    context,
                    ad,
                    new AdRequest.Builder().build(),
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd initAd) {
                            Log.d(TAG, "Ad was loaded.");
                            interstitialAd = initAd;
                            isLoading = false;
                            _adSuccessfulLoadedState.setValue(true);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            Log.d(TAG, loadAdError.getMessage());
                            isLoading = false;
                            interstitialAd = null;
                            _adSuccessfulLoadedState.setValue(false);
                        }
                    });
        }

    }

    private String TAG = "ADV";
    public void showRewardedVideo(Context context, Runnable onAdDismissed) {
        Activity activity = (Activity) context;

        /*if (rewardedAd != null) {
            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    rewardedAd = null;
                    _adSuccessfulLoadedState.setValue(false);
                    loadRewardedAd(context);
                    onAdDismissed.run();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    rewardedAd = null;
                    _adSuccessfulLoadedState.setValue(false);
                    loadRewardedAd(context);
                    onAdDismissed.run();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Ad showed fullscreen content.
                }
            });

            rewardedAd.show(activity, rewardItem -> {});

        }*/

        if (interstitialAd != null) {

            interstitialAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            Log.d(TAG, "The ad was dismissed.");
                            interstitialAd = null;
                            loadRewardedAd(context);
                            onAdDismissed.run();
                            _adSuccessfulLoadedState.setValue(false);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            Log.d(TAG, "The ad failed to show.");
                            interstitialAd = null;
                            onAdDismissed.run();
                            _adSuccessfulLoadedState.setValue(false);
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            Log.d(TAG, "The ad was shown.");
                        }

                        @Override
                        public void onAdImpression() {
                            Log.d(TAG, "The ad recorded an impression.");
                        }

                        @Override
                        public void onAdClicked() {
                            Log.d(TAG, "The ad was clicked.");
                        }
                    });
            interstitialAd.show(activity);
        }
    }
}
