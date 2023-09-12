package et.islam.durus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;

import et.islam.durus.admob.AdMob;
import et.islam.durus.courses.CourseActivity;
import et.islam.durus.ustaz.UstazActivity;

public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        MobileAds.initialize(this);
        AdMob.getInstance(this);
        new Handler().postDelayed(() -> {
            startActivity(new Intent(Splash.this, CourseActivity.class));
            finish();
        }, 3000);
    }
}