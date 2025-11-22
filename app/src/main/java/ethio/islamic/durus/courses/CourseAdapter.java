package ethio.islamic.durus.courses;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import ethio.islamic.durus.R;
import ethio.islamic.durus.admob.AdMob;
import ethio.islamic.durus.parts.PartActivity;
import ethio.islamic.durus.utils.Utils;

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
        View view = LayoutInflater.from(context).inflate(R.layout.single_ustaz_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        final CourseObject courseObject = courseObjects.get(position);
        //holder.course_name.setText(courseObject.getCourse_name());
        Glide.with(holder.course_img).load(courseObject.getImg_url()).into(holder.course_img);
        holder.title.setText(courseObject.getCourse_name());
    }

    @Override
    public int getItemCount() {
        return courseObjects.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView course_img;
        //TextView course_name;
        CardView relative;

        TextView title;
        private ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            course_img = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.title);
            relative = itemView.findViewById(R.id.relative);

            title.setOnClickListener(this);
            course_img.setOnClickListener(this);
            relative.setOnClickListener(this);
            //attendants.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (Boolean.TRUE.equals(AdMob.getInstance(context).adSuccessfulLoadedState.getValue()) &&
                        Utils.showAd(context)) {
                    AdMob.getInstance(context)
                            .showRewardedVideo(context, () -> context.startActivity(new Intent(context, PartActivity.class)
                                    .putExtra("ustaz", ustaz)
                                    .putExtra("course_name", courseObjects.get(getAdapterPosition()).getCourse_name())
                                    .putExtra("image", courseObjects.get(getAdapterPosition()).getImg_url())
                            ));
                } else {
                    context.startActivity(new Intent(context, PartActivity.class)
                            .putExtra("ustaz", ustaz)
                            .putExtra("course_name", courseObjects.get(getAdapterPosition()).getCourse_name())
                            .putExtra("image", courseObjects.get(getAdapterPosition()).getImg_url())
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
                context.startActivity(new Intent(context, PartActivity.class)
                        .putExtra("ustaz", ustaz)
                        .putExtra("course_name", courseObjects.get(getAdapterPosition()).getCourse_name())
                        .putExtra("image", courseObjects.get(getAdapterPosition()).getImg_url())
                );
            }

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
