package ethio.islamic.durus;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import ethio.islamic.durus.utils.Utils;

public class Settings extends AppCompatActivity {
    TextView uuid, payment_2, payment_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.my_statusbar_color));
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_settings);
        uuid = findViewById(R.id.uuid);
        String id = Utils.getUUID(this);
        uuid.setText(id);
        uuid.setOnClickListener(v -> {
            copyToClipboard(id);
            Toast.makeText(this, "User ID Copied" + " ✓", Toast.LENGTH_SHORT).show();

        });
        payment_2 = findViewById(R.id.payment_2);
        payment_3 = findViewById(R.id.payment_3);
        getPaymentDetail();
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("User ID", text);
        clipboard.setPrimaryClip(clip);
    }

    private String account_holder, payment_method, payment_account, telegram_username;
    private int payment_amount;

    private void getPaymentDetail() {
        FirebaseDatabase.getInstance().getReference("configs")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String key = data.getKey();
                            assert key != null;
                            if (key.equalsIgnoreCase("account_holder")) {
                                account_holder = data.getValue().toString();
                            } else if (key.equalsIgnoreCase("payment_method")) {
                                payment_method = data.getValue().toString();
                            } else if (key.equalsIgnoreCase("payment_account")) {
                                payment_account = data.getValue().toString();
                            } else if (key.equalsIgnoreCase("telegram_username")) {
                                telegram_username = data.getValue().toString();
                            } else if (key.equalsIgnoreCase("payment_amount")) {
                                payment_amount = Integer.parseInt(data.getValue().toString());
                            }
                        }
                        String text = getString(R.string.payment_instruction, payment_amount, account_holder, payment_account, payment_method);
                        payment_2.setText(text);
                        payment_3.setText(getString(R.string.send_screenshot_instruction, telegram_username));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}