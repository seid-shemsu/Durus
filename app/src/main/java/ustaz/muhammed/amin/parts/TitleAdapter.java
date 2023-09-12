package ustaz.muhammed.amin.parts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import ustaz.muhammed.amin.R;
import ustaz.muhammed.amin.detail.DetailActivity;
import ustaz.muhammed.amin.player.VideoPlayManager;

public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.Holder> {

    Context context;
    List<PartObject> parts;
    String name, image;

    public TitleAdapter(Context context, List<PartObject> parts, String name, String image) {
        this.context = context;
        this.name = name;
        this.parts = parts;
        this.image = image;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.part_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        //I have changed passed with lesson sharedPreference
        PartObject part = parts.get(position);
        holder.title.setText(part.getName());
        Picasso.get().load(image).into(holder.image);
        if (part.getName().equalsIgnoreCase("music"))
            holder.download.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return parts.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView youtube, download, image;
        LinearLayout musicPlayer;
        public Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);

            youtube = itemView.findViewById(R.id.youtube);
            download = itemView.findViewById(R.id.download);
            image = itemView.findViewById(R.id.image);

            musicPlayer = itemView.findViewById(R.id.musicplayer);

            download.setOnClickListener(v -> {
                musicPlayer.setVisibility(View.VISIBLE);
            });
            youtube.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            //context.startActivity(new Intent(context, DetailActivity.class).putExtra("ustaz", ustaz).putExtra("course_name", name).putExtra("part_number", Integer.toString(getAdapterPosition() + 1)));
            context.startActivity(new Intent(context, VideoPlayManager.class).putExtra("link", parts.get(getAdapterPosition()).getYoutube()));
        }
    }
}
