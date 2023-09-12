package ustaz.muhammed.amin.ustaz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ustaz.muhammed.amin.R;
import ustaz.muhammed.amin.admob.AdMob;

public class UstazActivity extends AppCompatActivity {
    private List<UstazObject> ustazObjects = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.app_name));
        setContentView(R.layout.activity_ustaz);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progress_bar);
        databaseReference = FirebaseDatabase.getInstance().getReference("Durus").child("ustazs");
        addUstaz();
    }

    private void addUstaz() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ustazObjects.clear();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue().toString();
                    String img_url = snapshot.child("img").getValue().toString();
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
}