package ustaz.muhammed.amin.parts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ustaz.muhammed.amin.R;

public class PartActivity extends AppCompatActivity {
    private ProgressBar progress;
    private RecyclerView title;
    private List<String> titles = new ArrayList<>();
    private List<String> numbers = new ArrayList<>();
    private List<String> icons = new ArrayList<>();
    private List<PartObject> parts = new ArrayList<>();
    private String ustaz = "_", course_name = "_", image = "_";
    //LinearLayout lb;
    TitleAdapter titleAdapter;
    LinearLayout l;
    //private String[] numbersArray, titlesArray;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.my_statusbar_color));
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_part);
        title = findViewById(R.id.title_recycler);
        progress = findViewById(R.id.progress_bar);
        l = findViewById(R.id.l);
        title.setHasFixedSize(true);
        title.setLayoutManager(new LinearLayoutManager(this));
        course_name = getIntent().getExtras().getString("course_name");
        ustaz = getIntent().getExtras().getString("ustaz");
        image = getIntent().getExtras().getString("image");
        setTitle(course_name);
        getItems();
    }


    private void getItems() {
        //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Durus").child("content").child(ustaz).child(course_name);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("content")
                .child(ustaz)
                .child("contents")
                .child(course_name);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    parts.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.hasChildren()) {
                            parts.add(snapshot.getValue(PartObject.class));
                        }
                    }
                    titleAdapter = new TitleAdapter(PartActivity.this, parts, course_name, image);
                    title.setAdapter(titleAdapter);
                    //l.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                } else {
                    progress.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}