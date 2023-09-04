package et.islam.durus.parts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import et.islam.durus.R;
import et.islam.durus.detail.DetailActivity;

public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.Holder> {

    Context context;
    List<String> titles, icons, numbers;
    String name;
    String ustaz;

    public TitleAdapter(Context context, List<String> titles, List<String> icons, List<String> numbers, String name, String ustaz) {
        this.context = context;
        this.titles = titles;
        this.name = name;
        this.icons = icons;
        this.numbers = numbers;
        this.ustaz = ustaz;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.title, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        //I have changed passed with lesson sharedPreference
        holder.title.setText(titles.get(position));
        //number
        SharedPreferences passed = context.getSharedPreferences("passed", Context.MODE_PRIVATE);
        SharedPreferences lesson = context.getSharedPreferences("lessons", Context.MODE_PRIVATE);
        holder.number.setTextColor(context.getResources().getColor(R.color.bunny));
        holder.number.setBackground(null);
        holder.number.setText(numbers.get(position));
        if (passed.getBoolean(icons.get(position), false))
            holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.circle_correct));

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, number;
        ImageView icon;
        RelativeLayout linear;
        FragmentManager fragmentManager;

        public Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            linear = itemView.findViewById(R.id.linear);
            number = itemView.findViewById(R.id.number);
            icon = itemView.findViewById(R.id.icon);
            this.fragmentManager = fragmentManager;
            title.setOnClickListener(this);
            linear.setOnClickListener(this);
            number.setOnClickListener(this);
            icon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            context.startActivity(new Intent(context, DetailActivity.class)
                    .putExtra("ustaz", ustaz)
                    .putExtra("course_name", name)
                    .putExtra("part_number",Integer.toString(getAdapterPosition() + 1))
            );
        }
    }
}
