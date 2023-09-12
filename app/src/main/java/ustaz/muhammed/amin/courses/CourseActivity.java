package ustaz.muhammed.amin.courses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
        setContentView(R.layout.activity_kitab);
        //ustaz = getIntent().getExtras().getString("ustaz");
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        progressBar = findViewById(R.id.progress_bar);
        /*databaseReference = FirebaseDatabase.getInstance().
                getReference("Durus").child("derses")
                .child(ustaz);*/
        databaseReference = FirebaseDatabase.getInstance().
                getReference("titles");
                /*.child("derses")
                .child(ustaz);*/
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