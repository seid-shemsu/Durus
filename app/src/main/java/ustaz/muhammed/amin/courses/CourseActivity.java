package ustaz.muhammed.amin.courses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
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


public class CourseActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private List<CourseObject> courseObjects = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Integer> codes = new ArrayList<>();
    private int i = 1;
    String ustaz = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.my_statusbar_color));
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_kitab);
        ustaz = getIntent().getExtras().getString("ustaz");
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progress_bar);
        databaseReference = FirebaseDatabase.getInstance().
                getReference("content")
                .child(ustaz)
                .child("titles")
        ;
        addCourses();

    }

    private void addCourses() {
        //String p;
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                i = 1;
                courseObjects.clear();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        String name = snapshot.getKey();
                        String img_url = snapshot.child("image").getValue().toString();
                        courseObjects.add(new CourseObject(name, img_url));

                    } catch (Exception e) {
                    }
                }
                CourseAdapter courseAdapter = new CourseAdapter(CourseActivity.this, courseObjects, ustaz);
                recyclerView.setAdapter(courseAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CourseActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}