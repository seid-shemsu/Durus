package et.islam.durus.courses;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import et.islam.durus.R;
import et.islam.durus.admob.AdMob;
import et.islam.durus.parts.PartActivity;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ImageViewHolder> {
    private Context context;
    private List<CourseObject> courseObjects;
    private String ustaz;

    public CourseAdapter(Context context, List<CourseObject> courseObjects, String ustaz) {
        this.context = context;
        this.courseObjects = courseObjects;
        this.ustaz = ustaz;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_course_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        final CourseObject courseObject = courseObjects.get(position);
        holder.course_name.setText(courseObject.getCourse_name());
        File img = context.getApplicationContext().getFileStreamPath("course" + courseObject.getCourse_name());
        if (img.exists()) {
            holder.course_img.setImageBitmap(loadImage(context, "course" + courseObject.getCourse_name()));
        } else {
            Picasso.get().load(courseObject.getImg_url()).into(holder.course_img);
            try {
                new Thread(() -> {
                    try {
                        Looper.prepare();
                        saveImage(context, Picasso.get().load(courseObject.getImg_url()).get(), "course" + courseObject.getCourse_name());
                    } catch (IOException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).start();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public int getItemCount() {
        return courseObjects.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView course_img;
        TextView course_name;
        RelativeLayout relative;

        private ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            course_img = itemView.findViewById(R.id.course_img);
            course_name = itemView.findViewById(R.id.course_name);
            relative = itemView.findViewById(R.id.relative);

            course_name.setOnClickListener(this);
            course_img.setOnClickListener(this);
            relative.setOnClickListener(this);
            //attendants.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            AdMob.getInstance(context)
                    .showRewardedVideo(context, () -> context.startActivity(new Intent(context, PartActivity.class)
                            .putExtra("ustaz", ustaz)
                            .putExtra("course_name", courseObjects.get(getAdapterPosition()).getCourse_name())
                    ));

        }
    }

    private Bitmap loadImage(Context context, String imageName) {
        Bitmap bitmap = null;
        FileInputStream fiStream;
        try {
            fiStream = context.openFileInput(imageName);
            bitmap = BitmapFactory.decodeStream(fiStream);
            fiStream.close();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return bitmap;
    }

    private void saveImage(Context context, Bitmap b, String imageName) {
        FileOutputStream foStream;
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.JPEG, 80, foStream);
            foStream.close();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}