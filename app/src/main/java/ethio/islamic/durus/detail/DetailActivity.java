package ethio.islamic.durus.detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.concurrent.TimeUnit;

import ethio.islamic.durus.R;
import ethio.islamic.durus.player.MusicManager;
import ethio.islamic.durus.player.VideoPlayManager;
import ethio.islamic.durus.reader.Reader;

public class DetailActivity extends AppCompatActivity {

    private SharedPreferences passed;
    private TextView audio;
    private String course_name, part_number, ustaz;
    private String youtube_link, audio_link, pdf_link;
    //music player elements
    private LinearLayout linearLayout;
    private TextView startTimeField;
    private TextView endTimeField;
    private MediaPlayer mediaPlayer;
    private Handler myHandler = new Handler();
    private SeekBar seekbar;
    private ImageButton playButton;
    public static int oneTimeOnly = 0;
    TextView percent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ImageView youtube = findViewById(R.id.youtube);
        audio = findViewById(R.id.audio);
        TextView pdf = findViewById(R.id.pdf);
        ustaz = getIntent().getExtras().getString("ustaz");
        course_name = getIntent().getExtras().getString("course_name");
        part_number = getIntent().getExtras().getString("part_number");
        SharedPreferences lessons = getSharedPreferences("lessons", Context.MODE_PRIVATE);
        passed = getSharedPreferences("passed", Context.MODE_PRIVATE);
        getLinks();
        youtube.setOnClickListener(v -> {
            try {
                mediaPlayer.pause();
            } catch (Exception e) {
            }
            startActivity(new Intent(DetailActivity.this, VideoPlayManager.class).putExtra("link", youtube_link).putExtra("title", course_name + part_number));
        });
        audio.setOnClickListener(v -> {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                download();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        });
        pdf.setOnClickListener(v -> {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                downloadPdf();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        linearLayout = findViewById(R.id.musicplayer);
        startTimeField = findViewById(R.id.starttime);
        endTimeField = findViewById(R.id.endtime);
        seekbar = findViewById(R.id.seekbar1);
        playButton = findViewById(R.id.pause_play_btn);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        playButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playButton.setImageResource(R.drawable.play);
            } else {
                mediaPlayer.start();
                playButton.setImageResource(R.drawable.pause);
            }
        });
    }

    private void downloadPdf() {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File dir = new File(root + "/Durus/" + ustaz + "/" + course_name + "/pdfs");
            if (!dir.exists())
                dir.mkdirs();
            final File file = new File(dir, ustaz + "_" + course_name + "_" + part_number + ".pdf");
            if (file.isFile() && file.length() > 0) {
                startActivity(new Intent(DetailActivity.this, Reader.class)
                        .putExtra("file", file.toString()));
            } else {
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.wait);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                try {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(pdf_link);
                    if (!file.exists())
                        file.createNewFile();
                    storageReference.getFile(file)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    dialog.dismiss();
                                    startActivity(new Intent(DetailActivity.this, Reader.class).putExtra("file", file.toString()));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    if (e.getMessage().contains("location"))
                                        Toast.makeText(DetailActivity.this, getString(R.string.file_not_exist), Toast.LENGTH_SHORT).show();

                                }
                            });
                } catch (Exception e) {
                    dialog.dismiss();
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    Toast.makeText(DetailActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(DetailActivity.this, "ERROR 103\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void getLinks() {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("contents")
                    .child(course_name)
                    .child(part_number);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        youtube_link = dataSnapshot.child("youtube").getValue().toString();
                        audio_link = dataSnapshot.child("music").getValue().toString();
                        //pdf_link = dataSnapshot.child("pdf").getValue().toString();
                        if (audio_link.equalsIgnoreCase("music")) {
                            audio.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(DetailActivity.this, "ERROR 102\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void download() {
        //final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), course_code + part_number + ".ogg");
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            String root = Environment.getExternalStorageDirectory().toString();
            File dir = new File(root + "/Durus/" + ustaz + "/" + course_name + "/audios");
            dir.mkdirs();
            final File file = new File(dir, ustaz + "_" + course_name + "_" + part_number + ".mp3");
            if (file.isFile() && file.length() > 0) {
                play();
            } else {
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.confirmation);
                final Button download = dialog.findViewById(R.id.download);
                final Button cancel = dialog.findViewById(R.id.cancel);
                percent = dialog.findViewById(R.id.percent);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                download.setOnClickListener(v -> {
                    download.setVisibility(View.GONE);
                    percent.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.GONE);
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(audio_link);
                    try {
                        file.createNewFile();
                        storageReference.getFile(file)
                                .addOnSuccessListener(taskSnapshot -> {
                                    dialog.dismiss();
                                    play();
                                })
                                .addOnProgressListener(taskSnapshot -> percent.setText(getResources().getString(R.string.downloading) + "...\n" + 100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount() + "%"))
                                .addOnFailureListener(e -> {
                                    dialog.dismiss();
                                    if (e.getMessage().contains("location"))
                                        Toast.makeText(DetailActivity.this, getString(R.string.file_not_exist), Toast.LENGTH_SHORT).show();
                                });
                    } catch (Exception e) {
                        dialog.dismiss();
                        Toast.makeText(DetailActivity.this, getString(R.string.try_again) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                cancel.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "ERROR 101\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void play() {
        linearLayout.setVisibility(View.VISIBLE);
        playButton.setImageResource(R.drawable.pause);
        mediaPlayer = null;
        mediaPlayer = new MediaPlayer();
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File dir = new File(root + "/Durus/" + ustaz + "/" + course_name + "/audios");
            final File file = new File(dir, ustaz + "_" + course_name + "_" + part_number + ".mp3");
            mediaPlayer.setDataSource(file.toString());
            mediaPlayer.prepare();
        } catch (final Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        MusicManager.stop();
        MusicManager.Player(mediaPlayer);
        int finalTime = mediaPlayer.getDuration();
        int startTime = mediaPlayer.getCurrentPosition();
        if (oneTimeOnly == 0) {
            seekbar.setMax(finalTime);
            oneTimeOnly = 0;
        }
        endTimeField.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) finalTime)))
        );
        startTimeField.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) startTime)))
        );
        seekbar.setProgress(startTime);
        myHandler.postDelayed(UpdateSongTime, 100);
    }

    private Runnable UpdateSongTime = new Runnable() {
        @SuppressLint("DefaultLocale")
        public void run() {
            int startTime = mediaPlayer.getCurrentPosition();
            startTimeField.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
            );
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    linearLayout.setVisibility(View.GONE);
                }
            });
            seekbar.setProgress(startTime);
            myHandler.postDelayed(this, 1000);
        }
    };

}