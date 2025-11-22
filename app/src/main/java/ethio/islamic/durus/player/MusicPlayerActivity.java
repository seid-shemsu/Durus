package ethio.islamic.durus.player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.concurrent.TimeUnit;

import ethio.islamic.durus.R;

public class MusicPlayerActivity extends AppCompatActivity {
    private TextView startTimeField;
    private TextView endTimeField;
    private MediaPlayer mediaPlayer;
    private Handler myHandler = new Handler();
    private SeekBar seekbar;
    private ImageButton playButton;
    public static int oneTimeOnly = 0;

    private LinearLayout linearLayout;
    TextView percent;
    private String course_name = "_", part_number = "_", audio_link = "", image = "";
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        course_name = getIntent().getExtras().getString("course_name");
        part_number = getIntent().getExtras().getString("part_number");
        audio_link = getIntent().getExtras().getString("audio_link");
        image = getIntent().getExtras().getString("image");
        img = findViewById(R.id.image);

        Glide.with(this).load(image).into(img);

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
        Log.e("Audio link", audio_link);
        download();
    }

    private void download() {
        //final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), course_code + part_number + ".ogg");
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return;
            }
            String root = Environment.getExternalStorageDirectory().toString();
            File dir = new File(root + "/Ustaz Muhammed Amin/" + course_name + "/audios");
            dir.mkdirs();
            final File file = new File(dir, part_number + ".mp3");
            if (file.isFile() && file.length() > 0) {
                play();
            } else {
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.confirmation);
                final TextView download = dialog.findViewById(R.id.download);
                final TextView cancel = dialog.findViewById(R.id.cancel);
                percent = dialog.findViewById(R.id.percent);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                download.setOnClickListener(v -> {
                    download.setVisibility(View.GONE);
                    percent.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.GONE);
                    StorageReference storageReference = FirebaseStorage
                            .getInstance()
                            .getReference(audio_link);
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
                                        Toast.makeText(MusicPlayerActivity.this, getString(R.string.file_not_exist), Toast.LENGTH_SHORT).show();
                                });
                    } catch (Exception e) {
                        dialog.dismiss();
                        Toast.makeText(MusicPlayerActivity.this, getString(R.string.try_again) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                cancel.setOnClickListener(v -> {
                    dialog.dismiss();
                    finish();
                });
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
            File dir = new File(root + "/Ustaz Muhammed Amin/" + course_name + "/audios");
            final File file = new File(dir, part_number + ".mp3");
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

                }
            });
            seekbar.setProgress(startTime);
            myHandler.postDelayed(this, 1000);
        }
    };
}