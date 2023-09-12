package ustaz.muhammed.amin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.MobileAds;

import java.util.Objects;

import ustaz.muhammed.amin.admob.AdMob;
import ustaz.muhammed.amin.courses.CourseActivity;
import ustaz.muhammed.amin.ustaz.UstazActivity;

public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.my_statusbar_color));
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);
        MobileAds.initialize(this);
        AdMob.getInstance(this);
        new Handler().postDelayed(() -> {
            startActivity(new Intent(Splash.this, UstazActivity.class));
            finish();
        }, 3000);
    }
}