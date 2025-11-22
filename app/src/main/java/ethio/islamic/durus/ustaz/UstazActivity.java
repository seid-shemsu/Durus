package ethio.islamic.durus.ustaz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ethio.islamic.durus.R;
import ethio.islamic.durus.Settings;
import ethio.islamic.durus.utils.Utils;

public class UstazActivity extends AppCompatActivity {
    private List<UstazObject> ustazObjects = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    DatabaseReference databaseReference;


    private TextView removeAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.my_statusbar_color));
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_ustaz);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);
        progressBar = findViewById(R.id.progress_bar);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ustaz");
        addUstaz();

        removeAds = findViewById(R.id.remove_ads);
        removeAds.setOnClickListener(v -> startActivity(new Intent(UstazActivity.this, Settings.class)));
    }

    private void addUstaz() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ustazObjects.clear();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue().toString();
                    String img_url = snapshot.child("image").getValue().toString();
                    ustazObjects.add(new UstazObject(name, img_url));
                }
                UstazAdapter ustazAdapter = new UstazAdapter(UstazActivity.this, ustazObjects);
                recyclerView.setAdapter(ustazAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UstazActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.showAd(this)) {
            removeAds.setVisibility(View.VISIBLE);
        } else {
            removeAds.setVisibility(View.GONE);
        }
    }
}