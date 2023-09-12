package ethio.islamic.durus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Objects;

public class UpdateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.my_statusbar_color));
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_update);
        Button updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(this::onUpdateButtonClick);
    }

    public void onUpdateButtonClick(View view) {
        String packageName = getPackageName(); // Get your app's package name
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            openTelegramChannel();
        }
    }

    public void openTelegramChannel() {
        String channelUsername = "your_channel_username";
        try {
            Intent telegramIntent = new Intent(Intent.ACTION_VIEW);
            telegramIntent.setData(Uri.parse("https://t.me/" + channelUsername));
            startActivity(telegramIntent);
        } catch (Exception e) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW);
            webIntent.setData(Uri.parse("https://t.me/" + channelUsername));
            startActivity(webIntent);
        }
    }
}