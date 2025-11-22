package ethio.islamic.durus.player;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.Objects;

import ethio.islamic.durus.R;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class VideoPlayManager extends AppCompatActivity {
    String videoId;
    private YouTubePlayerView youTubePlayerView;
    private FullScreenHelper fullScreenHelper = new FullScreenHelper(this);

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"; // DEV
    //private static final String AD_UNIT_ID = "ca-app-pub-9737688387656254/9630322947"; // PROD
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.my_statusbar_color));
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_video_play_manager);
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        videoId = getIntent().getExtras().getString("link");

        initYouTubePlayerView();
    }

    private void initYouTubePlayerView() {


        try {
            getLifecycle().addObserver(youTubePlayerView);
            addFullScreenListenerToPlayer();
            YouTubePlayerListener listener = (new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    YouTubePlayerUtils.loadOrCueVideo(
                            youTubePlayer,
                            getLifecycle(),
                            videoId,
                            0f
                    );
                }
            });

            IFramePlayerOptions options = new IFramePlayerOptions.Builder(this).controls(1).fullscreen(1).build();
            youTubePlayerView.initialize(listener, options);
        } catch (Exception e) {
            Toast.makeText(this, "go back and come back again", Toast.LENGTH_SHORT).show();
        }
    }

    private void addFullScreenListenerToPlayer() {
        youTubePlayerView.addFullscreenListener(new FullscreenListener() {
            @Override
            public void onEnterFullscreen(@NonNull View view, @NonNull Function0<Unit> function0) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                fullScreenHelper.enterFullScreen();
            }

            @Override
            public void onExitFullscreen() {

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fullScreenHelper.exitFullScreen();
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
