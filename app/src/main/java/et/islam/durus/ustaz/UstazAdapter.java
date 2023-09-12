package et.islam.durus.ustaz;

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
import et.islam.durus.courses.CourseActivity;
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
        View view = LayoutInflater.from(context).inflate(R.layout.single_ustaz_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final UstazObject ustazObject = ustazObjects.get(position);
        File img = context.getApplicationContext().getFileStreamPath(ustazObject.getName());
        if (img.exists()) {
            holder.img.setImageBitmap(loadImage(context, ustazObject.getName()));
        } else {
            Picasso.get().load(ustazObject.getImg()).into(holder.img);
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Looper.prepare();
                            saveImage(context, Picasso.get().load(ustazObject.getImg()).get(), ustazObject.getName());
                        } catch (IOException e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public int getItemCount() {
        return ustazObjects.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;

        private Holder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            img.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                if (AdMob.getInstance(context).adSuccessfulLoadedState.getValue() ==  true) {
                    Log.e("Ad", "-----------------true");
                    AdMob.getInstance(context).showRewardedVideo(context, () -> context.startActivity(new Intent(context, CourseActivity.class)
                            .putExtra("ustaz", ustazObjects.get(getAdapterPosition()).getName())));
                } else {
                    Log.e("Ad", "-----------------false");
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
