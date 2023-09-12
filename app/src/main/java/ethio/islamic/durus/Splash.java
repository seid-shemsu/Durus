package ethio.islamic.durus;

import static ethio.islamic.durus.utils.Constants.CURRENT_VERSION;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import ethio.islamic.durus.admob.AdMob;
import ethio.islamic.durus.ustaz.UstazActivity;
import ethio.islamic.durus.utils.Utils;

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

        new Handler().postDelayed(this::checkVersion, 2000);
        FirebaseDatabase.getInstance().getReference("configs")
                .child("show_ads")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.e("Adview", "--------------------------------------");
                        Log.e("Adview", Objects.requireNonNull(snapshot.getValue()).toString());
                        if (Objects.requireNonNull(snapshot.getValue()).toString().equalsIgnoreCase("true")) {
                            getSharedPreferences("ad", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("show", true)
                                    .apply();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkVersion() {
        FirebaseDatabase.getInstance().getReference("configs")
                .child("latest_version")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.e("Latest version", Objects.requireNonNull(snapshot.getValue()) + "");
                        if (Utils.compareVersions(Objects.requireNonNull(snapshot.getValue()).toString(), CURRENT_VERSION)) {
                            startActivity(new Intent(Splash.this, UpdateActivity.class));
                        } else {
                            startActivity(new Intent(Splash.this, UstazActivity.class));
                        }
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        startActivity(new Intent(Splash.this, UstazActivity.class));
                        finish();
                    }
                });

    }
}