package ustaz.muhammed.amin.ustaz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import ustaz.muhammed.amin.R;
import ustaz.muhammed.amin.admob.AdMob;
import ustaz.muhammed.amin.courses.CourseActivity;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class UstazAdapter extends RecyclerView.Adapter<UstazAdapter.Holder> {

    private Context context;
    private List<UstazObject> ustazObjects;

    public UstazAdapter(Context context, List<UstazObject> ustazObjects) {
        this.context = context;
        this.ustazObjects = ustazObjects;

    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_course_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final UstazObject ustazObject = ustazObjects.get(position);
        Picasso.get().load(ustazObject.getImg()).into(holder.img);
        holder.name.setText(ustazObject.getName());
    }

    @Override
    public int getItemCount() {
        return ustazObjects.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;
        TextView name;

        private Holder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.course_img);
            name = itemView.findViewById(R.id.course_name);
            img.setOnClickListener(this);
            name.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (Boolean.TRUE.equals(AdMob.getInstance(context).adSuccessfulLoadedState.getValue())) {
                    AdMob.getInstance(context).showRewardedVideo(context, () -> context.startActivity(new Intent(context, CourseActivity.class)
                            .putExtra("ustaz", ustazObjects.get(getAdapterPosition()).getName())));
                } else {
                    context.startActivity(new Intent(context, CourseActivity.class)
                            .putExtra("ustaz", ustazObjects.get(getAdapterPosition()).getName()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                context.startActivity(new Intent(context, CourseActivity.class)
                        .putExtra("ustaz", ustazObjects.get(getAdapterPosition()).getName()));
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
